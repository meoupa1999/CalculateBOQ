package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Config;
import java.util.ArrayList;
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

            int calculatedCable = calculateCableLength(cabinetIndex, floor, dto.getVerticalDistance());
            builder.cableLength(calculatedCable);

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

    private int calculateCableLength(Integer cabinetIndex, FloorRequest floor, Double verticalDistance) {
        if (cabinetIndex == null) {
            return 0;
        }
        int floorDiff = Math.abs(floor.getFloorIndex() - cabinetIndex) + 1;
        int baseCable = floor.getCableLength() != null ? floor.getCableLength() : 0;
        double vDist = verticalDistance != null ? verticalDistance : 0.0;
        return (int) Math.round((vDist * floorDiff) + baseCable);
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

    /*
    public Map<Integer, CabinetEquipmentDTO> calculateSwichPOE(Map<Integer, CabinetEquipmentDTO> mapResult,
            Config config) {
        for (Integer key : mapResult.keySet()) {
            int cameraQuantityInCabinet = mapResult.get(key).getCameraQuantityInCabinet();
            int quantitySw16 = 0;
            int quantitySw24 = 0;
            while (cameraQuantityInCabinet > 0) {
                if (cameraQuantityInCabinet >= config.getSw16ConditionQuanity()) {
                    mapResult.get(key).setSw24Quantity(++quantitySw24);
                    cameraQuantityInCabinet -= config.getSw24ConditionQuanity();
                } else {
                    mapResult.get(key).setSw16Quantity(++quantitySw16);
                    cameraQuantityInCabinet -= config.getSw16ConditionQuanity();
                }
            }
        }
        return mapResult;
    }
    */

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
            for (int S = minSwitches; ; S++) {
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

    // @Override
    // public List<CalculateBOQResponseDTO>
    // calculateBOQManual(CalculateBOQManualRequestDTO dto) {
    /*
     * Config config = configRepository
     * .findById(UUID.fromString("a2b0a797-8ff2-4a79-ac5d-78525bd25e90")).get();
     * 
     * // 1. Build rqMap from manualGroups
     * Map<Integer, Integer> rqMap = new TreeMap<>();
     * if (dto.getManualGroups() != null) {
     * for (CalculateBOQManualRequestDTO.ManualCabinetGroup group :
     * dto.getManualGroups()) {
     * if (group.getFloorRange() != null) {
     * for (Map.Entry<Integer, Integer> rangeEntry :
     * group.getFloorRange().entrySet()) {
     * rqMap.put(rangeEntry.getKey(), rangeEntry.getValue());
     * }
     * }
     * }
     * }
     * 
     * // 2. Identify remaining auto segments using our optimized sub-array division
     * algorithm
     * int totalFloors = dto.getFloors().size();
     * Map<Integer, Integer> autoSegments = new TreeMap<>();
     * int currentStart = 0;
     * for (Map.Entry<Integer, Integer> entry : rqMap.entrySet()) {
     * int manualStart = entry.getKey();
     * int manualEnd = entry.getValue();
     * if (currentStart < manualStart) {
     * autoSegments.put(currentStart, manualStart - 1);
     * }
     * currentStart = manualEnd + 1;
     * }
     * if (currentStart < totalFloors) {
     * autoSegments.put(currentStart, totalFloors - 1);
     * }
     * 
     * Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
     * 
     * // 3. Populate manual groups into mapResult
     * if (dto.getManualGroups() != null) {
     * for (CalculateBOQManualRequestDTO.ManualCabinetGroup group :
     * dto.getManualGroups()) {
     * if (group.getFloorRange() != null && !group.getFloorRange().isEmpty()) {
     * Map.Entry<Integer, Integer> rangeEntry =
     * group.getFloorRange().entrySet().iterator().next();
     * CabinetEquipmentDTO cab = new CabinetEquipmentDTO();
     * cab.setFrom(rangeEntry.getKey());
     * cab.setTo(rangeEntry.getValue());
     * mapResult.put(group.getCabinetIndex(), cab);
     * }
     * }
     * }
     * 
     * // 4. Run the auto-placement algorithm on each remaining segment
     * CalculateBOQRequestDTO tempDto = CalculateBOQRequestDTO.builder()
     * .floorsCount(dto.getFloorsCount())
     * .basementsCount(dto.getBasementsCount())
     * .hasRoof(dto.getHasRoof())
     * .horizontalDistance(dto.getHorizontalDistance())
     * .verticalDistance(dto.getVerticalDistance())
     * .rackType(dto.getRackType())
     * .floors(dto.getFloors())
     * .build();
     * 
     * for (Map.Entry<Integer, Integer> segment : autoSegments.entrySet()) {
     * int start = segment.getKey();
     * int end = segment.getValue();
     * if (start > end) {
     * continue;
     * }
     * calculateCabinetPlacementUitls(tempDto, mapResult, config, start, end);
     * }
     * 
     * // 5. Run standard equipment sizing calculators on the unified mapResult
     * calculateCameraQuantityInCabinet(mapResult, tempDto);
     * calculateSwichPOE(mapResult, config);
     * calculateUPS(mapResult, config);
     * calculateConverter(mapResult, config);
     * calculatePDU(mapResult, config);
     * 
     * // 6. Build the response list mapped by floor
     * List<CalculateBOQResponseDTO> result = new ArrayList<>();
     * for (CalculateBOQRequestDTO.FloorRequest floor : dto.getFloors()) {
     * boolean isPlaced = mapResult.containsKey(floor.getFloorIndex());
     * CalculateBOQResponseDTO.CalculateBOQResponseDTOBuilder builder =
     * CalculateBOQResponseDTO.builder()
     * .floorIndex(floor.getFloorIndex())
     * .label(floor.getLabel())
     * .camerasCount(floor.getCamerasCount())
     * .isCabinetPlaced(isPlaced);
     * 
     * // Find covering cabinet range
     * CabinetEquipmentDTO coveringCabinet = null;
     * for (Map.Entry<Integer, CabinetEquipmentDTO> entry : mapResult.entrySet()) {
     * CabinetEquipmentDTO cab = entry.getValue();
     * if (floor.getFloorIndex() >= cab.getFrom() && floor.getFloorIndex() <=
     * cab.getTo()) {
     * coveringCabinet = cab;
     * break;
     * }
     * }
     * 
     * if (coveringCabinet != null) {
     * builder.fromIndex(coveringCabinet.getFrom())
     * .toIndex(coveringCabinet.getTo());
     * }
     * 
     * if (isPlaced) {
     * CabinetEquipmentDTO cabinet = mapResult.get(floor.getFloorIndex());
     * builder.cameraQuantityInCabinet(cabinet.getCameraQuantityInCabinet())
     * .sw24Count(cabinet.getSw24Quantity())
     * .sw16Count(cabinet.getSw16Quantity())
     * .upsCount(cabinet.getUps())
     * .pduCount(cabinet.getPdu())
     * .convCount(cabinet.getConverter());
     * } else {
     * builder.cameraQuantityInCabinet(0)
     * .sw24Count(0)
     * .sw16Count(0)
     * .upsCount(0)
     * .pduCount(0)
     * .convCount(0);
     * }
     * result.add(builder.build());
     * }
     * 
     * return result;
     */
    // return new ArrayList<>();
    // }

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

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        if (dto.getManualGroups() != null) {
            for (CalculateBOQManualRequestDTO.ManualCabinetGroup group : dto.getManualGroups()) {
                if (group.getFloorRange() != null && !group.getFloorRange().isEmpty()) {
                    Map.Entry<Integer, Integer> rangeEntry = group.getFloorRange().entrySet().iterator().next();
                    CabinetEquipmentDTO cab = new CabinetEquipmentDTO();
                    cab.setFrom(rangeEntry.getKey());
                    cab.setTo(rangeEntry.getValue());
                    mapResult.put(group.getCabinetIndex(), cab);
                }
            }
        }

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
            calculateCabinetPlacementUitls(tempDto, mapResult, config, start, end);
        }

        calculateCameraQuantityInCabinet(mapResult, tempDto);
        calculateSwichPOE(mapResult, config);
        calculateUPS(mapResult, config);
        calculateConverter(mapResult, config);
        calculatePDU(mapResult, config);

        List<CalculateBOQResponseDTO> result = new ArrayList<>();
        for (CalculateBOQRequestDTO.FloorRequest floor : dto.getFloors()) {
            boolean isPlaced = mapResult.containsKey(floor.getFloorIndex());
            CalculateBOQResponseDTO.CalculateBOQResponseDTOBuilder builder = CalculateBOQResponseDTO.builder()
                    .floorIndex(floor.getFloorIndex())
                    .label(floor.getLabel())
                    .camerasCount(floor.getCamerasCount())
                    .isCabinetPlaced(isPlaced);

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

            int calculatedCable = calculateCableLength(cabinetIndex, floor, dto.getVerticalDistance());
            builder.cableLength(calculatedCable);

            if (isPlaced) {
                CabinetEquipmentDTO cabinet = mapResult.get(floor.getFloorIndex());
                String cabType = dto.getRackType();
                if (dto.getManualGroups() != null) {
                    for (CalculateBOQManualRequestDTO.ManualCabinetGroup group : dto.getManualGroups()) {
                        if (group.getCabinetIndex().equals(floor.getFloorIndex())) {
                            if (group.getRackType() != null && !group.getRackType().isEmpty()) {
                                cabType = group.getRackType();
                            }
                            break;
                        }
                    }
                }
                builder.cameraQuantityInCabinet(cabinet.getCameraQuantityInCabinet())
                        .sw24Count(cabinet.getSw24Quantity())
                        .sw16Count(cabinet.getSw16Quantity())
                        .upsCount(cabinet.getUps())
                        .pduCount(cabinet.getPdu())
                        .convCount(cabinet.getConverter())
                        .cabinetType(cabType);
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
}
