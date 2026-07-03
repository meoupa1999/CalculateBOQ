/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { Project, InventoryItem, StandardPreset, FloorData, SiteParameters, HardwareLogic } from "./types";

export const BASE_PRESETS: StandardPreset[] = [
  {
    id: "std-commercial",
    name: "Tiêu chuẩn Văn phòng (Commercial Office)",
    description: "Tỷ lệ camera Dome cao, tối ưu hóa thẩm mỹ trong nhà. Sử dụng Switch 24-Port hiệu năng cao.",
    cameraRatio: 70, // 70% Dome, 30% Bullet
    switchPreference: "SW24",
    upsType: "1K",
    cableFactor: 2.0,
  },
  {
    id: "std-industrial",
    name: "Tiêu chuẩn Nhà xưởng (Industrial Grade)",
    description: "Tỷ lệ camera Thân (Bullet) ngoài trời cao, chống bụi nước. Sử dụng nguồn dự phòng UPS dung lượng lớn.",
    cameraRatio: 30, // 30% Dome, 70% Bullet
    switchPreference: "Auto",
    upsType: "2K",
    cableFactor: 2.5,
  },
  {
    id: "std-economy",
    name: "Giải pháp Tiết kiệm (Economy Layout)",
    description: "Tối ưu hóa chi phí với Switch 16-Port và tủ Rack nhỏ gọn. Thích hợp cho dự án vừa và nhỏ.",
    cameraRatio: 50, // 50% Dome, 50% Bullet
    switchPreference: "SW16",
    upsType: "None",
    cableFactor: 1.8,
  },
  {
    id: "std-enterprise",
    name: "Doanh nghiệp Cao cấp (Enterprise Redundant)",
    description: "Nguồn dự phòng kép, cáp quang uplink tốc độ cao và tủ rack chịu tải nặng.",
    cameraRatio: 60, // 60% Dome, 40% Bullet
    switchPreference: "Auto",
    upsType: "2K",
    cableFactor: 2.2,
  }
];

export const BASE_INVENTORY: InventoryItem[] = [
  {
    id: "item-cam-dome",
    code: "CAM-DOM-02",
    name: "Camera IP Dome 2MP",
    category: "Camera",
    spec: "Độ phân giải 2MP, Hồng ngoại 30m, Chuẩn nén H.265+, IP67, Hỗ trợ PoE",
    unit: "Cái",
    basePrice: 1550000,
  },
  {
    id: "item-cam-bullet",
    code: "CAM-BUL-02",
    name: "Camera IP Thân Bullet 2MP",
    category: "Camera",
    spec: "Độ phân giải 2MP, Thân kim loại, Hồng ngoại 50m, Chuẩn nén H.265+, IP67, Hỗ trợ PoE",
    unit: "Cái",
    basePrice: 1850000,
  },
  {
    id: "item-sw-24",
    code: "SW-POE-24",
    name: "Switch PoE 24-Port Gigabit",
    category: "Switch",
    spec: "24-Port PoE 10/100/1000Mbps + 2-Port Uplink SFP, Tổng công suất PoE 370W",
    unit: "Cái",
    basePrice: 6500000,
  },
  {
    id: "item-sw-16",
    code: "SW-POE-16",
    name: "Switch PoE 16-Port Gigabit",
    category: "Switch",
    spec: "16-Port PoE 10/100/1000Mbps + 2-Port Uplink Gigabit, Tổng công suất PoE 250W",
    unit: "Cái",
    basePrice: 4200000,
  },
  {
    id: "item-rack-2u",
    code: "RACK-2U-W",
    name: "Tủ Rack treo tường 2U",
    category: "Rack",
    spec: "Kích thước H120 x W550 x D400mm, Thép mạ sơn tĩnh điện chống rỉ, Cửa mica có khóa",
    unit: "Cái",
    basePrice: 650000,
  },
  {
    id: "item-rack-6u",
    code: "RACK-6U-W",
    name: "Tủ Rack treo tường 6U",
    category: "Rack",
    spec: "Kích thước H320 x W550 x D400mm, Quạt tản nhiệt, Thanh nguồn 4 ổ cắm",
    unit: "Cái",
    basePrice: 950000,
  },
  {
    id: "item-rack-9u",
    code: "RACK-9U-W",
    name: "Tủ Rack treo tường 9U",
    category: "Rack",
    spec: "Kích thước H450 x W550 x D500mm, Quạt tản nhiệt, Ổ cắm nguồn đa năng",
    unit: "Cái",
    basePrice: 1350000,
  },
  {
    id: "item-rack-12u",
    code: "RACK-12U-F",
    name: "Tủ Rack đứng tự do 12U",
    category: "Rack",
    spec: "Kích thước H650 x W600 x D600mm, Bánh xe di chuyển, Quạt hút, Khóa an toàn",
    unit: "Cái",
    basePrice: 2200000,
  },
  {
    id: "item-ups-1k",
    code: "UPS-1KVA-OL",
    name: "Bộ lưu điện UPS 1KVA Online",
    category: "UPS",
    spec: "Công suất 1000VA/900W, Sóng sine chuẩn, Thời gian chuyển mạch 0ms",
    unit: "Bộ",
    basePrice: 4500000,
  },
  {
    id: "item-ups-2k",
    code: "UPS-2KVA-OL",
    name: "Bộ lưu điện UPS 2KVA Online",
    category: "UPS",
    spec: "Công suất 2000VA/1800W, Sóng sine chuẩn, Hiển thị LCD thông số điện áp",
    unit: "Bộ",
    basePrice: 7800000,
  },
  {
    id: "item-pdu",
    code: "PDU-06-MC",
    name: "Thanh quản lý nguồn PDU 6 ổ cắm",
    category: "PDU",
    spec: "Chuẩn rack 19 inch, 6 Outlet universal, Cầu chì chống quá tải 16A",
    unit: "Cái",
    basePrice: 450000,
  },
  {
    id: "item-converter",
    code: "CONV-1G-2F",
    name: "Bộ chuyển đổi Quang-Điện Converter Gigabit",
    category: "Converter",
    spec: "Bộ chuyển đổi quang 1 cổng 10/100/1000Mbps sang Single-mode song sợi, 20km",
    unit: "Bộ",
    basePrice: 1100000,
  },
  {
    id: "item-cable-cat6",
    code: "CAB-CAT6-UTP",
    name: "Cáp mạng Cat6 UTP",
    category: "Cable",
    spec: "Cáp đồng Cat6 UTP 4 cặp dây xoắn đôi, Tần số hoạt động lên đến 250MHz",
    unit: "Mét",
    basePrice: 12000,
  },
  {
    id: "item-accessories",
    code: "ACC-PACK-FL",
    name: "Gói phụ kiện thi công (Ống luồn, đầu bấm, băng keo...)",
    category: "Accessories",
    spec: "Hệ thống ống luồn PVC chống cháy, hạt mạng RJ45 mạ vàng, nhãn dán định danh cáp",
    unit: "Hộp",
    basePrice: 350000,
  }
];

