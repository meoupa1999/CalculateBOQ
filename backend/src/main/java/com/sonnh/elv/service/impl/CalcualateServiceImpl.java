package com.sonnh.elv.service.impl;

import com.sonnh.elv.data.domain.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        // int count = 0;

        Config config = configRepository
                .findById(UUID.fromString("a2b0a797-8ff2-4a79-ac5d-78525bd25e90")).get();
        Map<Integer, CabinetEquipmentDTO> mapResult = new HashMap<>();
        Map<Integer, CabinetEquipmentDTO> camaQuantityInCabinet = calculateCabinetPlacementUitls(dto, mapResult,
                config);
        // int pivot = cabinetPlacementIndex.getPivot();
        for (Integer key : camaQuantityInCabinet.keySet()) {
            System.out.println(key + " : " + camaQuantityInCabinet.get(key).toString());
        }

        List<CalculateBOQResponseDTO> result = new ArrayList<>();
        // Map<Integer, CabinetEquipmentDTO> cameraQuantityInCabinet =
        // calculateCameraQuantityInCabinet(pivot,
        // cabinetPlacementIndex.getResult(), dto);
        // calculateSwichPOE(cameraQuantityInCabinet, config);
        // calculateUPS(cameraQuantityInCabinet, config);
        // calculateConverter(cameraQuantityInCabinet, config);
        // calculatePDU(cameraQuantityInCabinet, config);

        // for (Integer key : cameraQuantityInCabinet.keySet()) {
        // System.out.println("lầu " + key + " : " +
        // cameraQuantityInCabinet.get(key).getSw16Quantity() + " sw16"
        // + " , " + cameraQuantityInCabinet.get(key).getSw24Quantity() + " sw24");
        // }

        // // add field vô result

        // for (FloorRequest floor : dto.getFloors()) {
        // for (Integer index : cabinetPlacementIndex.getResult()) {
        // if (floor.getFloorIndex().equals(index)) {
        // result.add(CalculateBOQResponseDTO.builder()
        // .floorIndex(index)
        // .camerasCount(floor.getCamerasCount())
        // .cameraQuantityInCabinet(cameraQuantityInCabinet.get(index).getCameraQuantityInCabinet())
        // .sw16Count(cameraQuantityInCabinet.get(index).getSw16Quantity())
        // .sw24Count(cameraQuantityInCabinet.get(index).getSw24Quantity())
        // .upsCount(cameraQuantityInCabinet.get(index).getUps())
        // .pduCount(cameraQuantityInCabinet.get(index).getPdu())
        // .convCount(cameraQuantityInCabinet.get(index).getConverter())
        // .build());
        // }

        // }
        // }
        // for (CalculateBOQResponseDTO calculateBOQResponseDTO : result) {
        // System.out.println(calculateBOQResponseDTO.toString());
        // System.out.println("--------------------------");
        // }

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
        while (pivotResult > 0) {
            pivotResult -= verticalDistance;
            if (pivotResult > 0) {
                pivot++;
            }
        }
        cabinetIndex = pivot - 1;
        System.out.println("pivot: " + pivot);
        // case thường
        if (cabinetIndex >= dto.getFloors().size()) {
            maxFloorInRange = dto.getFloors().size() - 1;
            cabinetIndex = maxFloorInRange;
            to = maxFloorInRange;
        } else {
            maxFloorInRange += pivot * 2 - 1;
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
                maxFloorInRange += pivot * 2 - 1;
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

        // case 2U

        // init
        maxFloorInRange = 0;
        cabinetIndex = 0;
        from = 0;
        to = 0;
        cabinetIndex = 0;
        cabinetIndex = pivot - 1;

        if (dto.getRackType().equals("2U")) {
            System.out.println("case 2U");
            if (cabinetIndex >= dto.getFloors().size()) {
                maxFloorInRange = dto.getFloors().size() - 1;
                cabinetIndex = maxFloorInRange;
                to = maxFloorInRange;
            } else {
                maxFloorInRange += pivot * 2 - 1;
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
                        maxFloorInRange += pivot * 2 - 1;
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

                        CabinetEquipmentDTO lastCabinet = new CabinetEquipmentDTO();
                        lastCabinet.setFrom(to);
                        lastCabinet.setTo(to);
                        mapResult.put(to, lastCabinet);

                        from = to + 1;
                        totalCamera2U = 0;
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
                        maxFloorInRange = from + pivot * 2 - 1;

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
    public Map<Integer, CabinetEquipmentDTO> calculateCameraQuantityInCabinet(int pivot, List<Integer> cabinetPlacement,
            CalculateBOQRequestDTO dto) {
        Map<Integer, CabinetEquipmentDTO> myMap = new HashMap<>();

        // Khởi tạo trước các phần tử trong map để tránh NullPointerException khi gọi ở
        // ngoài
        for (Integer index : cabinetPlacement) {
            CabinetEquipmentDTO defaultDto = new CabinetEquipmentDTO();
            defaultDto.setCameraQuantityInCabinet(0);
            defaultDto.setSw24Quantity(0);
            defaultDto.setSw16Quantity(0);
            defaultDto.setUps(0);
            defaultDto.setPdu(0);
            defaultDto.setConverter(0);
            myMap.put(index, defaultDto);
        }

        int myIndex = 0;
        int cansosanh = 0;
        int indexCount = 0;
        for (Integer index : cabinetPlacement) {
            indexCount++;
            int total = 0;
            // Thêm điều kiện i < dto.getFloors().size() để bảo vệ tránh Out Of Bound
            for (int i = myIndex; i < index + pivot && i < dto.getFloors().size(); i++) {
                cansosanh = index + pivot - 2;
                if (cansosanh > dto.getFloorsCount()) {
                    System.out.println("Trường hợp db đổi cansosanh");
                    cansosanh = dto.getFloorsCount() - 1;
                }
                System.out.println("myindex : " + myIndex);
                System.out.println(" cansosanh : " + cansosanh);
                System.out.println("i: " + i);
                if (i < cansosanh) {
                    total += dto.getFloors().get(i).getCamerasCount();
                    System.out.println("tính tổng nè ");
                }
                if (i == cansosanh && indexCount == cabinetPlacement.size()) {
                    total += dto.getFloors().get(i).getCamerasCount();
                    System.out.println("tính tổng nè đặc biệt");
                }
                if (i == cansosanh) {
                    System.out.println("put vào map ở index " + index);
                    CabinetEquipmentDTO cabinetEquipmentDTO = myMap.get(index);
                    if (cabinetEquipmentDTO != null) {
                        cabinetEquipmentDTO.setCameraQuantityInCabinet(total);
                    }
                    myIndex = i;
                    total = 0;
                    break;
                }
            }
        }
        return myMap;
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
