package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Project;
import com.sonnh.elv.data.domain.Tower;
import com.sonnh.elv.data.repository.ProjectRepository;
import com.sonnh.elv.dto.excel.BOQRowExcelDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;
import com.sonnh.elv.dto.request.CalculateBOMRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;
import com.sonnh.elv.service.CalculateService;
import com.sonnh.elv.service.CalculateBOMService;
import com.sonnh.elv.service.ExcelExportService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CalculateService calculateService;

    @Autowired
    private CalculateBOMService calculateBOMService;

    @Override
    public ByteArrayInputStream exportProjectExcel(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + projectId));

        try (InputStream is = getClass().getResourceAsStream("/templates/BOQ_TEMPLATE.xlsx")) {
            if (is == null) {
                throw new IllegalArgumentException("Template file /templates/BOQ_TEMPLATE.xlsx not found");
            }
            Workbook workbook = WorkbookFactory.create(is);

            // 1. Process BOQ Sheet (Sheet index 0)
            writeBOQSheet(workbook, project);

            // 2. Process BOM Sheet (Sheet index 1)
            try {
                List<CalculateBOMRequestDTO> bomRequests = buildBOMRequests(project);
                CalculateBOMResponseDTO bomResponse = calculateBOMService.calculateBOM(bomRequests);
                writeBOMSheet(workbook, bomResponse);
            } catch (Exception e) {
                System.err.println("Error writing BOM sheet: " + e.getMessage());
                e.printStackTrace();
            }

            // 3. Process Cable Calculation Sheet (Sheet index 2: "tính cáp")
            try {
                writeCableCalculationSheet(workbook, project);
            } catch (Exception e) {
                System.err.println("Error writing Cable Calculation sheet: " + e.getMessage());
                e.printStackTrace();
            }

            // Force evaluation of all formulas before saving
            try {
                workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
            } catch (Exception e) {
                // Ignore formula evaluation errors if some external references exist
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            return new ByteArrayInputStream(bos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export Excel file", e);
        }
    }

    private void writeBOQSheet(Workbook workbook, Project project) {
        Sheet sheet = workbook.getSheetAt(0); // Sheet 'BOQ' at index 0

        List<Tower> towers = project.getTowers();
        List<List<BOQRowExcelDTO>> towersData = new ArrayList<>();
        List<int[]> towersColors = new ArrayList<>(); // Color index for each row of each tower
        int maxFloors = 0;

        // 1. Fetch calculations for all towers
        for (Tower tower : towers) {
            List<CalculateBOQResponseDTO> boqResponse = calculateService.getCalculateBOQ(tower.getId());
            List<BOQRowExcelDTO> boqRows = new ArrayList<>();
            int[] rowColors = new int[boqResponse.size()];
            int groupCount = 0;
            Integer currentCabinetIndex = null;

            for (int i = boqResponse.size() - 1; i >= 0; i--) {
                CalculateBOQResponseDTO dto = boqResponse.get(i);
                int colorIdx = boqResponse.size() - 1 - i;
                
                // Determine alternating group colors based on cabinetIndex
                Integer cabIdx = dto.getCabinetIndex();
                if (cabIdx == null) {
                    rowColors[colorIdx] = 0; // No color (white)
                } else {
                    if (!cabIdx.equals(currentCabinetIndex)) {
                        currentCabinetIndex = cabIdx;
                        groupCount++;
                    }
                    // Alternate: group 1, 3, 5... get color 1 (yellow). group 2, 4, 6... get color 2 (white).
                    rowColors[colorIdx] = (groupCount % 2 != 0) ? 1 : 2;
                }

                int cab2U = 0;
                int cab6U = 0;
                if (dto.getCabinets() != null) {
                    for (CalculateBOQResponseDTO.CabinetDetailResponseDTO cab : dto.getCabinets()) {
                        if (cab.getCabinetType() != null) {
                            if (cab.getCabinetType().toUpperCase().contains("2U")) {
                                cab2U++;
                            } else if (cab.getCabinetType().toUpperCase().contains("6U")) {
                                cab6U++;
                            }
                        }
                    }
                }

                BOQRowExcelDTO rowDto = BOQRowExcelDTO.builder()
                        .floorName(dto.getLabel())
                        .cameraDome(dto.getDomeCount())
                        .cameraBullet(dto.getBulletCount())
                        .totalCamera(dto.getCamerasCount())
                        .cableLength(dto.getCableLength() != null ? dto.getCableLength().doubleValue() : 0.0)
                        .cameraToCabinet(dto.getCameraQuantityInCabinet())
                        .sw24(dto.getSw24Count())
                        .sw16(dto.getSw16Count())
                        .ups(dto.getUpsCount())
                        .pdu(dto.getPduCount())
                        .converter(dto.getConvCount())
                        .cabinet2U(cab2U)
                        .cabinet6U(cab6U)
                        .build();
                boqRows.add(rowDto);
            }
            towersData.add(boqRows);
            towersColors.add(rowColors);
            if (boqRows.size() > maxFloors) {
                maxFloors = boqRows.size();
            }
        }

        if (maxFloors > 0) {
            CellStyle borderlessStyle = workbook.createCellStyle();
            // 2. Prepare floor rows from row 14 (index 13) to (13 + maxFloors - 1)
            // Row index 13 is the template styled row
            for (int r = 14; r < 13 + maxFloors; r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    row = sheet.createRow(r);
                }
                row.setHeight(sheet.getRow(13).getHeight());
            }

            // 3. Process each tower
            for (int t = 0; t < towers.size(); t++) {
                Tower tower = towers.get(t);
                List<BOQRowExcelDTO> boqRows = towersData.get(t);
                int[] rowColors = towersColors.get(t);
                int startCol = t * 15; // Tower 1 starts at 0, Tower 2 at 15, etc.

                // If t > 0, we copy columns 0 to 13 of rows 0 to 13 to columns startCol to startCol + 13
                if (t > 0) {
                    copyColumns(sheet, 0, 13, startCol, 13);
                    copyMergedRegions(sheet, 0, 13, startCol, 13);
                    
                    // Clear duplicated summary table labels from target column N equivalent (startCol + 13)
                    for (int r = 1; r <= 5; r++) {
                        Row row = sheet.getRow(r);
                        if (row != null) {
                            Cell cell = row.getCell(startCol + 13);
                            if (cell != null) {
                                cell.setCellValue("");
                            }
                        }
                    }
                }

                // Update Tower Header Title (Row 9, index 8)
                Row towerHeaderRow = sheet.getRow(8);
                if (towerHeaderRow != null) {
                    // Clear the separator cell (Col A)
                    Cell separatorCell = towerHeaderRow.getCell(startCol);
                    if (separatorCell != null) {
                        separatorCell.setCellValue("");
                    }
                    
                    // Write to the merged cell (Col B, startCol + 1)
                    Cell headerCell = towerHeaderRow.getCell(startCol + 1);
                    if (headerCell == null) headerCell = towerHeaderRow.createCell(startCol + 1);
                    
                    String towerName = tower.getName();
                    if (towerName != null) {
                        towerName = towerName.trim();
                        if (!towerName.toUpperCase().startsWith("THÁP")) {
                            towerName = "THÁP " + towerName;
                        }
                        headerCell.setCellValue(towerName.toUpperCase());
                    }
                }

                // Prepare styles for this tower: Normal (White/No Fill) and Colored (Yellow background)
                CellStyle[] normalStyles = new CellStyle[14];
                CellStyle[] coloredStyles = new CellStyle[14];
                for (int c = 0; c < 14; c++) {
                    Cell templateCell = sheet.getRow(13).getCell(c);
                    if (templateCell != null) {
                        CellStyle templateStyle = templateCell.getCellStyle();
                        
                        // Colored style (clones original yellow style)
                        coloredStyles[c] = workbook.createCellStyle();
                        coloredStyles[c].cloneStyleFrom(templateStyle);
                        
                        // Normal style (clones original, but removes fill to be white)
                        normalStyles[c] = workbook.createCellStyle();
                        normalStyles[c].cloneStyleFrom(templateStyle);
                        normalStyles[c].setFillPattern(FillPatternType.NO_FILL);
                    }
                }

                // Populate data rows for this tower
                for (int i = 0; i < maxFloors; i++) {
                    Row row = sheet.getRow(13 + i);
                    BOQRowExcelDTO rowData = i < boqRows.size() ? boqRows.get(i) : null;
                    int colorType = (rowData != null && i < rowColors.length) ? rowColors[i] : 0;

                    for (int c = 0; c < 14; c++) {
                        int targetCol = startCol + c;
                        Cell cell = row.getCell(targetCol);
                        if (cell == null) {
                            cell = row.createCell(targetCol);
                        }

                        // Apply appropriate style (Colored if part of odd cabinet group, otherwise Normal)
                        if (rowData == null) {
                            cell.setCellStyle(borderlessStyle);
                            cell.setBlank();
                        } else {
                            if (colorType == 1 && coloredStyles[c] != null) {
                                cell.setCellStyle(coloredStyles[c]);
                            } else if (normalStyles[c] != null) {
                                cell.setCellStyle(normalStyles[c]);
                            }
                        }

                        // Write value if we have data for this row
                        if (rowData != null) {
                            switch (c) {
                                case 0:
                                    break;
                                case 1:
                                    if (rowData.getConverter() != null) cell.setCellValue(rowData.getConverter());
                                    break;
                                case 2:
                                    if (rowData.getPdu() != null) cell.setCellValue(rowData.getPdu());
                                    break;
                                case 3:
                                    if (rowData.getUps() != null) cell.setCellValue(rowData.getUps());
                                    break;
                                case 4:
                                    if (rowData.getSw24() != null) cell.setCellValue(rowData.getSw24());
                                    break;
                                case 5:
                                    if (rowData.getSw16() != null) cell.setCellValue(rowData.getSw16());
                                    break;
                                case 6:
                                    if (rowData.getCabinet2U() != null) cell.setCellValue(rowData.getCabinet2U());
                                    break;
                                case 7:
                                    if (rowData.getCabinet6U() != null) cell.setCellValue(rowData.getCabinet6U());
                                    break;
                                case 8:
                                    if (rowData.getCameraToCabinet() != null) cell.setCellValue(rowData.getCameraToCabinet());
                                    break;
                                case 9:
                                    if (rowData.getCameraBullet() != null) cell.setCellValue(rowData.getCameraBullet());
                                    break;
                                case 10:
                                    if (rowData.getCameraDome() != null) cell.setCellValue(rowData.getCameraDome());
                                    break;
                                case 11:
                                    if (rowData.getTotalCamera() != null) cell.setCellValue(rowData.getTotalCamera());
                                    break;
                                case 12:
                                    if (rowData.getFloorName() != null) cell.setCellValue(rowData.getFloorName());
                                    break;
                                case 13:
                                    if (rowData.getCableLength() != null) cell.setCellValue(rowData.getCableLength());
                                    break;
                            }
                        }
                    }
                }

                // 4. Update SUM formulas in Row 13 (index 12) to match actual data range
                Row sumRow = sheet.getRow(12);
                if (sumRow != null) {
                    int endRowIndex = 14 + maxFloors - 1;

                    // Converter to Camera DOME
                    for (int col = 1; col <= 10; col++) {
                        int targetCol = startCol + col;
                        Cell cell = sumRow.getCell(targetCol);
                        if (cell == null) cell = sumRow.createCell(targetCol);
                        String colLetter = CellReference.convertNumToColString(targetCol);
                        cell.setCellFormula("SUM(" + colLetter + "14:" + colLetter + endRowIndex + ")");
                    }

                    // Col L: Tổng CAM (= J13 + K13 relative to startCol)
                    int targetColL = startCol + 11;
                    Cell cellL = sumRow.getCell(targetColL);
                    if (cellL == null) cellL = sumRow.createCell(targetColL);
                    String letterJ = CellReference.convertNumToColString(startCol + 9);
                    String letterK = CellReference.convertNumToColString(startCol + 10);
                    cellL.setCellFormula(letterJ + "13+" + letterK + "13");

                    // Col N: Mét cáp (Col index 13)
                    int targetColN = startCol + 13;
                    Cell cellN = sumRow.getCell(targetColN);
                    if (cellN == null) cellN = sumRow.createCell(targetColN);
                    String letterN = CellReference.convertNumToColString(targetColN);
                    cellN.setCellFormula("SUM(" + letterN + "14:" + letterN + endRowIndex + ")");
                }
            }
            // 5. Update summary table formulas
            Row row2 = sheet.getRow(1);
            Row row3 = sheet.getRow(2);
            Row row4 = sheet.getRow(3);
            Row row5 = sheet.getRow(4);
            Row row6 = sheet.getRow(5);
            Row row7 = sheet.getRow(6);

            int numTowers = towers.size();
            if (row2 != null) {
                setSummaryFormula(row2, 14, 11, numTowers); // Cell O2, colIndex L (11)
                setSummaryFormula(row2, 17, 6, numTowers);  // Cell R2, colIndex G (6)
            }
            if (row3 != null) {
                setSummaryFormula(row3, 14, 9, numTowers);  // Cell O3, colIndex J (9)
                setSummaryFormula(row3, 17, 7, numTowers);  // Cell R3, colIndex H (7)
            }
            if (row4 != null) {
                setSummaryFormula(row4, 14, 10, numTowers); // Cell O4, colIndex K (10)
                setSummaryFormula(row4, 17, 2, numTowers);  // Cell R4, colIndex C (2)
            }
            if (row5 != null) {
                setSummaryFormula(row5, 14, 4, numTowers);  // Cell O5, colIndex E (4)
                setSummaryFormula(row5, 17, 1, numTowers);  // Cell R5, colIndex B (1)
            }
            if (row6 != null) {
                setSummaryFormula(row6, 14, 5, numTowers);  // Cell O6, colIndex F (5)
                setSummaryFormula(row6, 17, 3, numTowers);  // Cell R6, colIndex D (3)
            }
            if (row7 != null) {
                setSummaryFormula(row7, 17, 13, numTowers); // Cell R7, colIndex N (13)
            }
        }
    }

    private void writeCableCalculationSheet(Workbook workbook, Project project) {
        Sheet sheet = workbook.getSheet("tính cáp");
        if (sheet == null && workbook.getNumberOfSheets() > 2) {
            sheet = workbook.getSheetAt(2);
        }
        if (sheet == null) return;

        List<Tower> towers = project.getTowers();
        int maxFloors = 0;
        List<List<CalculateBOQResponseDTO>> towersBOQ = new ArrayList<>();
        
        // 1. Fetch calculations for all towers
        for (Tower tower : towers) {
            List<CalculateBOQResponseDTO> boqResponse = calculateService.getCalculateBOQ(tower.getId());
            towersBOQ.add(boqResponse);
            if (boqResponse.size() > maxFloors) {
                maxFloors = boqResponse.size();
            }
        }

        if (maxFloors == 0) return;

        // 2. Prepare rows from Row 5 (index 4) to (4 + maxFloors - 1)
        CellStyle borderlessStyle = workbook.createCellStyle();
        for (int r = 5; r < 4 + maxFloors; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                row = sheet.createRow(r);
            }
            row.setHeight(sheet.getRow(4).getHeight());
        }

        // 3. Process each tower
        for (int t = 0; t < towers.size(); t++) {
            Tower tower = towers.get(t);
            List<CalculateBOQResponseDTO> boqResponse = towersBOQ.get(t);
            int startCol = t * 10 + 1; // Tháp A starts at Col B (index 1), Tháp B at Col L (index 11), etc.
            double vDist = getVerticalDistance(boqResponse);

            // If t > 0, we copy columns index 1 to 9 of rows 0 to 4 (Row 1 to 5) to startCol to startCol + 8
            if (t > 0) {
                copyColumns(sheet, 1, 9, startCol, 4);
                copyMergedRegions(sheet, 1, 9, startCol, 4);
            }

            // Update Tower Header Title (Row 3, index 2)
            Row towerHeaderRow = sheet.getRow(2);
            if (towerHeaderRow != null) {
                Cell headerCell = towerHeaderRow.getCell(startCol);
                if (headerCell == null) headerCell = towerHeaderRow.createCell(startCol);
                String towerName = tower.getName();
                if (towerName != null) {
                    towerName = towerName.trim();
                    if (!towerName.toUpperCase().startsWith("THÁP")) {
                        towerName = "THÁP " + towerName;
                    }
                    headerCell.setCellValue(towerName.toUpperCase());
                }
            }

            // Prepare template styles for columns 0 to 8 of this tower from Row 5 (index 4)
            CellStyle[] colStyles = new CellStyle[9];
            Row templateRow = sheet.getRow(4);
            if (templateRow != null) {
                for (int c = 0; c < 9; c++) {
                    Cell templateCell = templateRow.getCell(c + 1);
                    if (templateCell != null) {
                        colStyles[c] = templateCell.getCellStyle();
                    }
                }
            }

            // Populate data rows for this tower (top floor first, so reverse order)
            for (int i = 0; i < maxFloors; i++) {
                int rowNum = 5 + i; // 1-indexed Excel row number
                Row row = sheet.getRow(rowNum - 1);
                
                CalculateBOQResponseDTO dto = i < boqResponse.size() ? boqResponse.get(boqResponse.size() - 1 - i) : null;

                for (int c = 0; c < 9; c++) {
                    int targetCol = startCol + c;
                    Cell cell = row.getCell(targetCol);
                    if (cell == null) {
                        cell = row.createCell(targetCol);
                    }

                    if (dto == null) {
                        cell.setCellStyle(borderlessStyle);
                        cell.setBlank();
                    } else {
                        if (colStyles[c] != null) {
                            cell.setCellStyle(colStyles[c]);
                        }

                        // Write value or formula
                        switch (c) {
                            case 0: // Tủ (Cabinet)
                                if (dto.getCabinetIndex() != null) {
                                    cell.setCellValue(getCabinetLabel(dto.getCabinetIndex(), boqResponse));
                                } else {
                                    cell.setCellValue("-");
                                }
                                break;
                            case 1: // Tầng camera (Camera floor)
                                cell.setCellValue(dto.getLabel());
                                break;
                            case 2: // Tầng đặt tủ (Cabinet floor)
                                if (dto.getCabinetIndex() != null) {
                                    cell.setCellValue(getCabinetFloorLabel(dto.getCabinetIndex(), boqResponse));
                                } else {
                                    cell.setCellValue("-");
                                }
                                break;
                            case 3: // Số camera (Camera count)
                                if (dto.getCamerasCount() != null && dto.getCamerasCount() > 0) {
                                    cell.setCellValue(dto.getCamerasCount());
                                } else {
                                    cell.setCellValue("");
                                }
                                break;
                            case 4: // Mét AutoCAD
                                if (dto.getAutocadLength() != null && dto.getAutocadLength() > 0) {
                                    cell.setCellValue(dto.getAutocadLength());
                                } else {
                                    cell.setCellValue("");
                                }
                                break;
                            case 5: // Thông tầng (Atrium)
                                if (dto.getCabinetIndex() != null) {
                                    cell.setCellFormula("ABS(" + dto.getFloorIndex() + "-" + dto.getCabinetIndex() + ")*" + vDist + "*E" + rowNum);
                                } else {
                                    cell.setCellValue(0);
                                }
                                break;
                            case 6: // Xuống tủ (Down cabinet)
                                cell.setCellFormula(vDist + "*E" + rowNum);
                                break;
                            case 7: // Trong tủ (In cabinet)
                                cell.setCellFormula("3*E" + rowNum);
                                break;
                            case 8: // Tổng cáp/tầng (Total cable length)
                                String colF = CellReference.convertNumToColString(startCol + 4);
                                String colI = CellReference.convertNumToColString(startCol + 7);
                                cell.setCellFormula("SUM(" + colF + rowNum + ":" + colI + rowNum + ")");
                                break;
                        }
                    }
                }
            }

            // 4. Update summary row formulas at Row (4 + maxFloors) (index 4 + maxFloors)
            int sumRowNum = 5 + maxFloors;
            Row sumRow = sheet.getRow(sumRowNum - 1);
            if (sumRow == null) {
                sumRow = sheet.createRow(sumRowNum - 1);
            }
            sumRow.setHeight(sheet.getRow(3).getHeight()); // Match header row height

            // Copy cell styles from header row (Row 4, index 3) for premium dark blue banner
            Row headerRow = sheet.getRow(3);
            for (int c = 0; c < 9; c++) {
                int targetCol = startCol + c;
                Cell cell = sumRow.getCell(targetCol);
                if (cell == null) cell = sumRow.createCell(targetCol);
                if (headerRow != null) {
                    Cell headerCell = headerRow.getCell(targetCol);
                    if (headerCell != null) {
                        cell.setCellStyle(headerCell.getCellStyle());
                    }
                }
            }
            
            Cell totalCamLabelCell = sumRow.getCell(startCol + 2); // Column D
            if (totalCamLabelCell == null) totalCamLabelCell = sumRow.createCell(startCol + 2);
            totalCamLabelCell.setCellValue("TỔNG SỐ CAM");

            Cell totalCamValCell = sumRow.getCell(startCol + 3); // Column E
            if (totalCamValCell == null) totalCamValCell = sumRow.createCell(startCol + 3);
            String colLetterE = CellReference.convertNumToColString(startCol + 3);
            totalCamValCell.setCellFormula("SUM(" + colLetterE + "5:" + colLetterE + (sumRowNum - 1) + ")");

            Cell totalCableLabelCell = sumRow.getCell(startCol + 6); // Column H
            if (totalCableLabelCell == null) totalCableLabelCell = sumRow.createCell(startCol + 6);
            totalCableLabelCell.setCellValue("TỔNG SỐ MÉT CÁP");

            Cell totalCableValCell = sumRow.getCell(startCol + 8); // Column J
            if (totalCableValCell == null) totalCableValCell = sumRow.createCell(startCol + 8);
            String colLetterJ = CellReference.convertNumToColString(startCol + 8);
            totalCableValCell.setCellFormula("SUM(" + colLetterJ + "5:" + colLetterJ + (sumRowNum - 1) + ")");
        }

        // 5. Update summary rows for blocks
        int sumRowNum = 5 + maxFloors;
        int rBlockCam = sumRowNum + 3; // 0-indexed row index
        Row rowBlockCam = sheet.getRow(rBlockCam);
        if (rowBlockCam == null) rowBlockCam = sheet.createRow(rBlockCam);
        
        CellStyle boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);

        // Merge cells Column B and C for all summary rows to look premium
        try {
            sheet.addMergedRegion(new CellRangeAddress(rBlockCam, rBlockCam, 1, 2));
        } catch (Exception e) {}
        
        Cell cellLabelBCam = rowBlockCam.getCell(1); // Column B
        if (cellLabelBCam == null) cellLabelBCam = rowBlockCam.createCell(1);
        cellLabelBCam.setCellValue("TỔNG SỐ CAMERA  2 BLOCK");
        cellLabelBCam.setCellStyle(boldStyle);
        
        Cell cellValBCam = rowBlockCam.getCell(3); // Column D
        if (cellValBCam == null) cellValBCam = rowBlockCam.createCell(3);
        cellValBCam.setCellStyle(boldStyle);
        
        int rBlockCable = sumRowNum + 4;
        Row rowBlockCable = sheet.getRow(rBlockCable);
        if (rowBlockCable == null) rowBlockCable = sheet.createRow(rBlockCable);
        
        try {
            sheet.addMergedRegion(new CellRangeAddress(rBlockCable, rBlockCable, 1, 2));
        } catch (Exception e) {}

        Cell cellLabelBCable = rowBlockCable.getCell(1); // Column B
        if (cellLabelBCable == null) cellLabelBCable = rowBlockCable.createCell(1);
        cellLabelBCable.setCellValue("TỔNG SỐ MÉT CÁP 2 BLOCK");
        cellLabelBCable.setCellStyle(boldStyle);
        
        Cell cellValBCable = rowBlockCable.getCell(3); // Column D
        if (cellValBCable == null) cellValBCable = rowBlockCable.createCell(3);
        cellValBCable.setCellStyle(boldStyle);

        StringBuilder camFormula = new StringBuilder();
        StringBuilder cableFormula = new StringBuilder();
        for (int t = 0; t < towers.size(); t++) {
            int colE = t * 10 + 1 + 3; // Column E (index startCol + 3)
            int colJ = t * 10 + 1 + 8; // Column J (index startCol + 8)
            String letterE = CellReference.convertNumToColString(colE);
            String letterJ = CellReference.convertNumToColString(colJ);
            if (t > 0) {
                camFormula.append("+");
                cableFormula.append("+");
            }
            camFormula.append(letterE).append(sumRowNum);
            cableFormula.append(letterJ).append(sumRowNum);
        }
        cellValBCam.setCellFormula(camFormula.toString());
        cellValBCable.setCellFormula(cableFormula.toString());

        int rRatio = sumRowNum + 5;
        Row rowRatio = sheet.getRow(rRatio);
        if (rowRatio == null) rowRatio = sheet.createRow(rRatio);
        
        try {
            sheet.addMergedRegion(new CellRangeAddress(rRatio, rRatio, 1, 2));
        } catch (Exception e) {}

        Cell cellLabelRatio = rowRatio.getCell(1); // Column B
        if (cellLabelRatio == null) cellLabelRatio = rowRatio.createCell(1);
        cellLabelRatio.setCellValue("TỈ LỆ 30%");
        cellLabelRatio.setCellStyle(boldStyle);
        
        Cell cellValRatio = rowRatio.getCell(3); // Column D
        if (cellValRatio == null) cellValRatio = rowRatio.createCell(3);
        cellValRatio.setCellFormula("D" + (rBlockCable + 1) + "*30%");
        cellValRatio.setCellStyle(boldStyle);

        int rTotalWithRatio = sumRowNum + 6;
        Row rowTotalWithRatio = sheet.getRow(rTotalWithRatio);
        if (rowTotalWithRatio == null) rowTotalWithRatio = sheet.createRow(rTotalWithRatio);
        
        try {
            sheet.addMergedRegion(new CellRangeAddress(rTotalWithRatio, rTotalWithRatio, 1, 2));
        } catch (Exception e) {}

        Cell cellLabelTotal = rowTotalWithRatio.getCell(1); // Column B
        if (cellLabelTotal == null) cellLabelTotal = rowTotalWithRatio.createCell(1);
        cellLabelTotal.setCellValue("TỔNG SỐ MÉT CÁP ĐÃ CỘNG 30%");
        cellLabelTotal.setCellStyle(boldStyle);
        
        Cell cellValTotal = rowTotalWithRatio.getCell(3); // Column D
        if (cellValTotal == null) cellValTotal = rowTotalWithRatio.createCell(3);
        cellValTotal.setCellFormula("D" + (rBlockCable + 1) + "+D" + (rRatio + 1));
        cellValTotal.setCellStyle(boldStyle);

        // 6. Clean up remaining rows in the sheet to prevent ghost data
        int maxTemplateRows = Math.max(sheet.getLastRowNum() + 1, 80);
        for (int r = sumRowNum; r < maxTemplateRows; r++) {
            Row row = sheet.getRow(r);
            if (row != null) {
                if (r != rBlockCam && r != rBlockCable && r != rRatio && r != rTotalWithRatio) {
                    for (int c = 1; c < 21; c++) {
                        Cell cell = row.getCell(c);
                        if (cell != null) {
                            cell.setBlank();
                        }
                    }
                }
            }
        }
    }

    private double getVerticalDistance(List<CalculateBOQResponseDTO> boqResponse) {
        for (CalculateBOQResponseDTO f : boqResponse) {
            if (f.getCamerasCount() != null && f.getCamerasCount() > 0 && f.getDownCabinet() != null && f.getDownCabinet() > 0) {
                return (double) f.getDownCabinet() / f.getCamerasCount();
            }
        }
        return 4.0;
    }

    private String getCabinetLabel(int cabinetIndex, List<CalculateBOQResponseDTO> boqResponse) {
        for (CalculateBOQResponseDTO f : boqResponse) {
            if (f.getFloorIndex() != null && f.getFloorIndex().equals(cabinetIndex)) {
                return "Tủ " + f.getLabel().replace("Tầng ", "").replace("Tầng", "").trim();
            }
        }
        return "Tủ " + cabinetIndex;
    }

    private String getCabinetFloorLabel(int cabinetIndex, List<CalculateBOQResponseDTO> boqResponse) {
        for (CalculateBOQResponseDTO f : boqResponse) {
            if (f.getFloorIndex() != null && f.getFloorIndex().equals(cabinetIndex)) {
                return f.getLabel();
            }
        }
        return "Tầng " + cabinetIndex;
    }

    private void copyColumns(Sheet sheet, int srcStartCol, int srcEndCol, int destStartCol, int maxRow) {
        for (int r = 0; r <= maxRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;
            for (int c = srcStartCol; c <= srcEndCol; c++) {
                Cell srcCell = row.getCell(c);
                if (srcCell == null) continue;
                int destCol = destStartCol + (c - srcStartCol);
                Cell destCell = row.getCell(destCol);
                if (destCell == null) {
                    destCell = row.createCell(destCol);
                }
                copyCell(srcCell, destCell);
            }
        }
    }

    private void copyMergedRegions(Sheet sheet, int srcStartCol, int srcEndCol, int destStartCol, int maxRow) {
        List<CellRangeAddress> mergedRegions = new ArrayList<>();
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.getFirstRow() <= maxRow && region.getLastRow() <= maxRow) {
                if (region.getFirstColumn() >= srcStartCol && region.getLastColumn() <= srcEndCol) {
                    mergedRegions.add(region);
                }
            }
        }
        for (CellRangeAddress region : mergedRegions) {
            int firstCol = destStartCol + (region.getFirstColumn() - srcStartCol);
            int lastCol = destStartCol + (region.getLastColumn() - srcStartCol);
            sheet.addMergedRegion(new CellRangeAddress(region.getFirstRow(), region.getLastRow(), firstCol, lastCol));
        }
    }

    private void copyCell(Cell srcCell, Cell destCell) {
        destCell.setCellStyle(srcCell.getCellStyle());
        switch (srcCell.getCellType()) {
            case FORMULA:
                destCell.setCellFormula(srcCell.getCellFormula());
                break;
            case NUMERIC:
                destCell.setCellValue(srcCell.getNumericCellValue());
                break;
            case STRING:
                destCell.setCellValue(srcCell.getStringCellValue());
                break;
            case BOOLEAN:
                destCell.setCellValue(srcCell.getBooleanCellValue());
                break;
            case ERROR:
                destCell.setCellErrorValue(srcCell.getErrorCellValue());
                break;
            case BLANK:
                destCell.setBlank();
                break;
            default:
                break;
        }
    }

    private void setSummaryFormula(Row row, int targetColIndex, int sourceColOffset, int numTowers) {
        Cell cell = row.getCell(targetColIndex);
        if (cell == null) {
            cell = row.createCell(targetColIndex);
        }
        
        StringBuilder formula = new StringBuilder();
        for (int t = 0; t < numTowers; t++) {
            int colIndex = t * 15 + sourceColOffset;
            String colLetter = CellReference.convertNumToColString(colIndex);
            if (t > 0) {
                formula.append("+");
            }
            formula.append(colLetter).append("13");
        }
        cell.setCellFormula(formula.toString());
    }

    private List<CalculateBOMRequestDTO> buildBOMRequests(Project project) {
        List<CalculateBOMRequestDTO> bomRequests = new ArrayList<>();
        if (project == null || project.getTowers() == null) {
            return bomRequests;
        }
        for (Tower tower : project.getTowers()) {
            List<CalculateBOQResponseDTO> boqResponse = calculateService.getCalculateBOQ(tower.getId());
            if (boqResponse == null || boqResponse.isEmpty()) {
                continue;
            }

            int totalCamera = boqResponse.stream().mapToInt(f -> f.getCamerasCount() != null ? f.getCamerasCount() : 0).sum();
            int totalCamDome = boqResponse.stream().mapToInt(f -> f.getDomeCount() != null ? f.getDomeCount() : 0).sum();
            int totalCamBullet = boqResponse.stream().mapToInt(f -> f.getBulletCount() != null ? f.getBulletCount() : 0).sum();
            int totalSw16 = boqResponse.stream().mapToInt(f -> f.getSw16Count() != null ? f.getSw16Count() : 0).sum();
            int totalSw24 = boqResponse.stream().mapToInt(f -> f.getSw24Count() != null ? f.getSw24Count() : 0).sum();
            int totalSwichPOE = totalSw16 + totalSw24;
            
            java.util.Map<String, Integer> cabinets = new java.util.HashMap<>();
            cabinets.put("2U", 0);
            cabinets.put("6U", 0);
            cabinets.put("10U", 0);
            cabinets.put("32U", 0);
            cabinets.put("42U", 0);

            for (CalculateBOQResponseDTO f : boqResponse) {
                if (f.getIsCabinetPlaced() != null && f.getIsCabinetPlaced()) {
                    if (f.getCabinets() != null && !f.getCabinets().isEmpty()) {
                        for (CalculateBOQResponseDTO.CabinetDetailResponseDTO cab : f.getCabinets()) {
                            String type = cab.getCabinetType() != null ? cab.getCabinetType() : "2U";
                            String typeKey = "2U";
                            if (type.toUpperCase().contains("6U")) {
                                typeKey = "6U";
                            } else if (type.toUpperCase().contains("10U")) {
                                typeKey = "10U";
                            } else if (type.toUpperCase().contains("32U")) {
                                typeKey = "32U";
                            } else if (type.toUpperCase().contains("42U")) {
                                typeKey = "42U";
                            }
                            cabinets.put(typeKey, cabinets.getOrDefault(typeKey, 0) + 1);
                        }
                    } else {
                        String type = f.getCabinetType() != null ? f.getCabinetType() : "2U";
                        String typeKey = "2U";
                        if (type.toUpperCase().contains("6U")) {
                            typeKey = "6U";
                        } else if (type.toUpperCase().contains("10U")) {
                            typeKey = "10U";
                        } else if (type.toUpperCase().contains("32U")) {
                            typeKey = "32U";
                        } else if (type.toUpperCase().contains("42U")) {
                            typeKey = "42U";
                        }
                        cabinets.put(typeKey, cabinets.getOrDefault(typeKey, 0) + 1);
                    }
                }
            }

            int totalUPS = (int) boqResponse.stream().filter(f -> f.getIsCabinetPlaced() != null && f.getIsCabinetPlaced() && f.getUpsCount() != null && f.getUpsCount() > 0).count();
            int totalPDU = boqResponse.stream().mapToInt(f -> f.getPduCount() != null ? f.getPduCount() : 0).sum();
            int totalConverter = boqResponse.stream().mapToInt(f -> f.getConvCount() != null ? f.getConvCount() : 0).sum();
            int totalCableLength = boqResponse.stream().mapToInt(f -> f.getCableLength() != null ? f.getCableLength().intValue() : 0).sum();

            List<CalculateBOMRequestDTO.FloorBOMInfo> floorInfos = new ArrayList<>();
            for (CalculateBOQResponseDTO f : boqResponse) {
                floorInfos.add(CalculateBOMRequestDTO.FloorBOMInfo.builder()
                        .floorIndex(f.getFloorIndex())
                        .isCabinetPlaced(f.getIsCabinetPlaced() != null ? f.getIsCabinetPlaced() : false)
                        .label(f.getLabel())
                        .camerasCount(f.getCamerasCount() != null ? f.getCamerasCount() : 0)
                        .domeCount(f.getDomeCount() != null ? f.getDomeCount() : 0)
                        .bulletCount(f.getBulletCount() != null ? f.getBulletCount() : 0)
                        .cameraQuantityInCabinet(f.getCameraQuantityInCabinet() != null ? f.getCameraQuantityInCabinet() : 0)
                        .sw24Count(f.getSw24Count() != null ? f.getSw24Count() : 0)
                        .sw16Count(f.getSw16Count() != null ? f.getSw16Count() : 0)
                        .upsCount(f.getUpsCount() != null ? f.getUpsCount() : 0)
                        .pduCount(f.getPduCount() != null ? f.getPduCount() : 0)
                        .convCount(f.getConvCount() != null ? f.getConvCount() : 0)
                        .cabinetType(f.getCabinetType())
                        .build());
            }

            bomRequests.add(CalculateBOMRequestDTO.builder()
                    .towerId(tower.getId())
                    .totalCamera(totalCamera)
                    .totalCamDome(totalCamDome)
                    .totalCamBullet(totalCamBullet)
                    .totalSwichPOE(totalSwichPOE)
                    .totalSw16(totalSw16)
                    .totalSw24(totalSw24)
                    .cabinets(cabinets)
                    .totalUPS(totalUPS)
                    .totalPDU(totalPDU)
                    .totalConverter(totalConverter)
                    .totalCableLength(totalCableLength)
                    .floors(floorInfos)
                    .build());
        }
        return bomRequests;
    }

    private void writeBOMSheet(Workbook workbook, CalculateBOMResponseDTO bomResponse) {
        Sheet bomSheet = workbook.getSheet("BOM");
        if (bomSheet == null && workbook.getNumberOfSheets() > 1) {
            bomSheet = workbook.getSheetAt(1);
        }
        if (bomSheet == null) return;

        // Map of row index (0-indexed) to calculated quantity
        java.util.Map<Integer, Integer> bomQtyMap = new java.util.HashMap<>();
        bomQtyMap.put(8, bomResponse.getCamDomeQuantity());         // Row 9 (Camera Dome)
        bomQtyMap.put(9, bomResponse.getCamBulletQuantity());       // Row 10 (Camera Bullet)
        bomQtyMap.put(10, bomResponse.getRecorder16Quantity());     // Row 11 (Recorder 16ch)
        bomQtyMap.put(11, bomResponse.getRecorder32Quantity());     // Row 12 (Recorder 32ch)
        bomQtyMap.put(12, bomResponse.getHardDiskQuantity());       // Row 13 (HDD)
        bomQtyMap.put(13, bomResponse.getSwich16POEQuantity());     // Row 14 (Sw POE 16)
        bomQtyMap.put(14, bomResponse.getSwich24POEQuantity());     // Row 15 (Sw POE 24)
        bomQtyMap.put(15, bomResponse.getSwich16CISCOQuantity());   // Row 16 (Cisco 16 port)
        bomQtyMap.put(16, bomResponse.getSwich24CISCOQuantity());   // Row 17 (Cisco 24 port) - NEW
        bomQtyMap.put(17, bomResponse.getObserScreenQuantity());    // Row 18 (Samsung Screen)

        bomQtyMap.put(22, bomResponse.getFiberCableQuantity());     // Row 23 (Cáp quang 4FO)
        bomQtyMap.put(23, bomResponse.getCableQuantity());          // Row 24 (Cáp mạng Cat5E)
        bomQtyMap.put(24, bomResponse.getCableQuantity());          // Row 25 (Cáp mạng Cat5E) - NEW
        bomQtyMap.put(25, bomResponse.getConverterQuantity());      // Row 26 (Converter)
        bomQtyMap.put(27, bomResponse.getCabinet2UQuantity());      // Row 28 (Tủ 2U)
        bomQtyMap.put(28, bomResponse.getCabinet6UQuantity());      // Row 29 (Tủ 6U)
        bomQtyMap.put(29, bomResponse.getCabinet10UQuantity());     // Row 30 (Tủ 10U)
        bomQtyMap.put(30, bomResponse.getCabinet32UQuantity());     // Row 31 (Tủ 32U)
        bomQtyMap.put(31, bomResponse.getCabinet32UQuantity());     // Row 32 (Tủ 32U) - NEW
        bomQtyMap.put(32, bomResponse.getCabinet42UQuantity());     // Row 33 (Tủ 42U)
        bomQtyMap.put(33, bomResponse.getOdf12FOQuantity());        // Row 34 (ODF 12FO) - NEW
        bomQtyMap.put(34, bomResponse.getOdf24FOQuantity());        // Row 35 (ODF 24FO)

        bomQtyMap.put(36, bomResponse.getCvvCable());               // Row 37 (Dây điện CVV)
        bomQtyMap.put(37, bomResponse.getPduQuantity());            // Row 38 (PDU)

        bomQtyMap.put(39, bomResponse.getUps1000Quantity());        // Row 40 (UPS 1000)
        bomQtyMap.put(40, bomResponse.getUps3000Quantity());        // Row 41 (UPS 3000)

        bomQtyMap.put(44, bomResponse.getAmpCatQuantity());         // Row 45 (Đầu mạng AMP)
        bomQtyMap.put(45, bomResponse.getFiberOpticalPatchQuantity()); // Row 46 (Dây nhảy quang)
        bomQtyMap.put(46, bomResponse.getOdf4FOQuantity());         // Row 47 (ODF 4FO)
        bomQtyMap.put(47, bomResponse.getPatchCordQuantity());      // Row 48 (Dây nhảy mạng Cat5)
        bomQtyMap.put(48, bomResponse.getCablemanageQuantity());    // Row 49 (Thanh quản lý)

        // Populate Column E (SLG, index 4) and Column G (Tổng nhân công, index 6)
        for (int r = 8; r < 60; r++) {
            Row row = bomSheet.getRow(r);
            if (row == null) continue;

            // 1. Write quantity in Column E (index 4) if it doesn't already have a formula
            if (bomQtyMap.containsKey(r)) {
                Integer qty = bomQtyMap.get(r);
                Cell cellE = row.getCell(4);
                if (cellE == null) {
                    cellE = row.createCell(4);
                }
                if (cellE.getCellType() != CellType.FORMULA) {
                    if (qty != null) {
                        cellE.setCellValue(qty);
                    } else {
                        cellE.setBlank();
                    }
                }
            }

            // 2. Set formula for Column G (Tổng nhân công, index 6) = E * F if Column F is numeric
            Cell cellF = row.getCell(5); // Column F (index 5)
            if (cellF != null && cellF.getCellType() == CellType.NUMERIC) {
                Cell cellG = row.getCell(6); // Column G (index 6)
                if (cellG == null) {
                    cellG = row.createCell(6);
                }
                cellG.setCellFormula("E" + (r + 1) + "*F" + (r + 1));
            }
        }
    }
}