export const DEFAULT_SITE_PARAMS: SiteParameters = {
  cableFactor: 2.0,
  cableReserve: 10,
  defaultFloorHeight: 4,
  domeModel: "CAM-DOM-02",
  bulletModel: "CAM-BUL-02",
  maxCamsPerSwitch: 20, // keep some ports safe for other network devices
  uplinkType: "Fiber",
};

export const DEFAULT_HARDWARE_LOGIC: HardwareLogic = {
  switchPreference: "SW24",
  backupHours: 1,
  pduPerRack: 1,
  converterPerUplink: 1,
  cabinetSizeDefault: "2U",
};

export function localCalculateCabinetPlacement(floorCount: number, widthLength: number, heightLength: number): number[] {
  const result: number[] = [];
  const condition = 70; // seeded conditionLength
  let total = widthLength;
  let pivot = 0;
  while (total < condition) {
    total += heightLength;
    if (total <= condition) {
      pivot++;
    }
  }

  let count = 0;
  let subquantity = floorCount;
  while (count <= floorCount) {
    subquantity -= (pivot * 2) - 1;
    if (subquantity < 0) {
      count += Math.floor((floorCount - count) / 2) + 1;
      result.push(count);
      break;
    }
    count += pivot;
    result.push(count);
    count += pivot - 1;
  }
  return result;
}

