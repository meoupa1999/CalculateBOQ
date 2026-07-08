/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

export interface FloorData {
  floorIndex: number; // 0-based
  label: string; // e.g., "Tầng 1"
  camerasCount: number;
  domeCount: number;
  bulletCount: number;
  cableLength: number; // in meters
  sw24Count: number;
  sw16Count: number;
  upsType: "1K" | "2K" | "None";
  pduCount: number;
  convCount: number;
  cameraQuantityInCabinet?: number;
  fromIndex?: number;
  toIndex?: number;
  isCabinetPlaced?: boolean;
}

export interface SiteParameters {
  cableFactor: number; // average horizontal distance multiplier
  cableReserve: number; // percentage (e.g. 10 for 10%)
  defaultFloorHeight: number; // in meters
  domeModel: string;
  bulletModel: string;
  maxCamsPerSwitch: number;
  uplinkType: "Fiber" | "Copper";
}

export interface HardwareLogic {
  switchPreference: "SW24" | "SW16" | "Auto";
  backupHours: number; // e.g., 1 or 2 hours
  pduPerRack: number;
  converterPerUplink: number;
  cabinetSizeDefault: "2U" | "6U" | "10U" | "20U";
}

export interface InventoryItem {
  id: string;
  code: string;
  name: string;
  category: "Camera" | "Switch" | "Rack" | "UPS" | "PDU" | "Converter" | "Cable" | "Accessories";
  spec: string;
  unit: string;
  basePrice: number; // in VND
}

export interface StandardPreset {
  id: string;
  name: string;
  description: string;
  cameraRatio: number; // Percentage of dome cameras (e.g., 50 for 50% dome, 50% bullet)
  switchPreference: "SW24" | "SW16" | "Auto";
  upsType: "1K" | "2K" | "None";
  cableFactor: number;
}

export interface Tower {
  id: string;
  name: string;
  description: string;
  createdAt: string;
  floorsCount: number;
  basementsCount?: number;
  hasRoof?: boolean;
  horizontalDistance: number; // average run to IT room per floor (m)
  verticalDistance: number; // vertical cable run per floor (m)
  rackType: "2U" | "6U" | "10U" | "20U";
  standardPresetId: string;
  floorsData: FloorData[];
  siteParams: SiteParameters;
  hardwareLogic: HardwareLogic;
  customPrices: Record<string, number>; // itemId -> custom override price
}

export interface Project {
  id: string;
  name: string;
  description: string;
  createdAt: string;
  towers: Tower[];
}
