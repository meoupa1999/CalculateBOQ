package com.sonnh.elv;

import com.sonnh.elv.data.domain.Config;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO;
import com.sonnh.elv.dto.request.CalculateBOQRequestDTO.FloorRequest;
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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 1: Standard Rack Normal ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 2: Standard Rack Single Floor ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 3: Standard Rack Pivot >= Size ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 4: 2U Rack Standard No Exceed ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 5: 2U Rack Early Placement Exceed 20 ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(1));
        assertTrue(result.containsKey(3));
        assertTrue(result.containsKey(7));

        assertEquals(0, result.get(1).getFrom());
        assertEquals(2, result.get(1).getTo());
        assertEquals(3, result.get(3).getFrom());
        assertEquals(3, result.get(3).getTo());
        assertEquals(4, result.get(7).getFrom());
        assertEquals(9, result.get(7).getTo());
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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 6: 2U Rack Exceed At Cabinet Index ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 7: 2U Rack Multiple Exceeds ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(1));
        assertTrue(result.containsKey(4));
        assertTrue(result.containsKey(6));
        assertTrue(result.containsKey(9));
        assertTrue(result.containsKey(13));

        assertEquals(0, result.get(1).getFrom());
        assertEquals(3, result.get(1).getTo());
        
        assertEquals(4, result.get(4).getFrom());
        assertEquals(4, result.get(4).getTo());

        assertEquals(5, result.get(6).getFrom());
        assertEquals(8, result.get(6).getTo());

        assertEquals(9, result.get(9).getFrom());
        assertEquals(9, result.get(9).getTo());

        assertEquals(10, result.get(13).getFrom());
        assertEquals(14, result.get(13).getTo());
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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 8: 2U Rack Pivot >= Size ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

        assertTrue(result.containsKey(4));
        assertEquals(0, result.get(4).getFrom());
        assertEquals(4, result.get(4).getTo());
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
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 9: 2U Rack Pivot >= Size With Exceed ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

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
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(10).build()); // 10 cams on every floor
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(10).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 10: 2U Rack Constant Overloads ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

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
        int[] cams = {5, 24, 25, 16, 0, 4, 0, 5, 6, 0, 5, 9, 4, 4};
        for (int i = 0; i < cams.length; i++) {
            floors.add(FloorRequest.builder().floorIndex(i).label("Tầng " + (i + 1)).camerasCount(cams[i]).build());
        }

        CalculateBOQRequestDTO dto = CalculateBOQRequestDTO.builder()
                .floorsCount(cams.length).basementsCount(0).hasRoof(false)
                .horizontalDistance(52.0).verticalDistance(5.0)
                .rackType("2U").floors(floors).build();

        Map<Integer, CabinetEquipmentDTO> mapResult = new TreeMap<>();
        Map<Integer, CabinetEquipmentDTO> result = calculateService.calculateCabinetPlacementUitls(dto, mapResult, config);

        System.out.println("--- TEST 11: 2U Rack Boundary Group Case B ---");
        result.forEach((k, v) -> System.out.println("Tủ tại tầng " + k + " : covers " + v.getFrom() + " -> " + v.getTo()));

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
}
