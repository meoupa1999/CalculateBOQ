package com.sonnh.elv;

import com.sonnh.elv.data.domain.Config;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO.FloorRequest;
import com.sonnh.elv.dto.request.CalculateBOQManualRequestDTO;
import com.sonnh.elv.dto.response.CalculateBOQResponseDTO;
import com.sonnh.elv.dto.response.CabinetEquipmentDTO;
import com.sonnh.elv.service.impl.CalcualateServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ElvApplicationTests {

    @Autowired
    private CalcualateServiceImpl calculateService;

    private Config createConfig() {
        return Config.builder()
                .conditionLength(70)
                .sw24ConditionQuanity(20)
                .sw16ConditionQuanity(12)
                .sw24MaxPortUse(20)
                .build();
    }

    // ==========================================
    // STANDARD RACK TEST CASES
    // ==========================================

    @Test
    void test1_StandardRackNormal() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(1).build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(10).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("Standard").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 1: Standard Rack Normal ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(3));
        assertTrue(result.containsKey(8));
        assertEquals(0, result.get(3).getFrom());
        assertEquals(6, result.get(3).getTo());
        assertEquals(7, result.get(8).getFrom());
        assertEquals(9, result.get(8).getTo());
    }

    @Test
    void test2_StandardRackSingleFloor() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        floors.add(FloorRequest.builder().floorIndex(0).label("Tầng 1").camerasCount(1).build());

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(1).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("Standard").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 2: Standard Rack Single Floor ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(0));
        assertEquals(0, result.get(0).getFrom());
        assertEquals(0, result.get(0).getTo());
    }

    @Test
    void test3_StandardRackPivotGreaterOrEqualToSize() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(1).build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(3).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("Standard").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 3: Standard Rack Pivot >= Size ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(1));
        assertEquals(0, result.get(1).getFrom());
        assertEquals(2, result.get(1).getTo());
    }

    // ==========================================
    // 2U RACK TEST CASES
    // ==========================================

    @Test
    void test4_2URackStandardNoExceed() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(1).build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(10).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 4: 2U Rack Standard No Exceed ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(3));
        assertTrue(result.containsKey(8));
        assertEquals(0, result.get(3).getFrom());
        assertEquals(6, result.get(3).getTo());
        assertEquals(7, result.get(8).getFrom());
        assertEquals(9, result.get(8).getTo());
    }

    @Test
    void test5_2URackEarlyPlacementExceed20() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            floors.add(FloorRequest.builder()
                    .floorIndex(i)
                    .label("Tầng " + (i + 1))
                    .camerasCount(i == 3 ? 21 : 2) // Floor 3 has 21 cameras
                    .build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(10).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 5: 2U Rack Early Placement Exceed 20 ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(1));
        assertTrue(result.containsKey(3));
        assertTrue(result.containsKey(6));

        assertEquals(0, result.get(1).getFrom());
        assertEquals(2, result.get(1).getTo());
        assertEquals(3, result.get(3).getFrom());
        assertEquals(3, result.get(3).getTo());
        assertEquals(4, result.get(6).getFrom());
        assertEquals(9, result.get(6).getTo());
    }

    @Test
    void test6_2URackExceedAtCabinetIndex() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            floors.add(FloorRequest.builder()
                    .floorIndex(i)
                    .label("Tầng " + (i + 1))
                    .camerasCount(i == 2 ? 25 : 1) // Floor 2 (cabinetIndex) has 25 cameras
                    .build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(10).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 6: 2U Rack Exceed At Cabinet Index ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(0));
        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(6));

        assertEquals(0, result.get(0).getFrom());
        assertEquals(1, result.get(0).getTo());
        assertEquals(2, result.get(2).getFrom());
        assertEquals(2, result.get(2).getTo());
        assertEquals(3, result.get(6).getFrom());
        assertEquals(9, result.get(6).getTo());
    }

    @Test
    void test7_2URackMultipleExceeds() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            int camCount = 2;
            if (i == 4 || i == 9) {
                camCount = 25; // Overloaded at Floor 4 and Floor 9
            }
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(camCount).build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(15).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 7: 2U Rack Multiple Exceeds ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(1));
        assertTrue(result.containsKey(4));
        assertTrue(result.containsKey(6));
        assertTrue(result.containsKey(9));
        assertTrue(result.containsKey(12));

        assertEquals(0, result.get(1).getFrom());
        assertEquals(3, result.get(1).getTo());

        assertEquals(4, result.get(4).getFrom());
        assertEquals(4, result.get(4).getTo());

        assertEquals(5, result.get(6).getFrom());
        assertEquals(8, result.get(6).getTo());

        assertEquals(9, result.get(9).getFrom());
        assertEquals(9, result.get(9).getTo());

        assertEquals(10, result.get(12).getFrom());
        assertEquals(14, result.get(12).getTo());
    }

    @Test
    void test8_2URackPivotGreaterOrEqualToSize() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(1).build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(5).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(4.0) // pivot = 4
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 8: 2U Rack Pivot >= Size ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(2));
        assertEquals(0, result.get(2).getFrom());
        assertEquals(4, result.get(2).getTo());
    }

    @Test
    void test9_2URackPivotGreaterOrEqualToSizeWithExceed() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            floors.add(FloorRequest.builder()
                    .floorIndex(i)
                    .label("Tầng " + (i + 1))
                    .camerasCount(i == 2 ? 22 : 1) // Overload at Floor 2
                    .build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(5).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(4.0) // pivot = 4
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 9: 2U Rack Pivot >= Size With Exceed ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(0));
        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(3));

        assertEquals(0, result.get(0).getFrom());
        assertEquals(1, result.get(0).getTo());
        assertEquals(2, result.get(2).getFrom());
        assertEquals(2, result.get(2).getTo());
        assertEquals(3, result.get(3).getFrom());
        assertEquals(4, result.get(3).getTo());
    }

    @Test
    void test10_2URackConstantOverloads() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(10).build()); // 10
                                                                                                                // cams
                                                                                                                // on
                                                                                                                // every
                                                                                                                // floor
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(10).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 10: 2U Rack Constant Overloads ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(0));
        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(4));
        assertTrue(result.containsKey(6));
        assertTrue(result.containsKey(8));

        assertEquals(0, result.get(0).getFrom());
        assertEquals(1, result.get(0).getTo());
        assertEquals(2, result.get(2).getFrom());
        assertEquals(3, result.get(2).getTo());
        assertEquals(4, result.get(4).getFrom());
        assertEquals(5, result.get(4).getTo());
        assertEquals(6, result.get(6).getFrom());
        assertEquals(7, result.get(6).getTo());
        assertEquals(8, result.get(8).getFrom());
        assertEquals(9, result.get(8).getTo());
    }

    @Test
    void test11_2URackBoundaryGroupCaseB() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        int[] cams = { 5, 24, 25, 16, 0, 4, 0, 5, 6, 0, 5, 9, 4, 4 };
        for (int i = 0; i < cams.length; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(cams[i]).build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(cams.length).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 11: 2U Rack Boundary Group Case B ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        // We expect cabinets at:
        // key 0: [0, 0] (5 cams)
        // key 1: [1, 1] (24 cams)
        // key 2: [2, 2] (25 cams)
        // key 4: [3, 6] (16 + 0 + 4 + 0 = 20 cams)
        // key 8: [7, 10] (5 + 6 + 0 + 5 = 16 cams)
        // key 13: [11, 13] (9 + 4 + 4 = 17 cams)
        assertTrue(result.containsKey(0));
        assertTrue(result.containsKey(1));
        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(4));
        assertTrue(result.containsKey(8));
        assertTrue(result.containsKey(12));

        assertEquals(0, result.get(0).getFrom());
        assertEquals(0, result.get(0).getTo());
        assertEquals(1, result.get(1).getFrom());
        assertEquals(1, result.get(1).getTo());
        assertEquals(2, result.get(2).getFrom());
        assertEquals(2, result.get(2).getTo());
        assertEquals(3, result.get(4).getFrom());
        assertEquals(6, result.get(4).getTo());
        assertEquals(7, result.get(8).getFrom());
        assertEquals(10, result.get(8).getTo());
        assertEquals(11, result.get(12).getFrom());
        assertEquals(13, result.get(12).getTo());
    }

    @Test
    void test12_RooftopPlacement() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();

        // 1 basement, 5 floors, 1 rooftop = 7 total floors (indices 0 to 6)
        floors.add(FloorRequest.builder().floorIndex(0).label("B1").camerasCount(0).build()); // B1 has no cameras
        for (int i = 1; i <= 5; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + i).camerasCount(2).build());
        }
        floors.add(FloorRequest.builder().floorIndex(6).label("Tầng Mái").camerasCount(2).build()); // Rooftop has
                                                                                                    // cameras

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(5).basementsCount(1).hasRoof(true)
                .horizontalDistance(52.0).verticalDistance(4.0)
                .rackType("Standard").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 12: Rooftop Placement ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        // We expect:
        // from = 1, maxSize = 7
        // pivotResult = 70 - 52 = 18. pivot = 5. cabinetIndex = 1 + 5 - 1 = 5.
        // maxFloorInRange = 6. cabinetIndex is centered at 1 + (6 - 1) / 2 = 3.
        assertTrue(result.containsKey(3));
        assertEquals(1, result.get(3).getFrom());
        assertEquals(6, result.get(3).getTo());
    }

    @Test
    void test13_2URackPendingLastGroup() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            floors.add(FloorRequest.builder()
                    .floorIndex(i)
                    .label("Tầng " + (i + 1))
                    .camerasCount(4)
                    .build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(25).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 13: 2U Rack Pending Last Group ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(7));
        assertTrue(result.containsKey(12));
        assertTrue(result.containsKey(17));
        assertTrue(result.containsKey(22));

        assertEquals(0, result.get(2).getFrom());
        assertEquals(4, result.get(2).getTo());
        assertEquals(5, result.get(7).getFrom());
        assertEquals(9, result.get(7).getTo());
        assertEquals(10, result.get(12).getFrom());
        assertEquals(14, result.get(12).getTo());
        assertEquals(15, result.get(17).getFrom());
        assertEquals(19, result.get(17).getTo());
        assertEquals(20, result.get(22).getFrom());
        assertEquals(24, result.get(22).getTo());
    }

    @Test
    void test14_2URackPendingLastGroupExceeded() {
        Config config = createConfig();
        List<FloorRequest> floors = new ArrayList<>();
        // 25 floors: 0 to 19 have 4 cameras, 20 to 24 have 5 cameras
        for (int i = 0; i < 20; i++) {
            floors.add(FloorRequest.builder()
                    .floorIndex(i)
                    .label("Tầng " + (i + 1))
                    .camerasCount(4)
                    .build());
        }
        for (int i = 20; i < 25; i++) {
            floors.add(FloorRequest.builder()
                    .floorIndex(i)
                    .label("Tầng " + (i + 1))
                    .camerasCount(5)
                    .build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(25).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(4.0) // pivot = 5
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult,
                config, 0, dto.getFloors().size() - 1);

        System.out.println("--- TEST 14: 2U Rack Pending Last Group Exceeded ---");
        result.forEach(
                (k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        // We expect:
        // Group 1: 0 -> 4 (placed at 2)
        // Group 2: 5 -> 9 (placed at 7)
        // Group 3: 10 -> 14 (placed at 12)
        // Group 4: 15 -> 19 (placed at 17)
        // Group 5: 20 -> 23 (placed at 21)
        // Group 6: 24 -> 24 (placed at 24 - this was the bug where it was previously
        // omitted!)
        assertTrue(result.containsKey(2));
        assertTrue(result.containsKey(7));
        assertTrue(result.containsKey(12));
        assertTrue(result.containsKey(17));
        assertTrue(result.containsKey(21));
        assertTrue(result.containsKey(24));

        assertEquals(0, result.get(2).getFrom());
        assertEquals(4, result.get(2).getTo());
        assertEquals(5, result.get(7).getFrom());
        assertEquals(9, result.get(7).getTo());
        assertEquals(10, result.get(12).getFrom());
        assertEquals(14, result.get(12).getTo());
        assertEquals(15, result.get(17).getFrom());
        assertEquals(19, result.get(17).getTo());
        assertEquals(20, result.get(21).getFrom());
        assertEquals(23, result.get(21).getTo());
        assertEquals(24, result.get(24).getFrom());
        assertEquals(24, result.get(24).getTo());
    }

    @Test
    void myTest() {
        int totalFloors = 20; // có 20 tầng
        Map<Integer, Integer> rqMap = new TreeMap<>();
        rqMap.put(4, 9);
        rqMap.put(11, 15);
        rqMap.put(18, 19);

        Map<Integer, Integer> result = new TreeMap<>();
        int currentStart = 0;

        for (Map.Entry<Integer, Integer> entry : rqMap.entrySet()) {
            int manualStart = entry.getKey();
            int manualEnd = entry.getValue();

            // Nếu có khoảng trống giữa điểm bắt đầu hiện tại và điểm bắt đầu của nhóm thủ công
            if (currentStart < manualStart) {
                result.put(currentStart, manualStart - 1);
            }

            // Cập nhật điểm bắt đầu tiếp theo là ngay sau nhóm thủ công này
            currentStart = manualEnd + 1;
        }

        // Xử lý nốt đoạn tầng trống còn lại ở cuối tòa nhà (nếu có)
        if (currentStart < totalFloors) {
            result.put(currentStart, totalFloors - 1);
        }

        for (Integer key : result.keySet()) {
            System.out.println(key + " - " + result.get(key));
        }
    }

    @Test
    void testManualCabinetPlacement() {
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(1).build());
        }

        // Tạo 3 nhóm thủ công: [4-9], [11-15], [18-19]
        List<CalculateBOQManualRequestDTO.ManualCabinetGroup> manualGroups = new ArrayList<>();
        
        Map<Integer, Integer> range1 = new TreeMap<>();
        range1.put(4, 9);
        manualGroups.add(CalculateBOQManualRequestDTO.ManualCabinetGroup.builder()
                .cabinetIndex(4)
                .totalCamera(6)
                .floorRange(range1)
                .build());

        Map<Integer, Integer> range2 = new TreeMap<>();
        range2.put(11, 15);
        manualGroups.add(CalculateBOQManualRequestDTO.ManualCabinetGroup.builder()
                .cabinetIndex(11)
                .totalCamera(5)
                .floorRange(range2)
                .build());

        Map<Integer, Integer> range3 = new TreeMap<>();
        range3.put(18, 19);
        manualGroups.add(CalculateBOQManualRequestDTO.ManualCabinetGroup.builder()
                .cabinetIndex(18)
                .totalCamera(2)
                .floorRange(range3)
                .build());

        CalculateBOQManualRequestDTO dto = CalculateBOQManualRequestDTO.builder()
                .floorsCount(20)
                .basementsCount(0)
                .hasRoof(false)
                .horizontalDistance(52.0)
                .verticalDistance(5.0)
                .rackType("2U")
                .floors(floors)
                .manualGroups(manualGroups)
                .build();

        List<CalculateBOQResponseDTO> results = calculateService.calculateBOQManual(dto);

        // In kết quả các tủ được xếp ra màn hình console
        System.out.println("--- KẾT QUẢ XẾP TỦ THỦ CÔNG & TỰ ĐỘNG ---");
        for (CalculateBOQResponseDTO res : results) {
            if (res.getIsCabinetPlaced()) {
                System.out.println("Tủ tại tầng: " + res.getFloorIndex() + " (" + res.getLabel() + ") : covers " + res.getFromIndex() + " -> " + res.getToIndex());
            }
        }

        // Kiểm tra tủ tại các tầng thủ công đã chỉ định
        assertTrue(results.get(4).getIsCabinetPlaced(), "Tầng 4 phải có tủ thủ công");
        assertTrue(results.get(11).getIsCabinetPlaced(), "Tầng 11 phải có tủ thủ công");
        assertTrue(results.get(18).getIsCabinetPlaced(), "Tầng 18 phải có tủ thủ công");

        // Các mảng con trống: [0-3], [10-10], [16-17]
        // Hãy xem các tủ tự động có được sinh ra chính xác cho các khoảng này không
        // Khoảng [0-3] -> Cần tủ tự động đặt ở tầng trung tâm của nó (0 + 3)/2 = 1.
        assertTrue(results.get(1).getIsCabinetPlaced(), "Tầng 1 phải có tủ tự động");
        assertEquals(0, results.get(1).getFromIndex());
        assertEquals(3, results.get(1).getToIndex());

        // Khoảng [10-10] -> Tủ tự động đặt ở tầng 10.
        assertTrue(results.get(10).getIsCabinetPlaced(), "Tầng 10 phải có tủ tự động");
        assertEquals(10, results.get(10).getFromIndex());
        assertEquals(10, results.get(10).getToIndex());

        // Khoảng [16-17] -> Tủ tự động đặt ở tầng (16 + 17)/2 = 16.
        assertTrue(results.get(16).getIsCabinetPlaced(), "Tầng 16 phải có tủ tự động");
        assertEquals(16, results.get(16).getFromIndex());
        assertEquals(17, results.get(16).getToIndex());
    }

    @Test
    void testManualCabinetPlacementEdgeCases() {
        // Prepare 20 floors
        List<FloorRequest> floors = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(1).build());
        }

        // ==========================================
        // EDGE CASE 1: Null or Empty Manual Groups
        // ==========================================
        CalculateBOQManualRequestDTO dtoEmpty = CalculateBOQManualRequestDTO.builder()
                .floorsCount(20)
                .basementsCount(0)
                .hasRoof(false)
                .horizontalDistance(52.0)
                .verticalDistance(5.0)
                .rackType("2U")
                .floors(floors)
                .manualGroups(null)
                .build();
        List<CalculateBOQResponseDTO> resultsEmpty = calculateService.calculateBOQManual(dtoEmpty);
        long autoCabinetCount = resultsEmpty.stream().filter(CalculateBOQResponseDTO::getIsCabinetPlaced).count();
        assertTrue(autoCabinetCount > 0, "Khi không cấu hình manual, hệ thống phải tự động xếp tủ");

        // ==========================================
        // EDGE CASE 2: Out of order manual groups + Boundaries (floor 0 & last floor)
        // ==========================================
        List<CalculateBOQManualRequestDTO.ManualCabinetGroup> outOfOrderGroups = new ArrayList<>();
        
        // Manual group 2: covering floors 17 to 19 (placed at floor 17) - trailing edge case
        Map<Integer, Integer> rangeTrailing = new TreeMap<>();
        rangeTrailing.put(17, 19);
        outOfOrderGroups.add(CalculateBOQManualRequestDTO.ManualCabinetGroup.builder()
                .cabinetIndex(17)
                .totalCamera(3)
                .floorRange(rangeTrailing)
                .build());

        // Manual group 1: covering floors 0 to 2 (placed at floor 1) - leading edge case
        Map<Integer, Integer> rangeLeading = new TreeMap<>();
        rangeLeading.put(0, 2);
        outOfOrderGroups.add(CalculateBOQManualRequestDTO.ManualCabinetGroup.builder()
                .cabinetIndex(1)
                .totalCamera(3)
                .floorRange(rangeLeading)
                .build());

        CalculateBOQManualRequestDTO dtoEdge = CalculateBOQManualRequestDTO.builder()
                .floorsCount(20)
                .basementsCount(0)
                .hasRoof(false)
                .horizontalDistance(52.0)
                .verticalDistance(5.0)
                .rackType("2U")
                .floors(floors)
                .manualGroups(outOfOrderGroups)
                .build();

        List<CalculateBOQResponseDTO> resultsEdge = calculateService.calculateBOQManual(dtoEdge);

        // Verify out-of-order and boundaries work
        assertTrue(resultsEdge.get(1).getIsCabinetPlaced(), "Tầng 1 (thủ công) phải có tủ");
        assertEquals(0, resultsEdge.get(1).getFromIndex());
        assertEquals(2, resultsEdge.get(1).getToIndex());

        assertTrue(resultsEdge.get(17).getIsCabinetPlaced(), "Tầng 17 (thủ công) phải có tủ");
        assertEquals(17, resultsEdge.get(17).getFromIndex());
        assertEquals(19, resultsEdge.get(17).getToIndex());

        // The remaining auto segment should be [3, 16]
        boolean autoCabinetInMiddle = false;
        for (int i = 3; i <= 16; i++) {
            if (resultsEdge.get(i).getIsCabinetPlaced()) {
                autoCabinetInMiddle = true;
                assertTrue(resultsEdge.get(i).getFromIndex() >= 3);
                assertTrue(resultsEdge.get(i).getToIndex() <= 16);
            }
        }
        assertTrue(autoCabinetInMiddle, "Phải có ít nhất một tủ tự động được sinh ra trong khoảng trống ở giữa [3, 16]");

        // ==========================================
        // EDGE CASE 3: Fully Manual (No empty segments left)
        // ==========================================
        List<CalculateBOQManualRequestDTO.ManualCabinetGroup> fullGroups = new ArrayList<>();
        Map<Integer, Integer> rangePart1 = new TreeMap<>();
        rangePart1.put(0, 9);
        fullGroups.add(CalculateBOQManualRequestDTO.ManualCabinetGroup.builder()
                .cabinetIndex(4)
                .totalCamera(10)
                .floorRange(rangePart1)
                .build());

        Map<Integer, Integer> rangePart2 = new TreeMap<>();
        rangePart2.put(10, 19);
        fullGroups.add(CalculateBOQManualRequestDTO.ManualCabinetGroup.builder()
                .cabinetIndex(14)
                .totalCamera(10)
                .floorRange(rangePart2)
                .build());

        CalculateBOQManualRequestDTO dtoFull = CalculateBOQManualRequestDTO.builder()
                .floorsCount(20)
                .basementsCount(0)
                .hasRoof(false)
                .horizontalDistance(52.0)
                .verticalDistance(5.0)
                .rackType("2U")
                .floors(floors)
                .manualGroups(fullGroups)
                .build();

        List<CalculateBOQResponseDTO> resultsFull = calculateService.calculateBOQManual(dtoFull);

        long totalPlacedFull = resultsFull.stream().filter(CalculateBOQResponseDTO::getIsCabinetPlaced).count();
        assertEquals(2, totalPlacedFull, "Chỉ được phép có đúng 2 tủ thủ công khi toàn bộ các tầng đều được phủ thủ công");
        assertTrue(resultsFull.get(4).getIsCabinetPlaced());
        assertTrue(resultsFull.get(14).getIsCabinetPlaced());
    }
}
