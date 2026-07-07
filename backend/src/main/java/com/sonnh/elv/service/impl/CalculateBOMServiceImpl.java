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
        response.setObserScreenQuantity(calculateOberserScreen(dto));
        response.setConverterQuantity(calculateConverter(dto));

        String cabinetType = dto.getCabinetType();
        if ("2U".equals(cabinetType)) {
            response.setCabinet2UQuantity(calculateCabinet(dto));
        } else if ("6U".equals(cabinetType)) {
            response.setCabinet6UQuantity(calculateCabinet(dto));
        } else if ("10U".equals(cabinetType)) {
            response.setCabinet10UQuantity(calculateCabinet(dto));
        } else if ("20U".equals(cabinetType)) {
            response.setCabinet20UQuantity(calculateCabinet(dto));
        } else if ("32U".equals(cabinetType)) {
            response.setCabinet32UQuantity(calculateCabinet(dto));
        } else if ("42U".equals(cabinetType)) {
            response.setCabinet42UQuantity(calculateCabinet(dto));
        }

        response.setCvvCable(calculateCVVCable(dto));
        response.setPduQuantity(calcuatePDUPower(dto));
        response.setUps1000Quantity(calculateUPSS1000(dto));
        response.setAmpCatQuantity(calculateAmpCat(dto));
        response.setFiberOpticalPatchQuantity(calcuateFiberOpticalPatch(dto));
        response.setOdf4FOQuantity(calcuateODF4FO(dto));
        response.setPatchCordQuantity(calcuatePatchCord(dto));
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
        map.put("recorder32Quantity", totalCamera / 32);
        int digit = (int) (totalCamera * Math.pow(totalCamera, 1)) % 10;
        if (digit > 5) {
            map.put("recorder32Quantity", map.get("recorder32Quantity") + 1);
        } else {
            map.put("recorder16Quantity", 1);
        }
        return map;
    }

    public Integer calculateHardDisk(CalculateBOMRequestDTO dto, Map<String, Integer> map) {
        if (map == null) return 0;
        int total = map.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();

        return total * 2;
    }

    public Map<String, Integer> calculateswich16CISCO(CalculateBOMRequestDTO dto, Map<String, Integer> map) {
        Map<String, Integer> resultMap = new HashMap<>();
        if (map != null) {
            resultMap.putAll(map);
        }
        int totalRecorder = resultMap.values()
                .stream()
                .filter(val -> val != null)
                .mapToInt(Integer::intValue)
                .sum();
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

    public Integer calculateSwitch16CISCO(CalculateBOMRequestDTO dto) {
        return 0;
    }

    public Integer calculateSwitch24CISCO(CalculateBOMRequestDTO dto) {
        return 0;
    }

    public Integer calculateOberserScreen(CalculateBOMRequestDTO dto) {
        return 0;
    }

    public Integer calculateConverter(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalConverter());
    }

    public Integer calculateCabinet(CalculateBOMRequestDTO dto) {
        int cabinetQuantity = 0;
        String cabinetType = dto.getCabinetType();
        if ("2U".equals(cabinetType) || "6U".equals(cabinetType) || "10U".equals(cabinetType) || "20U".equals(cabinetType) || "32U".equals(cabinetType) || "42U".equals(cabinetType)) {
            cabinetQuantity = getSafeInt(dto.getTotalCabinet());
        }
        return cabinetQuantity;
    }

    public Integer calculateCVVCable(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCabinet()) * 20;
    }

    public Integer calcuatePDUPower(CalculateBOMRequestDTO dto) {
        return 0;
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

    public Integer calcuatePatchCord(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCabinet()) * 2;
    }

    public Integer calcuateCablemanage(CalculateBOMRequestDTO dto) {
        return getSafeInt(dto.getTotalCabinet()) + 2;
    }

}