export function calculateProjectBOQ(
  floorsCount: number,
  horizontalDistance: number,
  verticalDistance: number,
  rackType: "2U" | "6U" | "9U" | "12U",
  siteParams: SiteParameters,
  hardwareLogic: HardwareLogic,
  existingFloorsData?: FloorData[],
  basementsCount: number = 0,
  hasRoof: boolean = false,
  cabinetPlacements?: number[]
): FloorData[] {
  const list: FloorData[] = [];
  const labels: string[] = [];

  // 1. Basements (e.g. B3, B2, B1)
  for (let b = basementsCount; b >= 1; b--) {
    labels.push(`B${b}`);
  }

  // 2. Regular floors (Tầng 1, Tầng 2, ...)
  for (let f = 1; f <= floorsCount; f++) {
    labels.push(`Tầng ${f}`);
  }

  // 3. Roof (Tầng Mái)
  if (hasRoof) {
    labels.push("Tầng Mái");
  }

  // First pass: build floors with basic camera counts and level mapping
  const tempFloorsList = labels.map((label, idx) => {
    const floorIndex = idx; // 0-based ordering
    
    // Find matching floor in existingFloorsData to preserve user input
    const existing = existingFloorsData?.find(
      (ef) => ef.label === label || ef.floorIndex === floorIndex
    );
    const camerasCount = existing ? existing.camerasCount : 0;
    const domeCount = existing ? existing.domeCount : 0;
    const bulletCount = existing ? existing.bulletCount : 0;
    
    // Determine level
    let level = 1;
    if (label.includes("Mái")) {
      level = floorsCount + 1;
    } else {
      const matchT = label.match(/Tầng\s+(\d+)/);
      if (matchT) {
        level = parseInt(matchT[1]);
      } else {
        const matchB = label.match(/B(\d+)/);
        if (matchB) {
          level = 1 - parseInt(matchB[1]);
        }
      }
    }

    return {
      floorIndex,
      label,
      camerasCount,
      domeCount,
      bulletCount,
      level,
    };
  });

  // Determine cabinet levels using passed array or local calculation fallback
  const useCabinetPlacements = true; // Always calculate based on placement logic if available
  const cabinetLevels = (cabinetPlacements && cabinetPlacements.length > 0)
    ? cabinetPlacements
    : localCalculateCabinetPlacement(floorsCount, horizontalDistance, verticalDistance);

  // Helper to find nearest cabinet level
  const getAssignedCabinetLevel = (level: number): number => {
    if (cabinetLevels.length === 0) return level; // each floor is its own cabinet if none exists
    let nearestCabinet = cabinetLevels[0];
    let minDistance = Math.abs(level - nearestCabinet);
    for (let i = 1; i < cabinetLevels.length; i++) {
      const dist = Math.abs(level - cabinetLevels[i]);
      if (dist < minDistance || (dist === minDistance && cabinetLevels[i] < nearestCabinet)) {
        minDistance = dist;
        nearestCabinet = cabinetLevels[i];
      }
    }
    return nearestCabinet;
  };

  // Calculate aggregated cameras for each cabinet level
  const cabinetCameras: Record<number, number> = {};
  cabinetLevels.forEach(lvl => {
    cabinetCameras[lvl] = 0;
  });
  tempFloorsList.forEach(f => {
    const assigned = getAssignedCabinetLevel(f.level);
    if (cabinetCameras[assigned] !== undefined) {
      cabinetCameras[assigned] += f.camerasCount;
    }
  });

  // Second pass: compute equipment and cables
  tempFloorsList.forEach((f) => {
    const isCabinetFloor = cabinetLevels.includes(f.level);

    if (!isCabinetFloor) {
      // Non-cabinet floor: only camera counts, all other equipment/cable quantities are 0 / None
      list.push({
        floorIndex: f.floorIndex,
        label: f.label,
        camerasCount: f.camerasCount,
        domeCount: f.domeCount,
        bulletCount: f.bulletCount,
        cableLength: 0,
        sw24Count: 0,
        sw16Count: 0,
        upsType: "None",
        pduCount: 0,
        convCount: 0,
        cameraQuantityInCabinet: 0,
      });
    } else {
      // Cabinet floor: calculate for this cabinet
      const totalCams = cabinetCameras[f.level] || 0;

      // Calculate Switch count
      let sw24Count = 0;
      let sw16Count = 0;

      const preference = hardwareLogic.switchPreference;
      if (preference === "SW24") {
        sw24Count = Math.ceil(totalCams / 24);
      } else if (preference === "SW16") {
        sw16Count = Math.ceil(totalCams / 16);
      } else {
        // Auto logic
        if (totalCams <= 16 && totalCams > 0) {
          sw16Count = 1;
        } else if (totalCams <= 24) {
          sw24Count = 1;
        } else {
          sw24Count = Math.ceil(totalCams / 24);
        }
      }

      // UPS selection
      let upsType: "1K" | "2K" | "None" = "None";
      if (totalCams > 0) {
        if (totalCams > 16 || sw24Count > 1) {
          upsType = "2K";
        } else {
          upsType = "1K";
        }
      }

      // PDU selection
      const pduCount = (totalCams > 0) ? hardwareLogic.pduPerRack : 0;

      // Converter selection: 1 Converter on remote floors (floorIndex !== basementsCount) if fiber is used and has cameras served
      const convCount = (f.floorIndex !== basementsCount && totalCams > 0 && siteParams.uplinkType === "Fiber") ? 1 : 0;

      // Cable calculation (including horizontal runs and vertical runs for all served cameras, plus vertical backbone)
      let cableLength = 0;
      tempFloorsList.forEach(sf => {
        const assigned = getAssignedCabinetLevel(sf.level);
        if (assigned === f.level) {
          const horizontal = sf.camerasCount * horizontalDistance * siteParams.cableFactor * (1 + siteParams.cableReserve / 100);
          const vertical = sf.camerasCount * Math.abs(sf.level - f.level) * verticalDistance * (1 + siteParams.cableReserve / 100);
          cableLength += horizontal + vertical;
        }
      });
      // Add vertical backbone for the cabinet itself back to main server room (level 1)
      const backbone = f.level !== 1 ? Math.abs(f.level - 1) * verticalDistance : 0;
      cableLength += backbone;

      list.push({
        floorIndex: f.floorIndex,
        label: f.label,
        camerasCount: f.camerasCount,
        domeCount: f.domeCount,
        bulletCount: f.bulletCount,
        cableLength: Math.round(cableLength),
        sw24Count,
        sw16Count,
        upsType,
        pduCount,
        convCount,
        cameraQuantityInCabinet: totalCams,
      });
    }
  });

  return list;
}

