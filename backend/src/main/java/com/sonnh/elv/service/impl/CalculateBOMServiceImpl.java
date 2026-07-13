package com.sonnh.elv.service.impl;

import com.sonnh.elv.dto.request.CalculateBOMRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOMResponseDTO;
import com.sonnh.elv.service.CalculateBOMService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculateBOMServiceImpl implements CalculateBOMService {

    private int getSafeInt(Integer val) {
        return val == null ? 0 : val;
    }

    @Override
    public CalculateBOMResponseDTO calculateBOM(List<CalculateBOMRequestDTO> dtos) {
        System.out.println(dtos);
        // ------------------
        CalculateBOMResponseDTO response = new CalculateBOMResponseDTO();
        if (dtos == null || dtos.isEmpty()) {
            return response;
        }

        response.setCamDomeQuantity(calculateTotalCameraDome(dtos));
        response.setCamBulletQuantity(calculateTotalCamerBullet(dtos));

        Map<String, Integer> recorderMap = calculateRecorder16And32(dtos);
        response.setRecorder16Quantity(recorderMap.get("recorder16Quantity"));
        response.setRecorder32Quantity(recorderMap.get("recorder32Quantity"));
        response.setHardDiskQuantity(calculateHardDisk(dtos, recorderMap));

        Map<String, Integer> ciscoMap = calculateswich16And24CISCO(dtos, recorderMap);
        response.setSwich16CISCOQuantity(ciscoMap.get("sw16"));
        response.setSwich24CISCOQuantity(ciscoMap.get("sw24"));

        response.setSwich16POEQuantity(calculateSwitch16POE(dtos));
        response.setSwich24POEQuantity(calculateSwitch24POE(dtos));
        response.setObserScreenQuantity(calculateOberserScreen(dtos, recorderMap));
        response.setConverterQuantity(calculateConverter(dtos));

        Map<String, Integer> cabinetMap = calculateCabinet(dtos);
        response.setCabinet2UQuantity(cabinetMap.getOrDefault("2U", 0));
        response.setCabinet6UQuantity(cabinetMap.getOrDefault("6U", 0));
        response.setCabinet10UQuantity(cabinetMap.getOrDefault("10U", 0));
        response.setCabinet20UQuantity(cabinetMap.getOrDefault("20U", 0));
        response.setCabinet32UQuantity(cabinetMap.getOrDefault("32U", 0));
        response.setCabinet42UQuantity(cabinetMap.getOrDefault("42U", 0));

        response.setCvvCable(calculateCVVCable(dtos));
        response.setPduQuantity(calcuatePDUPower(dtos, recorderMap));
        response.setUps1000Quantity(calculateUPSS1000(dtos));
        response.setAmpCatQuantity(calculateAmpCat(dtos));
        response.setFiberOpticalPatchQuantity(calcuateFiberOpticalPatch(dtos));
        response.setOdf4FOQuantity(calcuateODF4FO(dtos));
        Map<String, Integer> odfMap = calcuateODF12FOAnd24FO(dtos);
        response.setOdf12FOQuantity(odfMap.getOrDefault("12FO", 0));
        response.setOdf24FOQuantity(odfMap.getOrDefault("24FO", 0));
        response.setPatchCordQuantity(calcuatePatchCord(dtos, recorderMap));
        response.setCablemanageQuantity(calcuateCablemanage(dtos));
        response.setCableQuantity(calcuateCableQuantity(dtos));

        System.out.println("Response BOM: " + response.toString());

        return response;
    }

    // -------------------------------------------------------------------------------
    public Integer calculateTotalCameraDome(List<CalculateBOMRequestDTO> dtos) {
        int totalCamDome = dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalCamDome)
                .mapToInt(Integer::intValue)
                .sum();
        return getSafeInt(totalCamDome);
    }

    public Integer calculateTotalCamerBullet(List<CalculateBOMRequestDTO> dtos) {
        int totalCamBullet = dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalCamBullet)
                .mapToInt(Integer::intValue)
                .sum();
        return getSafeInt(totalCamBullet);
    }

    public Map<String, Integer> calculateRecorder16And32(List<CalculateBOMRequestDTO> dtos) {
        Map<String, Integer> map = new HashMap<>();
        int totalCamera = 0;
        for (CalculateBOMRequestDTO dto : dtos) {
            totalCamera += getSafeInt(dto.getTotalCamera());
        }
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

    public Integer calculateHardDisk(List<CalculateBOMRequestDTO> dtos, Map<String, Integer> map) {
        if (map == null)
            return 0;
        int total = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();

        return total * 2;
    }

    public Map<String, Integer> calculateswich16And24CISCO(List<CalculateBOMRequestDTO> dtos,
            Map<String, Integer> map) {
        Map<String, Integer> resultMap = new HashMap<>();
        int totalRecorder = 0;
        if (map != null) {
            totalRecorder = map.values()
                    .stream()
                    .filter(val -> val != null)
                    .mapToInt(Integer::intValue)
                    .sum();
        }
        int totalConverter = dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalConverter)
                .mapToInt(Integer::intValue)
                .sum();
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

    public Integer calculateswich24CISCO(List<CalculateBOMRequestDTO> dtos, Map<String, Integer> map) {
        return 0;
    }

    public Integer calculateSwitch16POE(List<CalculateBOMRequestDTO> dtos) {
        int sw16POEQuantity = dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalSw16)
                .mapToInt(Integer::intValue)
                .sum();

        return getSafeInt(sw16POEQuantity);
    }

    public Integer calculateSwitch24POE(List<CalculateBOMRequestDTO> dtos) {
        int sw24POEQuantity = dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalSw24)
                .mapToInt(Integer::intValue)
                .sum();
        return getSafeInt(sw24POEQuantity);
    }

    public Integer calculateOberserScreen(List<CalculateBOMRequestDTO> dtos, Map<String, Integer> map) {
        int totalRecorder = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        return getSafeInt(totalRecorder);
    }

    public Integer calculateConverter(List<CalculateBOMRequestDTO> dtos) {
        int totalConverter = dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalConverter)
                .mapToInt(Integer::intValue)
                .sum();
        return getSafeInt(totalConverter);
    }

    public Map<String, Integer> calculateCabinet(List<CalculateBOMRequestDTO> dtos) {
        Map<String, Integer> map = new HashMap<>();
        map.put("2U", 0);
        map.put("6U", 0);
        map.put("10U", 0);
        map.put("32U", 0);
        map.put("42U", 0);
        for (CalculateBOMRequestDTO dto : dtos) {
            for (String cabinetType : dto.getCabinets().keySet()) {
                if (map.containsKey(cabinetType)) {
                    map.put(cabinetType, map.get(cabinetType) + dto.getCabinets().get(cabinetType));
                }
            }
        }
        return map;
    }

    public Integer calculateCVVCable(List<CalculateBOMRequestDTO> dtos) {
        return 0;
    }

    public Integer calcuatePDUPower(List<CalculateBOMRequestDTO> dtos, Map<String, Integer> map) {
        int totalRecorder = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        int observerScreen = calculateOberserScreen(dtos, map);
        int totalConverter = calculateConverter(dtos);
        int totalCisco = calculateswich16And24CISCO(dtos, map).values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        int totalFCC = (totalConverter + observerScreen + totalRecorder + totalCisco) / 6;
        if ((totalConverter + observerScreen + totalRecorder + totalCisco) % 6 != 0) {
            totalFCC += 1;
        }
        System.out.println("totalRecorder: " + totalRecorder);
        System.out.println("totalConverter: " + totalConverter);
        System.out.println("totalCisco: " + totalCisco);
        System.out.println("observerScreen: " + observerScreen);
        System.out.println("totalFCC: " + totalFCC);
        System.out.println("getTotalCabinet: " + getTotalCabinet(dtos));
        return totalFCC + getTotalCabinet(dtos);
    }

    public Integer calculateUPSS1000(List<CalculateBOMRequestDTO> dtos) {
        return getSafeInt(getTotalCabinet(dtos));
    }

    public Integer calculateAmpCat(List<CalculateBOMRequestDTO> dtos) {
        int totalCamera = dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalCamera)
                .mapToInt(Integer::intValue)
                .sum();
        totalCamera *= 2;
        return totalCamera + (100 - (totalCamera % 100));
    }

    public Integer calcuateFiberOpticalPatch(List<CalculateBOMRequestDTO> dtos) {
        return getSafeInt(getTotalCabinet(dtos)) * 2;
    }

    public Integer calcuateODF4FO(List<CalculateBOMRequestDTO> dtos) {
        return getSafeInt(getTotalCabinet(dtos));
    }

    public Map<String, Integer> calcuateODF12FOAnd24FO(List<CalculateBOMRequestDTO> dtos) {
        Map<String, Integer> map = new HashMap<>();
        int totalConverter = dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalConverter)
                .mapToInt(Integer::intValue)
                .sum();
        totalConverter *= 2;
        map.put("24FO", totalConverter / 24);
        if (totalConverter % 24 != 0) {
            map.put("12FO", 1);
        }
        return map;
    }

    public Integer calcuatePatchCord(List<CalculateBOMRequestDTO> dtos, Map<String, Integer> map) {
        int cisco = calculateswich16And24CISCO(dtos, map).values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        int totalRecorder = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
        return getSafeInt(getTotalCabinet(dtos)) * 2 + cisco + totalRecorder;
    }

    public Integer calcuateCablemanage(List<CalculateBOMRequestDTO> dtos) {
        return getSafeInt(getTotalCabinet(dtos)) + 2;
    }

    private Integer getTotalCabinet(List<CalculateBOMRequestDTO> dtos) {
        if (dtos == null)
            return 0;
        int total = 0;
        for (CalculateBOMRequestDTO dto : dtos) {
            if (dto != null && dto.getCabinets() != null) {
                total += dto.getCabinets().values().stream()
                        .filter(val -> val != null)
                        .mapToInt(Integer::intValue)
                        .sum();
            }
        }
        return total;
    }

    public Integer calcuateCableQuantity(List<CalculateBOMRequestDTO> dtos) {
        if (dtos == null) {
            return 0;
        }
        return dtos.stream()
                .filter(val -> val != null && val.getTotalCableLength() != null)
                .mapToInt(CalculateBOMRequestDTO::getTotalCableLength)
                .sum();
    }
}
