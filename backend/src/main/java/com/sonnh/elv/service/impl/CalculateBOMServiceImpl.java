package com.sonnh.elv.service.impl;

import com.sonnh.elv.dto.request.CalculateBOMRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;
import com.sonnh.elv.service.CalculateBOMService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculateBOMServiceImpl implements CalculateBOMService {

    private int getSafeInt(Integer val) {
        return val == null ? 0 : val;
    }

    @Override
    public CalculateBOMResponseDTO calculateBOM(CalculateBOMRequestDTO dto) {
        System.out.println(dto);
        // ------------------
        CalculateBOMResponseDTO response = new CalculateBOMResponseDTO();
        if (dto == null) {
            return response;
        }

        response.setCamDomeQuantity(calculateTotalCameraDome(dto));
        response.setCamBulletQuantity(calculateTotalCamerBullet(dto));

        Map<String, Integer> recorderMap = calculateRecorder16And32(dto);
        response.setRecorder16Quantity(recorderMap.get("recorder16Quantity"));
        response.setRecorder32Quantity(recorderMap.get("recorder32Quantity"));
        response.setHardDiskQuantity(calculateHardDisk(dto, recorderMap));

        Map<String, Integer> ciscoMap = calculateswich16CISCO(dto, recorderMap);
        response.setSwich16CISCOQuantity(ciscoMap.get("sw16"));
        response.setSwich24CISCOQuantity(ciscoMap.get("sw24"));

        response.setSwich16POEQuantity(calculateSwitch16POE(dto));
        response.setSwich24POEQuantity(calculateSwitch24POE(dto));
        response.setObserScreenQuantity(calculateOberserScreen(dto, recorderMap));
        response.setConverterQuantity(calculateConverter(dto));

        int cab2U = 0;
        int cab6U = 0;
        int cab10U = 0;
        int cab20U = 0;
        int cab32U = 0;
        int cab42U = 0;

        if (dto.getFloors() != null) {
            for (CalculateBOMRequestDTO.FloorBOMInfo f : dto.getFloors()) {
                if (f.getIsCabinetPlaced() != null && f.getIsCabinetPlaced()) {
                    String type = f.getCabinetType();
                    if (type == null || type.trim().isEmpty() || "Same as general".equalsIgnoreCase(type) || "Default".equalsIgnoreCase(type)) {
                        type = dto.getCabinetType();
                    }
                    if ("2U".equalsIgnoreCase(type)) {
                        cab2U++;
                    } else if ("6U".equalsIgnoreCase(type)) {
                        cab6U++;
                    } else if ("10U".equalsIgnoreCase(type)) {
                        cab10U++;
                    } else if ("20U".equalsIgnoreCase(type)) {
                        cab20U++;
                    } else if ("32U".equalsIgnoreCase(type)) {
                        cab32U++;
                    } else if ("42U".equalsIgnoreCase(type)) {
                        cab42U++;
                    }
                }
            }
        } else {
            String cabinetType = dto.getCabinetType();
            int totalCabinet = getSafeInt(dto.getTotalCabinet());
            if ("2U".equalsIgnoreCase(cabinetType)) {
                cab2U = totalCabinet;
            } else if ("6U".equalsIgnoreCase(cabinetType)) {
                cab6U = totalCabinet;
            } else if ("10U".equalsIgnoreCase(cabinetType)) {
                cab10U = totalCabinet;
            } else if ("20U".equalsIgnoreCase(cabinetType)) {
                cab20U = totalCabinet;
            } else if ("32U".equalsIgnoreCase(cabinetType)) {
                cab32U = totalCabinet;
            } else if ("42U".equalsIgnoreCase(cabinetType)) {
                cab42U = totalCabinet;
            }
        }

        response.setCabinet2UQuantity(cab2U);
        response.setCabinet6UQuantity(cab6U);
        response.setCabinet10UQuantity(cab10U);
        response.setCabinet20UQuantity(cab20U);
        response.setCabinet32UQuantity(cab32U);
        response.setCabinet42UQuantity(cab42U);

        response.setCvvCable(calculateCVVCable(dto));
        response.setPduQuantity(calcuatePDUPower(dto, recorderMap));
        response.setUps1000Quantity(calculateUPSS1000(dto));
        response.setAmpCatQuantity(calculateAmpCat(dto));
        response.setFiberOpticalPatchQuantity(calcuateFiberOpticalPatch(dto));
        response.setOdf4FOQuantity(calcuateODF4FO(dto));
        response.setPatchCordQuantity(calcuatePatchCord(dto, recorderMap));
        response.setCablemanageQuantity(calcuateCablemanage(dto));
        return response;
    }

    // -------------------------------------------------------------------------------
    public Integer calculateTotalCameraDome(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCamDome());
    }

    public Integer calculateTotalCamerBullet(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCamBullet());
    }

    public Map<String, Integer> calculateRecorder16And32(CalculateBOMRequestDTO dto) {
        Map<String, Integer> map = new HashMap<>();
        int totalCamera = getSafeInt(dto.getTotalCamera());
        if (totalCamera > 0) {
            map.put("recorder32Quantity", totalCamera / 32);
            map.put("recorder16Quantity", 0);
        }
        int digit = (int) (((double) totalCamera / 32) * 10) % 10;
        System.out.println("digit ne: " + digit);
        if (digit > 5) {
            map.put("recorder32Quantity", map.get("recorder32Quantity") + 1);
        } else if (digit <= 5 && digit != 0) {
            map.put("recorder16Quantity", map.get("recorder16Quantity") + 1);
        }
        return map;
    }

    public Integer calculateHardDisk(CalculateBOMRequestDTO dto, Map<String, Integer> map) {
        if (map == null)
            return 0;
        int total = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();

        return total * 2;
    }

    public Map<String, Integer> calculateswich16CISCO(CalculateBOMRequestDTO dto, Map<String, Integer> map) {
        Map<String, Integer> resultMap = new HashMap<>();
        int totalRecorder = 0;
        if (map != null) {
            totalRecorder = map.values()
                    .stream()
                    .filter(val -> val != null)
                    .mapToInt(Integer::intValue)
                    .sum();
        }
        int totalConverter = getSafeInt(dto.getTotalConverter());
        int condition = totalConverter + totalRecorder;
        resultMap.put("sw24", 0);
        resultMap.put("sw16", 0);
        if (condition > 16) {
            resultMap.put("sw24", resultMap.get("sw24") + 1);
        } else {
            resultMap.put("sw16", resultMap.get("sw16") + 1);
        }
        return resultMap;
    }

    public Integer calculateswich24CISCO(CalculateBOMRequestDTO dto, Map<String, Integer> map) {
        return 0;
    }

    public Integer calculateSwitch16POE(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalSw16());
    }

    public Integer calculateSwitch24POE(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalSw24());
    }

    public Integer calculateOberserScreen(CalculateBOMRequestDTO dto, Map<String, Integer> map) {
        int totalRecorder = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        return getSafeInt(totalRecorder);
    }

    public Integer calculateConverter(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalConverter());
    }

    public Integer calculateCabinet(CalculateBOMRequestDTO dto) {
        int cabinetQuantity = 0;
        String cabinetType = dto.getCabinetType();
        if ("2U".equals(cabinetType) || "6U".equals(cabinetType) || "10U".equals(cabinetType)
                || "32U".equals(cabinetType) || "42U".equals(cabinetType)) {
            cabinetQuantity = getSafeInt(dto.getTotalCabinet());
        }
        return cabinetQuantity;
    }

    public Integer calculateCVVCable(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCabinet()) * 20;
    }

    public Integer calcuatePDUPower(CalculateBOMRequestDTO dto, Map<String, Integer> map) {
        int totalRecorder = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("totalRecorder: " + totalRecorder);

        for (String key : calculateswich16CISCO(dto, map).keySet()) {
            System.out.println("key: " + key + " value: " + calculateswich16CISCO(dto, map).get(key));
        }
        int cisco = calculateswich16CISCO(dto, map).values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("cisco: " + cisco);
        int fcc = dto.getTotalConverter() + calculateOberserScreen(dto, map) + totalRecorder + cisco;
        return getSafeInt(dto.getTotalCabinet()) + fcc;
    }

    public Integer calculateUPSS1000(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCabinet());
    }

    public Integer calculateAmpCat(CalculateBOMRequestDTO dto) {
        int totalCamera = getSafeInt(dto.getTotalCamera()) * 2;
        return totalCamera + (100 - (totalCamera % 100));
    }

    public Integer calcuateFiberOpticalPatch(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCabinet()) * 2;
    }

    public Integer calcuateODF4FO(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCabinet());
    }

    public Integer calcuatePatchCord(CalculateBOMRequestDTO dto, Map<String, Integer> map) {
        int cisco = calculateswich16CISCO(dto, map).values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        int totalRecorder = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        return getSafeInt(dto.getTotalCabinet()) * 2 + cisco + totalRecorder;
    }

    public Integer calcuateCablemanage(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCabinet()) + 2;
    }

}
