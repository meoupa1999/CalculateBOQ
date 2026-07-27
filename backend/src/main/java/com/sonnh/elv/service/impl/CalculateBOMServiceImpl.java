package com.sonnh.elv.service.impl;

import com.sonnh.elv.dto.request.CalculateBOMRequestDTO;
import com.sonnh.elv.data.domain.ProductType;
import com.sonnh.elv.data.repository.ProductTypeRepository;
import com.sonnh.elv.service.CalculateBOMService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalculateBOMServiceImpl implements CalculateBOMService {

    private static final String CAMERA_DOME = "CAMERA_DOME";
    private static final String CAMERA_BULLET = "CAMERA_BULLET";
    private static final String RECORDER_16 = "RECORDER_16";
    private static final String RECORDER_32 = "RECORDER_32";
    private static final String HARD_DISK_10T = "HARD_DISK_10T";
    private static final String SWITCH_16_CISCO = "SWITCH_16_CISCO";
    private static final String SWITCH_24_CISCO = "SWITCH_24_CISCO";
    private static final String SWITCH_16_POE = "SWITCH_16_POE";
    private static final String SWITCH_24_POE = "SWITCH_24_POE";
    private static final String OBSERVER_SCREEN_43 = "OBSERVER_SCREEN_43";
    private static final String CONVERTER_GIGABIT = "CONVERTER_GIGABIT";
    private static final String RACK_CABINET_2U = "RACK_CABINET_2U";
    private static final String RACK_CABINET_6U = "RACK_CABINET_6U";
    private static final String RACK_CABINET_10U = "RACK_CABINET_10U";
    private static final String RACK_CABINET_20U = "RACK_CABINET_20U";
    private static final String RACK_CABINET_32U = "RACK_CABINET_32U";
    private static final String RACK_CABINET_42U = "RACK_CABINET_42U";
    private static final String ELECTRIC_CABLE_CVV = "ELECTRIC_CABLE_CVV";
    private static final String PDU_POWER_6 = "PDU_POWER_6";
    private static final String UPS_1000VA = "UPS_1000VA";
    private static final String UPS_3000VA = "UPS_3000VA";
    private static final String AMP_CAT5_CONNECTOR = "AMP_CAT5_CONNECTOR";
    private static final String FIBER_PATCH_CORD_3M = "FIBER_PATCH_CORD_3M";
    private static final String ODF_4FO = "ODF_4FO";
    private static final String ODF_12FO = "ODF_12FO";
    private static final String ODF_24FO = "ODF_24FO";
    private static final String LAN_PATCH_CORD = "LAN_PATCH_CORD";
    private static final String CABLE_MANAGEMENT_19 = "CABLE_MANAGEMENT_19";
    private static final String LAN_CABLE_CAT5E = "LAN_CABLE_CAT5E";
    private static final String FIBER_CABLE_4FO = "FIBER_CABLE_4FO";

    private final ProductTypeRepository productTypeRepository;

    private int getSafeInt(Integer val) {
        return val == null ? 0 : val;
    }

    @Override
    public Map<String, Integer> calculateBOM(List<CalculateBOMRequestDTO> dtos) {
        System.out.println(dtos);
        // ------------------
        Map<String, Integer> response = new HashMap<>();
        if (dtos == null || dtos.isEmpty()) {
            return response;
        }

        List<ProductType> productTypes = productTypeRepository.findAll();

        Map<String, Integer> recorderMap = calculateRecorder16And32(dtos);
        Map<String, Integer> ciscoMap = calculateswich16And24CISCO(dtos, recorderMap);
        Map<String, Integer> cabinetMap = calculateCabinet(dtos);
        Map<String, Integer> odfMap = calcuateODF12FOAnd24FO(dtos);

        for (ProductType pt : productTypes) {
            String code = pt.getCode();
            if (code == null) {
                continue;
            }

            int qty = 0;
            switch (code) {
                case CAMERA_DOME:
                    qty = calculateTotalCameraDome(dtos);
                    break;
                case CAMERA_BULLET:
                    qty = calculateTotalCamerBullet(dtos);
                    break;
                case RECORDER_16:
                    qty = recorderMap.getOrDefault("recorder16Quantity", 0);
                    break;
                case RECORDER_32:
                    qty = recorderMap.getOrDefault("recorder32Quantity", 0);
                    break;
                case HARD_DISK_10T:
                    qty = calculateHardDisk(dtos, recorderMap);
                    break;
                case SWITCH_16_CISCO:
                    qty = ciscoMap.getOrDefault("sw16", 0);
                    break;
                case SWITCH_24_CISCO:
                    qty = ciscoMap.getOrDefault("sw24", 0);
                    break;
                case SWITCH_16_POE:
                    qty = calculateSwitch16POE(dtos);
                    break;
                case SWITCH_24_POE:
                    qty = calculateSwitch24POE(dtos);
                    break;
                case OBSERVER_SCREEN_43:
                    qty = calculateOberserScreen(dtos, recorderMap);
                    break;
                case CONVERTER_GIGABIT:
                    qty = calculateConverter(dtos);
                    break;
                case RACK_CABINET_2U:
                    qty = cabinetMap.getOrDefault("2U", 0);
                    break;
                case RACK_CABINET_6U:
                    qty = cabinetMap.getOrDefault("6U", 0);
                    break;
                case RACK_CABINET_10U:
                    qty = cabinetMap.getOrDefault("10U", 0);
                    break;
                case RACK_CABINET_20U:
                    qty = cabinetMap.getOrDefault("20U", 0);
                    break;
                case RACK_CABINET_32U:
                    qty = cabinetMap.getOrDefault("32U", 0);
                    break;
                case RACK_CABINET_42U:
                    qty = cabinetMap.getOrDefault("42U", 0);
                    break;
                case ELECTRIC_CABLE_CVV:
                    qty = calculateCVVCable(dtos);
                    break;
                case PDU_POWER_6:
                    qty = calcuatePDUPower(dtos, recorderMap);
                    break;
                case UPS_1000VA:
                    qty = calculateUPSS1000(dtos);
                    break;
                case AMP_CAT5_CONNECTOR:
                    qty = calculateAmpCat(dtos);
                    break;
                case FIBER_PATCH_CORD_3M:
                    qty = calcuateFiberOpticalPatch(dtos);
                    break;
                case ODF_4FO:
                    qty = calcuateODF4FO(dtos);
                    break;
                case ODF_12FO:
                    qty = odfMap.getOrDefault("12FO", 0);
                    break;
                case ODF_24FO:
                    qty = odfMap.getOrDefault("24FO", 0);
                    break;
                case LAN_PATCH_CORD:
                    qty = calcuatePatchCord(dtos, recorderMap);
                    break;
                case CABLE_MANAGEMENT_19:
                    qty = calcuateCablemanage(dtos);
                    break;
                case LAN_CABLE_CAT5E:
                    qty = calcuateCableQuantity(dtos);
                    break;
                case FIBER_CABLE_4FO:
                    qty = 0;
                    break;
                default:
                    qty = 0;
                    break;
            }

            response.put(code, qty);
        }

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
        // System.out.println("totalRecorder: " + totalRecorder);
        // System.out.println("totalConverter: " + totalConverter);
        // System.out.println("totalCisco: " + totalCisco);
        // System.out.println("observerScreen: " + observerScreen);
        // System.out.println("totalFCC: " + totalFCC);
        // System.out.println("getTotalCabinet: " + getTotalCabinet(dtos));
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
        return getSafeInt(dtos.stream()
                .filter(val -> val != null)
                .map(CalculateBOMRequestDTO::getTotalCableLength)
                .mapToInt(Integer::intValue)
                .sum());
    }
}
