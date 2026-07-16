package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import com.sonnh.elv.data.repository.ConfigRepository;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO.FloorRequest;
import com.sonnh.elv.dto.request.CalculateBOQManualRequestDTO;
import com.sonnh.elv.dto.response.CabinetEquipmentDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;
import com.sonnh.elv.service.CalculateService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CalcualateServiceImpl implements CalculateService {
    private final ConfigRepository configRepository;

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    private static class CableDetail {
        private int atrium;
        private int downCabinet;
        private int inCabinet;
        private int autocadLength;
        private int totalCable;
    }

    @Override
    public List<CalculateBOQResponseDTO> calculateBOQ(CalculateBOQRequestDTO dto) {
        Config config = configRepository
                .findById(UUID.fromString("a2b0a797-8ff2-4a79-ac5d-78525bd25e90")).get();
        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        calculateCabinetPlacementUitls(dto, mapResult, config, 0, dto.getFloors().size() - 1);
        calculateCameraQuantityInCabinet(mapResult, dto);
        calculateSwichPOE(mapResult, config);
        calculateUPS(mapResult, config);
        calculateConverter(mapResult, config);
        calculatePDU(mapResult, config);
        for (Integer key : mapResult.keySet()) {
            System.out.println("key: " + key + " value: " + mapResult.get(key).toString());
        }

        List<CalculateBOQResponseDTO> result = new ArrayList<>();
        for (FloorRequest floor : dto.getFloors()) {
            boolean isPlaced = mapResult.containsKey(floor.getFloorIndex());
            CalculateBOQResponseDTO.CalculateBOQResponseDTOBuilder builder = CalculateBOQResponseDTO.builder()
                    .floorIndex(floor.getFloorIndex())
                    .label(floor.getLabel())
                    .camerasCount(floor.getCamerasCount())
                    .isCabinetPlaced(isPlaced);

            // Find covering cabinet range
            CabinetEquipmentDTO coveringCabinet = null;
            Integer cabinetIndex = null;
            for (Map.Entry<Integer, CabinetEquipmentDTO> entry : mapResult.entrySet()) {
                CabinetEquipmentDTO cab = entry.getValue();
                if (floor.getFloorIndex() >= cab.getFrom() && floor.getFloorIndex() <= cab.getTo()) {
                    coveringCabinet = cab;
                    cabinetIndex = entry.getKey();
                    break;
                }
            }

            if (coveringCabinet != null) {
                builder.fromIndex(coveringCabinet.getFrom())
                        .toIndex(coveringCabinet.getTo())
                        .cabinetIndex(cabinetIndex);
            }

            CableDetail cableDetail = calculateCableLength(cabinetIndex, floor, dto.getVerticalDistance(),
                    floor.getCamerasCount());
            builder.cableLength(cableDetail.getTotalCable())
                    .atrium(cableDetail.getAtrium())
                    .downCabinet(cableDetail.getDownCabinet())
                    .inCabinet(cableDetail.getInCabinet())
                    .autocadLength(cableDetail.getAutocadLength());

            if (isPlaced) {
                CabinetEquipmentDTO cabinet = mapResult.get(floor.getFloorIndex());
                builder.cameraQuantityInCabinet(cabinet.getCameraQuantityInCabinet())
                        .sw24Count(cabinet.getSw24Quantity())
                        .sw16Count(cabinet.getSw16Quantity())
                        .upsCount(cabinet.getUps())
                        .pduCount(cabinet.getPdu())
                        .convCount(cabinet.getConverter())
                        .cabinetType(dto.getRackType());
            } else {
                builder.cameraQuantityInCabinet(0)
                        .sw24Count(0)
                        .sw16Count(0)
                        .upsCount(0)
                        .pduCount(0)
                        .convCount(0)
                        .cabinetType(null);
            }
            result.add(builder.build());
        }

        return result;
    }

    private CableDetail calculateCableLength(Integer cabinetIndex, FloorRequest floor, Double verticalDistance,
            Integer cameraQuantity) {
        if (cabinetIndex == null) {
            return CableDetail.builder()
                    .atrium(0)
                    .downCabinet(0)
                    .inCabinet(0)
                    .autocadLength(0)
                    .totalCable(0)
                    .build();
        }
        int floorDiff = Math.abs(floor.getFloorIndex() - cabinetIndex) + 1;
        int baseCable = floor.getCableLength() != null ? floor.getCableLength() : 0;
        double vDist = verticalDistance != null ? verticalDistance : 0.0;
        int atrium = (int) Math.round(floorDiff * vDist * cameraQuantity);
        int downCabinet = (int) Math.round(cameraQuantity * verticalDistance);
        int inCabinet = (int) Math.round(cameraQuantity * 3);
        int totalCable = inCabinet + downCabinet + atrium + baseCable;
        return CableDetail.builder()
                .atrium(atrium)
                .downCabinet(downCabinet)
                .inCabinet(inCabinet)
                .autocadLength(baseCable)
                .totalCable(totalCable)
                .build();
    }

    public Map<Integer, CabinetEquipmentDTO> calculateCabinetPlacementUitls(CalculateBOQRequestDTO dto,
            Map<Integer, CabinetEquipmentDTO> mapResult, Config config, int min, int max) {
        // tính pivot
        int pivot = 0;
        int maxFloorInRange = 0;
        int cabinetIndex = 0;
        int from = min;
        int to = 0;
        // int flag = 0;
        Integer horizontalDistance = dto.getHorizontalDistance().intValue();
        Integer verticalDistance = dto.getVerticalDistance().intValue();
        int pivotResult = config.getConditionLength() - horizontalDistance;

        int maxSize = min;
        for (int i = max; i >= min; i--) {
            if (dto.getFloors().get(i).getCamerasCount() != 0) {
                maxSize = dto.getFloors().get(i).getFloorIndex() + 1;
                break;
            }
        }

        // tính from
        from = min;
        for (int i = min; i <= max; i++) {
            FloorRequest floor = dto.getFloors().get(i);
            if (floor.getCamerasCount() != 0) {
                from = floor.getFloorIndex();
                break;
            }
            from++;
        }
        // tính pivot ( tầng cao nhất có thể đặt tủ)
        while (pivotResult >= 0) {
            pivotResult -= verticalDistance;
            if (pivotResult > 0) {
                pivot++;
            }
        }
        System.out.println("from: " + from);
        pivot++;
        cabinetIndex = from + pivot - 1;
        System.out.println("pivot: " + pivot);
        // case thường
        if (!dto.getRackType().equals("2U")) {
            if (cabinetIndex >= maxSize) {
                maxFloorInRange = maxSize - 1;
                cabinetIndex = from + (maxFloorInRange - from) / 2;
                to = maxFloorInRange;
            } else {
                maxFloorInRange = from + pivot * 2 - 2;
                to = maxFloorInRange;
            }
            if (maxFloorInRange >= maxSize) {
                maxFloorInRange = maxSize - 1;
                to = maxFloorInRange;
                cabinetIndex = from + (maxFloorInRange - from) / 2;
            }
            for (int i = min; i <= max; i++) {
                FloorRequest floor = dto.getFloors().get(i);
                if (floor.getFloorIndex() < from) {
                    continue;
                }
                System.out.println("--------------------------------");
                System.out.println("maxFloorInRange: " + maxFloorInRange);
                System.out.println("cabinetIndex: " + cabinetIndex);
                if (from < maxSize && floor.getFloorIndex() == cabinetIndex) {
                    System.out.println("Put vao map");
                    CabinetEquipmentDTO cabinetEquipmentDTO = new CabinetEquipmentDTO();
                    cabinetEquipmentDTO.setFrom(from);
                    cabinetEquipmentDTO.setTo(to);
                    mapResult.put(floor.getFloorIndex(), cabinetEquipmentDTO);
                    from = maxFloorInRange + 1;
                    maxFloorInRange = from + pivot * 2 - 2;
                    cabinetIndex = from + pivot - 1;
                    to = maxFloorInRange;

                    if (maxFloorInRange >= maxSize) {
                        maxFloorInRange = maxSize - 1;
                        to = maxFloorInRange;
                        cabinetIndex = from + (maxFloorInRange - from) / 2;
                    }
                }
            }
        } else {
            // case 2U
            System.out.println("case 2U");

            // init
            maxFloorInRange = 0;
            cabinetIndex = 0;
            from = min;
            to = 0;
            // tính from
            for (int i = min; i <= max; i++) {
                FloorRequest floor = dto.getFloors().get(i);
                if (floor.getCamerasCount() != 0) {
                    from = floor.getFloorIndex();
                    break;
                }
                from++;
            }
            cabinetIndex = from + pivot - 1;

            if (cabinetIndex >= maxSize) {
                maxFloorInRange = maxSize - 1;
                cabinetIndex = from + (maxFloorInRange - from) / 2;
                to = maxFloorInRange;
            } else {
                maxFloorInRange = from + pivot * 2 - 2;
                to = maxFloorInRange;
            }
            if (maxFloorInRange >= maxSize) {
                maxFloorInRange = maxSize - 1;
                to = maxFloorInRange;
                cabinetIndex = from + (maxFloorInRange - from) / 2;
            }
            int totalCamera2U = 0;
            for (int i = min; i <= max; i++) {
                FloorRequest floor = dto.getFloors().get(i);
                if (floor.getFloorIndex() < from) {
                    continue;
                }
                System.out.println("cabinetIndex: " + cabinetIndex);
                System.out.println("maxFloorInRange: " + maxFloorInRange);
                if (from < maxSize && floor.getFloorIndex() == to) {
                    totalCamera2U += floor.getCamerasCount();
                    if (totalCamera2U <= config.getSw24ConditionQuanity()) {
                        System.out.println("Put vao map");
                        CabinetEquipmentDTO cabinetEquipmentDTO = new CabinetEquipmentDTO();
                        cabinetEquipmentDTO.setFrom(from);
                        cabinetEquipmentDTO.setTo(to);
                        mapResult.put(cabinetIndex, cabinetEquipmentDTO);
                        from = maxFloorInRange + 1;
                        maxFloorInRange = from + pivot * 2 - 2;
                        cabinetIndex = from + pivot - 1;
                        to = maxFloorInRange;
                        totalCamera2U = 0;

                        if (maxFloorInRange >= maxSize) {
                            maxFloorInRange = maxSize - 1;
                            to = maxFloorInRange;
                            cabinetIndex = from + (maxFloorInRange - from) / 2;
                        }
                    } else {
                        System.out.println("Vượt quá " + config.getSw24ConditionQuanity() + " camera, dat tu som");
                        if (to - 1 >= from) {
                            CabinetEquipmentDTO cabinetEquipmentDTO = new CabinetEquipmentDTO();
                            cabinetEquipmentDTO.setFrom(from);
                            cabinetEquipmentDTO.setTo(to - 1);
                            mapResult.put(from + (to - 1 - from) / 2, cabinetEquipmentDTO);
                        }

                        from = floor.getFloorIndex();
                        totalCamera2U = floor.getCamerasCount();
                        cabinetIndex = from + pivot - 1;
                        maxFloorInRange = from + pivot * 2 - 2;
                        to = maxFloorInRange;
                        if (maxFloorInRange >= maxSize) {
                            maxFloorInRange = maxSize - 1;
                            to = maxFloorInRange;
                            cabinetIndex = from + (maxFloorInRange - from) / 2;
                        }
                    }
                } else if (from < maxSize && floor.getFloorIndex() < maxFloorInRange) {
                    if (totalCamera2U + floor.getCamerasCount() <= config.getSw24ConditionQuanity()) {
                        totalCamera2U += floor.getCamerasCount();
                    } else {
                        CabinetEquipmentDTO cabinetEquipmentDTO = new CabinetEquipmentDTO();
                        to = floor.getFloorIndex() - 1;
                        if (to < from) {
                            to = from;
                        }
                        cabinetEquipmentDTO.setFrom(from);
                        cabinetEquipmentDTO.setTo(to);
                        mapResult.put(from + (to - from) / 2, cabinetEquipmentDTO);
                        from = floor.getFloorIndex();
                        totalCamera2U = floor.getCamerasCount();
                        cabinetIndex = from + pivot - 1;
                        maxFloorInRange = from + pivot * 2 - 2;

                        to = maxFloorInRange;
                        if (maxFloorInRange >= maxSize) {
                            maxFloorInRange = maxSize - 1;
                            to = maxFloorInRange;
                            cabinetIndex = from + (maxFloorInRange - from) / 2;
                        }
                    }
                }
            }
            if (from < maxSize) {
                int finalTo = maxSize - 1;
                int finalCabinetIndex = from + (finalTo - from) / 2;
                CabinetEquipmentDTO cabinetEquipmentDTO = new CabinetEquipmentDTO();
                cabinetEquipmentDTO.setFrom(from);
                cabinetEquipmentDTO.setTo(finalTo);
                mapResult.put(finalCabinetIndex, cabinetEquipmentDTO);
            }
        }

        return mapResult;
    }

    // -----------------------------------------------------
    public Map<Integer, CabinetEquipmentDTO> calculateCameraQuantityInCabinet(
            Map<Integer, CabinetEquipmentDTO> mapResult,
            CalculateBOQRequestDTO dto) {
        for (Integer key : mapResult.keySet()) {
            int total = 0;
            for (int i = mapResult.get(key).getFrom(); i <= mapResult.get(key).getTo(); i++) {
                total += dto.getFloors().get(i).getCamerasCount();
            }
            mapResult.get(key).setCameraQuantityInCabinet(total);
            total = 0;
        }

        return mapResult;
    }

    public Map<Integer, CabinetEquipmentDTO> calculateSwichPOE(Map<Integer, CabinetEquipmentDTO> mapResult,
            Config config) {
        int limit24 = config.getSw24ConditionQuanity();
        int limit16 = config.getSw16ConditionQuanity();

        for (Integer key : mapResult.keySet()) {
            CabinetEquipmentDTO cabinet = mapResult.get(key);
            int cameraCount = cabinet.getCameraQuantityInCabinet();

            if (cameraCount <= 0) {
                cabinet.setSw24Quantity(0);
                cabinet.setSw16Quantity(0);
                continue;
            }

            // Tìm số lượng switch tối thiểu lý thuyết
            int minSwitches = (int) Math.ceil((double) cameraCount / limit24);
            int bestX = -1;
            int bestY = -1;
            int minCapacity = Integer.MAX_VALUE;

            // Duyệt từ số lượng switch tối thiểu tăng dần lên
            for (int S = minSwitches;; S++) {
                for (int x = 0; x <= S; x++) {
                    int y = S - x;
                    int capacity = x * limit24 + y * limit16;

                    if (capacity >= cameraCount && capacity < minCapacity) {
                        minCapacity = capacity;
                        bestX = x;
                        bestY = y;
                    }
                }
                if (bestX != -1) {
                    break;
                }
            }

            cabinet.setSw24Quantity(bestX);
            cabinet.setSw16Quantity(bestY);
        }
        return mapResult;
    }

    public void calculateUPS(Map<Integer, CabinetEquipmentDTO> mapResult, Config config) {
        for (Integer key : mapResult.keySet()) {
            mapResult.get(key).setUps(config.getUps());
        }
    }

    public void calculateConverter(Map<Integer, CabinetEquipmentDTO> mapResult, Config config) {
        for (Integer key : mapResult.keySet()) {
            mapResult.get(key).setConverter(config.getConverter());
        }
    }

    public void calculatePDU(Map<Integer, CabinetEquipmentDTO> mapResult, Config config) {
        for (Integer key : mapResult.keySet()) {
            CabinetEquipmentDTO cab = mapResult.get(key);
            int converterVal = cab.getConverter() != null ? cab.getConverter() : 0;
            int sw16Val = cab.getSw16Quantity() != null ? cab.getSw16Quantity() : 0;
            int sw24Val = cab.getSw24Quantity() != null ? cab.getSw24Quantity() : 0;

            int total = converterVal + sw16Val + sw24Val;
            int pdu = total / 6;
            if (total % 6 != 0) {
                pdu += 1;
            }
            cab.setPdu(pdu);
        }
    }

    @Override
    public List<CalculateBOQResponseDTO> calculateBOQManual(CalculateBOQManualRequestDTO dto) {
        Config config = configRepository
                .findById(UUID.fromString("a2b0a797-8ff2-4a79-ac5d-78525bd25e90")).get();
        // lấy map các nhóm tầng manual từ frontend
        Map<Integer, Integer> rqMap = new TreeMap<>();
        if (dto.getManualGroups() != null) {
            rqMap = dto.getManualGroups().stream()
                    .filter(group -> group.getFloorRange() != null)
                    .flatMap(group -> group.getFloorRange().entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (v1, v2) -> v1,
                            TreeMap::new));
        }
        // lấy ra mảng con
        int totalFloors = dto.getFloors().size();
        Map<Integer, Integer> autoSegments = new TreeMap<>();
        int currentStart = 0;

        for (Map.Entry<Integer, Integer> entry : rqMap.entrySet()) {
            int manualStart = entry.getKey();
            int manualEnd = entry.getValue();

            if (currentStart < manualStart) {
                autoSegments.put(currentStart, manualStart - 1);
            }

            currentStart = manualEnd + 1;
        }

        if (currentStart < totalFloors) {
            autoSegments.put(currentStart, totalFloors - 1);
        }

        // autoMapResult chỉ lưu các tủ tính toán tự động
        Map<Integer, CabinetEquipmentDTO> autoMapResult = new TreeMap<>();

        CalculateBOQRequestDTO tempDto = CalculateBOQRequestDTO.builder()
                .floorsCount(dto.getFloorsCount())
                .basementsCount(dto.getBasementsCount())
                .hasRoof(dto.getHasRoof())
                .horizontalDistance(dto.getHorizontalDistance())
                .verticalDistance(dto.getVerticalDistance())
                .rackType(dto.getRackType())
                .floors(dto.getFloors())
                .build();

        for (Map.Entry<Integer, Integer> segment : autoSegments.entrySet()) {
            int start = segment.getKey();
            int end = segment.getValue();
            if (start > end) {
                continue;
            }
            calculateCabinetPlacementUitls(tempDto, autoMapResult, config, start, end);
        }

        // Tính toán thiết bị cho các tủ tự động
        calculateCameraQuantityInCabinet(autoMapResult, tempDto);
        calculateSwichPOE(autoMapResult, config);
        calculateUPS(autoMapResult, config);
        calculateConverter(autoMapResult, config);
        calculatePDU(autoMapResult, config);

        // Tính toán thiết bị cho các tủ thủ công (manual cabinets)
        Map<Integer, List<CabinetEquipmentDTO>> manualCalculatedResult = new HashMap<>();
        if (dto.getManualGroups() != null) {
            for (CalculateBOQManualRequestDTO.ManualCabinetGroup group : dto.getManualGroups()) {
                List<CabinetEquipmentDTO> listCabinetOfFloor = new ArrayList<>();
                if (group.getCabinets() != null) {
                    for (CalculateBOQManualRequestDTO.Cabinet cab : group.getCabinets()) {
                        CabinetEquipmentDTO eq = calculateManualCabinetEquipment(cab, config);
                        eq.setCabinetId(cab.getId());
                        eq.setCabinetType(cab.getType());
                        eq.setAllocations(cab.getAllocations());
                        listCabinetOfFloor.add(eq);
                    }
                }
                manualCalculatedResult.put(group.getCabinetIndex(), listCabinetOfFloor);
            }
        }

        // Làm phẳng gom floorRange từ List manualGroups thành 1 map
        Map<Integer, Integer> floorRangesMap = new HashMap<>();
        Map<Integer, Integer> rangeToCabinetMap = new HashMap<>(); // map start floor of range to cabinetIndex

        if (dto.getManualGroups() != null) {
            for (CalculateBOQManualRequestDTO.ManualCabinetGroup group : dto.getManualGroups()) {
                if (group.getFloorRange() != null) {
                    for (Map.Entry<Integer, Integer> entry : group.getFloorRange().entrySet()) {
                        floorRangesMap.put(entry.getKey(), entry.getValue());
                        rangeToCabinetMap.put(entry.getKey(), group.getCabinetIndex());
                    }
                }
            }
        }

        List<CalculateBOQResponseDTO> result = new ArrayList<>();
        for (CalculateBOQRequestDTO.FloorRequest floor : dto.getFloors()) {
            boolean isAutoPlaced = autoMapResult.containsKey(floor.getFloorIndex());
            boolean isManualPlaced = manualCalculatedResult.containsKey(floor.getFloorIndex());
            boolean isPlaced = isAutoPlaced || isManualPlaced;

            CalculateBOQResponseDTO.CalculateBOQResponseDTOBuilder builder = CalculateBOQResponseDTO.builder()
                    .floorIndex(floor.getFloorIndex())
                    .label(floor.getLabel())
                    .camerasCount(floor.getCamerasCount())
                    .isCabinetPlaced(isPlaced);

            CabinetEquipmentDTO coveringCabinet = null;
            Integer cabinetIndex = null;

            for (Integer key : floorRangesMap.keySet()) {
                if (floor.getFloorIndex() >= key && floor.getFloorIndex() <= floorRangesMap.get(key)) {
                    cabinetIndex = rangeToCabinetMap.get(key);
                    coveringCabinet = CabinetEquipmentDTO.builder()
                            .from(key)
                            .to(floorRangesMap.get(key))
                            .build();
                    break;
                }
            }

            if (cabinetIndex == null) {
                // look up in autoMapResult
                for (Map.Entry<Integer, CabinetEquipmentDTO> entry : autoMapResult.entrySet()) {
                    CabinetEquipmentDTO cab = entry.getValue();
                    if (floor.getFloorIndex() >= cab.getFrom() && floor.getFloorIndex() <= cab.getTo()) {
                        coveringCabinet = cab;
                        cabinetIndex = entry.getKey();
                        break;
                    }
                }
            }

            if (coveringCabinet != null) {
                builder.fromIndex(coveringCabinet.getFrom())
                        .toIndex(coveringCabinet.getTo())
                        .cabinetIndex(cabinetIndex);
            }

            CableDetail cableDetail = calculateCableLength(cabinetIndex, floor, dto.getVerticalDistance(),
                    floor.getCamerasCount());
            builder.cableLength(cableDetail.getTotalCable())
                    .atrium(cableDetail.getAtrium())
                    .downCabinet(cableDetail.getDownCabinet())
                    .inCabinet(cableDetail.getInCabinet())
                    .autocadLength(cableDetail.getAutocadLength());

            if (isPlaced) {
                if (isManualPlaced) {
                    List<CabinetEquipmentDTO> listCabinets = manualCalculatedResult.get(floor.getFloorIndex());
                    List<CalculateBOQResponseDTO.CabinetDetailResponseDTO> listCabinetDetails = new ArrayList<>();
                    for (CabinetEquipmentDTO cabinet : listCabinets) {
                        listCabinetDetails.add(CalculateBOQResponseDTO.CabinetDetailResponseDTO.builder()
                                .cabinetId(cabinet.getCabinetId())
                                .cabinetType(cabinet.getCabinetType())
                                .cameraQuantityInCabinet(cabinet.getCameraQuantityInCabinet())
                                .sw24Count(cabinet.getSw24Quantity())
                                .sw16Count(cabinet.getSw16Quantity())
                                .upsCount(cabinet.getUps())
                                .pduCount(cabinet.getPdu())
                                .convCount(cabinet.getConverter())
                                .allocations(cabinet.getAllocations())
                                .build());
                    }
                    builder.cabinets(listCabinetDetails);

                    // Backward compatibility / Floor aggregation: Sum up details across all
                    // cabinets at this floor
                    if (!listCabinetDetails.isEmpty()) {
                        int totalCam = 0;
                        int totalSw24 = 0;
                        int totalSw16 = 0;
                        int totalUps = 0;
                        int totalPdu = 0;
                        int totalConv = 0;
                        List<String> types = new ArrayList<>();

                        for (CalculateBOQResponseDTO.CabinetDetailResponseDTO cab : listCabinetDetails) {
                            if (cab.getCameraQuantityInCabinet() != null)
                                totalCam += cab.getCameraQuantityInCabinet();
                            if (cab.getSw24Count() != null)
                                totalSw24 += cab.getSw24Count();
                            if (cab.getSw16Count() != null)
                                totalSw16 += cab.getSw16Count();
                            if (cab.getUpsCount() != null)
                                totalUps += cab.getUpsCount();
                            if (cab.getPduCount() != null)
                                totalPdu += cab.getPduCount();
                            if (cab.getConvCount() != null)
                                totalConv += cab.getConvCount();
                            if (cab.getCabinetType() != null && !cab.getCabinetType().isEmpty()) {
                                types.add(cab.getCabinetType());
                            }
                        }

                        builder.cameraQuantityInCabinet(totalCam)
                                .sw24Count(totalSw24)
                                .sw16Count(totalSw16)
                                .upsCount(totalUps)
                                .pduCount(totalPdu)
                                .convCount(totalConv)
                                .cabinetType(String.join(" + ", types));
                    }
                } else {
                    CabinetEquipmentDTO cabinet = autoMapResult.get(floor.getFloorIndex());

                    CalculateBOQResponseDTO.CabinetDetailResponseDTO autoCabinetDetail = CalculateBOQResponseDTO.CabinetDetailResponseDTO
                            .builder()
                            .cabinetId("auto_" + floor.getFloorIndex())
                            .cabinetType(dto.getRackType())
                            .cameraQuantityInCabinet(cabinet.getCameraQuantityInCabinet())
                            .sw24Count(cabinet.getSw24Quantity())
                            .sw16Count(cabinet.getSw16Quantity())
                            .upsCount(cabinet.getUps())
                            .pduCount(cabinet.getPdu())
                            .convCount(cabinet.getConverter())
                            .build();

                    builder.cabinets(List.of(autoCabinetDetail));

                    builder.cameraQuantityInCabinet(cabinet.getCameraQuantityInCabinet())
                            .sw24Count(cabinet.getSw24Quantity())
                            .sw16Count(cabinet.getSw16Quantity())
                            .upsCount(cabinet.getUps())
                            .pduCount(cabinet.getPdu())
                            .convCount(cabinet.getConverter())
                            .cabinetType(dto.getRackType());
                }
            } else {
                builder.cameraQuantityInCabinet(0)
                        .sw24Count(0)
                        .sw16Count(0)
                        .upsCount(0)
                        .pduCount(0)
                        .convCount(0)
                        .cabinetType(null)
                        .cabinets(new ArrayList<>());
            }
            result.add(builder.build());
        }

        return result;
    }

    private CabinetEquipmentDTO calculateManualCabinetEquipment(CalculateBOQManualRequestDTO.Cabinet manualCab,
            Config config) {
        CabinetEquipmentDTO result = new CabinetEquipmentDTO();
        int cameraCount = manualCab.getTotalCamera() != null ? manualCab.getTotalCamera() : 0;

        // 1. Tính toán Switch
        int limit24 = config.getSw24ConditionQuanity();
        int limit16 = config.getSw16ConditionQuanity();

        int minSwitches = (int) Math.ceil((double) cameraCount / limit24);
        int bestX = 0;
        int bestY = 0;
        int minCapacity = Integer.MAX_VALUE;

        for (int S = minSwitches;; S++) {
            for (int x = 0; x <= S; x++) {
                int y = S - x;
                int capacity = x * limit24 + y * limit16;

                if (capacity >= cameraCount && capacity < minCapacity) {
                    minCapacity = capacity;
                    bestX = x;
                    bestY = y;
                }
            }
            if (bestX != 0 || bestY != 0 || cameraCount == 0) {
                break;
            }
        }
        result.setSw24Quantity(bestX);
        result.setSw16Quantity(bestY);

        // 2. Tính toán UPS & Converter
        result.setUps(config.getUps());
        result.setConverter(config.getConverter());

        // 3. Tính toán PDU
        int converterVal = result.getConverter() != null ? result.getConverter() : 0;
        int sw16Val = result.getSw16Quantity() != null ? result.getSw16Quantity() : 0;
        int sw24Val = result.getSw24Quantity() != null ? result.getSw24Quantity() : 0;

        int total = converterVal + sw16Val + sw24Val;
        int pdu = total / 6;
        if (total % 6 != 0) {
            pdu += 1;
        }
        result.setPdu(pdu);
        result.setCameraQuantityInCabinet(cameraCount);

        return result;
    }
}
