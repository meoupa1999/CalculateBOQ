package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.sonnh.elv.data.repository.ConfigRepository;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO.FloorRequest;
import com.sonnh.elv.dto.response.CabinetEquipmentDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;
import com.sonnh.elv.dto.response.MyCalculateResDto;
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
        calculateCabinetPlacementUitls(dto, mapResult, config);
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
            for (Map.Entry<Integer, CabinetEquipmentDTO> entry : mapResult.entrySet()) {
                CabinetEquipmentDTO cab = entry.getValue();
                if (floor.getFloorIndex() >= cab.getFrom() && floor.getFloorIndex() <= cab.getTo()) {
                    coveringCabinet = cab;
                    break;
                }
            }

            if (coveringCabinet != null) {
                builder.fromIndex(coveringCabinet.getFrom())
                        .toIndex(coveringCabinet.getTo());
            }

            if (isPlaced) {
                CabinetEquipmentDTO cabinet = mapResult.get(floor.getFloorIndex());
                builder.cameraQuantityInCabinet(cabinet.getCameraQuantityInCabinet())
                        .sw24Count(cabinet.getSw24Quantity())
                        .sw16Count(cabinet.getSw16Quantity())
                        .upsCount(cabinet.getUps())
                        .pduCount(cabinet.getPdu())
                        .convCount(cabinet.getConverter());
            } else {
                builder.cameraQuantityInCabinet(0)
                        .sw24Count(0)
                        .sw16Count(0)
                        .upsCount(0)
                        .pduCount(0)
                        .convCount(0);
            }
            result.add(builder.build());
        }

        return result;
    }

    // public MyCalculateResDto
    // calculateCabinetPlacementUitls(CalculateBOQRequestDTO dto, Config config) {
    // MyCalculateResDto myCalculateResDto = new MyCalculateResDto();
    // for (FloorRequest floor : dto.getFloors()) {
    // System.out.println("floor: " + floor.getFloorIndex() + " có " +
    // floor.getCamerasCount() + " camera");
    // }
    // List<Integer> result = new ArrayList<>();
    // // Config config = configRepository
    // // .findById(UUID.fromString("a2b0a797-8ff2-4a79-ac5d-78525bd25e90")).get();
    // int quantity = dto.getFloorsCount();
    // int subquantity = quantity;
    // int count = 0;
    // Double width = dto.getHorizontalDistance();
    // Double height = dto.getVerticalDistance();
    // int condition = config.getConditionLength();
    // Double total = width;
    // int pivot = 0;

    // while (total < condition) {
    // total += height;
    // if (total <= condition) {
    // pivot++;
    // }
    // System.out.println("total: " + total);
    // }
    // System.out.println("pivot: " + pivot);

    // while (count <= quantity) {
    // subquantity -= (pivot * 2) + 1;
    // if (subquantity < 0) {
    // count += ((quantity - count) / 2) + 1;
    // System.out.println("count2: " + count);
    // result.add(count);
    // break;
    // }
    // count += pivot;
    // System.out.println("count: " + count);
    // result.add(count);
    // count += pivot - 1;
    // }
    // int floorIndex = 0;
    // int twoUTotal = 0;
    // int tempPivot = (pivot * 2 - 1);
    // int twoUCount = 0;
    // List<Integer> twoUResult = new ArrayList<>();
    // if (dto.getRackType().equals("2U")) {
    // System.out.println("------------------------------");
    // System.out.println("2U nè");

    // for (FloorRequest floor : dto.getFloors()) {
    // floorIndex++;
    // twoUCount++;
    // twoUTotal += floor.getCamerasCount();
    // System.out.println("--------------------------------");
    // System.out.println("floorIndex: " + floorIndex);
    // System.out.println("twoUTotal: " + twoUTotal);
    // System.out.println("tempPivot: " + tempPivot);

    // if (floorIndex == tempPivot || twoUTotal >= config.getSw24MaxPortUse()) {
    // System.out.println("chạy vào đây");
    // twoUResult.add(floorIndex - (twoUCount / 2));
    // twoUTotal = 0;
    // twoUCount = 0;
    // if (tempPivot < quantity) {
    // tempPivot += (pivot * 2 - 1);
    // }
    // continue;
    // }
    // if (tempPivot > quantity && twoUTotal <= config.getSw24MaxPortUse() &&
    // floorIndex == quantity) {
    // twoUResult.add(floorIndex - (twoUCount / 2));
    // break;
    // }
    // if (tempPivot > quantity && twoUTotal >= config.getSw24MaxPortUse()) {
    // twoUResult.add(floorIndex);
    // break;
    // }
    // System.out.println("--------------------------------");
    // }
    // myCalculateResDto.setResult(twoUResult);
    // myCalculateResDto.setPivot(pivot);
    // System.out.println("twoUResult: " + twoUResult);
    // return myCalculateResDto;
    // }
    // myCalculateResDto.setResult(result);
    // myCalculateResDto.setPivot(pivot);
    // System.out.println("------------------------------");
    // System.out.println("result: " + result);
    // return myCalculateResDto;
    // }

    public Map<Integer, CabinetEquipmentDTO> calculateCabinetPlacementUitls(CalculateBOQRequestDTO dto,
            Map<Integer, CabinetEquipmentDTO> mapResult, Config config) {
        // tính pivot
        int pivot = 0;
        int maxFloorInRange = 0;
        int cabinetIndex = 0;
        int from = 0;
        int to = 0;
        int flag = 0;
        Integer horizontalDistance = dto.getHorizontalDistance().intValue();
        Integer verticalDistance = dto.getVerticalDistance().intValue();
        int pivotResult = config.getConditionLength() - horizontalDistance;
        // tính from

        for (FloorRequest floor : dto.getFloors()) {
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
            if (cabinetIndex >= dto.getFloors().size()) {
                maxFloorInRange = dto.getFloors().size() - 1;
                cabinetIndex = maxFloorInRange;
                to = maxFloorInRange;
            } else {
                maxFloorInRange = from + pivot * 2 - 2;
                to = maxFloorInRange;
            }
            if (maxFloorInRange >= dto.getFloors().size()) {
                flag = maxFloorInRange;
                maxFloorInRange = dto.getFloors().size() - 1;
                to = maxFloorInRange;
                if (cabinetIndex >= dto.getFloors().size()) {
                    if (cabinetIndex <= flag) {
                        cabinetIndex = maxFloorInRange;
                    } else {
                        cabinetIndex = from + pivot - 1;
                    }
                }
            }
            for (FloorRequest floor : dto.getFloors()) {
                if (floor.getFloorIndex() < from) {
                    continue;
                }
                System.out.println("--------------------------------");
                System.out.println("maxFloorInRange: " + maxFloorInRange);
                System.out.println("cabinetIndex: " + cabinetIndex);
                if (from < dto.getFloors().size() && floor.getFloorIndex() == cabinetIndex) {
                    System.out.println("Put vao map");
                    CabinetEquipmentDTO cabinetEquipmentDTO = new CabinetEquipmentDTO();
                    cabinetEquipmentDTO.setFrom(from);
                    cabinetEquipmentDTO.setTo(to);
                    mapResult.put(floor.getFloorIndex(), cabinetEquipmentDTO);
                    from = maxFloorInRange + 1;
                    maxFloorInRange = from + pivot * 2 - 2;
                    cabinetIndex = from + pivot - 1;
                    to = maxFloorInRange;

                    if (maxFloorInRange >= dto.getFloors().size()) {
                        flag = maxFloorInRange;
                        maxFloorInRange = dto.getFloors().size() - 1;
                        to = maxFloorInRange;
                        if (cabinetIndex >= dto.getFloors().size()) {
                            // cabinetIndex = from + pivot - 1;
                            if (cabinetIndex <= flag) {
                                System.out.println("cabinetIndex trong special case: " + cabinetIndex);
                                System.out.println("maxFloorInRange trong special case: " + maxFloorInRange);
                                System.out.println("chay vao day");
                                cabinetIndex = maxFloorInRange;
                            } else {
                                cabinetIndex = from + pivot - 1;
                            }
                        }
                    }
                }
            }
        } else {
            // case 2U
            System.out.println("case 2U");

            // init
            maxFloorInRange = 0;
            cabinetIndex = 0;
            from = 0;
            to = 0;
            // tính from
            for (FloorRequest floor : dto.getFloors()) {
                if (floor.getCamerasCount() != 0) {
                    from = floor.getFloorIndex();
                    break;
                }
                from++;
            }
            cabinetIndex = from + pivot - 1;

            if (cabinetIndex >= dto.getFloors().size()) {
                maxFloorInRange = dto.getFloors().size() - 1;
                cabinetIndex = maxFloorInRange;
                to = maxFloorInRange;
            } else {
                maxFloorInRange = from + pivot * 2 - 2;
                to = maxFloorInRange;
            }
            if (maxFloorInRange >= dto.getFloors().size()) {
                flag = maxFloorInRange;
                maxFloorInRange = dto.getFloors().size() - 1;
                to = maxFloorInRange;
                if (cabinetIndex >= dto.getFloors().size()) {
                    if (cabinetIndex <= flag) {
                        cabinetIndex = maxFloorInRange;
                    } else {
                        cabinetIndex = from + pivot - 1;
                    }
                }
            }
            int totalCamera2U = 0;
            for (FloorRequest floor : dto.getFloors()) {
                if (floor.getFloorIndex() < from) {
                    continue;
                }
                System.out.println("cabinetIndex: " + cabinetIndex);
                System.out.println("maxFloorInRange: " + maxFloorInRange);
                if (from < dto.getFloors().size() && floor.getFloorIndex() == to) {
                    totalCamera2U += floor.getCamerasCount();
                    if (totalCamera2U <= 20) {
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

                        if (maxFloorInRange >= dto.getFloors().size()) {
                            flag = maxFloorInRange;
                            maxFloorInRange = dto.getFloors().size() - 1;
                            to = maxFloorInRange;
                            if (cabinetIndex >= dto.getFloors().size()) {
                                if (cabinetIndex <= flag) {
                                    System.out.println("cabinetIndex trong special case: " + cabinetIndex);
                                    System.out.println("maxFloorInRange trong special case: " + maxFloorInRange);
                                    System.out.println("chay vao day");
                                    cabinetIndex = maxFloorInRange;
                                } else {
                                    cabinetIndex = from + pivot - 1;
                                }
                            }
                        }
                    } else {
                        System.out.println("Vượt quá 20 camera, dat tu som");
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
                        if (maxFloorInRange >= dto.getFloors().size()) {
                            flag = maxFloorInRange;
                            maxFloorInRange = dto.getFloors().size() - 1;
                            to = maxFloorInRange;
                            if (cabinetIndex >= dto.getFloors().size()) {
                                if (cabinetIndex <= flag) {
                                    cabinetIndex = maxFloorInRange;
                                } else {
                                    cabinetIndex = from + pivot - 1;
                                }
                            }
                        }
                    }
                } else if (from < dto.getFloors().size() && floor.getFloorIndex() < maxFloorInRange) {
                    if (totalCamera2U + floor.getCamerasCount() <= 20) {
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
                        if (maxFloorInRange >= dto.getFloors().size()) {
                            flag = maxFloorInRange;
                            maxFloorInRange = dto.getFloors().size() - 1;
                            to = maxFloorInRange;
                            if (cabinetIndex >= dto.getFloors().size()) {
                                if (cabinetIndex <= flag) {
                                    cabinetIndex = maxFloorInRange;
                                } else {
                                    cabinetIndex = from + pivot - 1;
                                }
                            }
                        }
                    }
                }
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
        for (Integer key : mapResult.keySet()) {
            int cameraQuantityInCabinet = mapResult.get(key).getCameraQuantityInCabinet();
            int quantitySw16 = 0;
            int quantitySw24 = 0;
            while (cameraQuantityInCabinet > 0) {
                if (cameraQuantityInCabinet >= 20) {
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
            int total = (mapResult.get(key).getSw24Quantity() != null
                    ? mapResult.get(key).getSw24Quantity()
                    : 0) + (mapResult.get(key).getSw16Quantity() != null ? mapResult.get(key).getSw16Quantity() : 0) +
                    config.getConverter();
            mapResult.get(key).setPdu(total);

        }
    }
}