export const INITIAL_PROJECTS: Project[] = [
  {
    id: "proj-alpha",
    name: "Project Alpha",
    description: "Surveillance Infrastructure for high-tech headquarters including multi-floor real-time monitoring.",
    createdAt: "2026-06-25T10:00:00Z",
    towers: [
      {
        id: "tower-alpha-1",
        name: "Tháp Alpha 1",
        description: "Tháp chính Alpha",
        createdAt: "2026-06-25T10:00:00Z",
        floorsCount: 10,
        basementsCount: 0,
        hasRoof: false,
        horizontalDistance: 50,
        verticalDistance: 4,
        rackType: "2U",
        standardPresetId: "std-commercial",
        siteParams: { ...DEFAULT_SITE_PARAMS },
        hardwareLogic: { ...DEFAULT_HARDWARE_LOGIC },
        floorsData: [
          { floorIndex: 0, label: "Tầng 1", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 1, label: "Tầng 2", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 2, label: "Tầng 3", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 3, label: "Tầng 4", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 4, label: "Tầng 5", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 5, label: "Tầng 6", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 6, label: "Tầng 7", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 7, label: "Tầng 8", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 8, label: "Tầng 9", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 9, label: "Tầng 10", camerasCount: 20, domeCount: 10, bulletCount: 10, cableLength: 40, sw24Count: 2, sw16Count: 0, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
        ],
        customPrices: {},
      }
    ]
  },
  {
    id: "proj-beta",
    name: "Project Factory Campus",
    description: "Industrial grade heavy surveillance installation for warehouse perimeters and conveyor lines.",
    createdAt: "2026-06-26T00:30:00Z",
    towers: [
      {
        id: "tower-beta-1",
        name: "Tháp Beta 1",
        description: "Tháp chính Beta",
        createdAt: "2026-06-26T00:30:00Z",
        floorsCount: 4,
        basementsCount: 0,
        hasRoof: false,
        horizontalDistance: 120,
        verticalDistance: 6,
        rackType: "6U",
        standardPresetId: "std-industrial",
        siteParams: {
          cableFactor: 2.5,
          cableReserve: 15,
          defaultFloorHeight: 6,
          domeModel: "CAM-DOM-02",
          bulletModel: "CAM-BUL-02",
          maxCamsPerSwitch: 16,
          uplinkType: "Fiber",
        },
        hardwareLogic: {
          switchPreference: "Auto",
          backupHours: 2,
          pduPerRack: 1,
          converterPerUplink: 1,
          cabinetSizeDefault: "6U",
        },
        floorsData: [
          { floorIndex: 0, label: "Xưởng A", camerasCount: 32, domeCount: 10, bulletCount: 22, cableLength: 80, sw24Count: 1, sw16Count: 1, upsType: "2K", pduCount: 1, convCount: 0, cameraQuantityInCabinet: 0 },
          { floorIndex: 1, label: "Xưởng B", camerasCount: 28, domeCount: 8, bulletCount: 20, cableLength: 70, sw24Count: 1, sw16Count: 1, upsType: "2K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 2, label: "Kho Thành Phẩm", camerasCount: 16, domeCount: 4, bulletCount: 12, cableLength: 40, sw24Count: 0, sw16Count: 1, upsType: "1K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
          { floorIndex: 3, label: "Khối Văn Phòng", camerasCount: 24, domeCount: 18, bulletCount: 6, cableLength: 60, sw24Count: 1, sw16Count: 0, upsType: "2K", pduCount: 1, convCount: 1, cameraQuantityInCabinet: 0 },
        ],
        customPrices: {},
      }
    ]
  }
];
