/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from "react";
import { 
  Layers, 
  Settings, 
  HelpCircle, 
  Activity, 
  FileSpreadsheet, 
  Download, 
  Plus, 
  Trash2, 
  Copy, 
  Save, 
  RefreshCw, 
  Sliders, 
  Cpu, 
  Coins, 
  FileText, 
  Check, 
  AlertCircle, 
  Briefcase, 
  Warehouse, 
  SlidersHorizontal,
  ChevronRight,
  Info,
  Building,
  ArrowRight,
  FileDown,
  Printer,
  CheckCircle2,
  Trash
} from "lucide-react";
import { motion, AnimatePresence } from "motion/react";

import { BASE_PRESETS, BASE_INVENTORY, DEFAULT_SITE_PARAMS, DEFAULT_HARDWARE_LOGIC, calculateProjectBOQ, INITIAL_PROJECTS, localCalculateCabinetPlacement } from "./data";
import { Project, Tower, FloorData, SiteParameters, HardwareLogic, InventoryItem, StandardPreset, SystemConfig } from "./types";

interface BOMItem {
  stt: string;
  name: string;
  desc?: string;
  unit: string;
  field?: string;
  noteKey: string;
  subLevel?: boolean;
}

const BOM_ITEMS: BOMItem[] = [
  // I. HẠNG MỤC VẬT TƯ CHÍNH VÀ GIÁM SÁT VÀ ĐỊNH TUYẾN
  { stt: "", name: "I. HẠNG MỤC VẬT TƯ CHÍNH VÀ GIÁM SÁT VÀ ĐỊNH TUYẾN", unit: "", noteKey: "" },
  { stt: "1", name: "Camera IP Dome 2MP HIKVISION DS-2CD1121G0-I", unit: "Cái", field: "camDomeQuantity", noteKey: "cat1_1" },
  { stt: "2", name: "Camera IP thân 2MP HIKVISION DS-2CD1021G0-I", unit: "Cái", field: "camBulletQuantity", noteKey: "cat1_2" },
  { stt: "3", name: "Đầu ghi hình camera IP 32 kênh HIKVISION DS-7732NXI-K4", unit: "Cái", field: "recorder32Quantity", noteKey: "cat1_3" },
  { stt: "4", name: "Đầu ghi hình camera IP 16 kênh", unit: "Cái", field: "recorder16Quantity", noteKey: "cat1_4" },
  { stt: "5", name: "Ổ Cứng 10T WESTERN", unit: "Cái", field: "hardDiskQuantity", noteKey: "cat1_5" },
  { stt: "6", name: "Switch Hikvision POE 24 cổng DS-3E1326P-EI", unit: "Cái", field: "swich24POEQuantity", noteKey: "cat1_6" },
  { stt: "7", name: "Switch Hikvision POE 16 cổng DS-3E1318P-EI", unit: "Cái", field: "swich16POEQuantity", noteKey: "cat1_7" },
  { stt: "8", name: "Switch 16 port CISCO CBS110-16T-EU", unit: "Cái", field: "swich16CISCOQuantity", noteKey: "cat1_8" },
  { stt: "9", name: "Switch 24 port CISCO", unit: "Cái", field: "swich24CISCOQuantity", noteKey: "cat1_9" },
  { stt: "10", name: "Màn hình quan sát 43 inch SamSung(khung kê + HDMI (15m))", unit: "Bộ", field: "obserScreenQuantity", noteKey: "cat1_10" },

  // II. HẠNG MỤC TRUYỀN DẪN
  { stt: "", name: "II. HẠNG MỤC TRUYỀN DẪN", unit: "", noteKey: "" },
  { stt: "1", name: "Cáp quang 4FO", unit: "Mét", field: "fiberCableQuantity", noteKey: "cat2_1" },
  { stt: "2", name: "Cáp mạng Cat5E", unit: "Mét", field: "cableQuantity", noteKey: "cat2_2" },
  { stt: "3", name: "Bộ chuyển đổi quang điện Gigabit GNETCOM 10/100/1000M GNC-2111S-20A/B", unit: "Bộ", field: "converterQuantity", noteKey: "cat2_3" },
  { stt: "4", name: "Tủ mạng rack 2U", unit: "Bộ", field: "cabinet2UQuantity", noteKey: "cat2_4" },
  { stt: "5", name: "Tủ mạng rack 6U", unit: "Bộ", field: "cabinet6UQuantity", noteKey: "cat2_5" },
  { stt: "6", name: "Tủ mạng rack 10U (Có bánh xe)", unit: "Bộ", field: "cabinet10UQuantity", noteKey: "cat2_6" },
  { stt: "7", name: "Tủ mạng rack 32U", unit: "Bộ", field: "cabinet32UQuantity", noteKey: "cat2_8" },
  { stt: "8", name: "Tủ mạng rack 42U", unit: "Bộ", field: "cabinet42UQuantity", noteKey: "cat2_9" },
  { stt: "9", name: "ODF 12FO SC/UPC (Full Phụ kiện)", unit: "Cái", field: "odf12FOQuantity", noteKey: "cat2_10" },
  { stt: "10", name: "ODF 24FO SC/UPC (Full Phụ kiện)", unit: "Cái", field: "odf24FOQuantity", noteKey: "cat2_11" },

  // III. HẠNG MỤC ĐIỆN
  { stt: "", name: "III. HẠNG MỤC ĐIỆN", unit: "", noteKey: "" },
  { stt: "1", name: "Dây điện CVV 2x2.5", unit: "Mét", field: "cvvCable", noteKey: "cat3_1" },
  { stt: "2", name: "Thanh nguồn PDU đa năng 6 ổ cắm 3 chấu chuẩn 19\"", unit: "Cái", field: "pduQuantity", noteKey: "cat3_2" },

  // IV. HẠNG MỤC NGUỒN DỰ PHÒNG
  { stt: "", name: "IV. HẠNG MỤC NGUỒN DỰ PHÒNG", unit: "", noteKey: "" },
  { stt: "1", name: "Nguồn lưu điện UPS ARES Model AR610 1000VA/800W", unit: "Bộ", field: "ups1000Quantity", noteKey: "cat4_1" },
  { stt: "3", name: "Nguồn lưu điện UPS ARES Model AR630 3000VA-2400W", unit: "Bộ", field: "ups3000Quantity", noteKey: "cat4_3" },

  // V. VẬT TƯ PHỤ
  { stt: "", name: "V. VẬT TƯ PHỤ", unit: "", noteKey: "" },
  { stt: "1", name: "Vật tư phụ", desc: "Bao gồm ống điện, ruột gà, vít, tacke...", unit: "Gói", noteKey: "cat5_1" },
  { stt: "1.1", name: "Vật tư phụ kết nối", unit: "Gói", noteKey: "cat5_1_1" },
  { stt: "-", name: "Đầu mạng AMP Cat 5", unit: "Cái", field: "ampCatQuantity", noteKey: "cat5_1_1_sub1", subLevel: true },
  { stt: "-", name: "Dây nhảy quang SC/UPC SC/UPC 3M", unit: "Sợi", field: "fiberOpticalPatchQuantity", noteKey: "cat5_1_1_sub2", subLevel: true },
  { stt: "-", name: "ODF 4FO SC/UPC - SC/UPC (Full phụ kiện)", unit: "Bộ", field: "odf4FOQuantity", noteKey: "cat5_1_1_sub3", subLevel: true },
  { stt: "-", name: "Dây nhảy mạng Cat5", unit: "Sợi", field: "patchCordQuantity", noteKey: "cat5_1_1_sub4", subLevel: true },
  { stt: "-", name: "Thanh quản lý cáp mạng 19inch", unit: "Cái", field: "cablemanageQuantity", noteKey: "cat5_1_1_sub5", subLevel: true },
  { stt: "1.2", name: "Vật tư phụ thi công", unit: "Gói", noteKey: "cat5_1_2" },
  { stt: "1.2.1", name: "Ruột gà phi 20", unit: "Mét", field: "chickenTubeQuantity", noteKey: "cat5_1_2_1", subLevel: true },
  { stt: "1.2.2", name: "Ống điện D20", unit: "Mét", field: "electricTubeQuantity", noteKey: "cat5_1_2_2", subLevel: true },

  // VI. CHI PHÍ LẮP ĐẶT
  { stt: "", name: "VI. CHI PHÍ LẮP ĐẶT", unit: "", noteKey: "" },
  { stt: "1", name: "Chi phí lắp đặt", desc: "Thi công trọn gói và hướng dẫn vận hành", unit: "Gói", noteKey: "cat6_1" },
  { stt: "1.1", name: "Nhân công Cấu hình thiết lập", unit: "Công", noteKey: "cat6_1_1" },
  { stt: "-", name: "Thiết lập cấu hình", unit: "Công", noteKey: "cat6_1_1_sub1", subLevel: true },
  { stt: "-", name: "Hồ sơ hướng dẫn", unit: "Công", noteKey: "cat6_1_1_sub2", subLevel: true },
  { stt: "-", name: "Kiểm thử T&C", unit: "Công", noteKey: "cat6_1_1_sub3", subLevel: true },
  { stt: "-", name: "Dự trù Thay đổi cấu hình phát sinh", unit: "Công", noteKey: "cat6_1_1_sub4", subLevel: true },
  { stt: "-", name: "Nghiệm thu", unit: "Công", noteKey: "cat6_1_1_sub5", subLevel: true },
  { stt: "-", name: "Bảo hành thiết lập", unit: "Công", noteKey: "cat6_1_1_sub6", subLevel: true },
  { stt: "1.2", name: "Triển khai", unit: "Công", noteKey: "cat6_1_2" }
];

export default function App() {
  // Navigation active state
  // "dashboard" | "parameters" | "logic" | "cost" | "reports"
  const [activeNav, setActiveNav] = useState<"dashboard" | "parameters" | "logic" | "cost" | "reports">("dashboard");

  // Top header tabs state
  // "app" | "projects" | "inventory" | "standards" | "settings"
  const [activeTab, setActiveTab] = useState<"app" | "projects" | "inventory" | "standards" | "settings">("app");

  // Inventory form states
  const [newItemCode, setNewItemCode] = useState("");
  const [newItemName, setNewItemName] = useState("");
  const [newItemCategory, setNewItemCategory] = useState<"Camera" | "Switch" | "Rack" | "UPS" | "PDU" | "Converter" | "Cable" | "Accessories">("Camera");
  const [newItemSpec, setNewItemSpec] = useState("");
  const [newItemUnit, setNewItemUnit] = useState("Cái");
  const [newItemPrice, setNewItemPrice] = useState(100000);

  const API_BASE = "/api";

  // Configuration settings state
  const CONFIG_ID = "a2b0a797-8ff2-4a79-ac5d-78525bd25e90";
  const [systemConfig, setSystemConfig] = useState<SystemConfig | null>(null);
  const [isLoadingConfig, setIsLoadingConfig] = useState(false);

  const fetchSystemConfig = async () => {
    setIsLoadingConfig(true);
    try {
      const res = await fetch(`${API_BASE}/configs/${CONFIG_ID}`);
      if (res.ok) {
        const data = await res.json();
        setSystemConfig(data);
      } else {
        console.error("Failed to fetch system config");
      }
    } catch (err) {
      console.error("Error fetching system config", err);
    } finally {
      setIsLoadingConfig(false);
    }
  };

  const [isSavingConfig, setIsSavingConfig] = useState(false);

  const handleSaveConfig = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!systemConfig) return;
    setIsSavingConfig(true);
    try {
      const res = await fetch(`${API_BASE}/configs/${CONFIG_ID}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          conditionLength: systemConfig.conditionLength,
          sw24ConditionQuanity: systemConfig.sw24ConditionQuanity,
          sw16ConditionQuanity: systemConfig.sw16ConditionQuanity,
          ups: systemConfig.ups,
          pdu: systemConfig.pdu,
          converter: systemConfig.converter
        })
      });
      if (res.ok) {
        addToast("Cập nhật cấu hình hệ thống thành công!", "success");
        fetchSystemConfig();
      } else {
        addToast("Lỗi khi cập nhật cấu hình!", "error");
      }
    } catch (err) {
      console.error(err);
      addToast("Lỗi kết nối khi cập nhật cấu hình!", "error");
    } finally {
      setIsSavingConfig(false);
    }
  };

  // Load projects from backend
  const [projects, setProjects] = useState<Project[]>([]);
  const [activeProjectId, setActiveProjectId] = useState<string>("");
  const [activeTowerId, setActiveTowerId] = useState<string>("");
  const [isSummaryTabActive, setIsSummaryTabActive] = useState<boolean>(false);
  const [selectedTowersSummary, setSelectedTowersSummary] = useState<Record<string, boolean>>({});
  const [summaryBomData, setSummaryBomData] = useState<any>(null);
  const [isCalculatingSummary, setIsCalculatingSummary] = useState<boolean>(false);
  const [isExportingExcel, setIsExportingExcel] = useState<boolean>(false);

  const [leftWidth, setLeftWidth] = useState<number>(50);
  const [isDragging, setIsDragging] = useState<boolean>(false);
  const [isXl, setIsXl] = useState<boolean>(window.innerWidth >= 1280);

  useEffect(() => {
    const media = window.matchMedia("(min-width: 1280px)");
    const listener = () => setIsXl(media.matches);
    media.addEventListener("change", listener);
    return () => media.removeEventListener("change", listener);
  }, []);

  useEffect(() => {
    if (!isDragging) return;

    const handleMove = (clientX: number) => {
      const container = document.getElementById("bom-split-container");
      if (!container) return;
      const rect = container.getBoundingClientRect();
      const newWidth = ((clientX - rect.left) / rect.width) * 100;
      setLeftWidth(Math.min(Math.max(newWidth, 20), 80));
    };

    const handleMouseMove = (e: MouseEvent) => {
      handleMove(e.clientX);
    };

    const handleTouchMove = (e: TouchEvent) => {
      if (e.touches.length > 0) {
        handleMove(e.touches[0].clientX);
      }
    };

    const handleMouseUp = () => {
      setIsDragging(false);
    };

    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);
    document.addEventListener("touchmove", handleTouchMove);
    document.addEventListener("touchend", handleMouseUp);

    document.body.style.cursor = "col-resize";
    document.body.style.userSelect = "none";

    return () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
      document.removeEventListener("touchmove", handleTouchMove);
      document.removeEventListener("touchend", handleMouseUp);
      document.body.style.cursor = "";
      document.body.style.userSelect = "";
    };
  }, [isDragging]);

  const startDrag = (e: React.MouseEvent | React.TouchEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const fetchProjects = async (selectNewestId?: string) => {
    try {
      const response = await fetch(`${API_BASE}/projects?page=1&size=100`);
      if (!response.ok) throw new Error("Failed to fetch projects");
      const pageData = await response.json();
      const backendList = pageData.content || [];

      // Map backend list to Project list
      const mappedList: Project[] = await Promise.all(
        backendList.map(async (p: any) => {
          let mappedTowers: Tower[] = [];
          try {
            const towersRes = await fetch(`${API_BASE}/towers?projectId=${p.id}&page=1&size=100`);
            if (towersRes.ok) {
              const towersPage = await towersRes.json();
              const backendTowers = towersPage.content || [];
              mappedTowers = backendTowers.map((t: any) => {
                const selectedPreset = BASE_PRESETS.find(pr => pr.id === t.configId) || BASE_PRESETS[0];
                
                // Try to load custom floor configs from specialName JSON if present
                let parsedFloors = [];
                if (t.specialName) {
                  try {
                    parsedFloors = JSON.parse(t.specialName);
                  } catch (e) {
                    console.error("Error parsing specialName floorsData", e);
                  }
                }
                
                if ((!parsedFloors || parsedFloors.length === 0) && t.floorCount > 0) {
                  // Fallback: calculate default floors only if floorCount > 0
                  const projectParams: SiteParameters = {
                    ...DEFAULT_SITE_PARAMS,
                    cableFactor: selectedPreset.cableFactor,
                  };
                  const projectLogic: HardwareLogic = {
                    ...DEFAULT_HARDWARE_LOGIC,
                    switchPreference: selectedPreset.switchPreference,
                  };
                  parsedFloors = calculateProjectBOQ(
                    t.floorCount,
                    t.widthLength ?? 50,
                    t.heightLength ?? 4,
                    "2U",
                    projectParams,
                    projectLogic,
                    [],
                    t.basementCount ?? 0,
                    t.hasRoof ?? false
                  );
                }

                return {
                  id: t.id,
                  name: t.name,
                  description: t.description || "Tháp chính của dự án",
                  createdAt: t.audit?.createdAt || new Date().toISOString(),
                  floorsCount: t.floorCount ?? 0,
                  basementsCount: t.basementCount ?? 0,
                  hasRoof: t.hasRoof ?? false,
                  horizontalDistance: t.widthLength ?? 50,
                  verticalDistance: t.heightLength ?? 4,
                  rackType: "2U" as const,
                  quantity2U: t.quantity2U || 1,
                  standardPresetId: selectedPreset.id,
                  siteParams: {
                    ...DEFAULT_SITE_PARAMS,
                    cableFactor: selectedPreset.cableFactor,
                  },
                  hardwareLogic: {
                    ...DEFAULT_HARDWARE_LOGIC,
                    switchPreference: selectedPreset.switchPreference,
                  },
                  floorsData: parsedFloors,
                  customPrices: {},
                };
              });
            }
          } catch (e) {
            console.error("Error fetching towers for project", p.id, e);
          }

          return {
            id: p.id,
            name: p.name,
            description: p.description || "Dự án giám sát hạ tầng mới",
            createdAt: p.audit?.createdAt || new Date().toISOString(),
            towers: mappedTowers,
          };
        })
      );
      setProjects(mappedList);

      if (mappedList.length > 0) {
        let activeProjId = "";
        if (selectNewestId) {
          activeProjId = selectNewestId;
        } else {
          activeProjId = activeProjectId && mappedList.some((item) => item.id === activeProjectId)
            ? activeProjectId
            : mappedList[0].id;
        }
        setActiveProjectId(activeProjId);
        
        const activeProjObj = mappedList.find(p => p.id === activeProjId);
        if (activeProjObj && activeProjObj.towers.length > 0) {
          setActiveTowerId((prev) => {
            if (prev && activeProjObj.towers.some((t) => t.id === prev)) {
              return prev;
            }
            return activeProjObj.towers[0].id;
          });
        } else {
          setActiveTowerId("");
        }
      } else {
        setActiveProjectId("");
        setActiveTowerId("");
      }
    } catch (error) {
      console.error("Error fetching projects", error);
      addToast("Không thể kết nối đến Backend Server!", "error");
    }
  };

  useEffect(() => {
    fetchProjects();
    fetchSystemConfig();
  }, []);

  // Global base inventory (can be edited globally under Inventory tab)
  const [globalInventory, setGlobalInventory] = useState<InventoryItem[]>(BASE_INVENTORY);

  // New project creation state
  const [isCreatingProject, setIsCreatingProject] = useState(false);
  const [newProjectName, setNewProjectName] = useState("");
  const [newProjectDesc, setNewProjectDesc] = useState("");
  const [newProjectFloors, setNewProjectFloors] = useState(5);
  const [newProjectPreset, setNewProjectPreset] = useState("std-commercial");

  // State for left template table notes (Ghi chú)
  const [leftTableNotes, setLeftTableNotes] = useState<Record<string, string>>({
    cat2_2: "LS/Panduit/Comspose",
    cat3_2: "Dintek/Vietrack/TMC",
    cat5_1_1_sub4: "Dintek/AMP/Panduit",
    cat5_1_1_sub5: "Dintek/AMP/Panduit",
    cat5_1_2: "Dự trù 7 triệu chưa bao gồm 1.2.1",
    cat6_1_1: "6 Ngày hoặc chạy song song với triển khai hạ tầng",
  });

  // Toast alert notifications
  const [toasts, setToasts] = useState<{ id: string; message: string; type: "success" | "info" | "error" }[]>([]);

  // Selected floors for bulk editing
  const [selectedFloorIndexes, setSelectedFloorIndexes] = useState<number[]>([]);
  const [lastSelectedFloorIndex, setLastSelectedFloorIndex] = useState<number | null>(null);

  // Clear selected floors when switching projects
  useEffect(() => {
    setSelectedFloorIndexes([]);
    setLastSelectedFloorIndex(null);
  }, [activeProjectId]);

  const handleSelectAllFloors = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.checked) {
      setSelectedFloorIndexes(activeTower?.floorsData.map((f) => f.floorIndex));
    } else {
      setSelectedFloorIndexes([]);
    }
    setLastSelectedFloorIndex(null);
  };

  const handleToggleSelectFloor = (floorIndex: number, event?: React.MouseEvent) => {
    event?.stopPropagation();
    const isShiftKey = event ? event.shiftKey : false;
    
    setSelectedFloorIndexes((prev) => {
      let newSelection = [...prev];
      
      if (isShiftKey && lastSelectedFloorIndex !== null) {
        const floors = activeTower?.floorsData;
        const startIdx = floors.findIndex(f => f.floorIndex === lastSelectedFloorIndex);
        const endIdx = floors.findIndex(f => f.floorIndex === floorIndex);
        
        if (startIdx !== -1 && endIdx !== -1) {
          const minIdx = Math.min(startIdx, endIdx);
          const maxIdx = Math.max(startIdx, endIdx);
          
          const rangeFloorIndices = floors
            .slice(minIdx, maxIdx + 1)
            .map(f => f.floorIndex);
          
          const isSelecting = prev.includes(lastSelectedFloorIndex);
          
          if (isSelecting) {
            rangeFloorIndices.forEach(idx => {
              if (!newSelection.includes(idx)) {
                newSelection.push(idx);
              }
            });
          } else {
            newSelection = newSelection.filter(idx => !rangeFloorIndices.includes(idx));
          }
        }
      } else {
        if (prev.includes(floorIndex)) {
          newSelection = prev.filter((idx) => idx !== floorIndex);
        } else {
          newSelection = [...prev, floorIndex];
        }
      }
      
      return newSelection;
    });
    
    setLastSelectedFloorIndex(floorIndex);
  };

  const handleBulkUpdateCamera = (camsCount: number) => {
    if (selectedFloorIndexes.length === 0 || !activeTower) return;

    const baseFloors = activeTower.floorsData.map((f) => {
      if (selectedFloorIndexes.includes(f.floorIndex)) {
        const cams = camsCount;
        const dome = Math.round(cams * 0.5);
        const bullet = cams - dome;
        return {
          ...f,
          camerasCount: cams,
          domeCount: dome,
          bulletCount: bullet,
        };
      }
      return f;
    });

    const recalculatedFloors = calculateProjectBOQ(
      activeTower.floorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      activeTower.siteParams,
      activeTower.hardwareLogic,
      baseFloors,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      cabinetPlacements
    );

    // In manual mode, sync the camera updates to the manualGroups allocations
    let nextGroups = manualGroups;
    if (calculationMode === "manual") {
      nextGroups = manualGroups.map((g) => {
        let groupChanged = false;
        const newCabinets = g.cabinets.map((cab) => {
          let cabinetChanged = false;
          const newAllocations = cab.allocations.map((alloc) => {
            if (selectedFloorIndexes.includes(alloc.floorIndex)) {
              cabinetChanged = true;
              groupChanged = true;
              
              const cams = camsCount;
              const dome = Math.round(cams * 0.5);
              const bullet = cams - dome;
              
              return {
                ...alloc,
                domeCount: dome,
                bulletCount: bullet
              };
            }
            return alloc;
          });
          if (cabinetChanged) {
            return {
              ...cab,
              allocations: newAllocations
            };
          }
          return cab;
        });
        if (groupChanged) {
          return {
            ...g,
            cabinets: newCabinets
          };
        }
        return g;
      });
      setManualGroups(nextGroups);
    }

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                floorsData: recalculatedFloors,
                manualGroups: nextGroups,
              };
            }
            return t;
          });

          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    fetchCabinetPlacement(
      activeTower.floorsCount,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      recalculatedFloors,
      calculationMode,
      nextGroups,
      activeTower.quantity2U || 1
    );

    addToast(`Đồng bộ ${camsCount} camera cho ${selectedFloorIndexes.length} tầng thành công!`, "success");
    setSelectedFloorIndexes([]); // Clear selection after apply
  };

  const addToast = (message: string, type: "success" | "info" | "error" = "success") => {
    const id = Date.now().toString() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4000);
  };

  // Find active project
  const activeProject = projects.find((p) => p.id === activeProjectId) || projects[0];

  // Find active tower
  const activeTower = isSummaryTabActive ? null : (activeProject?.towers?.find((t) => t.id === activeTowerId) || activeProject?.towers?.[0]);

  // Temporary edit states for current tower top inputs (to be committed on "Tính toán BOQ" click)
  const [tempFloors, setTempFloors] = useState(activeTower?.floorsCount || 5);
  const [tempBasements, setTempBasements] = useState(activeTower?.basementsCount || 0);
  const [tempHasRoof, setTempHasRoof] = useState(activeTower?.hasRoof || false);
  const [tempH, setTempH] = useState(activeTower?.horizontalDistance || 50);
  const [tempV, setTempV] = useState(activeTower?.verticalDistance || 4);
  const [tempRack, setTempRack] = useState<"2U" | "6U" | "10U" | "20U">(activeTower?.rackType || "2U");
  const [tempQuantity2U, setTempQuantity2U] = useState<number>(activeTower?.quantity2U || 1);

  // State to store cabinet placement floors (indices of upper floors that have cabinets)
  const [cabinetPlacements, setCabinetPlacements] = useState<number[]>([]);
  const [bomData, setBomData] = useState<any>(null);
  const [customBOMOverrides, setCustomBOMOverrides] = useState<Record<string, Record<string, number>>>({});

  // Dual-mode calculation state
  const [calculationMode, setCalculationMode] = useState<"auto" | "manual">("auto");
  const [manualGroups, setManualGroups] = useState<{
    cabinetIndex: number;
    cabinets: {
      id: string;
      type: string;
      quantity2U?: number;
      allocations: {
        floorIndex: number;
        domeCount: number;
        bulletCount: number;
      }[];
    }[];
  }[]>([]);
  const [activeCabinetIndex, setActiveCabinetIndex] = useState<number | null>(null);
  const [editingCabinetIndex, setEditingCabinetIndex] = useState<number | null>(null);
  const [viewingFloorConnectionDetail, setViewingFloorConnectionDetail] = useState<number | null>(null);
  const [tempCabinets, setTempCabinets] = useState<{
    id: string;
    type: string;
    quantity2U?: number;
    allocations: {
      floorIndex: number;
      domeCount: number;
      bulletCount: number;
    }[];
  }[]>([]);

  // Bulk selection states for linked floors within the cabinet config popup
  const [selectedAllocIds, setSelectedAllocIds] = useState<string[]>([]);
  const [lastSelectedAllocId, setLastSelectedAllocId] = useState<string | null>(null);

  useEffect(() => {
    setSelectedAllocIds([]);
    setLastSelectedAllocId(null);
  }, [editingCabinetIndex]);

  const handleToggleSelectAlloc = (cabIdx: number, allocIdx: number, event?: React.MouseEvent) => {
    event?.stopPropagation();
    const id = `${cabIdx}_${allocIdx}`;
    const isShiftKey = event ? event.shiftKey : false;

    setSelectedAllocIds((prev) => {
      let newSelection = [...prev];

      if (isShiftKey && lastSelectedAllocId !== null) {
        const allAllocs: { cabIdx: number; allocIdx: number; id: string }[] = [];
        tempCabinets.forEach((c, cI) => {
          c.allocations.forEach((a, aI) => {
            allAllocs.push({ cabIdx: cI, allocIdx: aI, id: `${cI}_${aI}` });
          });
        });

        const startIdx = allAllocs.findIndex(item => item.id === lastSelectedAllocId);
        const endIdx = allAllocs.findIndex(item => item.id === id);

        if (startIdx !== -1 && endIdx !== -1) {
          const minIdx = Math.min(startIdx, endIdx);
          const maxIdx = Math.max(startIdx, endIdx);

          const rangeIds = allAllocs
            .slice(minIdx, maxIdx + 1)
            .map(item => item.id);

          const isSelecting = prev.includes(lastSelectedAllocId);

          if (isSelecting) {
            rangeIds.forEach(rId => {
              if (!newSelection.includes(rId)) {
                newSelection.push(rId);
              }
            });
          } else {
            newSelection = newSelection.filter(rId => !rangeIds.includes(rId));
          }
        }
      } else {
        if (newSelection.includes(id)) {
          newSelection = newSelection.filter(item => item !== id);
        } else {
          newSelection.push(id);
        }
      }

      return newSelection;
    });

    setLastSelectedAllocId(id);
  };

  const syncFloorsWithManualGroups = (currentFloors: FloorData[], groups: any[]) => {
    const allocMap = new Map<number, { dome: number; bullet: number }>();
    groups.forEach((g) => {
      g.cabinets.forEach((c: any) => {
        c.allocations.forEach((a: any) => {
          const key = a.floorIndex;
          const existing = allocMap.get(key) || { dome: 0, bullet: 0 };
          allocMap.set(key, {
            dome: existing.dome + a.domeCount,
            bullet: existing.bullet + a.bulletCount,
          });
        });
      });
    });

    return currentFloors.map((f) => {
      if (allocMap.has(f.floorIndex)) {
        const alloc = allocMap.get(f.floorIndex)!;
        return {
          ...f,
          domeCount: alloc.dome,
          bulletCount: alloc.bullet,
          camerasCount: alloc.dome + alloc.bullet,
        };
      }
      return f;
    });
  };

  const getFloorConnections = (floorIndex: number) => {
    const connections: {
      cabinetFloorIndex: number;
      cabinetId: string;
      cabinetType: string;
      domeCount: number;
      bulletCount: number;
    }[] = [];

    const floors = activeTower?.floorsData || [];
    
    // 1. Try finding from API response data
    floors.forEach((fl) => {
      if (fl.isCabinetPlaced && fl.cabinets) {
        fl.cabinets.forEach((cab) => {
          if (cab.allocations) {
            const match = cab.allocations.find((a) => a.floorIndex === floorIndex);
            if (match) {
              connections.push({
                cabinetFloorIndex: fl.floorIndex,
                cabinetId: cab.cabinetId,
                cabinetType: cab.cabinetType,
                domeCount: match.domeCount,
                bulletCount: match.bulletCount,
              });
            }
          }
        });
      }
    });

    // 2. If no connections found (e.g. before API response), fall back to manualGroups state
    if (connections.length === 0) {
      manualGroups.forEach((g) => {
        if (g.cabinets) {
          g.cabinets.forEach((cab: any) => {
            if (cab.allocations) {
              const match = cab.allocations.find((a: any) => a.floorIndex === floorIndex);
              if (match) {
                connections.push({
                  cabinetFloorIndex: g.cabinetIndex,
                  cabinetId: cab.id,
                  cabinetType: cab.type,
                  domeCount: match.domeCount,
                  bulletCount: match.bulletCount,
                });
              }
            }
          });
        }
      });
    }

    return connections;
  };

  const updateTowerFloorsData = (newFloorsData: FloorData[], nextGroups = manualGroups, nextMode = calculationMode) => {
    if (!activeTower || !activeProject) return;
    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower.id) {
              return {
                ...t,
                floorsData: newFloorsData,
                manualGroups: nextGroups,
                calculationMode: nextMode,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    fetchCabinetPlacement(
      tempFloors,
      tempBasements,
      tempHasRoof,
      tempH,
      tempV,
      tempRack,
      newFloorsData,
      nextMode,
      nextGroups,
      tempRack === "2U" ? tempQuantity2U : 1
    );
  };

  const handleToggleCabinet = (floorIndex: number) => {
    if (!activeTower) return;
    const exists = manualGroups.some((g) => g.cabinetIndex === floorIndex);
    let nextGroups = [];
    if (exists) {
      if (activeCabinetIndex === floorIndex) {
        setActiveCabinetIndex(null);
      }
      nextGroups = manualGroups.filter((g) => g.cabinetIndex !== floorIndex);
    } else {
      const floorDataRow = activeTower.floorsData.find(fd => fd.floorIndex === floorIndex);
      const newGroup = {
        cabinetIndex: floorIndex,
        cabinets: [
          {
            id: crypto.randomUUID(),
            type: "2U",
            quantity2U: 1,
            allocations: [
              {
                floorIndex: floorIndex,
                domeCount: floorDataRow ? (floorDataRow.domeCount || 0) : 0,
                bulletCount: floorDataRow ? (floorDataRow.bulletCount || 0) : 0,
              }
            ]
          }
        ]
      };
      setActiveCabinetIndex(floorIndex);
      nextGroups = [...manualGroups, newGroup];
    }
    setManualGroups(nextGroups);
    
    const updatedFloorsData = syncFloorsWithManualGroups(activeTower.floorsData, nextGroups);
    updateTowerFloorsData(updatedFloorsData, nextGroups);
  };

  const handleCabinetRackTypeChange = (cabinetIndex: number, newRackType: string) => {
    // Deprecated: Managed inside the cabinet config modal
  };

  const handleCtrlClickFloor = (floorIndex: number) => {
    // Deprecated: Managed inside the cabinet config modal
  };

  // Fetch saved cabinet placement from DB
  const fetchSavedCabinetPlacement = async (towerId: string): Promise<boolean> => {
    if (!towerId) return false;
    const isUuid = (id: string) => /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(id);
    if (!isUuid(towerId)) return false;

    try {
      const res = await fetch(`${API_BASE}/calculate/cabinet-placement?towerId=${towerId}`);
      if (res.ok) {
        const data = await res.json();
        if (data && data.length > 0) {
          // Kiểm tra data DB có hợp lệ không: nếu có cabinets nhưng tất cả cameraQuantity = 0
          // thì data bị lỗi từ lần save trước → fallback sang tính lại
          const placedFloors = data.filter((item: any) => item.isCabinetPlaced);
          const allCabinetsZero = placedFloors.length > 0 && placedFloors.every((item: any) =>
            (item.cameraQuantityInCabinet ?? 0) === 0 &&
            (!item.cabinets || item.cabinets.every((c: any) => (c.cameraQuantityInCabinet ?? 0) === 0))
          );
          if (allCabinetsZero) {
            console.warn("DB data corrupt: all cabinet camera counts are 0, triggering recalculation");
            return false;
          }

          const hasManualAllocations = data.some((item: any) => 
            item.isCabinetPlaced && 
            item.cabinets && 
            item.cabinets.some((c: any) => c.allocations !== null && c.allocations !== undefined)
          );

          const reconstructedGroups: any[] = [];
          if (hasManualAllocations) {
            data.forEach((item: any) => {
              if (item.isCabinetPlaced && item.cabinets && item.cabinets.length > 0) {
                const manualCabs = item.cabinets.filter((cab: any) => cab.allocations !== null && cab.allocations !== undefined);
                if (manualCabs.length > 0) {
                  const allocationsFloors = manualCabs.flatMap((c: any) => (c.allocations || []).map((a: any) => a.floorIndex));
                  const minFloor = allocationsFloors.length > 0 ? Math.min(...allocationsFloors) : item.floorIndex;
                  const maxFloor = allocationsFloors.length > 0 ? Math.max(...allocationsFloors) : item.floorIndex;
                  
                  const floorRange: Record<number, number> = {};
                  floorRange[minFloor] = maxFloor;

                  reconstructedGroups.push({
                    cabinetIndex: item.floorIndex,
                    cabinets: manualCabs.map((cab: any) => {
                      const hasAlloc = cab.allocations && cab.allocations.length > 0;
                      const defaultAlloc = hasAlloc ? cab.allocations : [
                        {
                          floorIndex: item.floorIndex,
                          domeCount: item.domeCount || 0,
                          bulletCount: item.bulletCount || 0,
                        }
                      ];
                      return {
                        id: cab.cabinetId,
                        type: cab.cabinetType,
                        quantity2U: cab.quantity2U || 1,
                        allocations: defaultAlloc
                      };
                    }),
                    floorRange
                  });
                }
              }
            });
            setCalculationMode("manual");
            setManualGroups(reconstructedGroups);
          } else {
            setCalculationMode("auto");
            setManualGroups([]);
          }

          const cabinetFloorIndices = data
            .filter((item: any) => item.isCabinetPlaced)
            .map((item: any) => item.floorIndex);
          setCabinetPlacements(cabinetFloorIndices);

          const cabinetRanges = data
            .filter((item: any) => item.isCabinetPlaced)
            .map((item: any) => ({
              floorIndex: item.floorIndex,
              fromIndex: item.fromIndex,
              toIndex: item.toIndex,
            }));

          const backendMap = new Map<number, any>();
          data.forEach((item: any) => backendMap.set(item.floorIndex, item));

          setProjects((prev) =>
            prev.map((p) => {
              if (p.id === activeProjectId) {
                const updatedTowers = p.towers.map((t) => {
                  if (t.id === towerId) {
                    const updatedFloors = t.floorsData.map((f) => {
                      const coveringCabinet = cabinetRanges.find(
                        (c: any) => f.floorIndex >= c.fromIndex && f.floorIndex <= c.toIndex
                      );

                      if (backendMap.has(f.floorIndex)) {
                        const backendInfo = backendMap.get(f.floorIndex);
                        if (backendInfo.isCabinetPlaced) {
                          const matchingGroup = hasManualAllocations ? reconstructedGroups.find((g: any) => g.cabinetIndex === f.floorIndex) : null;
                          const mappedCabinets = (backendInfo.cabinets ?? []).map((cab: any) => {
                            const matchCab = matchingGroup?.cabinets?.find((c: any) => c.id === cab.cabinetId);
                            return {
                              ...cab,
                              quantity2U: hasManualAllocations ? (matchCab?.quantity2U || 1) : 1
                            };
                          });
                          return {
                            ...f,
                            camerasCount: backendInfo.camerasCount ?? f.camerasCount ?? 0,
                            domeCount: backendInfo.domeCount ?? f.domeCount ?? 0,
                            bulletCount: backendInfo.bulletCount ?? f.bulletCount ?? 0,
                            cableLengthInput: backendInfo.autocadLength !== undefined ? backendInfo.autocadLength : f.cableLengthInput,
                            sw24Count: backendInfo.sw24Count ?? 0,
                            sw16Count: backendInfo.sw16Count ?? 0,
                            upsType: backendInfo.upsCount === 1 ? "1K" : (backendInfo.upsCount === 2 ? "2K" : "None"),
                            pduCount: backendInfo.pduCount ?? 0,
                            convCount: backendInfo.convCount ?? 0,
                            cameraQuantityInCabinet: backendInfo.cameraQuantityInCabinet ?? 0,
                            isCabinetPlaced: true,
                            cabinetType: backendInfo.cabinetType,
                            cabinetIndex: backendInfo.cabinetIndex ?? undefined,
                            cableLength: backendInfo.cableLength ?? 0,
                            atrium: backendInfo.atrium ?? 0,
                            downCabinet: backendInfo.downCabinet ?? 0,
                            inCabinet: backendInfo.inCabinet ?? 0,
                            autocadLength: backendInfo.autocadLength ?? 0,
                            fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                            toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                            cabinets: mappedCabinets,
                          };
                        }
                        return {
                          ...f,
                          camerasCount: backendInfo.camerasCount ?? f.camerasCount ?? 0,
                          domeCount: backendInfo.domeCount ?? f.domeCount ?? 0,
                          bulletCount: backendInfo.bulletCount ?? f.bulletCount ?? 0,
                          cableLengthInput: backendInfo.autocadLength !== undefined ? backendInfo.autocadLength : f.cableLengthInput,
                          sw24Count: 0,
                          sw16Count: 0,
                          upsType: "None",
                          pduCount: 0,
                          convCount: 0,
                          cameraQuantityInCabinet: 0,
                          isCabinetPlaced: false,
                          cabinetType: undefined,
                          cabinetIndex: backendInfo.cabinetIndex ?? undefined,
                          cableLength: backendInfo.cableLength ?? 0,
                          atrium: backendInfo.atrium ?? 0,
                          downCabinet: backendInfo.downCabinet ?? 0,
                          inCabinet: backendInfo.inCabinet ?? 0,
                          autocadLength: backendInfo.autocadLength ?? 0,
                          fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                          toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                        };
                      }
                      return f;
                    });
                    return {
                      ...t,
                      floorsData: updatedFloors,
                      manualGroups: hasManualAllocations ? reconstructedGroups : [],
                      calculationMode: hasManualAllocations ? "manual" : "auto",
                    };
                  }
                  return t;
                });
                return {
                  ...p,
                  towers: updatedTowers,
                };
              }
              return p;
            })
          );
          return true;
        }
      }
    } catch (err) {
      console.error("Error fetching saved cabinet placement:", err);
    }
    return false;
  };

  // Fetch cabinet placement from API
  const fetchCabinetPlacement = async (
    floorsCount: number,
    basementsCount: number,
    hasRoof: boolean,
    horizontalDistance: number,
    verticalDistance: number,
    rackType: string,
    floorsData: FloorData[],
    mode: "auto" | "manual" = calculationMode,
    groups = manualGroups,
    qty2U: number = activeTower?.quantity2U || 1
  ) => {
    if (floorsCount <= 0) {
      setCabinetPlacements([]);
      return;
    }
    try {
      const isUuid = (id: string) => /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(id);
      const towerIdParam = activeTower?.id && isUuid(activeTower.id) ? `?towerId=${activeTower.id}` : "";

      const sortedFloors = [...floorsData]
        .sort((a, b) => a.floorIndex - b.floorIndex)
        .map(f => ({
          floorIndex: f.floorIndex,
          label: f.label,
          camerasCount: f.camerasCount,
          domeCount: f.domeCount,
          bulletCount: f.bulletCount,
          cableLength: f.cableLengthInput !== undefined ? f.cableLengthInput : 0,
        }));

      let res;
      if (mode === "manual") {
        const manualGroupsPayload = groups.map((g) => {
          // Gom đúng các floorIndex được allocate (bao gồm cabinetIndex + allocations)
          const allocatedFloors = Array.from(new Set([
            g.cabinetIndex,
            ...g.cabinets.flatMap((c: any) => c.allocations.map((a: any) => a.floorIndex))
          ])).sort((a, b) => a - b);

          // floorRange: gửi đúng min/max của các tầng thực sự được allocate
          const minF = allocatedFloors.length > 0 ? Math.min(...allocatedFloors) : g.cabinetIndex;
          const maxF = allocatedFloors.length > 0 ? Math.max(...allocatedFloors) : g.cabinetIndex;

          return {
            cabinetIndex: g.cabinetIndex,
            floorRange: { [minF]: maxF },
            cabinets: g.cabinets.map((c: any) => {
              const totalDome = c.allocations.reduce((sum: number, a: any) => sum + (a.domeCount || 0), 0);
              const totalBullet = c.allocations.reduce((sum: number, a: any) => sum + (a.bulletCount || 0), 0);
              return {
                id: c.id,
                type: c.type,
                totalDome,
                totalBullet,
                totalCamera: totalDome + totalBullet,
                allocations: c.allocations
              };
            })
          };
        });

        res = await fetch(`${API_BASE}/calculate/cabinet-placement-manual${towerIdParam}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            floorsCount,
            basementsCount,
            hasRoof,
            horizontalDistance,
            verticalDistance,
            rackType,
            quantity2U: qty2U,
            floors: sortedFloors,
            manualGroups: manualGroupsPayload,
          })
        });
      } else {
        res = await fetch(`${API_BASE}/calculate/cabinet-placement${towerIdParam}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            floorsCount,
            basementsCount,
            hasRoof,
            horizontalDistance,
            verticalDistance,
            rackType,
            quantity2U: qty2U,
            floors: sortedFloors
          })
        });
      }

      if (res.ok) {
        const data = await res.json();
        
        // Extract floor indices where cabinets are placed
        const cabinetFloorIndices = data
          .filter((item: any) => item.isCabinetPlaced)
          .map((item: any) => item.floorIndex);
        setCabinetPlacements(cabinetFloorIndices);

        // Get cabinet ranges
        const cabinetRanges = data
          .filter((item: any) => item.isCabinetPlaced)
          .map((item: any) => ({
            floorIndex: item.floorIndex,
            fromIndex: item.fromIndex,
            toIndex: item.toIndex,
          }));

        // Map backend results by floorIndex
        const backendMap = new Map<number, any>();
        data.forEach((item: any) => backendMap.set(item.floorIndex, item));

        // Update active tower's floorsData with backend calculated equipment
        setProjects((prev) =>
          prev.map((p) => {
            if (p.id === activeProjectId) {
              const updatedTowers = p.towers.map((t) => {
                if (t.id === activeTowerId) {
                  const updatedFloors = t.floorsData.map((f) => {
                    const coveringCabinet = cabinetRanges.find(
                      (c: any) => f.floorIndex >= c.fromIndex && f.floorIndex <= c.toIndex
                    );

                    if (backendMap.has(f.floorIndex)) {
                      const backendInfo = backendMap.get(f.floorIndex);
                      if (backendInfo.floorIndex === 7) {
                        console.log("Floor 7 Backend Info:", JSON.stringify(backendInfo));
                      }
                      if (backendInfo.isCabinetPlaced) {
                        const matchingGroup = mode === "manual" ? groups.find(g => g.cabinetIndex === f.floorIndex) : null;
                        const mappedCabinets = (backendInfo.cabinets ?? []).map((cab: any) => {
                          const matchCab = matchingGroup?.cabinets?.find((c: any) => c.id === cab.cabinetId);
                          return {
                            ...cab,
                            quantity2U: mode === "manual" ? (matchCab?.quantity2U || 1) : (qty2U || 1)
                          };
                        });
                        return {
                          ...f,
                          camerasCount: backendInfo.camerasCount ?? f.camerasCount ?? 0,
                          domeCount: backendInfo.domeCount ?? f.domeCount ?? 0,
                          bulletCount: backendInfo.bulletCount ?? f.bulletCount ?? 0,
                          cableLengthInput: backendInfo.autocadLength !== undefined ? backendInfo.autocadLength : f.cableLengthInput,
                          sw24Count: backendInfo.sw24Count ?? 0,
                          sw16Count: backendInfo.sw16Count ?? 0,
                          upsType: backendInfo.upsCount === 1 ? "1K" : (backendInfo.upsCount === 2 ? "2K" : "None"),
                          pduCount: backendInfo.pduCount ?? 0,
                          convCount: backendInfo.convCount ?? 0,
                          cameraQuantityInCabinet: backendInfo.cameraQuantityInCabinet ?? 0,
                          isCabinetPlaced: true,
                          cabinetType: backendInfo.cabinetType,
                          cabinetIndex: backendInfo.cabinetIndex ?? undefined,
                          cableLength: backendInfo.cableLength ?? 0,
                          atrium: backendInfo.atrium ?? 0,
                          downCabinet: backendInfo.downCabinet ?? 0,
                          inCabinet: backendInfo.inCabinet ?? 0,
                          autocadLength: backendInfo.autocadLength ?? 0,
                          fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                          toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                          cabinets: mappedCabinets,
                        };
                      }
                      return {
                        ...f,
                        camerasCount: backendInfo.camerasCount ?? f.camerasCount ?? 0,
                        domeCount: backendInfo.domeCount ?? f.domeCount ?? 0,
                        bulletCount: backendInfo.bulletCount ?? f.bulletCount ?? 0,
                        cableLengthInput: backendInfo.autocadLength !== undefined ? backendInfo.autocadLength : f.cableLengthInput,
                        sw24Count: 0,
                        sw16Count: 0,
                        upsType: "None",
                        pduCount: 0,
                        convCount: 0,
                        cameraQuantityInCabinet: 0,
                        isCabinetPlaced: false,
                        cabinetType: undefined,
                        cabinetIndex: backendInfo.cabinetIndex ?? undefined,
                        cableLength: backendInfo.cableLength ?? 0,
                        atrium: backendInfo.atrium ?? 0,
                        downCabinet: backendInfo.downCabinet ?? 0,
                        inCabinet: backendInfo.inCabinet ?? 0,
                        autocadLength: backendInfo.autocadLength ?? 0,
                        fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                        toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                      };
                    }
                    // Non-cabinet floor fallback
                    return {
                      ...f,
                      sw24Count: 0,
                      sw16Count: 0,
                      upsType: "None",
                      pduCount: 0,
                      convCount: 0,
                      cameraQuantityInCabinet: 0,
                      isCabinetPlaced: false,
                      cabinetType: undefined,
                      cabinetIndex: undefined,
                      fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                      toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                    };
                  });
                  return {
                    ...t,
                    floorsData: updatedFloors,
                  };
                }
                return t;
              });
              return {
                ...p,
                towers: updatedTowers,
              };
            }
            return p;
          })
        );
      } else {
        const localPlacements = localCalculateCabinetPlacement(floorsCount, horizontalDistance, verticalDistance);
        setCabinetPlacements(localPlacements.map(lvl => basementsCount + lvl - 1));
      }
    } catch (err) {
      console.error("Error fetching cabinet placement", err);
      const localPlacements = localCalculateCabinetPlacement(floorsCount, horizontalDistance, verticalDistance);
      setCabinetPlacements(localPlacements.map(lvl => basementsCount + lvl - 1));
    }
  };

  const fetchBOM = async (tower: any) => {
    if (!tower || !tower.floorsData) return;
    try {
      const floorsData = tower.floorsData;
      const totalCamera = floorsData.reduce((acc: number, curr: any) => acc + (curr.camerasCount || 0), 0);
      const totalCamDome = floorsData.reduce((acc: number, curr: any) => acc + (curr.domeCount || 0), 0);
      const totalCamBullet = floorsData.reduce((acc: number, curr: any) => acc + (curr.bulletCount || 0), 0);
      const totalSw16 = floorsData.reduce((acc: number, curr: any) => acc + (curr.sw16Count || 0), 0);
      const totalSw24 = floorsData.reduce((acc: number, curr: any) => acc + (curr.sw24Count || 0), 0);
      const totalSwichPOE = totalSw16 + totalSw24;
      const totalCabinet = floorsData.filter((f: any) => f.isCabinetPlaced).length;
      const cabinets: Record<string, number> = {};
      floorsData.forEach((f: any) => {
        if (f.isCabinetPlaced) {
          if (f.cabinets && f.cabinets.length > 0) {
            f.cabinets.forEach((cab: any) => {
              const type = cab.cabinetType || cab.type || "2U";
              const qty2U = type === "2U" ? (cab.quantity2U || 1) : 1;
              cabinets[type] = (cabinets[type] || 0) + qty2U;
            });
          } else {
            const type = f.cabinetType || tower.rackType || "2U";
            const qty2U = type === "2U" ? (tower.quantity2U || 1) : 1;
            cabinets[type] = (cabinets[type] || 0) + qty2U;
          }
        }
      });
      if (Object.keys(cabinets).length === 0) {
        const type = tower.rackType || "2U";
        cabinets[type] = totalCabinet || 0;
      }
      const totalUPS = floorsData.filter((f: any) => f.isCabinetPlaced && f.upsType !== "None").length;
      const totalPDU = floorsData.reduce((acc: number, curr: any) => acc + (curr.pduCount || 0), 0);
      const totalConverter = floorsData.reduce((acc: number, curr: any) => acc + (curr.convCount || 0), 0);
      const totalCableLength = floorsData.reduce((acc: number, curr: any) => acc + (curr.cableLength || 0), 0);

      const floors = floorsData.map((f: any) => ({
        floorIndex: f.floorIndex,
        isCabinetPlaced: f.isCabinetPlaced || false,
        label: f.label,
        camerasCount: f.camerasCount || 0,
        domeCount: f.domeCount || 0,
        bulletCount: f.bulletCount || 0,
        cameraQuantityInCabinet: f.cameraQuantityInCabinet || 0,
        sw24Count: f.sw24Count || 0,
        sw16Count: f.sw16Count || 0,
        upsCount: f.upsType !== "None" ? 1 : 0,
        pduCount: f.pduCount || 0,
        convCount: f.convCount || 0,
        cabinetType: f.cabinetType
      }));

      const res = await fetch(`${API_BASE}/calculate/bom`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify([{
          towerId: tower.id, totalCamera,
          totalCamDome,
          totalCamBullet,
          totalSwichPOE,
          totalSw16,
          totalSw24,
          cabinets,

          totalUPS,
          totalPDU,
          totalConverter,
          totalCableLength,
          floors
        }])
      });
      if (res.ok) {
        const data = await res.json();
        setBomData(data);
      } else {
        console.error("Failed to fetch BOM", res.statusText);
        addToast("Lỗi khi tính toán BOM!", "error");
      }
    } catch (err) {
      console.error("Error fetching BOM data", err);
      addToast("Lỗi kết nối dịch vụ BOM!", "error");
    }
  };

  useEffect(() => {
    if (activeTower) {
      fetchBOM(activeTower);
    }
  }, [activeTower?.id, activeTower?.floorsData, activeTower?.rackType]);

  useEffect(() => {
    if (isSummaryTabActive && activeProject?.towers) {
      const initial: Record<string, boolean> = {};
      activeProject.towers.forEach(t => {
        initial[t.id] = true;
      });
      setSelectedTowersSummary(initial);
    }
  }, [isSummaryTabActive, activeProject?.towers]);

  const handleCalculateSummary = async () => {
    if (!activeProject?.towers) return;
    
    const selectedTowers = activeProject.towers.filter(t => selectedTowersSummary[t.id]);
    if (selectedTowers.length === 0) {
      addToast("Vui lòng chọn ít nhất một tháp để tính tổng BOM!", "error");
      return;
    }

    setIsCalculatingSummary(true);
    try {
      const payload = selectedTowers.map((t) => {
        const floorsData = t.floorsData;
        if (!floorsData || floorsData.length === 0) return null;
        const totalCamera = floorsData.reduce((acc: number, curr: any) => acc + (curr.camerasCount || 0), 0);
        const totalCamDome = floorsData.reduce((acc: number, curr: any) => acc + (curr.domeCount || 0), 0);
        const totalCamBullet = floorsData.reduce((acc: number, curr: any) => acc + (curr.bulletCount || 0), 0);
        const totalSw16 = floorsData.reduce((acc: number, curr: any) => acc + (curr.sw16Count || 0), 0);
        const totalSw24 = floorsData.reduce((acc: number, curr: any) => acc + (curr.sw24Count || 0), 0);
        const totalSwichPOE = totalSw16 + totalSw24;
        const totalCabinet = floorsData.filter((f: any) => f.isCabinetPlaced).length;
        const cabinets: Record<string, number> = {};
        floorsData.forEach((f: any) => {
          if (f.isCabinetPlaced) {
            if (f.cabinets && f.cabinets.length > 0) {
              f.cabinets.forEach((cab: any) => {
                const type = cab.cabinetType || cab.type || "2U";
                const qty2U = type === "2U" ? (cab.quantity2U || 1) : 1;
                cabinets[type] = (cabinets[type] || 0) + qty2U;
              });
            } else {
              const type = f.cabinetType || t.rackType || "2U";
              const qty2U = type === "2U" ? (t.quantity2U || 1) : 1;
              cabinets[type] = (cabinets[type] || 0) + qty2U;
            }
          }
        });
        if (Object.keys(cabinets).length === 0) {
          const type = t.rackType || "2U";
          cabinets[type] = totalCabinet || 0;
        }
        const totalUPS = floorsData.filter((f: any) => f.isCabinetPlaced && f.upsType !== "None").length;
        const totalPDU = floorsData.reduce((acc: number, curr: any) => acc + (curr.pduCount || 0), 0);
        const totalConverter = floorsData.reduce((acc: number, curr: any) => acc + (curr.convCount || 0), 0);
        const totalCableLength = floorsData.reduce((acc: number, curr: any) => acc + (curr.cableLength || 0), 0);

        const floors = floorsData.map((f: any) => ({
          floorIndex: f.floorIndex,
          isCabinetPlaced: f.isCabinetPlaced || false,
          label: f.label,
          camerasCount: f.camerasCount || 0,
          domeCount: f.domeCount || 0,
          bulletCount: f.bulletCount || 0,
          cameraQuantityInCabinet: f.cameraQuantityInCabinet || 0,
          sw24Count: f.sw24Count || 0,
          sw16Count: f.sw16Count || 0,
          upsCount: f.upsType !== "None" ? 1 : 0,
          pduCount: f.pduCount || 0,
          convCount: f.convCount || 0
        }));

        return {
          towerId: t.id,
          totalCamera,
          totalCamDome,
          totalCamBullet,
          totalSwichPOE,
          totalSw16,
          totalSw24,
          cabinets,

          totalUPS,
          totalPDU,
          totalConverter,
          totalCableLength,
          floors
        };
      }).filter(p => p !== null);

      if (payload.length === 0) {
        addToast("Không lấy được dữ liệu BOM cho tháp nào!", "error");
        return;
      }

      const res = await fetch(API_BASE + "/calculate/bom", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });

      if (res.ok) { setSummaryBomData(await res.json()); } else { addToast("Lỗi khi tính tổng BOM!", "error"); return; }
      addToast("Tính tổng BOM cho các tháp thành công!", "success");
    } catch (err) {
      console.error(err);
      addToast("Lỗi khi tính tổng BOM!", "error");
    } finally {
      setIsCalculatingSummary(false);
    }
  };

  const handleExportExcel = async () => {
    if (!activeProject) return;
    const selectedTowers = activeProject.towers.filter(t => selectedTowersSummary[t.id]);
    if (selectedTowers.length === 0) {
      addToast("Vui lòng chọn ít nhất một tháp để xuất Excel!", "error");
      return;
    }

    try {
      setIsExportingExcel(true);
      const towerIdsParam = selectedTowers.map(t => `towerIds=${t.id}`).join("&");
      const url = `${API_BASE}/calculate/export-excel?projectId=${activeProject.id}&${towerIdsParam}`;
      
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error("Không thể xuất file Excel");
      }
      
      const blob = await response.blob();
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = downloadUrl;
      
      const cleanProjectName = activeProject.name.replace(/\s+/g, "_");
      link.setAttribute("download", `BOQ_${cleanProjectName}_Export.xlsx`);
      
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(downloadUrl);
      addToast("Xuất file Excel thành công!", "success");
    } catch (err) {
      console.error(err);
      addToast("Lỗi khi xuất file Excel!", "error");
    } finally {
      setIsExportingExcel(false);
    }
  };

  // Sync temp values when active tower changes
  useEffect(() => {
    if (activeTower) {
      setTempFloors(activeTower?.floorsCount);
      setTempBasements(activeTower?.basementsCount || 0);
      setTempHasRoof(activeTower?.hasRoof || false);
      setTempH(activeTower?.horizontalDistance);
      setTempV(activeTower?.verticalDistance);
      setTempRack(activeTower?.rackType);
      setTempQuantity2U(activeTower?.quantity2U || 1);

      const nextMode = activeTower?.calculationMode || "auto";
      const nextGroups = activeTower?.manualGroups || [];

      setCalculationMode(nextMode);
      setManualGroups(nextGroups);
      
      // Chỉ load từ DB 1 lần khi tower chưa có kết quả tính toán (isCabinetPlaced chưa set)
      const isUuid = (id: string) => /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(id);
      const hasCabinetResult = activeTower?.floorsData?.some(f => f.isCabinetPlaced === true);
      
      if (!hasCabinetResult && activeTower?.id && isUuid(activeTower.id)) {
        // Lần đầu load tower từ DB (chưa có kết quả tính toán trong state)
        fetchSavedCabinetPlacement(activeTower.id).then((loaded) => {
          if (!loaded) {
            fetchCabinetPlacement(
              activeTower?.floorsCount || 0,
              activeTower?.basementsCount || 0,
              activeTower?.hasRoof || false,
              activeTower?.horizontalDistance || 0,
              activeTower?.verticalDistance || 0,
              activeTower?.rackType || "2U",
              activeTower?.floorsData || [],
              nextMode,
              nextGroups,
              activeTower?.rackType === "2U" ? (activeTower?.quantity2U || 1) : 1
            );
          }
        });
      } else if (!hasCabinetResult) {
        // Tower chưa có ID (mới tạo) → tính toán luôn
        fetchCabinetPlacement(
          activeTower?.floorsCount || 0,
          activeTower?.basementsCount || 0,
          activeTower?.hasRoof || false,
          activeTower?.horizontalDistance || 0,
          activeTower?.verticalDistance || 0,
          activeTower?.rackType || "2U",
          activeTower?.floorsData || [],
          nextMode,
          nextGroups,
          activeTower?.rackType === "2U" ? (activeTower?.quantity2U || 1) : 1
        );
      }
      // Nếu hasCabinetResult = true → đã có kết quả trong state, không cần gọi lại
    }
  }, [activeTower?.id]);

  // Report general meta details
  const [customerName, setCustomerName] = useState("Công ty TNHH Đầu tư & Phát triển Công nghệ");
  const [projectLocation, setProjectLocation] = useState("Khu Công nghệ cao Láng Hòa Lạc, Hà Nội");
  const [validDays, setValidDays] = useState(30);

  // Trigger recalculation of the active project
  const handleRecalculate = () => {
    if (!activeTower) return;
    if (tempFloors <= 0) {
      addToast("Số tầng nổi phải lớn hơn 0!", "error");
      return;
    }
    if (tempH <= 0 || tempV <= 0) {
      addToast("Khoảng cách ngang/dọc phải lớn hơn 0!", "error");
      return;
    }

    // Lưu tower settings vào backend trước
    const saveTowerAndRecalculate = (floorsDataToUse: FloorData[]) => {
      setProjects((prev) =>
        prev.map((p) => {
          if (p.id === activeProject.id) {
            const updatedTowers = p.towers.map((t) => {
              if (t.id === activeTower?.id) {
                return {
                  ...t,
                  floorsCount: tempFloors,
                  basementsCount: tempBasements,
                  hasRoof: tempHasRoof,
                  horizontalDistance: tempH,
                  verticalDistance: tempV,
                  rackType: tempRack,
                  quantity2U: tempRack === "2U" ? tempQuantity2U : 1,
                  floorsData: floorsDataToUse,
                  manualGroups: t.manualGroups || [],
                  calculationMode: t.calculationMode || "auto",
                };
              }
              return t;
            });
            return { ...p, towers: updatedTowers };
          }
          return p;
        })
      );

      if (activeTower) {
        fetch(`${API_BASE}/towers/${activeTower.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            configId: "a2b0a797-8ff2-4a79-ac5d-78525bd25e90",
            name: activeTower.name,
            floorCount: tempFloors,
            basementCount: tempBasements,
            hasRoof: tempHasRoof,
            widthLength: tempH,
            heightLength: tempV,
            quantity2U: tempRack === "2U" ? tempQuantity2U : 1
          })
        })
        .then(res => {
          if (res.ok) {
            fetchCabinetPlacement(
              tempFloors,
              tempBasements,
              tempHasRoof,
              tempH,
              tempV,
              tempRack,
              floorsDataToUse,
              calculationMode,
              manualGroups,
              tempRack === "2U" ? tempQuantity2U : 1
            );
          }
        })
        .catch(err => console.error("Error saving recalculated tower to backend", err));
      }
    };

    if (calculationMode === "manual") {
      // Trong chế độ thủ công: giữ nguyên floorsData hiện tại, chỉ update settings tower
      // Không chạy calculateProjectBOQ để tránh reset isCabinetPlaced và gây flicker
      saveTowerAndRecalculate(activeTower.floorsData);
    } else {
      // Chế độ tự động: chạy local calculation trước để estimate cabinet placement
      const localPlacements = localCalculateCabinetPlacement(tempFloors, tempH, tempV);
      const newCabinetPlacements = localPlacements.map(lvl => tempBasements + lvl - 1);
      setCabinetPlacements(newCabinetPlacements);

      const updatedFloorsData = calculateProjectBOQ(
        tempFloors,
        tempH,
        tempV,
        tempRack,
        activeTower?.siteParams,
        activeTower?.hardwareLogic,
        activeTower?.floorsData,
        tempBasements,
        tempHasRoof,
        newCabinetPlacements
      );
      saveTowerAndRecalculate(updatedFloorsData);
    }

    addToast("Tính toán lại BOQ thành công!", "success");
  };

  const handleResetBOQ = () => {
    if (!activeTower) return;

    // Reset all floors data in the current active tower to 0 cameras and 0 equipment
    const resetFloors = activeTower.floorsData.map((f) => ({
      ...f,
      camerasCount: 0,
      domeCount: 0,
      bulletCount: 0,
      sw24Count: 0,
      sw16Count: 0,
      upsType: "None" as const,
      pduCount: 0,
      convCount: 0,
      cameraQuantityInCabinet: 0,
      isCabinetPlaced: false,
      cabinetType: undefined,
      fromIndex: undefined,
      toIndex: undefined,
      cableLengthInput: undefined,
    }));

    setManualGroups([]);
    setCabinetPlacements([]);
    setCalculationMode("auto");

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                floorsData: resetFloors,
                manualGroups: [],
                calculationMode: "auto",
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    // Call PUT /api/towers/{id} to save empty config to backend
    fetch(`${API_BASE}/towers/${activeTower.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        configId: "a2b0a797-8ff2-4a79-ac5d-78525bd25e90",
        name: activeTower.name,
        floorCount: activeTower.floorsCount,
        basementCount: activeTower.basementsCount,
        hasRoof: activeTower.hasRoof,
        widthLength: activeTower.horizontalDistance,
        heightLength: activeTower.verticalDistance,
        quantity2U: activeTower.quantity2U || 1
      })
    })
    .then((res) => {
      if (res.ok) {
        fetchCabinetPlacement(
          activeTower.floorsCount,
          activeTower.basementsCount || 0,
          activeTower.hasRoof || false,
          activeTower.horizontalDistance,
          activeTower.verticalDistance,
          activeTower.rackType,
          resetFloors,
          "auto",
          [],
          activeTower.quantity2U || 1
        );
      }
    })
    .catch(err => console.error("Error resetting tower in backend", err));

    addToast("Đã reset cấu hình camera và thiết bị về 0!", "info");
  };

  // Directly update specific cell value in the detailed floor sheet
  const handleUpdateFloorCell = (floorIndex: number, field: keyof FloorData, value: number | string) => {
    if (!activeTower) return;

    const isSelected = selectedFloorIndexes.includes(floorIndex);

    const baseFloors = activeTower.floorsData.map((f) => {
      const shouldUpdate = f.floorIndex === floorIndex || (isSelected && selectedFloorIndexes.includes(f.floorIndex));
      if (shouldUpdate) {
        const updatedRow = { ...f, [field]: value };
        
        // If cameras count, dome or bullet count is updated, we do automatic minor adjustment of children
        if (field === "camerasCount") {
          const cams = Number(value);
          const dome = Math.round(cams * 0.5);
          const bullet = cams - dome;
          updatedRow.camerasCount = cams;
          updatedRow.domeCount = dome;
          updatedRow.bulletCount = bullet;
        } else if (field === "domeCount") {
          const dome = Number(value);
          updatedRow.domeCount = dome;
          updatedRow.camerasCount = dome + updatedRow.bulletCount;
        } else if (field === "bulletCount") {
          const bullet = Number(value);
          updatedRow.bulletCount = bullet;
          updatedRow.camerasCount = updatedRow.domeCount + bullet;
        }
        return updatedRow;
      }
      return f;
    });

    const recalculatedFloors = calculateProjectBOQ(
      activeTower.floorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      activeTower.siteParams,
      activeTower.hardwareLogic,
      baseFloors,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      cabinetPlacements
    );

    // In manual mode, sync the camera updates to the manualGroups allocations
    let nextGroups = manualGroups;
    if (calculationMode === "manual") {
      nextGroups = manualGroups.map((g) => {
        let groupChanged = false;
        const newCabinets = g.cabinets.map((cab) => {
          let cabinetChanged = false;
          const newAllocations = cab.allocations.map((alloc) => {
            const shouldUpdateAlloc = alloc.floorIndex === floorIndex || (isSelected && selectedFloorIndexes.includes(alloc.floorIndex));
            if (shouldUpdateAlloc) {
              cabinetChanged = true;
              groupChanged = true;
              
              let newDome = alloc.domeCount;
              let newBullet = alloc.bulletCount;
              
              if (field === "camerasCount") {
                const cams = Number(value);
                newDome = Math.round(cams * 0.5);
                newBullet = cams - newDome;
              } else if (field === "domeCount") {
                newDome = Number(value);
              } else if (field === "bulletCount") {
                newBullet = Number(value);
              }
              
              return {
                ...alloc,
                domeCount: newDome,
                bulletCount: newBullet
              };
            }
            return alloc;
          });
          if (cabinetChanged) {
            return {
              ...cab,
              allocations: newAllocations
            };
          }
          return cab;
        });
        if (groupChanged) {
          return {
            ...g,
            cabinets: newCabinets
          };
        }
        return g;
      });
      setManualGroups(nextGroups);
    }

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                floorsData: recalculatedFloors,
                manualGroups: nextGroups,
              };
            }
            return t;
          });

          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    fetchCabinetPlacement(
      activeTower.floorsCount,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      recalculatedFloors,
      calculationMode,
      nextGroups,
      activeTower.quantity2U || 1
    );
  };

  const handleDeleteFloor = (deletedFloorIndex: number) => {
    if (!activeTower) return;

    const basementsCount = activeTower.basementsCount || 0;
    const floorsCount = activeTower.floorsCount || 0;
    const hasRoof = activeTower.hasRoof || false;

    let newBasementsCount = basementsCount;
    let newFloorsCount = floorsCount;
    let newHasRoof = hasRoof;

    if (deletedFloorIndex < basementsCount) {
      newBasementsCount = Math.max(0, basementsCount - 1);
    } else if (deletedFloorIndex >= basementsCount && deletedFloorIndex < basementsCount + floorsCount) {
      newFloorsCount = Math.max(0, floorsCount - 1);
    } else if (hasRoof && deletedFloorIndex === basementsCount + floorsCount) {
      newHasRoof = false;
    }

    setTempBasements(newBasementsCount);
    setTempFloors(newFloorsCount);
    setTempHasRoof(newHasRoof);

    const remainingFloors = activeTower.floorsData
      .filter((f) => f.floorIndex !== deletedFloorIndex)
      .map((f, idx) => ({
        ...f,
        floorIndex: idx,
      }));

    const newCabinetPlacements = cabinetPlacements
      .filter((idx) => idx !== deletedFloorIndex)
      .map((idx) => (idx > deletedFloorIndex ? idx - 1 : idx));
    setCabinetPlacements(newCabinetPlacements);

    const newManualGroups = manualGroups
      .filter((g) => g.cabinetIndex !== deletedFloorIndex)
      .map((g) => {
        const updatedCabinetIndex = g.cabinetIndex > deletedFloorIndex ? g.cabinetIndex - 1 : g.cabinetIndex;
        const updatedAssociatedFloors = g.associatedFloors
          .filter((fIdx) => fIdx !== deletedFloorIndex)
          .map((fIdx) => (fIdx > deletedFloorIndex ? fIdx - 1 : fIdx));
        return {
          ...g,
          cabinetIndex: updatedCabinetIndex,
          associatedFloors: updatedAssociatedFloors,
        };
      });
    setManualGroups(newManualGroups);

    const recalculatedFloors = calculateProjectBOQ(
      newFloorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      activeTower.siteParams,
      activeTower.hardwareLogic,
      remainingFloors,
      newBasementsCount,
      newHasRoof,
      newCabinetPlacements
    );

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                floorsCount: newFloorsCount,
                basementsCount: newBasementsCount,
                hasRoof: newHasRoof,
                floorsData: recalculatedFloors,
                manualGroups: newManualGroups,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    fetch(`${API_BASE}/towers/${activeTower.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        configId: "a2b0a797-8ff2-4a79-ac5d-78525bd25e90",
        name: activeTower.name,
        floorCount: newFloorsCount,
        basementCount: newBasementsCount,
        hasRoof: newHasRoof,
        widthLength: activeTower.horizontalDistance,
        heightLength: activeTower.verticalDistance
      })
    })
    .then((res) => {
      if (res.ok) {
        fetchCabinetPlacement(
          newFloorsCount,
          newBasementsCount,
          newHasRoof,
          activeTower.horizontalDistance,
          activeTower.verticalDistance,
          activeTower.rackType,
          recalculatedFloors,
          calculationMode,
          manualGroups,
          activeTower.quantity2U || 1
        );
      }
    })
    .catch((err) => console.error("Error updating tower on floor deletion", err));

    addToast("Đã xóa tầng thành công và tính toán lại BOQ!", "success");
  };

  // Helper to render editable note input cell in the Excel-like BOQ Template Table (Left)
  const renderNoteCell = (key: string) => {
    return (
      <td className="py-1.5 px-2 text-slate-700">
        <input
          type="text"
          value={leftTableNotes[key] || ""}
          onChange={(e) => setLeftTableNotes(prev => ({ ...prev, [key]: e.target.value }))}
          placeholder="-"
          className="w-full bg-transparent hover:bg-slate-100/50 focus:bg-white border border-transparent hover:border-slate-300 focus:border-[#1A237E] rounded px-1.5 py-0.5 text-xs text-slate-700 focus:outline-none transition"
        />
      </td>
    );
  };

  const getLaborValue = (itemName: string): number | null => {
    const normalized = itemName.trim().toLowerCase();
    if (normalized.includes("camera ip dome 2mp hikvision ds-2cd1121g0-i")) return 0.085;
    if (normalized.includes("camera ip thân 2mp hikvision ds-2cd1021g0-i")) return 0.085;
    if (normalized.includes("đầu ghi hình camera ip 32 kênh hikvision ds-7732nxi-k4")) return 0.12;
    if (normalized.includes("đầu ghi hình camera ip 16 kênh")) return 0.12;
    if (normalized.includes("ổ cứng 10t western")) return 0.2;
    if (normalized.includes("switch hikvision poe 24 cổng ds-3e1326p-ei")) return 0.18;
    if (normalized.includes("switch hikvision poe 16 cổng ds-3e1318p-ei")) return 0.18;
    if (normalized.includes("switch 16 port cisco cbs110-16t-eu")) return 0.18;
    if (normalized.includes("switch 24 port cisco")) return 0.18;
    if (normalized.includes("màn hình quan sát 43 inch samsung")) return 0.34;
    if (normalized.includes("cáp quang 4fo")) return 0.007;
    if (normalized.includes("cáp mạng cat5e")) return 0.01;
    if (normalized.includes("bộ chuyển đổi quang điện gigabit gnetcom")) return 0.018;
    if (normalized.includes("tủ mạng rack 2u")) return 0.5;
    if (normalized.includes("tủ mạng rack 6u")) return 0.765;
    if (normalized.includes("tủ mạng rack 10u (có bánh xe)")) return 0.765;
    if (normalized.includes("tủ mạng rack 32u")) return 2;
    if (normalized.includes("tủ mạng rack 42u")) return 2;
    if (normalized.includes("odf 12fo sc/upc") || normalized.includes("odf 24fo sc/upc")) return 1;
    if (normalized.includes("dây điện cvv 2x2.5")) return 0.01;
    if (normalized.includes("thanh nguồn pdu đa năng")) return 0.04;
    if (normalized.includes("nguồn lưu điện ups ares model ar610")) return 0.245;
    if (normalized.includes("nguồn lưu điện ups ares model ar630")) return 0.245;
    if (normalized.includes("đầu mạng amp cat 5")) return 0.0267;
    if (normalized.includes("dây nhảy quang sc/upc sc/upc 3m") || normalized.includes("dây nhảy quang sc/upc")) return 0.013;
    if (normalized.includes("odf 4fo sc/upc")) return 0.165;
    if (normalized.includes("dây nhảy mạng cat5")) return 0.013;
    if (normalized.includes("thanh quản lý cáp mạng 19inch") || normalized.includes("thanh quản lý cáp mạng")) return 0.08;
    if (normalized.includes("ruột gà phi 20")) return 0.034;
    if (normalized.includes("ống điện d20")) return 0.034;
    return null;
  };

  const renderLaborCells = (itemName: string, quantity: number) => {
    const labor = getLaborValue(itemName);
    if (labor === null) {
      return (
        <>
          <td className="py-2.5 px-1 text-center font-mono"></td>
          <td className="py-2.5 px-1 text-center font-mono"></td>
        </>
      );
    }
    const totalLabor = quantity * labor;
    return (
      <>
        <td className="py-2.5 px-1 text-center font-mono">{labor}</td>
        <td className="py-2.5 px-1 text-center font-mono">{Number(totalLabor.toFixed(4))}</td>
      </>
    );
  };

  // Apply Selected Preset Standards
  const handleSelectPreset = (preset: StandardPreset) => {
    if (!activeTower) return;
    const updatedParams: SiteParameters = {
      ...activeTower?.siteParams,
      cableFactor: preset.cableFactor,
    };
    const updatedLogic: HardwareLogic = {
      ...activeTower?.hardwareLogic,
      switchPreference: preset.switchPreference,
    };
    
    // Recalculate whole floors with new parameters
    const recalculatedFloors = calculateProjectBOQ(
      activeTower?.floorsCount,
      activeTower?.horizontalDistance,
      activeTower?.verticalDistance,
      activeTower?.rackType,
      updatedParams,
      updatedLogic,
      activeTower?.floorsData,
      activeTower?.basementsCount || 0,
      activeTower?.hasRoof || false,
      cabinetPlacements
    );

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                standardPresetId: preset.id,
                siteParams: updatedParams,
                hardwareLogic: updatedLogic,
                floorsData: recalculatedFloors,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    addToast(`Đã áp dụng tiêu chuẩn: ${preset.name}`, "success");
    setActiveTab("app");
  };

  // Update specific site parameters
  const handleUpdateSiteParam = (key: keyof SiteParameters, value: string | number) => {
    if (!activeTower) return;
    const updatedParams = { ...activeTower.siteParams, [key]: value };
    const recalculatedFloors = calculateProjectBOQ(
      activeTower.floorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      updatedParams,
      activeTower.hardwareLogic,
      activeTower.floorsData,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      cabinetPlacements
    );

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                siteParams: updatedParams,
                floorsData: recalculatedFloors,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

  };

  // Update specific hardware logic parameters
  const handleUpdateHardwareLogic = (key: keyof HardwareLogic, value: string | number) => {
    if (!activeTower) return;
    const updatedLogic = { ...activeTower.hardwareLogic, [key]: value };
    const recalculatedFloors = calculateProjectBOQ(
      activeTower.floorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      activeTower.siteParams,
      updatedLogic,
      activeTower.floorsData,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      cabinetPlacements
    );

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                hardwareLogic: updatedLogic,
                floorsData: recalculatedFloors,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

  };

  // Reset current project custom price overrides
  const handleResetPrices = () => {
    if (!activeTower) return;
    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return { ...t, customPrices: {} };
            }
            return t;
          });
          return { ...p, towers: updatedTowers };
        }
        return p;
      })
    );
    addToast("Đã thiết lập lại giá gốc từ kho", "info");
  };

  // Set custom unit price for current project
  const handleSetCustomPrice = (itemId: string, price: number) => {
    if (!activeTower) return;
    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                customPrices: {
                  ...t.customPrices,
                  [itemId]: price,
                },
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );
  };
const handleAddGlobalInventory = () => {
    if (!newItemCode || !newItemName) {
      addToast("Vui lòng nhập Mã vật tư và Tên thiết bị!", "error");
      return;
    }
    const item: InventoryItem = {
      id: "item-" + Date.now(),
      code: newItemCode,
      name: newItemName,
      category: newItemCategory,
      spec: newItemSpec,
      unit: newItemUnit,
      basePrice: newItemPrice,
    };
    setGlobalInventory((prev) => [...prev, item]);
    setNewItemCode("");
    setNewItemName("");
    setNewItemSpec("");
    setNewItemPrice(100000);
    addToast("Đã thêm thiết bị mới vào Kho vật tư!", "success");
  };

  const handleDeleteInventoryItem = (id: string) => {
    setGlobalInventory((prev) => prev.filter((item) => item.id !== id));
    addToast("Đã xóa thiết bị khỏi Kho", "info");
  };

  // Create Project handler
  const handleCreateProject = async () => {
    if (!newProjectName) {
      addToast("Vui lòng điền tên dự án!", "error");
      return;
    }

    try {
      const response = await fetch(`${API_BASE}/projects`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: newProjectName,
          description: newProjectDesc || "Dự án giám sát hạ tầng mới",
        }),
      });

      if (!response.ok) throw new Error("Failed to create project");

      addToast("Tạo dự án trên backend thành công!", "success");

      // Fetch projects again
      const listResponse = await fetch(`${API_BASE}/projects?page=1&size=100`);
      if (!listResponse.ok) throw new Error("Failed to fetch projects after creation");
      const pageData = await listResponse.json();
      const backendList = pageData.content || [];

      const oldIds = projects.map(p => p.id);
      const newProjectBackend = backendList.find((p: any) => !oldIds.includes(p.id)) || backendList[0];

      if (newProjectBackend) {
        await fetchProjects(newProjectBackend.id);
      }

      setIsCreatingProject(false);
      setNewProjectName("");
      setNewProjectDesc("");
      setNewProjectFloors(5);
      setActiveTab("app");
      setActiveNav("dashboard");
    } catch (error) {
      console.error("Error creating project", error);
      addToast("Không thể tạo dự án!", "error");
    }
  };

  const handleCreateTower = (name: string) => {
    if (!activeProject) return;
    const selectedPreset = BASE_PRESETS[0];

    // Create local placeholder tower — NOT saved to backend yet
    // The init form will show because floorsCount=0 and floorsData=[]
    // User fills in details then clicks "Khởi tạo tháp & Bắt đầu" which does POST /api/towers
    const newTower: Tower = {
      id: "tower-new-" + Date.now(),
      name: name,
      description: "Tháp giám sát mới",
      createdAt: new Date().toISOString(),
      floorsCount: 0,
      basementsCount: 0,
      hasRoof: false,
      horizontalDistance: 50,
      verticalDistance: 4,
      rackType: "2U",
      standardPresetId: selectedPreset.id,
      siteParams: { ...DEFAULT_SITE_PARAMS, cableFactor: selectedPreset.cableFactor },
      hardwareLogic: { ...DEFAULT_HARDWARE_LOGIC, switchPreference: selectedPreset.switchPreference },
      floorsData: [],
      customPrices: {},
    };

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          return { ...p, towers: [...p.towers, newTower] };
        }
        return p;
      })
    );
    setActiveTowerId(newTower.id);
    addToast(`Đã tạo tháp mới: ${name}. Vui lòng nhập thông số và bấm Khởi tạo.`, "success");
  };

  const handleDeleteTower = async (towerId: string) => {
    if (!activeProject) return;

    if (!confirm("Bạn có chắc chắn muốn xóa tháp này?")) {
      return;
    }

    // Call DELETE /api/towers/{id} if tower is persisted in backend
    if (!towerId.startsWith("tower-new-")) {
      try {
        const response = await fetch(`${API_BASE}/towers/${towerId}`, {
          method: "DELETE",
        });
        if (!response.ok) throw new Error("Failed to delete tower");
      } catch (err) {
        console.error("Error deleting tower", err);
        addToast("Lỗi khi xóa tháp trên backend!", "error");
        return;
      }
    }

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const filteredTowers = p.towers.filter((t) => t.id !== towerId);
          return {
            ...p,
            towers: filteredTowers,
          };
        }
        return p;
      })
    );

    setActiveTowerId((prev) => {
      if (prev === towerId) {
        const remaining = activeProject.towers.filter((t) => t.id !== towerId);
        return remaining.length > 0 ? remaining[0].id : "";
      }
      return prev;
    });

    addToast("Đã xóa tháp thành công", "info");
  };

  const handleDeleteProject = async (id: string) => {
    if (projects.length <= 1) {
      addToast("Không thể xóa dự án duy nhất còn lại!", "error");
      return;
    }

    if (!confirm("Bạn có chắc chắn muốn xóa dự án này?")) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE}/projects/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) throw new Error("Failed to delete project");

      addToast("Đã xóa dự án thành công", "info");
      await fetchProjects();
    } catch (error) {
      console.error("Error deleting project", error);
      addToast("Không thể xóa dự án!", "error");
    }
  };
// BOQ Itemized Quantities Aggregation for Active Project
  const aggregateQuantities = () => {
    let totalDome = 0;
    let totalBullet = 0;
    let sw24Count = 0;
    let sw16Count = 0;
    let rack2u = 0;
    let rack6u = 0;
    let rack10u = 0;
    let rack20u = 0;
    let ups1k = 0;
    let ups2k = 0;
    let pduCount = 0;
    let convCount = 0;
    let cableMeters = 0;

    if (!activeProject || !activeTower || !activeTower?.floorsData) {
      return {};
    }

    activeTower?.floorsData.forEach((f) => {
      totalDome += f.domeCount;
      totalBullet += f.bulletCount;
      sw24Count += f.sw24Count;
      sw16Count += f.sw16Count;
      
      // Rack counts based on active project's rack size configuration
      if (f.isCabinetPlaced) {
        if (f.cabinets && f.cabinets.length > 0) {
          f.cabinets.forEach((cab: any) => {
            const type = cab.cabinetType || cab.type || "2U";
            const qty2U = type === "2U" ? (cab.quantity2U || 1) : 1;
            if (type === "2U") rack2u += qty2U;
            else if (type === "6U") rack6u += 1;
            else if (type === "10U") rack10u += 1;
            else if (type === "20U") rack20u += 1;
          });
        } else {
          const type = f.cabinetType || activeTower?.rackType || "2U";
          const qty2U = type === "2U" ? (activeTower?.quantity2U || 1) : 1;
          if (type === "2U") rack2u += qty2U;
          else if (type === "6U") rack6u += 1;
          else if (type === "10U") rack10u += 1;
          else if (type === "20U") rack20u += 1;
        }
      }

      if (f.upsType === "1K") ups1k += 1;
      else if (f.upsType === "2K") ups2k += 1;

      pduCount += f.pduCount;
      convCount += f.convCount;
      cableMeters += f.cableLength;
    });

    cableMeters = Math.round(cableMeters);

    // Build the quantified lines
    const quantities: Record<string, number> = {
      "item-cam-dome": totalDome,
      "item-cam-bullet": totalBullet,
      "item-sw-24": sw24Count,
      "item-sw-16": sw16Count,
      "item-rack-2u": rack2u,
      "item-rack-6u": rack6u,
      "item-rack-10u": rack10u,
      "item-rack-20u": rack20u,
      "item-ups-1k": ups1k,
      "item-ups-2k": ups2k,
      "item-pdu": pduCount,
      "item-converter": convCount,
      "item-cable-cat6": cableMeters,
      "item-accessories": activeTower?.floorsData.length, // Package of accessories per floor
    };

    return quantities;
  };

  const itemizedQuantities = aggregateQuantities();

  // Price calculations
  const getItemUnitPrice = (item: InventoryItem) => {
    if (activeProject && activeTower?.customPrices && activeTower?.customPrices[item.id] !== undefined) {
      return activeTower?.customPrices[item.id];
    }
    return item.basePrice;
  };

  const getSubTotal = () => {
    let subtotal = 0;
    globalInventory.forEach((item) => {
      const qty = itemizedQuantities[item.id] || 0;
      if (qty > 0) {
        subtotal += qty * getItemUnitPrice(item);
      }
    });
    return subtotal;
  };

  const subTotal = getSubTotal();
  const laborCost = Math.round(subTotal * 0.08); // 8% labor cost estimation
  const vatAmount = Math.round((subTotal + laborCost) * 0.1); // 10% VAT
  const totalProjectPrice = subTotal + laborCost + vatAmount;

  // Format currency helpers
  const formatVND = (value: number) => {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value);
  };

  // Export CSV / BOQ Table Excel Format trigger
  const handleExportCSV = () => {
    let csvContent = "data:text/csv;charset=utf-8,";
    csvContent += "Surveillance BOQ Engine Export - " + activeProject.name + "\r\n";
    csvContent += "Khach hang," + customerName + "\r\n";
    csvContent += "Dia diem," + projectLocation + "\r\n";
    csvContent += "Ngay xuat," + new Date().toLocaleDateString("vi-VN") + "\r\n\r\n";

    // Table detailed
    csvContent += "BANG TINH BOQ CHI TIET THEO TANG\r\n";
    csvContent += "Tang,So Camera,Cam Dome,Cam Than,Tu & So Cam,SW24,SW16,UPS,PDU,Converter\r\n";
    
    activeTower?.floorsData.forEach((f) => {
      const isCabinetPlaced = f.isCabinetPlaced;
      const cabinetCol = isCabinetPlaced 
        ? `Tủ ${f.cabinetType || ""} (${f.cameraQuantityInCabinet ?? 0} Cam)`
        : "-";
      csvContent += `"${f.label}",${f.camerasCount},${f.domeCount},${f.bulletCount},"${cabinetCol}",${f.sw24Count},${f.sw16Count},"${f.upsType}",${f.pduCount},${f.convCount}\r\n`;
    });

    csvContent += "\r\nBANG QUY CHUAN THIET BI TONG QUAN\r\n";
    csvContent += "Ma Vat Tu,Ten Thiet Bi,Thong So Ky Thuat,Don Vi,So Luong,Don Gia,Thanh Tien\r\n";

    globalInventory.forEach((item) => {
      const qty = itemizedQuantities[item.id] || 0;
      if (qty > 0) {
        const price = getItemUnitPrice(item);
        csvContent += `"${item.code}","${item.name}","${item.spec.replace(/"/g, '""')}","${item.unit}",${qty},${price},${qty * price}\r\n`;
      }
    });

    csvContent += `\r\n,,,,,Tong gia vat tu,${subTotal}\r\n`;
    csvContent += `,,,,,Chi phi thi cong lap dat (8%),${laborCost}\r\n`;
    csvContent += `,,,,,Thue gia tri gia tang VAT (10%),${vatAmount}\r\n`;
    csvContent += `,,,,,TONG CONG CHI PHI DU AN,${totalProjectPrice}\r\n`;

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `BOQ_${activeProject.name.replace(/\s+/g, "_")}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    addToast("Xuất bảng tính BOQ thành công!", "success");
  };

  // Save Draft Action
  const handleSaveDraft = () => {
    addToast("Tính năng lưu cấu hình đang tạm khóa ở chế độ Read-only", "info");
  };

  // Helper values for summary cards
  const totalCamerasCount = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.camerasCount, 0) || 0;
  const totalDomeCount = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.domeCount, 0) || 0;
  const totalBulletCount = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.bulletCount, 0) || 0;
  const totalSw24 = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.sw24Count, 0) || 0;
  const totalSw16 = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.sw16Count, 0) || 0;
  const totalUPS1K = activeTower?.floorsData?.filter((f) => f.upsType === "1K").length || 0;
  const totalUPS2K = activeTower?.floorsData?.filter((f) => f.upsType === "2K").length || 0;
  const totalPDU = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.pduCount, 0) || 0;
  const totalConv = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.convCount, 0) || 0;
  const totalRacks = activeTower?.floorsData?.filter((f) => f.isCabinetPlaced).length || 0;

  // Helper values for Summary BOM dashboard
  const selectedTowersForSummary = activeProject?.towers?.filter(t => selectedTowersSummary[t.id]) || [];
  
  const summaryTotalCamerasCount = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.camerasCount || 0), 0) || 0), 0);
  const summaryTotalDomeCount = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.domeCount || 0), 0) || 0), 0);
  const summaryTotalBulletCount = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.bulletCount || 0), 0) || 0), 0);
  const summaryTotalSw24 = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.sw24Count || 0), 0) || 0), 0);
  const summaryTotalSw16 = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.sw16Count || 0), 0) || 0), 0);
  const summaryTotalRacks = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.filter(f => f.isCabinetPlaced).length || 0), 0);
  const summaryTotalUPS1K = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.filter(f => f.isCabinetPlaced && f.upsType === "1K").length || 0), 0);
  const summaryTotalUPS2K = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.filter(f => f.isCabinetPlaced && f.upsType === "2K").length || 0), 0);
  const summaryTotalPDU = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.pduCount || 0), 0) || 0), 0);
  const summaryTotalConv = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.convCount || 0), 0) || 0), 0);
  const stickyHeaderStyle: React.CSSProperties = {
    position: 'sticky',
    top: '64px',
    zIndex: 20,
    backgroundColor: 'inherit',
  };

  return (
    <div className="min-h-screen bg-[#F5F7F9] font-sans text-[#191c1e] antialiased flex flex-col selection:bg-[#1A237E]/10 selection:text-[#1A237E]">
      
      {/* Dynamic Toast Alert System */}
      <div className="fixed top-4 right-4 z-50 flex flex-col gap-2 max-w-sm">
        <AnimatePresence>
          {toasts.map((t) => (
            <motion.div
              key={t.id}
              initial={{ opacity: 0, y: -20, scale: 0.95 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className={`px-4 py-3 rounded-lg shadow-lg border text-sm flex items-center gap-3 backdrop-blur-md ${
                t.type === "success" 
                  ? "bg-[#E8EAF6]/95 border-[#1A237E]/20 text-[#1A237E]" 
                  : t.type === "error" 
                  ? "bg-red-50/95 border-red-200 text-red-800" 
                  : "bg-slate-50/95 border-slate-200 text-slate-800"
              }`}
            >
              {t.type === "success" && <CheckCircle2 className="w-5 h-5 flex-shrink-0 text-[#1A237E]" />}
              {t.type === "error" && <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-600" />}
              {t.type === "info" && <Info className="w-5 h-5 flex-shrink-0 text-slate-600" />}
              <span>{t.message}</span>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {/* Main App Header */}
      <header className="bg-white border-b border-[#ECEFF1] sticky top-0 z-30">
        <div className="max-w-none w-full mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-8">
            <div className="flex items-center gap-3">
              <div className="w-9 h-9 bg-[#1A237E] rounded flex items-center justify-center text-white font-mono font-bold text-lg shadow-sm">
                S
              </div>
              <span className="font-sans font-bold text-xl tracking-tight text-[#1A237E]">
                Surveillance BOQ Engine
              </span>
            </div>

            {/* Header Tabs */}
            <nav className="hidden md:flex items-center gap-1">
              <button
                onClick={() => { setActiveTab("app"); }}
                className={`px-4 py-2 text-sm font-medium rounded transition ${
                  activeTab === "app" ? "text-[#1A237E] bg-[#E8EAF6]" : "text-[#455A64] hover:text-[#191c1e] hover:bg-slate-100"
                }`}
              >
                Workspace Thiết kế
              </button>
              <button
                onClick={() => { setActiveTab("projects"); }}
                className={`px-4 py-2 text-sm font-medium rounded transition ${
                  activeTab === "projects" ? "text-[#1A237E] bg-[#E8EAF6]" : "text-[#455A64] hover:text-[#191c1e] hover:bg-slate-100"
                }`}
              >
                Quản lý Dự án ({projects.length})
              </button>
              <button
                onClick={() => { setActiveTab("settings"); }}
                className={`px-4 py-2 text-sm font-medium rounded transition ${
                  activeTab === "settings" ? "text-[#1A237E] bg-[#E8EAF6]" : "text-[#455A64] hover:text-[#191c1e] hover:bg-slate-100"
                }`}
              >
                Cấu hình hệ thống
              </button>
              <button
                disabled
                className="px-4 py-2 text-sm font-medium rounded text-slate-400 opacity-50 cursor-not-allowed"
              >
                Kho thiết bị ({globalInventory.length})
              </button>
              <button
                disabled
                className="px-4 py-2 text-sm font-medium rounded text-slate-400 opacity-50 cursor-not-allowed"
              >
                Tiêu chuẩn kỹ thuật
              </button>
            </nav>
          </div>

          <div className="flex items-center gap-3">
            <button
              disabled
              className="px-4 py-2 border border-slate-200 text-sm font-medium text-slate-400 opacity-50 cursor-not-allowed rounded"
            >
              Save Draft
            </button>
            <button
              disabled
              className="px-4 py-2 bg-slate-100 text-slate-400 text-sm font-medium opacity-50 cursor-not-allowed rounded shadow-sm"
            >
              Export BOQ
            </button>
            
            <div className="w-8 h-8 rounded-full bg-[#1A237E]/10 border border-[#1A237E]/20 overflow-hidden flex items-center justify-center text-xs font-bold text-[#1A237E]">
              AD
            </div>
          </div>
        </div>
      </header>

      {/* Main Area layout */}
      <div className="flex-1 max-w-none w-full mx-auto px-6 py-6 flex gap-6">
        {projects.length === 0 ? (
          <div className="flex-1 flex flex-col items-center justify-center min-h-[400px] py-12">
            <div className="bg-white border border-[#ECEFF1] rounded-2xl p-8 max-w-md w-full shadow-lg text-center flex flex-col items-center gap-6">
              <div className="w-16 h-16 rounded-full bg-[#1A237E]/10 flex items-center justify-center text-[#1A237E]">
                <Building className="w-8 h-8" />
              </div>
              <div>
                <h2 className="font-bold text-xl text-[#191c1e] mb-2">Chưa có dự án nào</h2>
                <p className="text-sm text-[#455A64]">
                  Vui lòng tạo một dự án mới trước để bắt đầu thêm các tháp và tính toán BOQ.
                </p>
              </div>
              <button
                onClick={() => {
                  setActiveTab("projects");
                  setIsCreatingProject(true);
                }}
                className="w-full bg-[#1A237E] hover:bg-[#1A237E]/95 text-white font-semibold py-2.5 px-4 rounded-lg shadow-md transition flex items-center justify-center gap-2"
              >
                <Plus className="w-5 h-5" />
                <span>Tạo Dự Án Mới</span>
              </button>
            </div>
          </div>
        ) : (
          <>
        
        {/* Dynamic App Content Panels */}
        <main className="flex-1 flex flex-col gap-6 min-w-0">
          
          {/* TAB 1: App Workspace */}
          {activeTab === "app" && (
            <>
              {/* SCREEN 1: DASHBOARD (CALCULATOR + OVERVIEW CARDS + TABLE GRID) */}
              {activeNav === "dashboard" && (
                <div className="flex flex-col gap-6">
                  
                  {/* Dashboard Title */}
                  <div className="flex items-center justify-between">
                    <div>
                      <h1 className="font-sans font-bold text-2xl text-[#191c1e] tracking-tight">
                        Quản lý Tháp (Towers)
                      </h1>
                      <p className="text-sm text-[#455A64]">
                        Tính toán chi tiết thông số và phân bổ vật tư giám sát cho từng tháp
                      </p>
                    </div>
                    <div className="text-xs font-mono text-slate-400">
                      UTC: 2026-06-26 01:47:41
                    </div>
                  </div>

                  {/* Tower Tabs (Browser tab style) */}
                  <div className="flex items-center gap-1 border-b border-[#ECEFF1] overflow-x-auto whitespace-nowrap scrollbar-none pb-1">
                    {activeProject?.towers?.map((t) => (
                      <div key={t.id} className="relative group flex items-center">
                        <button
                          onClick={() => {
                            setActiveTowerId(t.id);
                            setIsSummaryTabActive(false);
                          }}
                          className={`px-4 py-2.5 text-sm font-semibold border-t-2 border-x rounded-t-lg transition-all duration-150 flex items-center gap-2 ${
                            !isSummaryTabActive && activeTowerId === t.id
                              ? "bg-white border-t-[#1A237E] border-x-[#ECEFF1] text-[#1A237E] -mb-px shadow-xs"
                              : "bg-[#F5F7F9] border-t-transparent border-x-transparent text-[#455A64] hover:text-[#191c1e] hover:bg-slate-200"
                          }`}
                        >
                          <Building className="w-4 h-4 flex-shrink-0" />
                          <span>{t.name}</span>
                          <span className="text-[10px] font-mono opacity-60">({t.floorsCount > 0 ? `${t.floorsCount} tầng` : "Chưa khởi tạo"})</span>
                        </button>
                        
                        {/* Remove Tower button */}
                          <button
                            onClick={(e) => {
                              e.stopPropagation();
                              handleDeleteTower(t.id);
                            }}
                            className="w-4 h-4 rounded-full hover:bg-red-100 hover:text-red-600 flex items-center justify-center text-[10px] text-slate-400 ml-1 hover:scale-110 transition duration-150"
                            title="Xóa Tháp"
                          >
                            ×
                          </button>
                      </div>
                    ))}
                    
                    {activeProject?.towers && activeProject.towers.length > 0 && (
                      <button
                        onClick={() => {
                          setIsSummaryTabActive(true);
                          setActiveTowerId("");
                        }}
                        className={`px-4 py-2.5 text-sm font-semibold border-t-2 border-x rounded-t-lg transition-all duration-150 flex items-center gap-2 ${
                          isSummaryTabActive
                            ? "bg-white border-t-[#E65100] border-x-[#ECEFF1] text-[#E65100] -mb-px shadow-xs"
                            : "bg-[#F5F7F9] border-t-transparent border-x-transparent text-[#455A64] hover:text-[#191c1e] hover:bg-slate-200"
                        }`}
                      >
                        <Activity className="w-4 h-4 text-[#E65100]" />
                        <span>Tổng Hợp BOM</span>
                      </button>
                    )}

                    <button
                      onClick={() => {
                        const name = prompt("Nhập tên Tháp (Tower) mới:", `Tháp ${String.fromCharCode(65 + (activeProject?.towers?.length || 0))}`);
                        if (name) {
                          handleCreateTower(name);
                          setIsSummaryTabActive(false);
                        }
                      }}
                      className="px-3 py-1.5 text-xs font-bold text-[#1A237E] hover:bg-[#E8EAF6] rounded transition flex items-center gap-1 ml-2 border border-[#1A237E]/20"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>Thêm Tháp</span>
                    </button>
                  </div>

                  {isSummaryTabActive ? (
                    /* Summary BOM Tab Content */
                    <div className="space-y-6 mt-6">
                      <div className="bg-white border border-[#ECEFF1] rounded-lg p-6 shadow-xs">
                        <h3 className="font-sans font-bold text-lg text-[#191c1e] flex items-center gap-2">
                          <Activity className="w-5 h-5 text-[#E65100]" />
                          <span>Tổng Hợp Vật Tư BOM Các Tháp</span>
                        </h3>
                        <p className="text-xs text-[#455A64] mt-1">
                          Chọn các tháp dưới đây để cộng gộp bảng thống kê vật tư BOM của dự án <strong>{activeProject.name}</strong>.
                        </p>
                        
                        <div className="mt-4 border-t border-[#ECEFF1] pt-4">
                          <div className="flex flex-wrap gap-4 items-center">
                            <span className="text-xs font-bold text-[#455A64] uppercase tracking-wide">
                              Danh sách tháp:
                            </span>
                            <div className="flex flex-wrap gap-4">
                              {activeProject?.towers?.map((t) => (
                                <label key={t.id} className="flex items-center gap-2 cursor-pointer bg-slate-50 hover:bg-slate-100 border border-[#ECEFF1] rounded px-3 py-1.5 transition text-sm font-semibold select-none">
                                  <input
                                    type="checkbox"
                                    checked={!!selectedTowersSummary[t.id]}
                                    onChange={(e) => {
                                      setSelectedTowersSummary(prev => ({
                                        ...prev,
                                        [t.id]: e.target.checked
                                      }));
                                    }}
                                    className="accent-[#E65100]"
                                  />
                                  <span>{t.name}</span>
                                  <span className="text-xs text-slate-400 font-mono">({t.floorsCount} tầng)</span>
                                </label>
                              ))}
                            </div>
                          </div>
                          
                          <div className="mt-6 flex flex-wrap gap-3">
                            <button
                              onClick={() => {
                                const next: Record<string, boolean> = {};
                                activeProject.towers?.forEach(t => {
                                  next[t.id] = true;
                                });
                                setSelectedTowersSummary(next);
                              }}
                              className="bg-slate-200 hover:bg-slate-300 text-slate-700 font-bold px-3 py-1.5 rounded text-xs transition"
                            >
                              Chọn tất cả
                            </button>
                            <button
                              onClick={() => {
                                setSelectedTowersSummary({});
                              }}
                              className="bg-slate-200 hover:bg-slate-300 text-slate-700 font-bold px-3 py-1.5 rounded text-xs transition"
                            >
                              Bỏ chọn tất cả
                            </button>
                            <div className="flex items-center gap-2 ml-auto">
                              <button
                                onClick={handleCalculateSummary}
                                disabled={isCalculatingSummary}
                                className="bg-[#E65100] hover:bg-[#E65100]/95 disabled:bg-slate-300 text-white font-bold px-5 py-1.5 rounded text-xs transition flex items-center gap-1.5 shadow-sm"
                              >
                                {isCalculatingSummary ? (
                                  <>
                                    <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                                    <span>Đang tính toán...</span>
                                  </>
                                ) : (
                                  <>
                                    <RefreshCw className="w-3.5 h-3.5" />
                                    <span>Tính tổng BOM</span>
                                  </>
                                )}
                              </button>

                              <button
                                onClick={handleExportExcel}
                                disabled={isExportingExcel}
                                className="bg-[#2e7d32] hover:bg-[#1b5e20] disabled:bg-slate-300 text-white font-bold px-5 py-1.5 rounded text-xs transition flex items-center gap-1.5 shadow-sm"
                              >
                                {isExportingExcel ? (
                                  <>
                                    <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                                    <span>Đang xuất...</span>
                                  </>
                                ) : (
                                  <>
                                    <FileSpreadsheet className="w-3.5 h-3.5" />
                                    <span>Xuất Excel</span>
                                  </>
                                )}
                              </button>
                            </div>
                          </div>
                        </div>
                      </div>

                      {summaryBomData ? (
                        <div className="flex flex-col gap-6 w-full">
                          {/* Mini Dashboard for Summary BOM */}
                          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            
                            {/* Left Block: CAMERA SYSTEM */}
                            <div className="bg-white border-l-4 border-l-[#E65100] border border-y-[#ECEFF1] border-r-[#ECEFF1] rounded-r-lg p-5 flex flex-col justify-between shadow-xs">
                              <div>
                                <div className="text-xs font-bold text-[#455A64] uppercase tracking-wider mb-2">
                                  HỆ THỐNG CAMERA
                                </div>
                                <div className="flex items-baseline gap-2">
                                  <span className="font-sans font-extrabold text-4xl text-[#E65100]">
                                    {summaryTotalCamerasCount}
                                  </span>
                                  <span className="text-sm text-[#455A64] font-medium">
                                    Tổng thiết bị
                                  </span>
                                </div>
                              </div>

                              <div className="grid grid-cols-2 gap-4 mt-6 pt-4 border-t border-slate-100">
                                <div>
                                  <div className="text-sm font-bold text-[#191c1e] font-mono">
                                    {summaryTotalDomeCount}
                                  </div>
                                  <div className="text-xs text-[#455A64] font-medium">
                                    Camera Dome (Bán cầu)
                                  </div>
                                </div>
                                <div>
                                  <div className="text-sm font-bold text-[#191c1e] font-mono">
                                    {summaryTotalBulletCount}
                                  </div>
                                  <div className="text-xs text-[#455A64] font-medium">
                                    Camera Thân (Bullet)
                                  </div>
                                </div>
                              </div>
                            </div>

                            {/* Right Block: RACK, NETWORK & ACCESSORIES */}
                            <div className="bg-white border-l-4 border-l-[#455A64] border border-y-[#ECEFF1] border-r-[#ECEFF1] rounded-r-lg p-5 shadow-xs">
                              <div className="text-xs font-bold text-[#455A64] uppercase tracking-wider mb-2">
                                TỦ, MẠNG & PHỤ KIỆN
                              </div>
                              <div className="flex items-baseline gap-2 mb-4">
                                <span className="font-sans font-extrabold text-4xl text-[#455A64]">
                                  {summaryTotalSw24 + summaryTotalSw16}
                                </span>
                                <span className="text-sm text-[#455A64] font-medium">
                                  Switch PoE
                                </span>
                              </div>

                              <div className="grid grid-cols-3 gap-y-4 gap-x-2 pt-3 border-t border-slate-100">
                                <div>
                                  <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                    TỦ RACK
                                  </div>
                                  <div className="text-base font-bold text-[#191c1e] font-mono">
                                    {summaryTotalRacks}
                                  </div>
                                </div>
                                <div>
                                  <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                    SW24
                                  </div>
                                  <div className="text-base font-bold text-[#191c1e] font-mono">
                                    {summaryTotalSw24}
                                  </div>
                                </div>
                                <div>
                                  <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                    SW16
                                  </div>
                                  <div className="text-base font-bold text-[#191c1e] font-mono">
                                    {summaryTotalSw16}
                                  </div>
                                </div>
                                <div>
                                  <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                    UPS
                                  </div>
                                  <div className="text-base font-bold text-[#191c1e] font-mono">
                                    {summaryTotalUPS1K + summaryTotalUPS2K}
                                  </div>
                                </div>
                                <div>
                                  <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                    PDU
                                  </div>
                                  <div className="text-base font-bold text-[#191c1e] font-mono">
                                    {summaryTotalPDU}
                                  </div>
                                </div>
                                <div>
                                  <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                    CONV.
                                  </div>
                                  <div className="text-base font-bold text-[#191c1e] font-mono">
                                    {summaryTotalConv}
                                  </div>
                                </div>
                              </div>
                            </div>

                          </div>

                          {/* Table Card */}
                          <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs overflow-hidden w-full">
                            <div className="px-6 py-4 border-b border-[#ECEFF1] flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-slate-50/50">
                              <div>
                                <h3 className="font-sans font-bold text-base text-[#191c1e]">
                                  Khung BOM Tổng Hợp Thiết Bị &amp; Vật Tư
                                </h3>
                                <p className="text-xs text-[#455A64]">
                                  Khung sườn danh mục vật tư chính, phụ kiện tổng cộng từ các tháp đã chọn
                                </p>
                              </div>
                            </div>
                            
                            <div className="sticky-table-wrapper p-4 bg-slate-50/30">
                              <div className="border border-slate-200 rounded overflow-hidden shadow-xs bg-white min-w-[850px]">
                                
                                <div className="border-b border-slate-200 bg-[#F8F9FA] px-4 py-3 text-center">
                                  <div className="text-sm font-sans font-bold text-[#E65100] uppercase tracking-wide">
                                    BẢNG TỔNG HỢP VẬT TƯ BOM - DỰ ÁN {activeProject?.name?.toUpperCase()}
                                  </div>
                                </div>

                                <table className="w-full text-xs text-left border-collapse font-sans">
                                  <thead>
                                    <tr className="bg-[#E8EAED] text-[#3c4043] font-bold text-center border-b border-slate-300 divide-x divide-slate-200 select-none">
                                      <th className="py-2 px-1 text-center w-12">STT</th>
                                      <th className="py-2 px-2 text-left w-52">VẬT TƯ</th>
                                      <th className="py-2 px-2 text-left w-72">MÔ TẢ</th>
                                      <th className="py-2 px-1 w-16">Đ.VỊ</th>
                                      <th className="py-2 px-1 w-16">SLG</th>
                                      <th className="py-2 px-1 w-20">Nhân công</th>
                                      <th className="py-2 px-1 w-20">Tổng công</th>
                                      <th className="py-2 px-2 text-left w-44">GHI CHÚ</th>
                                    </tr>
                                  </thead>
                                  <tbody className="divide-y divide-slate-200 font-sans">
                                    {BOM_ITEMS.map((item, idx) => {
                                      if (item.stt === "" && item.unit === "") {
                                        // Category Header Row
                                        return (
                                          <tr key={idx} className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                                            <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                                              {item.name}
                                            </td>
                                          </tr>
                                        );
                                      }

                                      const quantity = item.field ? (summaryBomData?.[item.field] ?? 0) : 0;
                                      
                                      const isYellow = item.stt === "1.1" || item.stt === "1.2" || item.name === "Vật tư phụ";
                                      const rowClass = isYellow
                                        ? "divide-x divide-slate-200 bg-yellow-100/70 hover:bg-yellow-100 transition"
                                        : "divide-x divide-slate-200 hover:bg-slate-50/50 transition";

                                      const sttClass = isYellow
                                        ? "py-2.5 px-1 text-center font-semibold text-slate-700"
                                        : item.stt === "-" 
                                          ? "py-2.5 px-1 text-center text-slate-400"
                                          : "py-2.5 px-1 text-center font-semibold text-slate-600";

                                      const nameClass = `py-2.5 px-2 font-semibold text-slate-800 leading-tight ${item.subLevel ? "pl-6" : ""}`;
                                      const unitClass = isYellow 
                                        ? "py-2.5 px-1 text-center text-slate-700 font-semibold"
                                        : "py-2.5 px-1 text-center text-slate-700";

                                      const noteKey = `summary_${item.noteKey}`;

                                      return (
                                        <tr key={idx} className={rowClass}>
                                          <td className={sttClass}>{item.stt}</td>
                                          <td className={nameClass}>{item.name}</td>
                                          <td className="py-2.5 px-2 text-slate-600">{item.desc || ""}</td>
                                          <td className={unitClass}>{item.unit}</td>
                                          <td className="py-2.5 px-1 text-center font-mono">{quantity}</td>
                                          {renderLaborCells(item.name, quantity)}
                                          {renderNoteCell(noteKey)}
                                        </tr>
                                      );
                                    })}
                                  </tbody>
                                </table>
                              </div>
                            </div>
                          </div>
                        </div>
                      ) : (
                        <div className="bg-white border border-[#ECEFF1] rounded-lg p-12 text-center text-slate-400">
                          <Activity className="w-12 h-12 mx-auto text-slate-300 mb-3" />
                          <p className="text-sm font-semibold">Chưa có dữ liệu tổng hợp BOM</p>
                          <p className="text-xs text-slate-400 mt-1">Vui lòng chọn các tháp và nhấn nút "Tính tổng BOM" ở trên.</p>
                        </div>
                      )}
                    </div>
                  ) : !activeTower ? (
                    /* No towers: show empty state */
                    <div className="bg-white border border-[#ECEFF1] rounded-lg p-12 shadow-xs flex flex-col items-center justify-center max-w-md mx-auto text-center gap-6 my-12 animate-in fade-in zoom-in-95 duration-200">
                      <div className="w-14 h-14 rounded-full bg-[#1A237E]/10 flex items-center justify-center text-[#1A237E]">
                        <Building className="w-7 h-7" />
                      </div>
                      <div>
                        <h3 className="font-sans font-bold text-lg text-[#191c1e]">
                          Chưa có tháp nào
                        </h3>
                        <p className="text-sm text-[#455A64] mt-1">
                          Bấm nút "Thêm Tháp" ở trên để bắt đầu cấu hình tháp cho dự án {activeProject.name}.
                        </p>
                      </div>
                      <button
                        onClick={() => {
                          const name = prompt("Nhập tên Tháp (Tower) mới:", `Tháp ${String.fromCharCode(65 + (activeProject?.towers?.length || 0))}`);
                          if (name) {
                            handleCreateTower(name);
                          }
                        }}
                        className="bg-[#1A237E] hover:bg-[#1A237E]/95 text-white py-2.5 px-6 rounded text-sm font-semibold shadow-sm transition flex items-center justify-center gap-2"
                      >
                        <Plus className="w-4 h-4" />
                        <span>Thêm Tháp đầu tiên</span>
                      </button>
                    </div>
                  ) : activeTower.floorsCount === 0 || activeTower.floorsData.length === 0 ? (
                    /* Initial state: Only show Floor input */
                    <div className="bg-white border border-[#ECEFF1] rounded-lg p-8 shadow-xs flex flex-col items-center justify-center max-w-md mx-auto text-center gap-6 my-12 animate-in fade-in zoom-in-95 duration-200">
                      <div className="w-12 h-12 rounded-full bg-[#1A237E]/10 flex items-center justify-center text-[#1A237E]">
                        <Building className="w-6 h-6 animate-pulse" />
                      </div>
                      <div>
                        <h3 className="font-sans font-bold text-lg text-[#191c1e]">
                          Khởi tạo cấu trúc cho {activeProject.name}
                        </h3>
                        <p className="text-sm text-[#455A64] mt-1">
                          Nhập cấu trúc tầng nổi, tầng hầm và thông số kỹ thuật để bắt đầu cấu hình hệ thống.
                        </p>
                      </div>
                      
                      <div className="w-full grid grid-cols-2 gap-4">
                        <div>
                          <label className="block text-left text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                            Số tầng nổi
                          </label>
                          <input
                            type="number"
                            min="1"
                            placeholder="Ví dụ: 5, 10..."
                            value={tempFloors === 0 ? "" : tempFloors}
                            onFocus={(e) => e.target.select()}
                            onChange={(e) => setTempFloors(Math.max(0, parseInt(e.target.value) || 0))}
                            className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-base font-semibold text-center focus:border-[#1A237E] focus:outline-none transition font-mono"
                          />
                        </div>
                        <div>
                          <label className="block text-left text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                            Số tầng hầm
                          </label>
                          <input
                            type="number"
                            min="0"
                            placeholder="0"
                            value={tempBasements === 0 ? "" : tempBasements}
                            onFocus={(e) => e.target.select()}
                            onChange={(e) => setTempBasements(Math.max(0, parseInt(e.target.value) || 0))}
                            className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-base font-semibold text-center focus:border-[#1A237E] focus:outline-none transition font-mono"
                          />
                        </div>
                      </div>

                      <div className="w-full grid grid-cols-3 gap-4">
                        <div>
                          <label className="block text-left text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                            K.cách ngang (m)
                          </label>
                          <input
                            type="number"
                            min="1"
                            placeholder="0"
                            value={tempH === 0 ? "" : tempH}
                            onFocus={(e) => e.target.select()}
                            onChange={(e) => setTempH(Math.max(0, parseInt(e.target.value) || 0))}
                            className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-base font-semibold text-center focus:border-[#1A237E] focus:outline-none transition font-mono"
                          />
                        </div>
                        <div>
                          <label className="block text-left text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                            K.cách dọc (m)
                          </label>
                          <input
                            type="number"
                            min="1"
                            placeholder="0"
                            value={tempV === 0 ? "" : tempV}
                            onFocus={(e) => e.target.select()}
                            onChange={(e) => setTempV(Math.max(0, parseInt(e.target.value) || 0))}
                            className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-base font-semibold text-center focus:border-[#1A237E] focus:outline-none transition font-mono"
                          />
                        </div>
                        <div className="flex flex-col gap-4">
                          <div>
                            <label className="block text-left text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                              Loại tủ
                            </label>
                            <select
                              value={tempRack}
                              onChange={(e) => setTempRack(e.target.value as any)}
                              className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-base font-semibold text-center focus:border-[#1A237E] focus:outline-none transition"
                            >
                              <option value="2U">2U</option>
                              <option value="6U">6U</option>
                              <option value="10U">10U</option>
                            </select>
                          </div>
                          {tempRack === "2U" && (
                            <div>
                              <label className="block text-left text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                                Số lượng 2U / tầng
                              </label>
                              <input
                                type="number"
                                min="1"
                                value={tempQuantity2U}
                                onChange={(e) => setTempQuantity2U(Math.max(1, parseInt(e.target.value) || 1))}
                                className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-base font-semibold text-center focus:border-[#1A237E] focus:outline-none transition font-mono"
                              />
                            </div>
                          )}
                        </div>
                      </div>

                      <div className="w-full flex items-center justify-start gap-2 pt-1 border-t border-slate-100">
                        <input
                          type="checkbox"
                          id="initHasRoofCheckbox"
                          checked={tempHasRoof}
                          onChange={(e) => setTempHasRoof(e.target.checked)}
                          className="w-4.5 h-4.5 text-[#1A237E] border-gray-300 rounded focus:ring-[#1A237E]"
                        />
                        <label htmlFor="initHasRoofCheckbox" className="text-sm font-semibold text-[#455A64] select-none cursor-pointer">
                          Có Tầng Mái (Tum / Mái)
                        </label>
                      </div>

                      <button
                        onClick={async () => {
                          if (tempFloors <= 0) {
                            addToast("Vui lòng nhập số tầng nổi lớn hơn 0!", "error");
                            return;
                          }
                          try {
                            const response = await fetch(`${API_BASE}/towers`, {
                              method: "POST",
                              headers: { "Content-Type": "application/json" },
                              body: JSON.stringify({
                                projectId: activeProject.id,
                                configId: "a2b0a797-8ff2-4a79-ac5d-78525bd25e90",
                                name: activeTower?.name || "Tháp A",
                                floorCount: tempFloors,
                                basementCount: tempBasements,
                                hasRoof: tempHasRoof,
                                widthLength: tempH,
                                heightLength: tempV,
                                quantity2U: tempRack === "2U" ? tempQuantity2U : 1
                              })
                            });
                            if (!response.ok) throw new Error("Failed to create tower");
                            addToast(`Khởi tạo thành công cấu trúc cho ${activeProject.name}!`, "success");
                            await fetchProjects(activeProject.id);
                            const defaultFloors = calculateProjectBOQ(
                              tempFloors,
                              tempH,
                              tempV,
                              tempRack,
                              activeTower?.siteParams || DEFAULT_SITE_PARAMS,
                              activeTower?.hardwareLogic || DEFAULT_HARDWARE_LOGIC,
                              [],
                              tempBasements,
                              tempHasRoof
                            );
                            fetchCabinetPlacement(
                              tempFloors,
                              tempBasements,
                              tempHasRoof,
                              tempH,
                              tempV,
                              tempRack,
                              defaultFloors,
                              calculationMode,
                              manualGroups,
                              tempRack === "2U" ? tempQuantity2U : 1
                            );
                          } catch (err) {
                            console.error("Error creating tower", err);
                            addToast("Lỗi khi khởi tạo tháp trên backend!", "error");
                          }
                        }}
                        className="w-full bg-[#1A237E] hover:bg-[#1A237E]/95 text-white py-2.5 px-4 rounded text-sm font-semibold shadow-sm transition flex items-center justify-center gap-2"
                      >
                        <RefreshCw className="w-4 h-4" />
                        <span>Khởi tạo tháp &amp; Bắt đầu</span>
                      </button>
                    </div>
                  ) : (
                    /* Full UI: Show remaining inputs + table */
                    <>
                      {/* Summary Cards section - "Tổng quan tháp" */}
                      <div className="flex flex-col gap-3">
                        <h2 className="font-sans font-bold text-lg text-[#191c1e] tracking-tight">
                          Tổng quan {activeProject.name}
                        </h2>

                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                          
                          {/* Left Block: CAMERA SYSTEM */}
                          <div className="bg-white border-l-4 border-l-[#1A237E] border border-y-[#ECEFF1] border-r-[#ECEFF1] rounded-r-lg p-5 flex flex-col justify-between">
                            <div>
                              <div className="text-xs font-bold text-[#455A64] uppercase tracking-wider mb-2">
                                HỆ THỐNG CAMERA
                              </div>
                              <div className="flex items-baseline gap-2">
                                <span className="font-sans font-extrabold text-4xl text-[#1A237E]">
                                  {totalCamerasCount}
                                </span>
                                <span className="text-sm text-[#455A64] font-medium">
                                  Tổng thiết bị
                                </span>
                              </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4 mt-6 pt-4 border-t border-slate-100">
                              <div>
                                <div className="text-sm font-bold text-[#191c1e] font-mono">
                                  {totalDomeCount}
                                </div>
                                <div className="text-xs text-[#455A64] font-medium">
                                  Camera Dome (Bán cầu)
                                </div>
                              </div>
                              <div>
                                <div className="text-sm font-bold text-[#191c1e] font-mono">
                                  {totalBulletCount}
                                </div>
                                <div className="text-xs text-[#455A64] font-medium">
                                  Camera Thân (Bullet)
                                </div>
                              </div>
                            </div>
                          </div>

                          {/* Right Block: RACK, NETWORK & ACCESSORIES */}
                          <div className="bg-white border-l-4 border-l-[#455A64] border border-y-[#ECEFF1] border-r-[#ECEFF1] rounded-r-lg p-5">
                            <div className="text-xs font-bold text-[#455A64] uppercase tracking-wider mb-2">
                              TỦ, MẠNG & PHỤ KIỆN
                            </div>
                            <div className="flex items-baseline gap-2 mb-4">
                              <span className="font-sans font-extrabold text-4xl text-[#455A64]">
                                {totalSw24 + totalSw16}
                              </span>
                              <span className="text-sm text-[#455A64] font-medium">
                                Switch PoE
                              </span>
                            </div>

                            <div className="grid grid-cols-3 gap-y-4 gap-x-2 pt-3 border-t border-slate-100">
                              <div>
                                <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                  TỦ RACK
                                </div>
                                <div className="text-base font-bold text-[#191c1e] font-mono">
                                  {totalRacks}
                                </div>
                              </div>
                              <div>
                                <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                  SW24
                                </div>
                                <div className="text-base font-bold text-[#191c1e] font-mono">
                                  {totalSw24}
                                </div>
                              </div>
                              <div>
                                <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                  SW16
                                </div>
                                <div className="text-base font-bold text-[#191c1e] font-mono">
                                  {totalSw16}
                                </div>
                              </div>
                              <div>
                                <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                  UPS
                                </div>
                                <div className="text-base font-bold text-[#191c1e] font-mono">
                                  {totalUPS1K + totalUPS2K}
                                </div>
                              </div>
                              <div>
                                <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                  PDU
                                </div>
                                <div className="text-base font-bold text-[#191c1e] font-mono">
                                  {totalPDU}
                                </div>
                              </div>
                              <div>
                                <div className="text-xs text-slate-400 font-bold uppercase tracking-wide">
                                  CONV.
                                </div>
                                <div className="text-base font-bold text-[#191c1e] font-mono">
                                  {totalConv}
                                </div>
                              </div>
                            </div>
                          </div>

                        </div>
                      </div>

                      {/* Top Calculator Input Section */}
                      <div className="bg-white border border-[#ECEFF1] rounded-lg p-4 shadow-xs">
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 lg:grid-cols-8 gap-4 items-end">
                          
                           {/* Floor Input */}
                          <div>
                            <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                              Tầng nổi
                            </label>
                            <input
                              type="number"
                              value={tempFloors === 0 ? "" : tempFloors}
                              placeholder="0"
                              onFocus={(e) => e.target.select()}
                              onChange={(e) => setTempFloors(Math.max(0, parseInt(e.target.value) || 0))}
                              className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono text-center"
                            />
                          </div>

                          {/* Basement Input */}
                          <div>
                            <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                              Tầng hầm
                            </label>
                            <input
                              type="number"
                              value={tempBasements === 0 ? "" : tempBasements}
                              placeholder="0"
                              onFocus={(e) => e.target.select()}
                              onChange={(e) => setTempBasements(Math.max(0, parseInt(e.target.value) || 0))}
                              className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono text-center"
                            />
                          </div>

                          {/* Roof Checkbox */}
                          <div className="flex flex-col justify-end h-[62px]">
                            <div className="flex items-center gap-2 bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-[9px] hover:border-slate-300 transition cursor-pointer select-none">
                              <input
                                type="checkbox"
                                id="topHasRoofCheckbox"
                                checked={tempHasRoof}
                                onChange={(e) => setTempHasRoof(e.target.checked)}
                                className="w-4 h-4 text-[#1A237E] border-gray-300 rounded focus:ring-[#1A237E]"
                              />
                              <label htmlFor="topHasRoofCheckbox" className="text-xs font-bold text-[#455A64] uppercase tracking-wide cursor-pointer">
                                Tầng Mái
                              </label>
                            </div>
                          </div>

                          {/* Horizontal Run */}
                          <div>
                            <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                              K.Cách ngang (m)
                            </label>
                            <input
                              type="number"
                              value={tempH === 0 ? "" : tempH}
                              placeholder="0"
                              onFocus={(e) => e.target.select()}
                              onChange={(e) => setTempH(Math.max(0, parseInt(e.target.value) || 0))}
                              className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono text-center"
                            />
                          </div>

                          {/* Vertical Run */}
                          <div>
                            <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                              K.Cách dọc (m)
                            </label>
                            <input
                              type="number"
                              value={tempV === 0 ? "" : tempV}
                              placeholder="0"
                              onFocus={(e) => e.target.select()}
                              onChange={(e) => setTempV(Math.max(0, parseInt(e.target.value) || 0))}
                              className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono text-center"
                            />
                          </div>

                          {/* Cabinet Type Selector */}
                          <div className="flex flex-col gap-4">
                            <div>
                              <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                                Loại Tủ
                              </label>
                              <select
                                value={tempRack}
                                onChange={(e) => setTempRack(e.target.value as any)}
                                className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition text-center"
                              >
                                <option value="2U">2U</option>
                                <option value="6U">6U</option>
                                <option value="10U">10U</option>
                              </select>
                            </div>
                            {tempRack === "2U" && (
                              <div>
                                <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                                  Số lượng 2U / tầng
                                </label>
                                <input
                                  type="number"
                                  min="1"
                                  value={tempQuantity2U}
                                  onChange={(e) => setTempQuantity2U(Math.max(1, parseInt(e.target.value) || 1))}
                                  className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono text-center"
                                />
                              </div>
                            )}
                          </div>

                          {/* Compute Trigger Button */}
                          <div className="lg:col-span-1">
                            <button
                              onClick={handleRecalculate}
                              className="w-full bg-[#1A237E] hover:bg-[#1A237E]/95 text-white py-2 px-4 rounded text-sm font-semibold shadow-xs transition flex items-center justify-center gap-1.5 h-[38px] whitespace-nowrap"
                            >
                              <RefreshCw className="w-4 h-4" />
                              <span>Tính toán</span>
                            </button>
                          </div>

                          {/* Reset Button */}
                          <div className="lg:col-span-1">
                            <button
                              onClick={handleResetBOQ}
                              className="w-full bg-red-50 hover:bg-red-100 text-red-700 border border-red-200 py-2 px-4 rounded text-sm font-semibold shadow-xs transition flex items-center justify-center gap-1.5 h-[38px] whitespace-nowrap"
                            >
                              <Trash2 className="w-4 h-4" />
                              <span>Reset</span>
                            </button>
                          </div>

                        </div>
                      </div>

                      {/* Grid Container for Excel Table (Left) and BOQ Table (Right) */}
                      <div id="bom-split-container" className="flex flex-col xl:flex-row gap-0 items-start w-full relative">
                        
                        {/* Excel-like BOQ Template Table (Left) */}
                        <div 
                          className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs w-full"
                          style={isXl ? { width: `calc(${leftWidth}% - 12px)`, flexShrink: 0 } : {}}
                        >
                          <div className="px-6 py-4 border-b border-[#ECEFF1] flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-slate-50/50">
                            <div>
                              <h3 className="font-sans font-bold text-base text-[#191c1e]">
                                Khung BOQ Thiết bị &amp; Vật tư (Mẫu Excel)
                              </h3>
                              <p className="text-xs text-[#455A64]">
                                Khung sườn danh mục vật tư chính, phụ kiện và nhân công lắp đặt
                              </p>
                            </div>
                            <div className="text-xs font-mono text-[#1A237E] bg-[#E8EAF6] px-2.5 py-1 rounded font-bold border border-[#1A237E]/20">
                              EXCEL SKELETON
                            </div>
                          </div>

                          {/* Excel Grid container */}
                          <div className="overflow-x-auto xl:overflow-visible p-4 bg-slate-50/30">
                            <div className="border border-slate-200 rounded overflow-visible shadow-xs bg-white min-w-[850px]">
                              
                              {/* Spreadsheet Title Block */}
                              <div className="border-b border-slate-200 bg-[#F8F9FA] px-4 py-3 text-center">
                                <div className="text-sm font-sans font-bold text-[#1A237E] uppercase tracking-wide">
                                  KHỐI LƯỢNG BOQ GÓI 62 CAMERA HÀNH LANG LÔ E2 (BLOCK H)
                                </div>
                              </div>

                              <table className="w-full text-xs text-left border-collapse font-sans">
                                <thead className="bg-[#E8EAED] shadow-xs">
                                  {/* Main Table Column Titles */}
                                  <tr className="bg-[#E8EAED] text-[#3c4043] font-bold text-center border-b border-slate-300 divide-x divide-slate-200 select-none">
                                    <th style={stickyHeaderStyle} className="py-2 px-1 text-center w-12">STT</th>
                                    <th style={stickyHeaderStyle} className="py-2 px-2 text-left w-52">VẬT TƯ</th>
                                    <th style={stickyHeaderStyle} className="py-2 px-2 text-left w-72">MÔ TẢ</th>
                                    <th style={stickyHeaderStyle} className="py-2 px-1 w-16">Đ.VỊ</th>
                                    <th style={stickyHeaderStyle} className="py-2 px-1 w-16">SLG</th>
                                    <th style={stickyHeaderStyle} className="py-2 px-1 w-20">Nhân công</th>
                                    <th style={stickyHeaderStyle} className="py-2 px-1 w-20">Tổng công</th>
                                    <th style={stickyHeaderStyle} className="py-2 px-2 text-left w-44">GHI CHÚ</th>
                                  </tr>
                                </thead>
                                <tbody className="divide-y divide-slate-200 font-sans">
                                  {BOM_ITEMS.map((item, idx) => {
                                    if (item.stt === "" && item.unit === "") {
                                      // Category Header Row
                                      return (
                                        <tr key={idx} className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                                          <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                                            {item.name}
                                          </td>
                                        </tr>
                                      );
                                    }

                                    const quantity = item.field
                                      ? (customBOMOverrides[activeTower?.id || ""]?.[item.field] ?? bomData?.[item.field] ?? 0)
                                      : 0;

                                    const isEditableField = [
                                      "fiberCableQuantity",

                                      "cabinet32UQuantity",
                                      "cabinet42UQuantity",
                                      "ups3000Quantity",
                                      "chickenTubeQuantity",
                                      "electricTubeQuantity",
                                       "cvvCable"
                                    ].includes(item.field || "");
                                    
                                    const isYellow = item.stt === "1.1" || item.stt === "1.2" || item.name === "Vật tư phụ";
                                    const rowClass = isYellow
                                      ? "divide-x divide-slate-200 bg-yellow-100/70 hover:bg-yellow-100 transition"
                                      : "divide-x divide-slate-200 hover:bg-slate-50/50 transition";

                                    const sttClass = isYellow
                                      ? "py-2.5 px-1 text-center font-semibold text-slate-700"
                                      : item.stt === "-" 
                                        ? "py-2.5 px-1 text-center text-slate-400"
                                        : "py-2.5 px-1 text-center font-semibold text-slate-600";

                                    const nameClass = `py-2.5 px-2 font-semibold text-slate-800 leading-tight ${item.subLevel ? "pl-6" : ""}`;
                                    const unitClass = isYellow 
                                      ? "py-2.5 px-1 text-center text-slate-700 font-semibold"
                                      : "py-2.5 px-1 text-center text-slate-700";

                                    const noteKey = item.noteKey;

                                    return (
                                      <tr key={idx} className={rowClass}>
                                        <td className={sttClass}>{item.stt}</td>
                                        <td className={nameClass}>{item.name}</td>
                                        <td className="py-2.5 px-2 text-slate-600">{item.desc || ""}</td>
                                        <td className={unitClass}>{item.unit}</td>
                                        <td className="py-1 px-1 text-center font-mono">
                                          {isEditableField && activeTower ? (
                                            <input
                                              type="number"
                                              min="0"
                                              value={quantity === 0 ? "" : quantity}
                                              placeholder="0"
                                              onChange={(e) => {
                                                const val = Math.max(0, parseFloat(e.target.value) || 0);
                                                setCustomBOMOverrides(prev => ({
                                                  ...prev,
                                                  [activeTower.id]: {
                                                    ...(prev[activeTower.id] || {}),
                                                    [item.field!]: val
                                                  }
                                                }));
                                              }}
                                              className="w-20 bg-white border border-slate-200 focus:border-[#1A237E] rounded px-1 text-center font-mono text-xs focus:outline-none py-0.5 focus:ring-1 focus:ring-[#1A237E]"
                                            />
                                          ) : (
                                            quantity
                                          )}
                                        </td>
                                        {renderLaborCells(item.name, quantity)}
                                        {renderNoteCell(noteKey)}
                                      </tr>
                                    );
                                  })}
                                </tbody>
                              </table>
                            </div>
                          </div>
                        </div>

                        {/* Split Resizer Bar */}
                        {isXl && (
                          <div
                            onMouseDown={startDrag}
                            onTouchStart={startDrag}
                            className={`w-6 self-stretch flex items-center justify-center cursor-col-resize group relative z-10 select-none flex-shrink-0`}
                          >
                            <div className={`w-[2px] h-full bg-slate-200 group-hover:bg-indigo-500 transition-colors ${isDragging ? 'bg-indigo-600 w-[3px]' : ''}`} />
                            <div className={`absolute top-1/2 -translate-y-1/2 w-4 h-8 bg-white border border-slate-200 rounded-md shadow-sm flex flex-col gap-[3px] items-center justify-center group-hover:border-indigo-400 transition-colors ${isDragging ? 'border-indigo-500 ring-1 ring-indigo-100' : ''}`}>
                              <div className="w-[1.5px] h-3 bg-slate-400 group-hover:bg-indigo-500" />
                              <div className="w-[1.5px] h-3 bg-slate-400 group-hover:bg-indigo-500" />
                            </div>
                          </div>
                        )}

                        {/* Detailed Interactive BOQ Sheet */}
                        <div 
                          className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs w-full"
                          style={isXl ? { width: `calc(${100 - leftWidth}% - 12px)`, flexShrink: 0 } : {}}
                        >
                        <div className="px-6 py-4 border-b border-[#ECEFF1] flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-slate-50/50">
                          <div>
                            <h3 className="font-sans font-bold text-base text-[#191c1e]">
                              Bảng tính BOQ chi tiết
                            </h3>
                            <p className="text-xs text-[#455A64]">
                              Nhấp trực tiếp vào ô để thay đổi số lượng camera của từng tầng hoặc chọn nhiều tầng để đồng bộ nhanh
                            </p>
                          </div>

                          {/* Calculation Mode Switcher & Actions */}
                          <div className="flex items-center gap-4 self-end sm:self-auto">
                            <div className="flex items-center bg-slate-100 p-0.5 rounded-lg border border-slate-200/60 shadow-xs">
                              <button
                                onClick={() => {
                                  setCalculationMode("auto");
                                  updateTowerFloorsData(activeTower?.floorsData || [], manualGroups, "auto");
                                  fetchCabinetPlacement(
                                    tempFloors,
                                    tempBasements,
                                    tempHasRoof,
                                    tempH,
                                    tempV,
                                    tempRack,
                                    activeTower?.floorsData || [],
                                    "auto",
                                    manualGroups,
                                    tempRack === "2U" ? tempQuantity2U : 1
                                  );
                                }}
                                className={`px-3 py-1 text-xs font-semibold font-sans transition-all duration-200 rounded-md ${
                                  calculationMode === "auto"
                                    ? "bg-white text-[#1A237E] shadow-sm font-bold"
                                    : "text-slate-500 hover:text-slate-800"
                                }`}
                              >
                                Tự động tối ưu
                              </button>
                              <button
                                onClick={() => {
                                  setCalculationMode("manual");
                                  updateTowerFloorsData(activeTower?.floorsData || [], manualGroups, "manual");
                                  fetchCabinetPlacement(
                                    tempFloors,
                                    tempBasements,
                                    tempHasRoof,
                                    tempH,
                                    tempV,
                                    tempRack,
                                    activeTower?.floorsData || [],
                                    "manual",
                                    manualGroups,
                                    tempRack === "2U" ? tempQuantity2U : 1
                                  );
                                }}
                                className={`px-3 py-1 text-xs font-semibold font-sans transition-all duration-200 rounded-md ${
                                  calculationMode === "manual"
                                    ? "bg-emerald-600 text-white shadow-sm font-bold"
                                    : "text-slate-500 hover:text-slate-800"
                                }`}
                              >
                                Phân nhóm thủ công
                              </button>
                            </div>

                            <button
                              onClick={handleExportCSV}
                              title="Tải về file excel CSV"
                              className="p-1.5 text-[#455A64] hover:text-[#1A237E] hover:bg-slate-100 rounded transition"
                            >
                              <Download className="w-5 h-5" />
                            </button>
                          </div>
                        </div>

                        <div className="overflow-x-auto xl:overflow-visible">
                          <table className="w-full text-left border-collapse min-w-[900px]">
                            <thead className="bg-slate-50 shadow-xs">
                              <tr className="bg-slate-50 border-b border-[#ECEFF1] text-[11px] font-bold text-[#455A64] uppercase tracking-wider">
                                <th style={stickyHeaderStyle} className="py-3 px-4 w-12 text-center">
                                  <input
                                    type="checkbox"
                                    checked={selectedFloorIndexes.length === activeTower?.floorsData.length && activeTower?.floorsData.length > 0}
                                    onChange={handleSelectAllFloors}
                                    className="rounded text-[#1A237E] focus:ring-[#1A237E] w-4 h-4 cursor-pointer"
                                  />
                                </th>
                                {calculationMode === "manual" && (
                                  <th style={stickyHeaderStyle} className="py-3 px-3 w-24 text-center">ĐẶT TỦ (MC)</th>
                                )}
                                <th style={stickyHeaderStyle} className="py-3 px-4 w-28">TẦNG</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-32">KHOẢNG CÁCH DÂY (M)</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-28">CAM DOME</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-28">CAM THÂN</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-32">TỔNG CÁP/TẦNG</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-20">SW24</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-20">SW16</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-24">UPS 1K/2K</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-20">PDU</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-28">CONVERTER</th>
                                <th style={stickyHeaderStyle} className="py-3 px-3 w-16 text-center text-rose-600 font-bold">XÓA</th>
                              </tr>
                            </thead>
                            <tbody className="divide-y divide-[#ECEFF1] text-sm">
                              {(() => {
                                const floors = activeTower?.floorsData || [];
                                const basementsCount = activeTower?.basementsCount || 0;
                                const floorsCount = activeTower?.floorsCount || 0;
                                const hasRoof = activeTower?.hasRoof || false;

                                const roofFloors = floors.filter(f => hasRoof && f.floorIndex === basementsCount + floorsCount);
                                const upperFloors = floors.filter(f => f.floorIndex >= basementsCount && f.floorIndex < basementsCount + floorsCount);
                                const basementFloors = floors.filter(f => f.floorIndex < basementsCount);

                                const sortedUpperFloors = [...upperFloors].sort((a, b) => b.floorIndex - a.floorIndex);
                                const sortedBasementFloors = [...basementFloors].sort((a, b) => b.floorIndex - a.floorIndex);

                                const cabinetRangesMap = new Map<string, {fromIndex: number, toIndex: number}>();
                                floors.forEach(f => {
                                  if (f.fromIndex !== undefined && f.toIndex !== undefined) {
                                    const key = `${f.fromIndex}-${f.toIndex}`;
                                    cabinetRangesMap.set(key, { fromIndex: f.fromIndex, toIndex: f.toIndex });
                                  }
                                });
                                const cabinetRanges = Array.from(cabinetRangesMap.values())
                                  .sort((a, b) => a.fromIndex - b.fromIndex);

                                const rangeColors = [
                                  { bg: 'bg-indigo-100 hover:bg-indigo-200/80', border: 'border-l-4 border-indigo-600', labelBg: 'bg-indigo-200 text-indigo-900 border border-indigo-300 font-bold' },
                                  { bg: 'bg-amber-100 hover:bg-amber-200/80', border: 'border-l-4 border-amber-600', labelBg: 'bg-amber-200 text-amber-900 border border-amber-300 font-bold' },
                                  { bg: 'bg-teal-100 hover:bg-teal-200/80', border: 'border-l-4 border-teal-600', labelBg: 'bg-teal-200 text-teal-900 border border-teal-300 font-bold' },
                                  { bg: 'bg-rose-100 hover:bg-rose-200/80', border: 'border-l-4 border-rose-600', labelBg: 'bg-rose-200 text-rose-900 border border-rose-300 font-bold' },
                                  { bg: 'bg-emerald-100 hover:bg-emerald-200/80', border: 'border-l-4 border-emerald-600', labelBg: 'bg-emerald-200 text-emerald-900 border border-emerald-300 font-bold' },
                                  { bg: 'bg-sky-100 hover:bg-sky-200/80', border: 'border-l-4 border-sky-600', labelBg: 'bg-sky-200 text-sky-900 border border-sky-300 font-bold' },
                                  { bg: 'bg-purple-100 hover:bg-purple-200/80', border: 'border-l-4 border-purple-600', labelBg: 'bg-purple-200 text-purple-900 border border-purple-300 font-bold' }
                                ];

                                const getRangeStyle = (f: FloorData) => {
                                  if (f.fromIndex === undefined || f.toIndex === undefined) {
                                    return { bg: 'hover:bg-slate-50/80', border: '', labelBg: 'bg-slate-100 text-slate-400 border border-slate-200' };
                                  }
                                  const idx = cabinetRanges.findIndex(
                                    r => r.fromIndex === f.fromIndex && r.toIndex === f.toIndex
                                  );
                                  if (idx === -1) {
                                    return { bg: 'hover:bg-slate-50/80', border: '', labelBg: 'bg-slate-100 text-slate-400 border border-slate-200' };
                                  }
                                  return rangeColors[idx % rangeColors.length];
                                };

                                const getManualRangeStyle = (f: FloorData) => {
                                  const groupIdx = manualGroups.findIndex(g => {
                                    if (g.cabinetIndex === f.floorIndex) return true;
                                    return g.cabinets.some((c: any) => 
                                      c.allocations.some((a: any) => a.floorIndex === f.floorIndex)
                                    );
                                  });
                                  if (groupIdx === -1) {
                                    const autoStyle = getRangeStyle(f);
                                    if (autoStyle.border !== '') {
                                      const autoIdx = cabinetRanges.findIndex(
                                        r => r.fromIndex === f.fromIndex && r.toIndex === f.toIndex
                                      );
                                      if (autoIdx !== -1) {
                                        return rangeColors[(manualGroups.length + autoIdx) % rangeColors.length];
                                      }
                                      return autoStyle;
                                    }
                                    return { bg: 'hover:bg-slate-50/80', border: '', labelBg: 'bg-slate-100 text-slate-400 border border-slate-200' };
                                  }
                                  return rangeColors[groupIdx % rangeColors.length];
                                };

                                const getFloorWarning = (f: FloorData) => {
                                  if (!activeTower || calculationMode !== "manual") return null;

                                  const group = manualGroups.find((g) => {
                                    if (g.cabinetIndex === f.floorIndex) return true;
                                    return g.cabinets.some((c: any) => 
                                      c.allocations.some((a: any) => a.floorIndex === f.floorIndex)
                                    );
                                  });
                                  if (!group) return null;

                                  const cabinetIndex = group.cabinetIndex;
                                  const hDist = activeTower.horizontalDistance || 0;
                                  const vDist = activeTower.verticalDistance || 0;

                                  const singleRunDistance = hDist + Math.abs(f.floorIndex - cabinetIndex) * vDist;
                                  const maxCableLength = 70;

                                  let distanceExceeded = false;
                                  if (f.camerasCount > 0 && singleRunDistance > maxCableLength) {
                                    distanceExceeded = true;
                                  }                                  let cameraExceeded = false;
                                  let totalCamsInCabinet = 0;
                                  let limit2U = 20;
                                  group.cabinets.forEach((c: any) => {
                                    if (c.type === "2U") {
                                      const currentLimit = 20 * (c.quantity2U || 1);
                                      const totalCams = c.allocations.reduce((sum: number, a: any) => sum + a.domeCount + a.bulletCount, 0);
                                      if (totalCams > currentLimit) {
                                        cameraExceeded = true;
                                        totalCamsInCabinet = totalCams;
                                        limit2U = currentLimit;
                                      }
                                    }
                                  });

                                  return {
                                    distanceExceeded,
                                    singleRunDistance,
                                    maxCableLength,
                                    cameraExceeded,
                                    totalCamsInCabinet,
                                    limit2U,
                                    isCabinetPlaced: cabinetIndex === f.floorIndex,
                                  };
                                };

                                const renderRow = (f: FloorData) => {
                                  const isCabinetPlaced = cabinetPlacements.includes(f.floorIndex);
                                  const styleGroup = calculationMode === "manual" ? getManualRangeStyle(f) : getRangeStyle(f);
                                  const isActiveCabinet = calculationMode === "manual" && activeCabinetIndex === f.floorIndex;

                                  return (
                                    <tr 
                                      key={f.floorIndex} 
                                      className={`transition select-none cursor-pointer ${
                                        selectedFloorIndexes.includes(f.floorIndex) 
                                          ? 'bg-slate-300 font-semibold text-slate-900' 
                                          : styleGroup.bg
                                      } ${styleGroup.border} ${isActiveCabinet ? 'ring-2 ring-emerald-500 ring-inset' : ''}`}                                      onClick={(e) => {
                                        if (calculationMode === "manual" && activeTower) {
                                          if (e.ctrlKey || e.metaKey) {
                                            if (activeCabinetIndex === null) {
                                              addToast("Vui lòng chọn/đặt tủ (bằng cách click dòng có đặt tủ) trước khi giữ Ctrl + click để liên kết!", "info");
                                              return;
                                            }
                                            const activeGroup = manualGroups.find(g => g.cabinetIndex === activeCabinetIndex);
                                            if (activeGroup && activeGroup.cabinets && activeGroup.cabinets.length > 1) {
                                              addToast("Tủ đang chọn là tủ kép (nhiều tủ). Vui lòng nhấn nút bánh răng của tủ để phân bổ chi tiết!", "error");
                                              return;
                                            }
                                            if (f.floorIndex !== activeCabinetIndex) {
                                              if (activeGroup && activeGroup.cabinets && activeGroup.cabinets.length > 0) {
                                                const cab = activeGroup.cabinets[0];
                                                const isAllocated = cab.allocations.some(a => a.floorIndex === f.floorIndex);
                                                if (!isAllocated && cab.type === "2U") {
                                                  const currentTotal = cab.allocations.reduce((sum, a) => sum + a.domeCount + a.bulletCount, 0);
                                                  const newFloorCamCount = (f.domeCount || 0) + (f.bulletCount || 0);
                                                  const newTotal = currentTotal + newFloorCamCount;
                                                  const limit2U = 20 * (cab.quantity2U || 1);
                                                  if (newTotal > limit2U) {
                                                    addToast(`Không thể liên kết thêm tầng này vì tổng số camera (${newTotal} cam) vượt quá giới hạn tối đa của tủ 2U (${limit2U} cam)!`, "error");
                                                    return;
                                                  }
                                                }
                                              }
                                              const updatedGroups = manualGroups.map((g) => {
                                                if (g.cabinetIndex === activeCabinetIndex) {
                                                  let found = false;
                                                  const newCabinets = g.cabinets.map((cab) => {
                                                    const isAllocated = cab.allocations.some(a => a.floorIndex === f.floorIndex);
                                                    if (isAllocated) {
                                                      found = true;
                                                      return {
                                                        ...cab,
                                                        allocations: cab.allocations.filter(a => a.floorIndex !== f.floorIndex)
                                                      };
                                                    }
                                                    return cab;
                                                  });

                                                  if (!found) {
                                                    const updatedCabinets = [...g.cabinets];
                                                    if (updatedCabinets.length > 0) {
                                                      updatedCabinets[0] = {
                                                        ...updatedCabinets[0],
                                                        allocations: [
                                                          ...updatedCabinets[0].allocations,
                                                          {
                                                            floorIndex: f.floorIndex,
                                                            domeCount: f.domeCount || 0,
                                                            bulletCount: f.bulletCount || 0
                                                          }
                                                        ]
                                                      };
                                                    }
                                                    return {
                                                      ...g,
                                                      cabinets: updatedCabinets
                                                    };
                                                  } else {
                                                    return {
                                                      ...g,
                                                      cabinets: newCabinets
                                                    };
                                                  }
                                                }
                                                return g;
                                              });

                                              setManualGroups(updatedGroups);
                                              const updatedFloorsData = syncFloorsWithManualGroups(activeTower.floorsData, updatedGroups);
                                              updateTowerFloorsData(updatedFloorsData, updatedGroups);
                                            }
                                            return;
                                          }

                                          const isCab = manualGroups.some(g => g.cabinetIndex === f.floorIndex);
                                          if (isCab) {
                                            setActiveCabinetIndex(f.floorIndex);
                                          }
                                          setViewingFloorConnectionDetail(f.floorIndex);
                                        }
                                      }}
                                    >
                                    <td className="py-2 px-4 text-center" onClick={(e) => e.stopPropagation()}>
                                      <input
                                        type="checkbox"
                                        checked={selectedFloorIndexes.includes(f.floorIndex)}
                                        onClick={(e) => handleToggleSelectFloor(f.floorIndex, e)}
                                        onChange={() => {}}
                                        className="rounded text-[#1A237E] focus:ring-[#1A237E] w-4 h-4 cursor-pointer"
                                      />
                                    </td>
                                    {calculationMode === "manual" && (
                                      <td className="py-2 px-3 text-center" onClick={(e) => e.stopPropagation()}>
                                        <div className="flex items-center justify-center gap-1.5">
                                          <button
                                            onClick={() => handleToggleCabinet(f.floorIndex)}
                                            title={manualGroups.some(g => g.cabinetIndex === f.floorIndex) ? "Hủy đặt tủ tại tầng này" : "Đặt tủ MC tại tầng này"}
                                            className={`p-1.5 rounded-lg transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-1 focus:ring-emerald-500 border ${
                                              manualGroups.some(g => g.cabinetIndex === f.floorIndex)
                                                ? "bg-emerald-600 hover:bg-emerald-700 text-white border-emerald-700 shadow-sm scale-105"
                                                : "bg-slate-50 hover:bg-slate-200/80 text-slate-400 hover:text-slate-700 border-slate-200/60"
                                            }`}
                                          >
                                            <svg className="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                                              <line x1="7" y1="8" x2="17" y2="8"></line>
                                              <line x1="7" y1="12" x2="17" y2="12"></line>
                                              <line x1="7" y1="16" x2="17" y2="16"></line>
                                            </svg>
                                          </button>
                                          {manualGroups.some(g => g.cabinetIndex === f.floorIndex) && (
                                            <button
                                              onClick={() => {
                                                setEditingCabinetIndex(f.floorIndex);
                                                const group = manualGroups.find(g => g.cabinetIndex === f.floorIndex);
                                                setTempCabinets(JSON.parse(JSON.stringify(group?.cabinets || [])));
                                              }}
                                              title="Cấu hình danh sách tủ và phân bổ camera"
                                              className="p-1.5 rounded bg-blue-50 hover:bg-blue-100 text-blue-600 border border-blue-200 transition-all focus:outline-none"
                                            >
                                              <svg className="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                                                <circle cx="12" cy="12" r="3"></circle>
                                                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 1 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
                                              </svg>
                                            </button>
                                          )}
                                        </div>
                                      </td>
                                    )}
                                    <td className="py-2 px-4 font-semibold text-[#191c1e]">
                                      <div className="flex flex-col gap-1">
                                        <div className="flex items-center gap-2">
                                          <input
                                            type="text"
                                            value={f.label}
                                            onChange={(e) => handleUpdateFloorCell(f.floorIndex, "label", e.target.value)}
                                            onClick={(e) => e.stopPropagation()}
                                            className="bg-transparent border-0 hover:bg-slate-200/80 focus:bg-white focus:ring-1 focus:ring-[#1A237E]/30 focus:border-[#1A237E] rounded px-1.5 py-0.5 font-semibold text-[#191c1e] text-sm focus:outline-none transition w-36 text-left"
                                          />
                                          {calculationMode === "manual" ? (
                                            manualGroups.some(g => g.cabinetIndex === f.floorIndex) ? (
                                              (() => {
                                                const group = manualGroups.find(g => g.cabinetIndex === f.floorIndex);
                                                const cabinetsStr = group?.cabinets.map((c: any) => c.type).join(", ") || "2U";
                                                return (
                                                  <span className={`inline-flex items-center gap-1 px-1.5 py-0.5 text-[9px] font-bold rounded border transition-all ${
                                                    isActiveCabinet
                                                      ? "bg-emerald-600 text-white border-emerald-700 shadow-sm font-extrabold animate-pulse"
                                                      : "bg-emerald-100 text-emerald-800 border-emerald-200"
                                                  }`} title={isActiveCabinet ? "Tủ đang chọn để phân nhóm" : "Tủ đặt thủ công"}>
                                                    <svg className="w-3 h-3 font-bold" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                                                      <path strokeLinecap="round" strokeLinejoin="round" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                                    </svg>
                                                    {isActiveCabinet ? `MC RACKS (${cabinetsStr}) (ĐANG CHỌN)` : `MC RACKS (${cabinetsStr})`}
                                                  </span>
                                                );
                                              })()
                                            ) : (
                                              (() => {
                                                const associatedGroup = manualGroups.find(g => {
                                                  if (g.cabinetIndex === f.floorIndex) return true;
                                                  return g.cabinets.some((c: any) => 
                                                    c.allocations.some((a: any) => a.floorIndex === f.floorIndex)
                                                  );
                                                });
                                                if (associatedGroup) {
                                                  return (
                                                    <span className="inline-flex items-center gap-1 px-1.5 py-0.5 text-[9px] font-bold rounded bg-blue-100 text-blue-800 border border-blue-200">
                                                      Liên kết Tủ T.{associatedGroup.cabinetIndex + 1}
                                                    </span>
                                                  );
                                                }
                                                return (
                                                  <span className="inline-flex items-center gap-1 px-1.5 py-0.5 text-[9px] font-medium rounded bg-slate-100 text-slate-400 border border-slate-200 italic">
                                                    Tự động tối ưu
                                                  </span>
                                                );
                                              })()
                                            )
                                          ) : (
                                            isCabinetPlaced && (
                                              <span className="inline-flex items-center gap-1 px-1.5 py-0.5 text-[10px] font-bold rounded bg-[#1A237E]/10 text-[#1A237E] border border-[#1A237E]/20" title="Tầng đặt tủ rack">
                                                <svg className="w-3.5 h-3.5 text-[#1A237E]" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                                                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01" />
                                                </svg>
                                                RACK
                                              </span>
                                            )
                                          )}
                                        </div>

                                        {/* Cabinet and Camera Details under the Floor Name */}
                                        {isCabinetPlaced && (
                                          <div className="flex flex-wrap gap-1 mt-1 text-left">
                                            {(() => {
                                              const floorCabs = f.cabinets || [];
                                              const isManualGroup = calculationMode === "manual" && manualGroups.some(g => g.cabinetIndex === f.floorIndex);
                                              console.log("Cabinet Render:", {
                                                floorIndex: f.floorIndex,
                                                label: f.label,
                                                cabinetType: f.cabinetType,
                                                calculationMode,
                                                tempQuantity2U,
                                                activeTowerQty2U: activeTower?.quantity2U,
                                                floorCabsLength: floorCabs.length,
                                                floorCabs
                                              });
                                              if (floorCabs.length > 0) {
                                                return floorCabs.map((c: any, cIdx: number) => {
                                                  const qty = isManualGroup ? (c.quantity2U || 1) : tempQuantity2U;
                                                  return (
                                                    <span key={cIdx} className="inline-flex items-center gap-2 px-2.5 py-1 text-[13px] font-bold rounded-md bg-[#E8EAF6] text-[#1A237E] border border-[#C5CAE9] shadow-sm">
                                                      <span className="w-2 h-2 rounded-full bg-[#1A237E] animate-pulse"></span>
                                                      {c.cabinetType === "2U" && qty > 1 ? `${qty} ` : ""}Tủ {c.cabinetType || ""} ({c.cameraQuantityInCabinet ?? 0} Cam)
                                                    </span>
                                                  );
                                                });
                                              }
                                              const fallbackQty = isManualGroup ? (activeTower?.quantity2U || 1) : tempQuantity2U;
                                              return (
                                                <span className="inline-flex items-center gap-2 px-2.5 py-1 text-[13px] font-bold rounded-md bg-[#E8EAF6] text-[#1A237E] border border-[#C5CAE9] shadow-sm">
                                                  <span className="w-2 h-2 rounded-full bg-[#1A237E] animate-pulse"></span>
                                                  {f.cabinetType === "2U" && fallbackQty > 1 ? `${fallbackQty} ` : ""}Tủ {f.cabinetType || ""} ({f.cameraQuantityInCabinet ?? 0} Cam)
                                                </span>
                                              );
                                            })()}
                                          </div>
                                        )}

                                        {/* Warnings in Manual Mode */}
                                        {(() => {
                                          const warn = getFloorWarning(f);
                                          if (!warn) return null;
                                          return (
                                            <div className="flex flex-col gap-1 mt-0.5">
                                              {warn.distanceExceeded && (
                                                <span className="inline-flex items-center gap-1 text-[10px] font-bold text-rose-600 bg-rose-50 border border-rose-100 rounded px-2 py-0.5 w-max shadow-sm animate-pulse">
                                                  <svg className="w-3.5 h-3.5 text-rose-500" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                                                  </svg>
                                                  LỖI CÁP: {warn.singleRunDistance}m &gt; {warn.maxCableLength}m
                                                </span>
                                              )}
                                              {warn.cameraExceeded && warn.isCabinetPlaced && (
                                                <span className="inline-flex items-center gap-1 text-[10px] font-bold text-amber-600 bg-amber-50 border border-amber-100 rounded px-2 py-0.5 w-max shadow-sm">
                                                  <svg className="w-3.5 h-3.5 text-amber-500" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                                                  </svg>
                                                  QUÁ TẢI TỦ 2U: {warn.totalCamsInCabinet} cam &gt; {warn.limit2U} cam
                                                </span>
                                              )}
                                            </div>
                                          );
                                        })()}
                                      </div>
                                    </td>
                                    
                                    {/* Editable Cable Length Input */}
                                     <td className="py-2 px-3" onClick={(e) => e.stopPropagation()}>
                                       <input
                                         type="number"
                                         min="0"
                                         placeholder="0"
                                         value={f.cableLengthInput === undefined ? "" : f.cableLengthInput}
                                         onFocus={(e) => e.target.select()}
                                         onChange={(e) => handleUpdateFloorCell(f.floorIndex, "cableLengthInput", Math.max(0, parseInt(e.target.value) || 0))}
                                         className="w-20 bg-[#f8f9fb] border border-[#ECEFF1] hover:border-slate-300 focus:border-[#1A237E] rounded px-2 py-1 text-center font-mono font-semibold focus:outline-none transition"
                                       />
                                     </td>


                                    {/* Editable Dome Count */}
                                    <td className="py-2 px-3" onClick={(e) => e.stopPropagation()}>
                                      <input
                                        type="number"
                                        min="0"
                                        placeholder="0"
                                        value={f.domeCount === 0 ? "" : f.domeCount}
                                        onFocus={(e) => e.target.select()}
                                        onChange={(e) => handleUpdateFloorCell(f.floorIndex, "domeCount", Math.max(0, parseInt(e.target.value) || 0))}
                                        className="w-20 bg-[#f8f9fb] border border-[#ECEFF1] hover:border-slate-300 focus:border-[#1A237E] rounded px-2 py-1 text-center font-mono focus:outline-none transition"
                                      />
                                    </td>

                                    {/* Editable Bullet Count */}
                                    <td className="py-2 px-3" onClick={(e) => e.stopPropagation()}>
                                      <input
                                        type="number"
                                        min="0"
                                        placeholder="0"
                                        value={f.bulletCount === 0 ? "" : f.bulletCount}
                                        onFocus={(e) => e.target.select()}
                                        onChange={(e) => handleUpdateFloorCell(f.floorIndex, "bulletCount", Math.max(0, parseInt(e.target.value) || 0))}
                                        className="w-20 bg-[#f8f9fb] border border-[#ECEFF1] hover:border-slate-300 focus:border-[#1A237E] rounded px-2 py-1 text-center font-mono focus:outline-none transition"
                                      />
                                    </td>

                                    {/* Total calculated cable length for this floor */}
                                    <td className="py-2 px-3 text-center font-mono font-bold text-[#1A237E]">
                                      {f.cableLength !== undefined ? `${f.cableLength} m` : "-"}
                                    </td>

                                    {/* SW24 read-only calculated */}
                                    <td className={`py-2 px-3 font-mono text-center ${isCabinetPlaced ? "text-[#191c1e]" : "text-slate-300"}`}>
                                      {f.sw24Count || "-"}
                                    </td>

                                    {/* SW16 read-only calculated */}
                                    <td className={`py-2 px-3 font-mono text-center ${isCabinetPlaced ? "text-[#191c1e]" : "text-slate-300"}`}>
                                      {f.sw16Count || "-"}
                                    </td>

                                    {/* UPS type read-only */}
                                    <td className="py-2 px-3 text-center">
                                      <span className={`px-2 py-0.5 rounded-full text-xs font-semibold font-mono ${
                                        f.upsType === "2K" 
                                          ? "bg-amber-100 text-amber-800" 
                                          : f.upsType === "1K" 
                                          ? "bg-emerald-100 text-emerald-800" 
                                          : "bg-slate-100 text-slate-300"
                                      }`}>
                                        {f.upsType === "None" ? "-" : f.upsType === "1K" ? "1 (1K)" : "1 (2K)"}
                                      </span>
                                    </td>

                                    {/* PDU */}
                                    <td className={`py-2 px-3 font-mono text-center ${isCabinetPlaced ? "text-[#191c1e]" : "text-slate-300"}`}>
                                      {f.pduCount || "-"}
                                    </td>

                                    {/* Converter */}
                                    <td className={`py-2 px-3 font-mono text-center ${isCabinetPlaced ? "text-[#191c1e]" : "text-slate-300"}`}>
                                      {f.convCount || "-"}
                                    </td>
                                    {/* Delete button */}
                                    <td className="py-2 px-3 text-center" onClick={(e) => e.stopPropagation()}>
                                      <button
                                        onClick={() => {
                                          if (window.confirm(`Bạn có chắc chắn muốn xóa tầng "${f.label}" không?`)) {
                                            handleDeleteFloor(f.floorIndex);
                                          }
                                        }}
                                        className="p-1 rounded-md text-slate-400 hover:text-rose-600 hover:bg-rose-50 transition-colors duration-200"
                                        title="Xóa tầng"
                                      >
                                        <Trash2 className="w-4 h-4" />
                                      </button>
                                    </td>
                                  </tr>
                                );
                              };

                                return (
                                  <>
                                    {/* Group 3: Tầng Mái */}
                                    {roofFloors.length > 0 && (
                                      <>
                                        <tr className="bg-slate-100/90 border-y border-[#ECEFF1] text-[11px] font-bold text-[#1A237E] select-none">
                                          <td colSpan={calculationMode === "manual" ? 13 : 12} className="py-2.5 px-4">
                                            <div className="flex items-center gap-2">
                                              <span className="w-2.5 h-2.5 rounded-full bg-[#1A237E]"></span>
                                              <span>NHÓM 3: TẦNG MÁI ({roofFloors.length} tầng)</span>
                                            </div>
                                          </td>
                                        </tr>
                                        {roofFloors.map((f) => renderRow(f))}
                                      </>
                                    )}

                                    {/* Group 2: Tầng Nổi */}
                                    {sortedUpperFloors.length > 0 && (
                                      <>
                                        <tr className="bg-slate-100/90 border-y border-[#ECEFF1] text-[11px] font-bold text-[#2E7D32] select-none">
                                          <td colSpan={calculationMode === "manual" ? 13 : 12} className="py-2.5 px-4">
                                            <div className="flex items-center gap-2">
                                              <span className="w-2.5 h-2.5 rounded-full bg-[#2E7D32]"></span>
                                              <span>NHÓM 2: TẦNG NỔI ({sortedUpperFloors.length} tầng)</span>
                                            </div>
                                          </td>
                                        </tr>
                                        {sortedUpperFloors.map((f) => renderRow(f))}
                                      </>
                                    )}

                                    {/* Group 1: Tầng Hầm */}
                                    {sortedBasementFloors.length > 0 && (
                                      <>
                                        <tr className="bg-slate-100/90 border-y border-[#ECEFF1] text-[11px] font-bold text-[#C62828] select-none">
                                          <td colSpan={calculationMode === "manual" ? 13 : 12} className="py-2.5 px-4">
                                            <div className="flex items-center gap-2">
                                              <span className="w-2.5 h-2.5 rounded-full bg-[#C62828]"></span>
                                              <span>NHÓM 1: TẦNG HẦM ({sortedBasementFloors.length} tầng)</span>
                                            </div>
                                          </td>
                                        </tr>
                                        {sortedBasementFloors.map((f) => renderRow(f))}
                                      </>
                                    )}
                                  </>
                                );
                              })()}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                      
                      {/* BẢNG TÍNH CHI TIẾT CÁP THEO TẦNG */}
                      <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs w-full mt-6">
                        <div className="px-6 py-4 border-b border-[#ECEFF1] flex flex-col sm:flex-row sm:items-center justify-between bg-slate-50/50">
                          <div>
                            <h3 className="font-sans font-bold text-base text-[#191c1e] uppercase tracking-wide flex items-center gap-2">
                              <span className="w-2.5 h-2.5 rounded-full bg-[#1A237E]"></span>
                              Bảng tính chi tiết cáp theo tầng ({activeTower?.name || "Tháp chính"})
                            </h3>
                            <p className="text-xs text-[#455A64]">
                              Phân tích chi tiết các thành phần cáp (thông tầng, xuống tủ, trong tủ, AutoCAD) cho từng tầng
                            </p>
                          </div>
                          <div className="text-xs font-mono text-[#2E7D32] bg-[#E8F5E9] px-2.5 py-1 rounded font-bold border border-[#2E7D32]/20">
                            CABLE DETAILS
                          </div>
                        </div>
                        
                        <div className="overflow-x-auto xl:overflow-visible p-4 bg-slate-50/30">
                          <table className="w-full text-xs text-center border-collapse font-sans border border-slate-200 min-w-[900px] bg-white rounded shadow-xs overflow-visible">
                            <thead className="bg-[#1A237E] shadow-xs">
                              <tr className="bg-[#1A237E] text-white font-bold border-b border-slate-300 divide-x divide-slate-200 select-none uppercase tracking-wider text-[10px]">
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-24">Tủ</th>
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-28 text-left pl-4">Tầng camera</th>
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-28">Tầng đặt tủ</th>
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-24">Số camera</th>
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-32">Mét AutoCAD</th>
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-28">Thông tầng</th>
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-28">Xuống tủ</th>
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-28">Trong tủ</th>
                                <th style={stickyHeaderStyle} className="py-3 px-2 w-32">Tổng cáp/tầng</th>
                              </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-200">
                              {(() => {
                                const floors = activeTower?.floorsData || [];
                                const basementsCount = activeTower?.basementsCount || 0;
                                const floorsCount = activeTower?.floorsCount || 0;
                                const hasRoof = activeTower?.hasRoof || false;

                                const roofFloors = floors.filter(f => hasRoof && f.floorIndex === basementsCount + floorsCount);
                                const upperFloors = floors.filter(f => f.floorIndex >= basementsCount && f.floorIndex < basementsCount + floorsCount);
                                const basementFloors = floors.filter(f => f.floorIndex < basementsCount);

                                const sortedUpperFloors = [...upperFloors].sort((a, b) => b.floorIndex - a.floorIndex);
                                const sortedBasementFloors = [...basementFloors].sort((a, b) => b.floorIndex - a.floorIndex);

                                const allSortedFloors = [
                                  ...roofFloors,
                                  ...sortedUpperFloors,
                                  ...sortedBasementFloors
                                ];

                                if (allSortedFloors.length === 0) {
                                  return (
                                    <tr>
                                      <td colSpan={9} className="py-8 text-slate-400 italic text-center">
                                        Không có dữ liệu cho tháp này. Vui lòng bấm Tính toán để bắt đầu.
                                      </td>
                                    </tr>
                                  );
                                }

                                const totalCameras = allSortedFloors.reduce((sum, f) => sum + (f.camerasCount || 0), 0);
                                const totalCable = allSortedFloors.reduce((sum, f) => sum + (f.cableLength || 0), 0);

                                return (
                                  <>
                                    {allSortedFloors.map((f) => {
                                      const cabinetFloor = floors.find(fl => fl.floorIndex === f.cabinetIndex);
                                      const cabinetLabel = cabinetFloor ? cabinetFloor.label : (f.cabinetIndex !== undefined ? `Tầng ${f.cabinetIndex}` : "");

                                      return (
                                        <tr 
                                          key={f.floorIndex} 
                                          className="hover:bg-slate-50 transition divide-x divide-slate-200"
                                        >
                                          {/* Tủ */}
                                          <td className="py-2.5 px-2 text-slate-700 font-semibold bg-slate-50/10">
                                            {(() => {
                                              if (!cabinetLabel) return "";
                                              const cleaned = cabinetLabel.trim();
                                              if (cleaned.toLowerCase().startsWith("tầng ")) {
                                                return "Tủ " + cleaned.substring(5).trim();
                                              }
                                              return "Tủ " + cleaned;
                                            })()}
                                          </td>
                                          {/* Tầng camera */}
                                          <td className="py-2.5 px-2 text-left pl-4 font-semibold text-slate-800 bg-[#E8F5E9]/10">
                                            {f.label}
                                          </td>
                                          {/* Tầng đặt tủ */}
                                          <td className="py-2.5 px-2 font-medium text-slate-700 bg-[#E3F2FD]/10">
                                            {cabinetLabel}
                                          </td>
                                          {/* Số camera */}
                                          <td className="py-2.5 px-2 font-bold text-indigo-700 bg-indigo-50/5">
                                            {f.camerasCount}
                                          </td>
                                          {/* Mét AutoCAD */}
                                          <td className="py-2.5 px-2 font-mono font-medium text-slate-700">
                                            {f.autocadLength ?? 0}
                                          </td>
                                          {/* Thông tầng */}
                                          <td className="py-2.5 px-2 font-mono text-slate-600">
                                            {f.atrium ?? 0}
                                          </td>
                                          {/* Xuống tủ */}
                                          <td className="py-2.5 px-2 font-mono text-slate-600">
                                            {f.downCabinet ?? 0}
                                          </td>
                                          {/* Trong tủ */}
                                          <td className="py-2.5 px-2 font-mono text-slate-600">
                                            {f.inCabinet ?? 0}
                                          </td>
                                          {/* Tổng cáp/tầng */}
                                          <td className="py-2.5 px-2 font-mono font-bold text-[#2E7D32] bg-[#E8F5E9]/10">
                                            {f.cableLength}
                                          </td>
                                        </tr>
                                      );
                                    })}
                                    
                                    {/* Summary Row */}
                                    <tr className="bg-[#0D1B2A] text-white font-bold text-center border-t-2 border-slate-700 select-none divide-x divide-slate-700">
                                      <td colSpan={3} className="py-3 px-4 text-right uppercase tracking-wider text-xs">
                                        TỔNG SỐ CAM
                                      </td>
                                      <td className="py-3 px-2 text-center text-sm font-bold text-yellow-300">
                                        {totalCameras}
                                      </td>
                                      <td colSpan={4} className="py-3 px-4 text-right uppercase tracking-wider text-xs">
                                        TỔNG SỐ MÉT CÁP
                                      </td>
                                      <td className="py-3 px-2 text-center text-sm font-bold text-green-400 font-mono">
                                        {totalCable}
                                      </td>
                                    </tr>
                                  </>
                                );
                              })()}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </>
                  )}

                </div>
              )}

              {/* SCREEN 2: SITE PARAMETERS */}
              {activeNav === "parameters" && (
                <div className="bg-white border border-[#ECEFF1] rounded-lg p-6 flex flex-col gap-6">
                  <div>
                    <h2 className="font-sans font-bold text-xl text-[#191c1e] tracking-tight">
                      Site Parameters
                    </h2>
                    <p className="text-sm text-[#455A64]">
                      Cấu hình các tham số môi trường và vật tư chung cho dự án giám sát của bạn
                    </p>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-2 border-t border-slate-100">
                    
                    {/* Cable parameters */}
                    <div className="flex flex-col gap-4">
                      <h3 className="text-sm font-bold text-[#1A237E] uppercase tracking-wider">
                        Thông số cáp & Khoảng cách
                      </h3>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Hệ số nhân độ dài cáp (Cable Factor)
                        </label>
                        <input
                          type="number"
                          step="0.1"
                          value={activeTower?.siteParams.cableFactor}
                          onChange={(e) => handleUpdateSiteParam("cableFactor", parseFloat(e.target.value) || 1.0)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none font-mono"
                        />
                        <p className="text-xs text-slate-400 mt-1 leading-normal">
                          Dùng để tính độ dài cáp trung bình từ camera tới tủ phân phối. Mặc định là 2.0 (nghĩa là độ dài thực tế gấp đôi khoảng cách tính toán để bù hao hụt uốn lượn).
                        </p>
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Tỷ lệ dự phòng hao hụt cáp (%)
                        </label>
                        <input
                          type="number"
                          value={activeTower?.siteParams.cableReserve}
                          onChange={(e) => handleUpdateSiteParam("cableReserve", parseInt(e.target.value) || 0)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none font-mono"
                        />
                        <p className="text-xs text-slate-400 mt-1">
                          Hao hụt thi công, cắt đấu nối. Mặc định là 10%.
                        </p>
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Chiều cao tầng mặc định (m)
                        </label>
                        <input
                          type="number"
                          value={activeTower?.siteParams.defaultFloorHeight}
                          onChange={(e) => handleUpdateSiteParam("defaultFloorHeight", parseInt(e.target.value) || 3)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none font-mono"
                        />
                      </div>
                    </div>

                    {/* Camera models & Defaults */}
                    <div className="flex flex-col gap-4">
                      <h3 className="text-sm font-bold text-[#1A237E] uppercase tracking-wider">
                        Thiết bị camera mặc định
                      </h3>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Dòng Camera Dome mặc định
                        </label>
                        <select
                          value={activeTower?.siteParams.domeModel}
                          onChange={(e) => handleUpdateSiteParam("domeModel", e.target.value)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none"
                        >
                          {globalInventory.filter(i => i.category === "Camera").map(item => (
                            <option key={item.id} value={item.code}>{item.name} ({item.code})</option>
                          ))}
                        </select>
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Dòng Camera Thân mặc định
                        </label>
                        <select
                          value={activeTower?.siteParams.bulletModel}
                          onChange={(e) => handleUpdateSiteParam("bulletModel", e.target.value)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none"
                        >
                          {globalInventory.filter(i => i.category === "Camera").map(item => (
                            <option key={item.id} value={item.code}>{item.name} ({item.code})</option>
                          ))}
                        </select>
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Hình thức uplink toà nhà (Backbone)
                        </label>
                        <select
                          value={activeTower?.siteParams.uplinkType}
                          onChange={(e) => handleUpdateSiteParam("uplinkType", e.target.value as any)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none"
                        >
                          <option value="Fiber">Fiber (Cáp quang quang học + Converter)</option>
                          <option value="Copper">Copper (Cáp đồng mạng Gigabit)</option>
                        </select>
                        <p className="text-xs text-slate-400 mt-1 leading-normal">
                          Hình thức liên kết mạng giữa các tủ tầng về trung tâm. Chọn Fiber hệ thống sẽ tự động bổ sung Converter Quang-Điện cho các tầng trên.
                        </p>
                      </div>
                    </div>

                  </div>
                </div>
              )}

              {/* SCREEN 3: HARDWARE LOGIC */}
              {activeNav === "logic" && (
                <div className="bg-white border border-[#ECEFF1] rounded-lg p-6 flex flex-col gap-6">
                  <div>
                    <h2 className="font-sans font-bold text-xl text-[#191c1e] tracking-tight">
                      Hardware Logic
                    </h2>
                    <p className="text-sm text-[#455A64]">
                      Định nghĩa thuật toán phân bổ thiết bị mạng, lưu điện và quy chuẩn tủ rack
                    </p>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pt-2 border-t border-slate-100">
                    
                    {/* Switch allocation rules */}
                    <div className="flex flex-col gap-4">
                      <h3 className="text-sm font-bold text-[#1A237E] uppercase tracking-wider">
                        Phân bổ Switch PoE
                      </h3>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Ưu tiên sử dụng loại Switch nào?
                        </label>
                        <div className="flex flex-col gap-2 mt-1">
                          {[
                            { value: "SW24", label: "Chỉ sử dụng Switch 24-Port (Tối ưu hạ tầng lớn)" },
                            { value: "SW16", label: "Chỉ sử dụng Switch 16-Port (Hạ tầng mật độ trung bình)" },
                            { value: "Auto", label: "Tự động lựa chọn tối ưu theo số camera thực tế" }
                          ].map((option) => (
                            <label key={option.value} className="flex items-center gap-2.5 text-sm cursor-pointer p-2 rounded hover:bg-slate-50">
                              <input
                                type="radio"
                                name="switchPreference"
                                value={option.value}
                                checked={activeTower?.hardwareLogic.switchPreference === option.value}
                                onChange={() => handleUpdateHardwareLogic("switchPreference", option.value)}
                                className="text-[#1A237E] focus:ring-[#1A237E] w-4 h-4"
                              />
                              <span>{option.label}</span>
                            </label>
                          ))}
                        </div>
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Số lượng camera tối đa trên 1 switch (ngưỡng an toàn)
                        </label>
                        <input
                          type="number"
                          value={activeTower?.siteParams.maxCamsPerSwitch}
                          onChange={(e) => handleUpdateSiteParam("maxCamsPerSwitch", parseInt(e.target.value) || 24)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none font-mono"
                        />
                        <p className="text-xs text-slate-400 mt-1">
                          Bảo toàn cổng dự phòng cho kỹ thuật (mặc định chừa 4 cổng trống trên Switch 24 để đảm bảo an toàn băng thông).
                        </p>
                      </div>
                    </div>

                    {/* UPS & Cabinet allocation */}
                    <div className="flex flex-col gap-4">
                      <h3 className="text-sm font-bold text-[#1A237E] uppercase tracking-wider">
                        Tủ Cabinet & Lưu điện (UPS)
                      </h3>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Số lượng PDU trên mỗi tủ
                        </label>
                        <input
                          type="number"
                          value={activeTower?.hardwareLogic.pduPerRack}
                          onChange={(e) => handleUpdateHardwareLogic("pduPerRack", parseInt(e.target.value) || 1)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none font-mono"
                        />
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Số lượng Converter trên mỗi uplink cáp quang
                        </label>
                        <input
                          type="number"
                          value={activeTower?.hardwareLogic.converterPerUplink}
                          onChange={(e) => handleUpdateHardwareLogic("converterPerUplink", parseInt(e.target.value) || 1)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none font-mono"
                        />
                      </div>

                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Thời gian dự phòng điện UPS tối thiểu (Giờ)
                        </label>
                        <select
                          value={activeTower?.hardwareLogic.backupHours}
                          onChange={(e) => handleUpdateHardwareLogic("backupHours", parseInt(e.target.value) || 1)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none"
                        >
                          <option value="1">1 Giờ (Dùng dòng UPS 1KVA/2KVA Online thông thường)</option>
                          <option value="2">2 Giờ (Tăng cường ắc quy mở rộng)</option>
                        </select>
                      </div>
                    </div>

                  </div>
                </div>
              )}

              {/* SCREEN 4: COST ANALYSIS */}
              {activeNav === "cost" && (
                <div className="flex flex-col gap-6">
                  
                  {/* Title & Actions */}
                  <div className="flex items-center justify-between">
                    <div>
                      <h2 className="font-sans font-bold text-xl text-[#191c1e] tracking-tight">
                        Cost Analysis
                      </h2>
                      <p className="text-sm text-[#455A64]">
                        Bảng phân tích vật tư, đơn giá dự kiến và tổng hợp kinh phí đầu tư
                      </p>
                    </div>
                    <button
                      onClick={handleResetPrices}
                      className="px-3 py-1.5 text-xs font-semibold text-[#1A237E] border border-[#1A237E]/20 hover:bg-[#E8EAF6] rounded transition flex items-center gap-1.5"
                    >
                      <RefreshCw className="w-3.5 h-3.5" />
                      <span>Đặt lại giá gốc</span>
                    </button>
                  </div>

                  {/* Summary of calculations row */}
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <div className="bg-white border border-[#ECEFF1] rounded-lg p-4">
                      <div className="text-xs text-[#455A64] font-semibold uppercase tracking-wider mb-1">Tổng thiết bị vật tư</div>
                      <div className="text-xl font-bold text-[#191c1e] font-mono">
                        {formatVND(subTotal)}
                      </div>
                    </div>
                    <div className="bg-white border border-[#ECEFF1] rounded-lg p-4">
                      <div className="text-xs text-[#455A64] font-semibold uppercase tracking-wider mb-1">Thi công lắp đặt (8%)</div>
                      <div className="text-xl font-bold text-[#455A64] font-mono">
                        {formatVND(laborCost)}
                      </div>
                    </div>
                    <div className="bg-white border border-[#ECEFF1] rounded-lg p-4">
                      <div className="text-xs text-[#455A64] font-semibold uppercase tracking-wider mb-1">Thuế VAT (10%)</div>
                      <div className="text-xl font-bold text-[#455A64] font-mono">
                        {formatVND(vatAmount)}
                      </div>
                    </div>
                    <div className="bg-white border-l-4 border-l-[#1A237E] bg-[#E8EAF6]/30 border border-y-[#ECEFF1] border-r-[#ECEFF1] rounded-r-lg p-4">
                      <div className="text-xs text-[#1A237E] font-bold uppercase tracking-wider mb-1">TỔNG KINH PHÍ DỰ KIẾN</div>
                      <div className="text-2xl font-black text-[#1A237E] font-mono">
                        {formatVND(totalProjectPrice)}
                      </div>
                    </div>
                  </div>

                  {/* Pricing table with editable override unit price */}
                  <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs overflow-hidden">
                    <div className="px-6 py-4 border-b border-[#ECEFF1] bg-slate-50/50">
                      <h3 className="font-sans font-bold text-base text-[#191c1e]">
                        Định giá vật tư chi tiết
                      </h3>
                      <p className="text-xs text-[#455A64]">
                        Bạn có thể sửa trực tiếp &apos;Đơn giá&apos; dưới đây để khớp với báo giá thực tế của nhà cung cấp
                      </p>
                    </div>

                    <div className="overflow-x-auto">
                      <table className="w-full text-left border-collapse min-w-[800px]">
                        <thead>
                          <tr className="bg-slate-50 border-b border-[#ECEFF1] text-[11px] font-bold text-[#455A64] uppercase tracking-wider">
                            <th className="py-3 px-4 w-32">MÃ VẬT TƯ</th>
                            <th className="py-3 px-4">TÊN THIẾT BỊ / VẬT TƯ</th>
                            <th className="py-3 px-3 w-20 text-center">ĐVT</th>
                            <th className="py-3 px-3 w-20 text-center">SỐ LƯỢNG</th>
                            <th className="py-3 px-4 w-44 text-right">ĐƠN GIÁ (VND)</th>
                            <th className="py-3 px-4 w-44 text-right">THÀNH TIỀN (VND)</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-[#ECEFF1] text-sm">
                          {globalInventory.map((item) => {
                            const qty = itemizedQuantities[item.id] || 0;
                            if (qty === 0) return null; // only show quantified items

                            const unitPrice = getItemUnitPrice(item);
                            const totalLine = qty * unitPrice;
                            const isOverridden = activeTower?.customPrices && activeTower?.customPrices[item.id] !== undefined;

                            return (
                              <tr key={item.id} className="hover:bg-slate-50/80 transition">
                                <td className="py-3 px-4 font-mono text-xs font-semibold text-[#455A64]">
                                  {item.code}
                                </td>
                                <td className="py-3 px-4">
                                  <div className="font-semibold text-[#191c1e]">{item.name}</div>
                                  <div className="text-xs text-slate-400 font-normal line-clamp-1 mt-0.5">{item.spec}</div>
                                </td>
                                <td className="py-3 px-3 text-center text-[#455A64]">
                                  {item.unit}
                                </td>
                                <td className="py-3 px-3 text-center font-mono font-bold text-[#191c1e]">
                                  {qty}
                                </td>
                                
                                {/* Editable Unit Price */}
                                <td className="py-3 px-4 text-right">
                                  <div className="flex items-center justify-end gap-1.5">
                                    {isOverridden && (
                                      <span className="w-1.5 h-1.5 rounded-full bg-[#1A237E]" title="Giá đã tùy chỉnh riêng cho dự án" />
                                    )}
                                    <input
                                      type="number"
                                      value={unitPrice}
                                      onChange={(e) => handleSetCustomPrice(item.id, Math.max(0, parseInt(e.target.value) || 0))}
                                      className="w-32 bg-[#f8f9fb] border border-[#ECEFF1] text-right font-mono text-sm font-semibold hover:border-slate-300 focus:border-[#1A237E] rounded px-2 py-1 focus:outline-none transition"
                                    />
                                  </div>
                                </td>

                                <td className="py-3 px-4 text-right font-mono font-bold text-[#1A237E]">
                                  {formatVND(totalLine)}
                                </td>
                              </tr>
                            );
                          })}
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              )}

              {/* SCREEN 5: REPORTS / PRINT READY EXPORT */}
              {activeNav === "reports" && (
                <div className="flex flex-col gap-6">
                  
                  {/* Meta input customizer */}
                  <div className="bg-white border border-[#ECEFF1] rounded-lg p-5 flex flex-col gap-4">
                    <h3 className="font-sans font-bold text-base text-[#191c1e]">
                      Cấu hình thông tin báo cáo
                    </h3>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Tên khách hàng / Đối tác
                        </label>
                        <input
                          type="text"
                          value={customerName}
                          onChange={(e) => setCustomerName(e.target.value)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none"
                        />
                      </div>
                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Địa điểm dự án
                        </label>
                        <input
                          type="text"
                          value={projectLocation}
                          onChange={(e) => setProjectLocation(e.target.value)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none"
                        />
                      </div>
                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Hiệu lực báo giá (Ngày)
                        </label>
                        <input
                          type="number"
                          value={validDays}
                          onChange={(e) => setValidDays(parseInt(e.target.value) || 30)}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none font-mono"
                        />
                      </div>
                    </div>
                  </div>

                  {/* Print document layout */}
                  <div className="bg-white border border-[#ECEFF1] shadow-md rounded-lg p-8 max-w-[900px] mx-auto w-full text-slate-800" id="print-boq-area">
                    
                    {/* Header Document */}
                    <div className="flex justify-between items-start border-b-2 border-[#1A237E] pb-6 mb-8">
                      <div>
                        <div className="font-bold text-xl text-[#1A237E] uppercase tracking-wider font-sans">
                          Surveillance BOQ Engine
                        </div>
                        <div className="text-xs text-slate-400 mt-1">
                          Giải pháp Giám sát Công nghệ cao & Đơn giá Hạ tầng
                        </div>
                        <div className="text-xs text-slate-500 mt-4 leading-relaxed">
                          Hệ thống: {activeProject.name}<br />
                          Số tầng thiết kế: {activeTower?.floorsCount} tầng nổi {activeTower?.basementsCount ? `+ ${activeTower?.basementsCount} tầng hầm` : ""} {activeTower?.hasRoof ? `+ 1 tầng mái` : ""} (Tổng: {activeTower?.floorsData.length} tầng)<br />
                          Quy chuẩn áp dụng: {BASE_PRESETS.find(p => p.id === activeTower?.standardPresetId)?.name || "Tự chọn"}
                        </div>
                      </div>

                      <div className="text-right">
                        <div className="text-2xl font-extrabold text-[#191c1e] tracking-tight uppercase">
                          BÁO GIÁ DỰ ÁN
                        </div>
                        <div className="text-xs text-[#455A64] font-mono mt-1">
                          Số: BOQ-2026-{activeProject.id.slice(-6).toUpperCase()}
                        </div>
                        <div className="text-xs text-slate-500 mt-4 leading-relaxed">
                          Ngày tạo: {new Date().toLocaleDateString("vi-VN")}<br />
                          Hiệu lực báo giá: {validDays} ngày<br />
                          Người lập: Ban Quản lý Dự án
                        </div>
                      </div>
                    </div>

                    {/* Customer details row */}
                    <div className="mb-6 p-4 bg-[#F5F7F9] rounded text-xs leading-relaxed grid grid-cols-2 gap-4">
                      <div>
                        <span className="font-bold text-slate-400 block uppercase mb-1">KHÁCH HÀNG</span>
                        <span className="font-bold text-[#191c1e] text-sm">{customerName}</span>
                      </div>
                      <div>
                        <span className="font-bold text-slate-400 block uppercase mb-1">ĐỊA ĐIỂM DỰ ÁN</span>
                        <span className="font-medium text-[#191c1e] text-sm">{projectLocation}</span>
                      </div>
                    </div>

                    {/* Table of products */}
                    <div className="mb-8">
                      <table className="w-full text-xs text-left border-collapse">
                        <thead>
                          <tr className="border-b-2 border-[#ECEFF1] text-slate-400 font-bold uppercase">
                            <th className="py-2 px-1 w-24">MÃ VẬT TƯ</th>
                            <th className="py-2 px-2">THÔNG TIN SẢN PHẨM & ĐẶC TÍNH KỸ THUẬT</th>
                            <th className="py-2 px-2 text-center w-14">ĐVT</th>
                            <th className="py-2 px-2 text-center w-14">SL</th>
                            <th className="py-2 px-2 text-right w-32">ĐƠN GIÁ</th>
                            <th className="py-2 px-2 text-right w-36">THÀNH TIỀN</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-100">
                          {globalInventory.map((item) => {
                            const qty = itemizedQuantities[item.id] || 0;
                            if (qty === 0) return null;

                            const unitPrice = getItemUnitPrice(item);
                            const totalLine = qty * unitPrice;

                            return (
                              <tr key={item.id} className="py-2">
                                <td className="py-2.5 px-1 font-mono font-semibold text-[#455A64]">{item.code}</td>
                                <td className="py-2.5 px-2">
                                  <div className="font-bold text-[#191c1e]">{item.name}</div>
                                  <div className="text-slate-400 text-[10px] mt-0.5">{item.spec}</div>
                                </td>
                                <td className="py-2.5 px-2 text-center">{item.unit}</td>
                                <td className="py-2.5 px-2 text-center font-bold text-[#191c1e] font-mono">{qty}</td>
                                <td className="py-2.5 px-2 text-right font-mono">{formatVND(unitPrice)}</td>
                                <td className="py-2.5 px-2 text-right font-bold text-[#1A237E] font-mono">{formatVND(totalLine)}</td>
                              </tr>
                            );
                          })}
                        </tbody>
                      </table>
                    </div>

                    {/* Cost summary block */}
                    <div className="flex justify-end mb-12">
                      <div className="w-96 text-xs flex flex-col gap-2">
                        <div className="flex justify-between border-b border-slate-100 py-1">
                          <span className="text-[#455A64]">Cộng tiền vật tư thiết bị:</span>
                          <span className="font-mono font-bold">{formatVND(subTotal)}</span>
                        </div>
                        <div className="flex justify-between border-b border-slate-100 py-1">
                          <span className="text-[#455A64]">Chi phí thi công lắp đặt liên quan (8%):</span>
                          <span className="font-mono font-bold">{formatVND(laborCost)}</span>
                        </div>
                        <div className="flex justify-between border-b border-slate-100 py-1">
                          <span className="text-[#455A64]">Thuế giá trị gia tăng VAT (10%):</span>
                          <span className="font-mono font-bold">{formatVND(vatAmount)}</span>
                        </div>
                        <div className="flex justify-between py-2 text-sm border-t-2 border-[#1A237E]">
                          <span className="font-bold text-[#191c1e] uppercase">Tổng cộng chi phí đầu tư:</span>
                          <span className="font-mono font-extrabold text-[#1A237E] text-base">{formatVND(totalProjectPrice)}</span>
                        </div>
                      </div>
                    </div>

                    {/* Signature block */}
                    <div className="grid grid-cols-2 gap-12 text-xs pt-8 border-t border-dashed border-slate-200">
                      <div className="text-center">
                        <span className="font-bold text-slate-400 uppercase block mb-12">ĐẠI DIỆN KHÁCH HÀNG</span>
                        <div className="border-t border-slate-300 w-48 mx-auto pt-2 text-slate-400">
                          (Ký, ghi rõ họ tên và đóng dấu)
                        </div>
                      </div>
                      <div className="text-center">
                        <span className="font-bold text-[#1A237E] uppercase block mb-12">NGƯỜI LẬP PHƯƠNG ÁN kỹ thuật</span>
                        <div className="border-t border-slate-300 w-48 mx-auto pt-2 font-bold text-[#1A237E]">
                          Surveillance BOQ Engine
                        </div>
                      </div>
                    </div>

                  </div>

                  {/* Print and Export Buttons */}
                  <div className="flex items-center justify-center gap-4 py-4">
                    <button
                      onClick={() => window.print()}
                      className="px-6 py-2.5 bg-slate-800 hover:bg-slate-900 text-white font-medium rounded shadow transition flex items-center gap-2"
                    >
                      <Printer className="w-4.5 h-4.5" />
                      <span>In báo cáo / Lưu PDF</span>
                    </button>
                    <button
                      onClick={handleExportCSV}
                      className="px-6 py-2.5 bg-[#1A237E] hover:bg-[#1A237E]/95 text-white font-medium rounded shadow transition flex items-center gap-2"
                    >
                      <FileDown className="w-4.5 h-4.5" />
                      <span>Xuất Excel CSV</span>
                    </button>
                  </div>

                </div>
              )}
            </>
          )}

          {/* TAB 2: PROJECTS MANAGEMENT */}
          {activeTab === "projects" && (
            <div className="flex flex-col gap-6">
              
              <div className="flex items-center justify-between">
                <div>
                  <h1 className="font-sans font-bold text-2xl text-[#191c1e] tracking-tight">
                    Quản lý Dự án
                  </h1>
                  <p className="text-sm text-[#455A64]">
                    Tạo lập, nhân bản và đồng bộ các cấu hình hạ tầng cho nhiều khách hàng khác nhau
                  </p>
                </div>
                <button
                  onClick={() => setIsCreatingProject(true)}
                  className="px-4 py-2 bg-[#1A237E] text-white text-sm font-semibold hover:bg-[#1A237E]/95 rounded shadow-sm transition flex items-center gap-2"
                >
                  <Plus className="w-4.5 h-4.5" />
                  <span>Dự án mới</span>
                </button>
              </div>

              {/* Create new project inline editor dialog */}
              <AnimatePresence>
                {isCreatingProject && (
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: "auto" }}
                    exit={{ opacity: 0, height: 0 }}
                    className="bg-white border border-[#1A237E]/20 rounded-lg p-5 flex flex-col gap-4 overflow-hidden shadow-xs"
                  >
                    <h3 className="font-bold text-base text-[#1A237E] flex items-center gap-2">
                      <Building className="w-5 h-5" />
                      <span>Thiết lập dự án mới</span>
                    </h3>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Tên dự án *
                        </label>
                        <input
                          type="text"
                          value={newProjectName}
                          onChange={(e) => setNewProjectName(e.target.value)}
                          placeholder="Ví dụ: Tòa nhà văn phòng Hoàng Mai"
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none"
                        />
                      </div>
                      <div>
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Số tầng thiết kế mặc định
                        </label>
                        <input
                          type="number"
                          value={newProjectFloors}
                          onChange={(e) => setNewProjectFloors(Math.max(1, parseInt(e.target.value) || 1))}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none font-mono"
                        />
                      </div>
                      <div className="md:col-span-2">
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Mô tả ngắn
                        </label>
                        <input
                          type="text"
                          value={newProjectDesc}
                          onChange={(e) => setNewProjectDesc(e.target.value)}
                          placeholder="Thông tin thêm về địa điểm, tiến độ, hoặc ghi chú kỹ thuật..."
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm focus:border-[#1A237E] focus:outline-none"
                        />
                      </div>
                      <div className="md:col-span-2">
                        <label className="block text-xs font-semibold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Áp dụng tiêu chuẩn kỹ thuật ban đầu
                        </label>
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 mt-1">
                          {BASE_PRESETS.map((pr) => (
                            <label key={pr.id} className="flex items-start gap-3 p-3 border border-[#ECEFF1] rounded hover:bg-slate-50 cursor-pointer transition">
                              <input
                                type="radio"
                                name="newProjectPreset"
                                value={pr.id}
                                checked={newProjectPreset === pr.id}
                                onChange={() => setNewProjectPreset(pr.id)}
                                className="mt-1 text-[#1A237E]"
                              />
                              <div>
                                <div className="text-xs font-bold text-[#191c1e]">{pr.name}</div>
                                <div className="text-[10px] text-slate-400 mt-0.5 line-clamp-1">{pr.description}</div>
                              </div>
                            </label>
                          ))}
                        </div>
                      </div>
                    </div>

                    <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-100">
                      <button
                        onClick={() => setIsCreatingProject(false)}
                        className="px-4 py-2 border border-slate-200 text-sm font-medium hover:bg-slate-50 rounded transition"
                      >
                        Hủy bỏ
                      </button>
                      <button
                        onClick={handleCreateProject}
                        className="px-4 py-2 bg-[#1A237E] text-white text-sm font-semibold hover:bg-[#1A237E]/95 rounded transition"
                      >
                        Tạo dự án mới
                      </button>
                    </div>
                  </motion.div>
                )}
              </AnimatePresence>

              {/* List of current projects */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {projects.map((proj) => {
                  const isActive = proj.id === activeProjectId;
                  const projCams = proj.towers?.reduce(
                    (total, t) => total + (t.floorsData?.reduce((acc, curr) => acc + curr.camerasCount, 0) || 0),
                    0
                  ) || 0;
                  const firstPresetId = proj.towers?.[0]?.standardPresetId;
                  const presetName = BASE_PRESETS.find(p => p.id === firstPresetId)?.name || "Tự chọn";

                  return (
                    <div
                      key={proj.id}
                      className={`bg-white border rounded-lg p-5 flex flex-col justify-between shadow-xs transition hover:shadow-md relative overflow-hidden ${
                        isActive ? "border-2 border-[#1A237E]" : "border-[#ECEFF1]"
                      }`}
                    >
                      {isActive && (
                        <div className="absolute top-0 right-0 bg-[#1A237E] text-white text-[10px] font-bold px-3 py-1 rounded-bl">
                          ĐANG MỞ
                        </div>
                      )}

                      <div>
                        <div className="flex items-center gap-2 mb-2 text-xs text-[#455A64] font-mono">
                          <span>Khởi tạo: {new Date(proj.createdAt).toLocaleDateString("vi-VN")}</span>
                        </div>
                        <h3 className="font-sans font-bold text-lg text-[#191c1e] mb-1 line-clamp-1">
                          {proj.name}
                        </h3>
                        <p className="text-xs text-[#455A64] line-clamp-2 min-h-[32px] mb-4">
                          {proj.description}
                        </p>

                        <div className="grid grid-cols-3 gap-2 py-3 border-y border-slate-100 text-xs">
                          <div>
                            <div className="text-[#455A64]">Quy mô:</div>
                            <div className="font-bold text-[#191c1e] font-mono mt-0.5">{proj.towers?.length || 0} Tháp</div>
                          </div>
                          <div>
                            <div className="text-[#455A64]">Tổng camera:</div>
                            <div className="font-bold text-[#1A237E] font-mono mt-0.5">{projCams} cái</div>
                          </div>
                          <div>
                            <div className="text-[#455A64]">Tiêu chuẩn:</div>
                            <div className="font-bold text-[#191c1e] mt-0.5 truncate" title={presetName}>{presetName.split(" ")[0]}</div>
                          </div>
                        </div>
                      </div>

                      <div className="flex items-center justify-between gap-2 mt-5 pt-3 border-t border-slate-50">
                        <button
                          onClick={() => {
                            if (isActive) {
                              addToast("Dự án này đã được mở sẵn!", "info");
                            } else {
                              setActiveProjectId(proj.id);
                              addToast(`Đã mở dự án: ${proj.name}`, "success");
                            }
                            setActiveTab("app");
                          }}
                          className={`px-4 py-1.5 rounded text-xs font-semibold flex items-center gap-1 transition ${
                            isActive 
                              ? "bg-[#E8EAF6] text-[#1A237E]" 
                              : "bg-slate-100 text-slate-700 hover:bg-[#1A237E]/5 hover:text-[#1A237E]"
                          }`}
                        >
                          <span>Mở Workspace</span>
                          <ArrowRight className="w-3.5 h-3.5" />
                        </button>

                        <div className="flex gap-1.5">
                          <button
                            onClick={() => {
                              addToast("Tính năng sao chép dự án đang tạm khóa ở chế độ Read-only", "info");
                            }}
                            className="p-1.5 hover:bg-slate-100 rounded text-slate-500 hover:text-slate-800 transition"
                            title="Sao chép dự án"
                          >
                            <Copy className="w-4 h-4" />
                          </button>
                          
                          <button
                            onClick={() => handleDeleteProject(proj.id)}
                            className="p-1.5 hover:bg-red-50 rounded text-slate-400 hover:text-red-600 transition"
                            title="Xóa dự án"
                          >
                            <Trash className="w-4 h-4" />
                          </button>
                        </div>
                      </div>

                    </div>
                  );
                })}
              </div>

            </div>
          )}

          {/* TAB 3: INVENTORY DATABASE EDITOR */}
          {activeTab === "inventory" && (
            <div className="flex flex-col gap-6">
              
              <div className="flex items-center justify-between">
                <div>
                  <h1 className="font-sans font-bold text-2xl text-[#191c1e] tracking-tight">
                    Kho thiết bị vật tư
                  </h1>
                  <p className="text-sm text-[#455A64]">
                    Quản lý danh sách giá cơ bản toàn hệ thống và bổ sung cấu hình thiết bị mới
                  </p>
                </div>
              </div>

              {/* Form to insert new standard equipment to inventory database */}
              <div className="bg-white border border-[#ECEFF1] rounded-lg p-5 flex flex-col gap-4 shadow-xs">
                <h3 className="font-bold text-sm text-[#1A237E] uppercase tracking-wider">
                  Thêm thiết bị / Vật tư mới vào kho
                </h3>
                
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-6 gap-3">
                  <div>
                    <label className="block text-[11px] font-bold text-[#455A64] uppercase tracking-wider mb-1">Mã vật tư *</label>
                    <input
                      type="text"
                      placeholder="e.g. CAM-DOM-4K"
                      value={newItemCode}
                      onChange={(e) => setNewItemCode(e.target.value)}
                      className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-1.5 text-xs focus:border-[#1A237E] focus:outline-none"
                    />
                  </div>
                  <div className="sm:col-span-2">
                    <label className="block text-[11px] font-bold text-[#455A64] uppercase tracking-wider mb-1">Tên thiết bị *</label>
                    <input
                      type="text"
                      placeholder="e.g. Camera Ultra HD 4K Dome"
                      value={newItemName}
                      onChange={(e) => setNewItemName(e.target.value)}
                      className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-1.5 text-xs focus:border-[#1A237E] focus:outline-none"
                    />
                  </div>
                  <div>
                    <label className="block text-[11px] font-bold text-[#455A64] uppercase tracking-wider mb-1">Phân loại</label>
                    <select
                      value={newItemCategory}
                      onChange={(e) => setNewItemCategory(e.target.value as any)}
                      className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-2 py-1.5 text-xs focus:border-[#1A237E] focus:outline-none"
                    >
                      <option value="Camera">Camera</option>
                      <option value="Switch">Switch POE</option>
                      <option value="Rack">Tủ Rack</option>
                      <option value="UPS">Bộ lưu điện</option>
                      <option value="PDU">Thanh nguồn PDU</option>
                      <option value="Converter">Converter quang</option>
                      <option value="Cable">Cáp tín hiệu</option>
                      <option value="Accessories">Phụ kiện thi công</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-[11px] font-bold text-[#455A64] uppercase tracking-wider mb-1">Giá gốc (VND)</label>
                    <input
                      type="number"
                      value={newItemPrice}
                      onChange={(e) => setNewItemPrice(Math.max(0, parseInt(e.target.value) || 0))}
                      className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-1.5 text-xs focus:border-[#1A237E] focus:outline-none font-mono"
                    />
                  </div>
                  <div className="flex items-end">
                    <button
                      onClick={handleAddGlobalInventory}
                      className="w-full bg-[#1A237E] hover:bg-[#1A237E]/95 text-white py-1.5 px-3 rounded text-xs font-bold transition flex items-center justify-center gap-1 h-[32px]"
                    >
                      <Plus className="w-4 h-4" />
                      <span>Thêm thiết bị</span>
                    </button>
                  </div>
                  <div className="sm:col-span-6">
                    <label className="block text-[11px] font-bold text-[#455A64] uppercase tracking-wider mb-1">Thông số kỹ thuật sản phẩm</label>
                    <input
                      type="text"
                      placeholder="e.g. Cảm biến hình ảnh 1/2.8 inch, Zoom quang 4x, IP67 chống chịu thời tiết, đèn thông minh..."
                      value={newItemSpec}
                      onChange={(e) => setNewItemSpec(e.target.value)}
                      className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-1.5 text-xs focus:border-[#1A237E] focus:outline-none"
                    />
                  </div>
                </div>
              </div>

              {/* Table of whole inventory database list */}
              <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs overflow-hidden">
                <div className="overflow-x-auto">
                  <table className="w-full text-left border-collapse">
                    <thead>
                      <tr className="bg-slate-50 border-b border-[#ECEFF1] text-[11px] font-bold text-[#455A64] uppercase tracking-wider">
                        <th className="py-3 px-4 w-32">MÃ VẬT TƯ</th>
                        <th className="py-3 px-4">TÊN THIẾT BỊ</th>
                        <th className="py-3 px-3 w-32">PHÂN LOẠI</th>
                        <th className="py-3 px-4 w-48 text-right">ĐƠN GIÁ CƠ BẢN</th>
                        <th className="py-3 px-4 w-24 text-center">XÓA</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-[#ECEFF1] text-sm">
                      {globalInventory.map((item) => (
                        <tr key={item.id} className="hover:bg-slate-50/80 transition">
                          <td className="py-2.5 px-4 font-mono text-xs font-semibold text-[#1A237E]">
                            {item.code}
                          </td>
                          <td className="py-2.5 px-4">
                            <div className="font-semibold text-[#191c1e]">{item.name}</div>
                            <div className="text-xs text-slate-400 mt-0.5 max-w-md truncate">{item.spec}</div>
                          </td>
                          <td className="py-2.5 px-3">
                            <span className="px-2 py-0.5 bg-slate-100 text-[#455A64] rounded text-xs font-medium">
                              {item.category}
                            </span>
                          </td>
                          <td className="py-2.5 px-4 text-right font-mono font-bold text-[#191c1e]">
                            {formatVND(item.basePrice)}
                          </td>
                          <td className="py-2.5 px-4 text-center">
                            <button
                              onClick={() => handleDeleteInventoryItem(item.id)}
                              className="p-1 text-slate-400 hover:text-red-600 rounded transition"
                              title="Xóa khỏi kho"
                            >
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>

            </div>
          )}

          {/* TAB 4: STANDARDS AND PRESETS CARD VIEWS */}
          {activeTab === "standards" && (
            <div className="flex flex-col gap-6">
              
              <div>
                <h1 className="font-sans font-bold text-2xl text-[#191c1e] tracking-tight">
                  Tiêu chuẩn kỹ thuật thi công
                </h1>
                <p className="text-sm text-[#455A64]">
                  Lựa chọn các quy chuẩn lắp đặt đã tối ưu sẵn cho từng loại hình công trình khác nhau
                </p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                {BASE_PRESETS.map((preset) => {
                  const isApplied = activeTower?.standardPresetId === preset.id;
                  
                  return (
                    <div
                      key={preset.id}
                      className={`bg-white border rounded-lg p-5 flex flex-col justify-between shadow-xs hover:shadow-md transition relative ${
                        isApplied ? "border-2 border-[#1A237E] bg-[#E8EAF6]/5" : "border-[#ECEFF1]"
                      }`}
                    >
                      {isApplied && (
                        <div className="absolute top-4 right-4 text-xs font-semibold px-2 py-0.5 bg-[#E8EAF6] text-[#1A237E] rounded-full flex items-center gap-1">
                          <Check className="w-3.5 h-3.5" />
                          <span>ĐANG ÁP DỤNG</span>
                        </div>
                      )}

                      <div className="flex flex-col gap-3">
                        <div className="p-2 rounded bg-slate-100 text-[#1A237E] self-start">
                          {preset.id === "std-commercial" && <Building className="w-6 h-6" />}
                          {preset.id === "std-industrial" && <Warehouse className="w-6 h-6" />}
                          {preset.id === "std-economy" && <SlidersHorizontal className="w-6 h-6" />}
                          {preset.id === "std-enterprise" && <Activity className="w-6 h-6" />}
                        </div>

                        <h3 className="font-sans font-bold text-lg text-[#191c1e]">
                          {preset.name}
                        </h3>
                        
                        <p className="text-xs text-[#455A64] leading-relaxed">
                          {preset.description}
                        </p>

                        <div className="grid grid-cols-2 gap-x-4 gap-y-2 mt-4 pt-4 border-t border-slate-100 text-xs">
                          <div>
                            <span className="text-slate-400">Hệ số cáp mạng:</span>
                            <span className="font-bold font-mono text-[#191c1e] block mt-0.5">{preset.cableFactor.toFixed(1)}x</span>
                          </div>
                          <div>
                            <span className="text-slate-400">Tỷ lệ camera Dome:</span>
                            <span className="font-bold font-mono text-[#191c1e] block mt-0.5">{preset.cameraRatio}%</span>
                          </div>
                          <div>
                            <span className="text-slate-400">Quy chuẩn Switch PoE:</span>
                            <span className="font-bold text-[#191c1e] block mt-0.5">{preset.switchPreference === "Auto" ? "Tự động tối ưu" : preset.switchPreference}</span>
                          </div>
                          <div>
                            <span className="text-slate-400">Dòng UPS khuyến nghị:</span>
                            <span className="font-bold text-[#191c1e] block mt-0.5">{preset.upsType === "None" ? "Không lắp riêng lẻ" : preset.upsType + "VA Online"}</span>
                          </div>
                        </div>
                      </div>

                      <div className="mt-6 pt-3 border-t border-slate-50 flex justify-end">
                        <button
                          onClick={() => handleSelectPreset(preset)}
                          disabled={isApplied}
                          className={`px-4 py-2 rounded text-xs font-bold transition ${
                            isApplied 
                              ? "bg-slate-100 text-slate-400 cursor-not-allowed" 
                              : "bg-[#1A237E] hover:bg-[#1A237E]/95 text-white shadow-xs"
                          }`}
                        >
                          {isApplied ? "Đang áp dụng cho dự án hiện tại" : "Áp dụng tiêu chuẩn này"}
                        </button>
                      </div>

                    </div>
                  );
                })}
              </div>

            </div>
          )}

          {/* TAB 5: SYSTEM CONFIGURATION SETTINGS */}
          {activeTab === "settings" && (
            <div className="flex flex-col gap-6 w-full max-w-4xl mx-auto">
              <div>
                <h1 className="font-sans font-bold text-2xl text-[#191c1e] tracking-tight">
                  Cấu hình tham số hệ thống
                </h1>
                <p className="text-sm text-[#455A64]">
                  Thay đổi các tham số kỹ thuật mặc định và ngưỡng tối ưu hóa phân phối thiết bị (Switch, UPS, PDU)
                </p>
              </div>

              {isLoadingConfig || !systemConfig ? (
                <div className="bg-white border border-[#ECEFF1] rounded-xl p-12 text-center shadow-xs flex flex-col items-center justify-center gap-4">
                  <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[#1A237E]"></div>
                  <span className="text-sm font-medium text-slate-500">Đang tải cấu hình hệ thống...</span>
                </div>
              ) : (
                <form onSubmit={handleSaveConfig} className="bg-white border border-[#ECEFF1] rounded-xl shadow-sm overflow-hidden">
                  <div className="p-6 border-b border-[#ECEFF1] bg-slate-50/50">
                    <h3 className="font-sans font-bold text-base text-[#191c1e]">
                      Tham số tính toán BOQ &amp; BOM mặc định
                    </h3>
                    <p className="text-xs text-[#455A64] mt-0.5">
                      Các giá trị này sẽ được áp dụng trực tiếp khi tính toán thiết bị cho dự án
                    </p>
                  </div>

                  <div className="p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
                    {/* Column 1: Switch & Physical Thresholds */}
                    <div className="flex flex-col gap-4">
                      <h4 className="text-sm font-bold text-[#1A237E] uppercase tracking-wider border-b border-indigo-50 pb-1.5 flex items-center gap-2">
                        <SlidersHorizontal className="w-4 h-4" />
                        <span>Ngưỡng phân phối Switch &amp; Trục đứng</span>
                      </h4>

                      <div>
                        <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Ngưỡng tối đa camera cho Switch 24 (cổng)
                        </label>
                        <input
                          type="number"
                          value={systemConfig.sw24ConditionQuanity}
                          onChange={(e) => setSystemConfig({
                            ...systemConfig,
                            sw24ConditionQuanity: Math.max(0, parseInt(e.target.value) || 0)
                          })}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
                        />
                        <p className="text-[11px] text-slate-400 mt-1">
                          Số lượng camera tối đa trước khi thuật toán tự động tách tủ hoặc đặt thêm Switch 24.
                        </p>
                      </div>

                      <div>
                        <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Ngưỡng tối đa camera cho Switch 16 (cổng)
                        </label>
                        <input
                          type="number"
                          value={systemConfig.sw16ConditionQuanity}
                          onChange={(e) => setSystemConfig({
                            ...systemConfig,
                            sw16ConditionQuanity: Math.max(0, parseInt(e.target.value) || 0)
                          })}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
                        />
                        <p className="text-[11px] text-slate-400 mt-1">
                          Ngưỡng camera ưu tiên chọn Switch 16 để tránh lãng phí dung lượng cổng trống.
                        </p>
                      </div>

                      <div>
                        <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Giới hạn chiều dài cáp trục đứng (mét)
                        </label>
                        <input
                          type="number"
                          value={systemConfig.conditionLength}
                          onChange={(e) => setSystemConfig({
                            ...systemConfig,
                            conditionLength: Math.max(0, parseInt(e.target.value) || 0)
                          })}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
                        />
                        <p className="text-[11px] text-slate-400 mt-1">
                          Chiều dài tối đa của cáp mạng đồng trước khi bắt buộc phân phối thêm tủ trung gian.
                        </p>
                      </div>
                    </div>

                    {/* Column 2: Equipment and physical properties */}
                    <div className="flex flex-col gap-4">
                      <h4 className="text-sm font-bold text-[#1A237E] uppercase tracking-wider border-b border-indigo-50 pb-1.5 flex items-center gap-2">
                        <Building className="w-4 h-4" />
                        <span>Quy chuẩn thiết bị tủ mặc định</span>
                      </h4>

                      <div>
                        <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Số lượng bộ lưu điện UPS (mặc định)
                        </label>
                        <input
                          type="number"
                          value={systemConfig.ups}
                          onChange={(e) => setSystemConfig({
                            ...systemConfig,
                            ups: Math.max(0, parseInt(e.target.value) || 0)
                          })}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
                        />
                        <p className="text-[11px] text-slate-400 mt-1">
                          Số lượng UPS 1000VA Online tiêu chuẩn lắp đặt cho mỗi tủ cabinet.
                        </p>
                      </div>

                      <div>
                        <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Số lượng thanh nguồn PDU (mặc định)
                        </label>
                        <input
                          type="number"
                          value={systemConfig.pdu}
                          onChange={(e) => setSystemConfig({
                            ...systemConfig,
                            pdu: Math.max(0, parseInt(e.target.value) || 0)
                          })}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
                        />
                        <p className="text-[11px] text-slate-400 mt-1">
                          Số lượng thanh nguồn 6 lỗ tiêu chuẩn lắp đặt cho mỗi tủ cabinet.
                        </p>
                      </div>

                      <div>
                        <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                          Số lượng Converter quang (mặc định)
                        </label>
                        <input
                          type="number"
                          value={systemConfig.converter}
                          onChange={(e) => setSystemConfig({
                            ...systemConfig,
                            converter: Math.max(0, parseInt(e.target.value) || 0)
                          })}
                          className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
                        />
                        <p className="text-[11px] text-slate-400 mt-1">
                          Số lượng bộ chuyển đổi quang điện mặc định khi kéo kết nối Uplink về phòng trung tâm.
                        </p>
                      </div>
                    </div>
                  </div>

                  <div className="px-6 py-4 bg-slate-50 border-t border-[#ECEFF1] flex justify-end gap-3">
                    <button
                      type="button"
                      onClick={() => fetchSystemConfig()}
                      className="px-4 py-2 border border-slate-200 text-sm font-semibold rounded-lg hover:bg-slate-100 transition text-[#455A64]"
                    >
                      Hủy &amp; Tải lại
                    </button>
                    <button
                      type="submit"
                      disabled={isSavingConfig}
                      className="px-5 py-2 bg-[#1A237E] hover:bg-[#1A237E]/95 disabled:bg-slate-300 disabled:cursor-not-allowed text-white text-sm font-semibold rounded-lg shadow-sm transition flex items-center gap-2"
                    >
                      {isSavingConfig ? (
                        <>
                          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                          <span>Đang lưu...</span>
                        </>
                      ) : (
                        <>
                          <Save className="w-4 h-4" />
                          <span>Lưu cấu hình</span>
                        </>
                      )}
                    </button>
                  </div>
                </form>
              )}
            </div>
          )}

        </main>
      </>
    )}
  </div>

      {/* Modal Cấu hình danh sách tủ và Phân bổ camera */}
      {editingCabinetIndex !== null && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs">
          <div className="bg-white rounded-xl shadow-2xl border border-slate-200 w-full max-w-4xl max-h-[85vh] flex flex-col overflow-hidden animate-in fade-in zoom-in-95 duration-150 text-left">
            {/* Modal Header */}
            <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50">
              <div>
                <h3 className="text-lg font-bold text-[#1A237E]">
                  Cấu hình tủ điện tại {activeTower?.floorsData.find(f => f.floorIndex === editingCabinetIndex)?.label || `Tầng ${editingCabinetIndex + 1}`}
                </h3>
                <p className="text-xs text-slate-500 mt-0.5">
                  Thêm nhiều tủ và phân bổ camera từ các tầng kéo dây về tủ tại tầng này.
                </p>
              </div>
              <button
                onClick={() => setEditingCabinetIndex(null)}
                className="text-slate-400 hover:text-slate-600 rounded-lg p-1 hover:bg-slate-100 transition"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            {/* Modal Content */}
            <div className="p-6 overflow-y-auto flex flex-col gap-6 flex-1 bg-slate-50/50">
              <div className="flex justify-between items-center">
                <span className="text-sm font-semibold text-slate-700">
                  Danh sách tủ ({tempCabinets.length})
                </span>
                <button
                  onClick={() => {
                    const floorDataRow = activeTower?.floorsData.find(fd => fd.floorIndex === editingCabinetIndex);
                    setTempCabinets([
                      ...tempCabinets,
                      {
                        id: crypto.randomUUID(),
                        type: "2U",
                        quantity2U: 1,
                        allocations: [
                          {
                            floorIndex: editingCabinetIndex!,
                            domeCount: floorDataRow ? (floorDataRow.domeCount || 0) : 0,
                            bulletCount: floorDataRow ? (floorDataRow.bulletCount || 0) : 0,
                          }
                        ]
                      }
                    ]);
                  }}
                  className="px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-xs font-bold flex items-center gap-1.5 shadow-sm transition"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                  </svg>
                  Thêm tủ mới
                </button>
              </div>

              {tempCabinets.length === 0 ? (
                <div className="py-12 text-center bg-white border border-dashed border-slate-200 rounded-xl">
                  <p className="text-sm text-slate-400 font-medium">Chưa có tủ nào được cấu hình tại tầng này.</p>
                  <p className="text-xs text-slate-400 mt-1">Vui lòng bấm nút "Thêm tủ mới" ở trên để bắt đầu.</p>
                </div>
              ) : (
                <div className="flex flex-col gap-4">
                  {tempCabinets.map((cab, cabIdx) => {
                    const totalDome = cab.allocations.reduce((sum, a) => sum + a.domeCount, 0);
                    const totalBullet = cab.allocations.reduce((sum, a) => sum + a.bulletCount, 0);
                    const totalCam = totalDome + totalBullet;

                    return (
                      <div key={cab.id} className="bg-white border border-slate-200 rounded-xl shadow-xs overflow-hidden">
                        {/* Cabinet Header */}
                        <div className="px-5 py-3 bg-slate-50 border-b border-slate-100 flex justify-between items-center">
                          <div className="flex items-center gap-3">
                            <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
                              Tủ #{cabIdx + 1}
                            </span>
                            <input
                              type="text"
                              value={cab.id}
                              onChange={(e) => {
                                const next = [...tempCabinets];
                                next[cabIdx].id = e.target.value;
                                setTempCabinets(next);
                              }}
                              className="text-sm font-bold text-slate-800 bg-transparent border-b border-transparent hover:border-slate-300 focus:border-blue-500 focus:outline-none px-1"
                              placeholder="Mã tủ"
                              title="Nhấp để đổi mã tủ"
                            />
                            <select
                              value={cab.type}
                              onChange={(e) => {
                                const next = [...tempCabinets];
                                const nextType = e.target.value;
                                const currentQty2U = cab.quantity2U || 1;
                                const limit2U = 20 * currentQty2U;
                                if (nextType === "2U") {
                                  const totalCam = cab.allocations.reduce((sum: number, a: any) => sum + a.domeCount + a.bulletCount, 0);
                                  if (totalCam > limit2U) {
                                    addToast(`Không thể đổi sang tủ 2U vì số lượng camera hiện tại đã vượt quá ${limit2U}!`, "error");
                                    return;
                                  }
                                  next[cabIdx].quantity2U = currentQty2U;
                                }
                                next[cabIdx].type = nextType;
                                setTempCabinets(next);
                              }}
                              className="text-xs bg-white border border-slate-300 rounded px-2 py-1 text-slate-700 font-semibold focus:ring-blue-500 focus:border-blue-500 cursor-pointer shadow-xs focus:outline-none"
                            >
                              <option value="2U">2U Rack</option>
                              <option value="6U">6U Rack</option>
                              <option value="10U">10U Rack</option>
                              <option value="20U">20U Rack</option>
                            </select>
                            {cab.type === "2U" && (
                              <div className="flex items-center gap-1.5 bg-white border border-slate-300 rounded px-2 py-1 shadow-xs">
                                <span className="text-[10px] font-bold text-slate-500 uppercase tracking-wide whitespace-nowrap">SL 2U:</span>
                                <input
                                  type="number"
                                  min="1"
                                  value={cab.quantity2U || 1}
                                  onChange={(e) => {
                                    const val = Math.max(1, parseInt(e.target.value) || 1);
                                    const next = [...tempCabinets];
                                    const limit2U = 20 * val;
                                    const totalCam = cab.allocations.reduce((sum: number, a: any) => sum + a.domeCount + a.bulletCount, 0);
                                    if (totalCam > limit2U) {
                                      addToast(`Không thể giảm số lượng tủ 2U vì tổng số camera (${totalCam}) vượt quá giới hạn ${limit2U}!`, "error");
                                      return;
                                    }
                                    next[cabIdx].quantity2U = val;
                                    setTempCabinets(next);
                                  }}
                                  className="w-10 text-xs font-mono font-bold bg-transparent border-0 focus:ring-0 p-0 text-center focus:outline-none"
                                />
                              </div>
                            )}
                            <span className="text-xs font-semibold px-2 py-0.5 rounded-full bg-blue-50 text-blue-700">
                              Tổng: {totalCam} Cam ({totalDome} Dome, {totalBullet} Thân)
                            </span>
                            {cab.type === "2U" && totalCam > (20 * (cab.quantity2U || 1)) && (
                              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-rose-50 text-rose-700 border border-rose-100 animate-pulse">
                                Quá tải tủ 2U (&gt;{20 * (cab.quantity2U || 1)} cam)
                              </span>
                            )}
                          </div>
                          <button
                            onClick={() => {
                              setTempCabinets(tempCabinets.filter((_, idx) => idx !== cabIdx));
                            }}
                            className="text-rose-500 hover:text-rose-700 hover:bg-rose-50 rounded-lg p-1.5 transition"
                            title="Xóa tủ này"
                          >
                            <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                            </svg>
                          </button>
                        </div>

                        {/* Cabinet Body - Allocations */}
                        <div className="p-5 flex flex-col gap-3">
                          <div className="flex justify-between items-center mb-1">
                            <span className="text-xs font-bold text-slate-500 uppercase tracking-wide">
                              Phân bổ tầng liên kết
                            </span>
                            <button
                                onClick={() => {
                                const next = [...tempCabinets];
                                const floorDataRow = activeTower?.floorsData.find(fd => fd.floorIndex === editingCabinetIndex);
                                next[cabIdx].allocations.push({
                                  floorIndex: editingCabinetIndex!,
                                  domeCount: floorDataRow ? (floorDataRow.domeCount || 0) : 0,
                                  bulletCount: floorDataRow ? (floorDataRow.bulletCount || 0) : 0,
                                });
                                setTempCabinets(next);
                                setSelectedAllocIds([]);
                                setLastSelectedAllocId(null);
                              }}
                              className="text-xs font-bold text-blue-600 hover:text-blue-800 flex items-center gap-1"
                            >
                              <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" d="M12 4v16m8-8H4" />
                              </svg>
                              Thêm tầng liên kết
                            </button>
                          </div>

                          {cab.allocations.length === 0 ? (
                            <p className="text-xs text-slate-400 italic text-center py-2">
                              Chưa liên kết tầng nào. Vui lòng bấm "Thêm tầng liên kết".
                            </p>
                          ) : (
                            <div className="flex flex-col gap-2">
                              {cab.allocations.map((alloc, allocIdx) => {
                                const allocId = `${cabIdx}_${allocIdx}`;
                                const isSelected = selectedAllocIds.includes(allocId);
                                return (
                                  <div key={allocIdx} className={`grid grid-cols-12 gap-3 items-center p-2.5 rounded-lg border transition ${isSelected ? 'bg-blue-50/50 border-blue-200' : 'bg-slate-50/50 border-slate-100'}`}>
                                    {/* Selection Checkbox */}
                                    <div className="col-span-1 flex items-center justify-center">
                                      <input
                                        type="checkbox"
                                        checked={isSelected}
                                        onClick={(e) => handleToggleSelectAlloc(cabIdx, allocIdx, e)}
                                        onChange={() => {}}
                                        className="w-4 h-4 text-blue-600 border-slate-300 rounded focus:ring-blue-500 cursor-pointer"
                                      />
                                    </div>

                                    {/* Select Floor */}
                                    <div className="col-span-4">
                                      <select
                                        value={alloc.floorIndex}
                                        onChange={(e) => {
                                          const next = [...tempCabinets];
                                          const targetFloorIndex = parseInt(e.target.value);
                                          const targetFloor = activeTower?.floorsData.find(fd => fd.floorIndex === targetFloorIndex);
                                          next[cabIdx].allocations[allocIdx].floorIndex = targetFloorIndex;
                                          next[cabIdx].allocations[allocIdx].domeCount = targetFloor ? (targetFloor.domeCount || 0) : 0;
                                          next[cabIdx].allocations[allocIdx].bulletCount = targetFloor ? (targetFloor.bulletCount || 0) : 0;
                                          setTempCabinets(next);
                                        }}
                                        className="w-full text-xs bg-white border border-slate-200 rounded px-2.5 py-1.5 text-slate-700 font-semibold focus:outline-none focus:ring-1 focus:ring-blue-500 cursor-pointer"
                                      >
                                        {activeTower?.floorsData.map((fd) => (
                                          <option key={fd.floorIndex} value={fd.floorIndex}>
                                            {fd.label} (Index: {fd.floorIndex})
                                          </option>
                                        ))}
                                      </select>
                                    </div>

                                    {/* Dome Count */}
                                    <div className="col-span-3 flex items-center gap-1.5">
                                      <span className="text-[10px] text-slate-500 font-bold uppercase whitespace-nowrap">Dome:</span>
                                      <input
                                        type="number"
                                        min="0"
                                        value={alloc.domeCount === 0 ? "" : alloc.domeCount}
                                        placeholder="0"
                                        onChange={(e) => {
                                          const val = Math.max(0, parseInt(e.target.value) || 0);
                                          const next = [...tempCabinets];
                                          const targets = isSelected 
                                            ? selectedAllocIds.map(sId => {
                                                const [cI, aI] = sId.split("_").map(Number);
                                                return { cabIdx: cI, allocIdx: aI };
                                              })
                                            : [{ cabIdx, allocIdx }];

                                          targets.forEach((target) => {
                                            const tCab = next[target.cabIdx];
                                            if (tCab.type === "2U") {
                                              const limit2U = 20 * (tCab.quantity2U || 1);
                                              const currentAllocTotal = tCab.allocations.reduce((sum, a, idx) => {
                                                if (idx === target.allocIdx) {
                                                  return sum + a.bulletCount;
                                                }
                                                return sum + a.domeCount + a.bulletCount;
                                              }, 0);
                                              if (currentAllocTotal + val > limit2U) {
                                                tCab.allocations[target.allocIdx].domeCount = limit2U - currentAllocTotal;
                                                return;
                                              }
                                            }
                                            tCab.allocations[target.allocIdx].domeCount = val;
                                          });

                                          setTempCabinets(next);
                                        }}
                                        className="w-full text-xs font-mono font-bold bg-white border border-slate-200 rounded px-2 py-1 focus:outline-none focus:ring-1 focus:ring-blue-500 text-center"
                                      />
                                    </div>

                                    {/* Bullet Count */}
                                    <div className="col-span-3 flex items-center gap-1.5">
                                      <span className="text-[10px] text-slate-500 font-bold uppercase whitespace-nowrap">Thân:</span>
                                      <input
                                        type="number"
                                        min="0"
                                        value={alloc.bulletCount === 0 ? "" : alloc.bulletCount}
                                        placeholder="0"
                                        onChange={(e) => {
                                          const val = Math.max(0, parseInt(e.target.value) || 0);
                                          const next = [...tempCabinets];
                                          const targets = isSelected 
                                            ? selectedAllocIds.map(sId => {
                                                const [cI, aI] = sId.split("_").map(Number);
                                                return { cabIdx: cI, allocIdx: aI };
                                              })
                                            : [{ cabIdx, allocIdx }];

                                          targets.forEach((target) => {
                                            const tCab = next[target.cabIdx];
                                            if (tCab.type === "2U") {
                                              const limit2U = 20 * (tCab.quantity2U || 1);
                                              const currentAllocTotal = tCab.allocations.reduce((sum, a, idx) => {
                                                if (idx === target.allocIdx) {
                                                  return sum + a.domeCount;
                                                }
                                                return sum + a.domeCount + a.bulletCount;
                                              }, 0);
                                              if (currentAllocTotal + val > limit2U) {
                                                tCab.allocations[target.allocIdx].bulletCount = limit2U - currentAllocTotal;
                                                return;
                                              }
                                            }
                                            tCab.allocations[target.allocIdx].bulletCount = val;
                                          });

                                          setTempCabinets(next);
                                        }}
                                        className="w-full text-xs font-mono font-bold bg-white border border-slate-200 rounded px-2 py-1 focus:outline-none focus:ring-1 focus:ring-blue-500 text-center"
                                      />
                                    </div>

                                    {/* Delete Allocation */}
                                    <div className="col-span-1 flex justify-end">
                                      <button
                                        onClick={() => {
                                          const next = [...tempCabinets];
                                          next[cabIdx].allocations = next[cabIdx].allocations.filter((_, idx) => idx !== allocIdx);
                                          setTempCabinets(next);
                                          setSelectedAllocIds([]);
                                          setLastSelectedAllocId(null);
                                        }}
                                        className="text-slate-400 hover:text-rose-600 p-1 hover:bg-rose-50 rounded transition"
                                        title="Xóa liên kết"
                                      >
                                        <svg className="w-4 h-4" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                                          <path strokeLinecap="round" strokeLinejoin="round" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                        </svg>
                                      </button>
                                    </div>
                                  </div>
                                );
                              })}
                            </div>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </div>

            {/* Modal Footer */}
            <div className="px-6 py-4 border-t border-slate-100 flex justify-end gap-3 bg-slate-50">
              <button
                onClick={() => setEditingCabinetIndex(null)}
                className="px-4 py-2 border border-slate-200 hover:bg-slate-100 text-slate-600 rounded-lg text-sm font-semibold transition"
              >
                Hủy bỏ
              </button>
              <button
                onClick={() => {
                  if (editingCabinetIndex === null) return;

                  // Validate 2U cabinet limit
                  let limitExceededMessage = "";
                  const hasExceeded = tempCabinets.some((cab) => {
                    if (cab.type === "2U") {
                      const limit2U = 20 * (cab.quantity2U || 1);
                      const totalCams = cab.allocations.reduce((sum, a) => sum + a.domeCount + a.bulletCount, 0);
                      if (totalCams > limit2U) {
                        limitExceededMessage = `Có tủ 2U vượt quá giới hạn camera (${totalCams} > ${limit2U} cam)! Vui lòng chọn loại tủ lớn hơn, tăng số lượng tủ hoặc phân bổ lại camera trước khi áp dụng.`;
                        return true;
                      }
                    }
                    return false;
                  });

                  if (hasExceeded) {
                    addToast(limitExceededMessage || `Có tủ 2U vượt quá giới hạn camera!`, "error");
                    return;
                  }

                  const nextGroups = manualGroups.map((g) => {
                    if (g.cabinetIndex === editingCabinetIndex) {
                      return {
                        ...g,
                        cabinets: tempCabinets,
                      };
                    }
                    return g;
                  });
                  setManualGroups(nextGroups);
                  setEditingCabinetIndex(null);

                  // Sync floorsData and fetch placement
                  const updatedFloorsData = syncFloorsWithManualGroups(activeTower?.floorsData || [], nextGroups);
                  updateTowerFloorsData(updatedFloorsData, nextGroups);
                  addToast("Cấu hình tủ điện và đồng bộ số lượng camera thành công!", "success");
                }}
                className="px-4 py-2 bg-[#1A237E] hover:bg-[#283593] text-white rounded-lg text-sm font-semibold shadow-md transition"
              >
                Áp dụng & Đồng bộ
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Modal Chi tiết liên kết và phân bổ camera (Manual Mode) */}
      {viewingFloorConnectionDetail !== null && (() => {
        const floorIndex = viewingFloorConnectionDetail;
        const targetFloor = activeTower?.floorsData.find(fl => fl.floorIndex === floorIndex);
        if (!targetFloor) return null;

        const connections = getFloorConnections(floorIndex);
        const hostedGroup = manualGroups.find(g => g.cabinetIndex === floorIndex);

        return (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs">
            <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-full max-w-lg overflow-hidden animate-in fade-in zoom-in-95 duration-150 text-left">
              {/* Header */}
              <div className="px-6 py-4 bg-[#1A237E] text-white flex items-center justify-between">
                <div>
                  <h3 className="text-lg font-bold">
                    Chi tiết liên kết - {targetFloor.label}
                  </h3>
                  <p className="text-xs text-slate-200 mt-0.5">
                    Chế độ thủ công (Manual Mode)
                  </p>
                </div>
                <button
                  onClick={() => setViewingFloorConnectionDetail(null)}
                  className="text-white/80 hover:text-white rounded-lg p-1 hover:bg-white/10 transition"
                >
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
                  </svg>
                </button>
              </div>

              {/* Content */}
              <div className="p-6 max-h-[60vh] overflow-y-auto bg-slate-50/50 flex flex-col gap-5">
                {/* 1. If this floor hosts cabinet(s) */}
                {hostedGroup && (
                  <div className="flex flex-col gap-3">
                    <h4 className="text-sm font-bold text-slate-800 uppercase tracking-wider flex items-center gap-1.5">
                      <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
                      Tầng đặt tủ rack ({hostedGroup.cabinets?.length || 0} tủ)
                    </h4>
                    
                    {(hostedGroup.cabinets || []).map((cab: any, cabIdx: number) => {
                      const totalCams = cab.allocations?.reduce((sum: number, a: any) => sum + a.domeCount + a.bulletCount, 0) || 0;
                      
                      return (
                        <div key={cab.id || cabIdx} className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex flex-col gap-2.5">
                          <div className="flex items-center justify-between border-b border-slate-100 pb-2">
                            <span className="font-bold text-slate-800 text-sm">
                              Tủ #{cabIdx + 1}: {cab.type} ({cab.id?.includes('-') ? cab.id.slice(-8) : (cab.id?.split('_').slice(-2).join('_') || cab.id)})
                            </span>
                            <span className="px-2 py-0.5 text-xs font-bold bg-emerald-50 text-emerald-700 rounded-md border border-emerald-100">
                              Tổng: {totalCams} Cam
                            </span>
                          </div>

                          <div className="text-xs text-slate-600 flex flex-col gap-1.5">
                            <span className="font-semibold text-slate-700">Các tầng kéo về tủ này:</span>
                            {cab.allocations && cab.allocations.length > 0 ? (
                              <div className="flex flex-col gap-1.5">
                                {cab.allocations.map((alloc: any) => {
                                  const flLabel = activeTower?.floorsData.find(fl => fl.floorIndex === alloc.floorIndex)?.label || `Tầng ${alloc.floorIndex + 1}`;
                                  return (
                                    <div key={alloc.floorIndex} className="flex justify-between items-center py-1 border-b border-slate-50 last:border-0 pl-2">
                                      <span className="font-medium text-slate-700">{flLabel}</span>
                                      <div className="flex gap-2">
                                        <span className="px-1.5 py-0.5 rounded bg-sky-50 text-sky-700 font-semibold text-[10px] border border-sky-100">
                                          {alloc.domeCount} Dome
                                        </span>
                                        <span className="px-1.5 py-0.5 rounded bg-indigo-50 text-indigo-700 font-semibold text-[10px] border border-indigo-100">
                                          {alloc.bulletCount} Thân
                                        </span>
                                      </div>
                                    </div>
                                  );
                                })}
                              </div>
                            ) : (
                              <span className="text-slate-400 italic pl-2">Chưa phân bổ camera.</span>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}

                {/* 2. If this floor connects to cabinet(s) on other floor(s) */}
                {connections.length > 0 && (
                  <div className="flex flex-col gap-3">
                    <h4 className="text-sm font-bold text-slate-800 uppercase tracking-wider flex items-center gap-1.5">
                      <span className="w-2 h-2 rounded-full bg-blue-500"></span>
                      Kết nối truyền dẫn cáp
                    </h4>

                    {connections.map((conn, idx) => {
                      const cabFloorLabel = activeTower?.floorsData.find(fl => fl.floorIndex === conn.cabinetFloorIndex)?.label || `Tầng ${conn.cabinetFloorIndex + 1}`;
                      const totalCams = conn.domeCount + conn.bulletCount;
                      
                      return (
                        <div key={idx} className="bg-white border border-slate-200 rounded-xl p-4 shadow-sm flex flex-col gap-2.5">
                          <div className="flex items-center justify-between border-b border-slate-100 pb-2">
                            <span className="font-bold text-slate-800 text-sm">
                              Kéo dây về {cabFloorLabel}
                            </span>
                            <span className="px-2 py-0.5 text-xs font-bold bg-blue-50 text-blue-700 rounded-md border border-blue-100">
                              {totalCams} Cam
                            </span>
                          </div>

                          <div className="text-xs text-slate-600 flex flex-col gap-1.5">
                            <div className="flex justify-between items-center py-1">
                              <span className="text-slate-500 font-medium">Tủ nhận dây:</span>
                              <span className="font-semibold text-slate-800">{conn.cabinetType} ({conn.cabinetId?.includes('-') ? conn.cabinetId.slice(-8) : (conn.cabinetId?.split('_').slice(-2).join('_') || conn.cabinetId)})</span>
                            </div>
                            <div className="flex justify-between items-center py-1">
                              <span className="text-slate-500 font-medium">Camera Dome:</span>
                              <span className="font-bold text-sky-700 bg-sky-50 px-1.5 py-0.5 rounded border border-sky-100">{conn.domeCount}</span>
                            </div>
                            <div className="flex justify-between items-center py-1">
                              <span className="text-slate-500 font-medium">Camera Thân (Bullet):</span>
                              <span className="font-bold text-indigo-700 bg-indigo-50 px-1.5 py-0.5 rounded border border-indigo-100">{conn.bulletCount}</span>
                            </div>
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}

                {/* 3. If neither */}
                {!hostedGroup && connections.length === 0 && (
                  <div className="text-center py-8 flex flex-col items-center gap-2">
                    <svg className="w-10 h-10 text-slate-300" fill="none" stroke="currentColor" strokeWidth="1.5" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span className="text-sm font-medium text-slate-400">
                      Tầng này chưa liên kết với tủ thủ công nào.
                    </span>
                  </div>
                )}
              </div>

              {/* Footer */}
              <div className="px-6 py-4 border-t border-slate-100 flex justify-end bg-slate-50">
                <button
                  onClick={() => setViewingFloorConnectionDetail(null)}
                  className="px-5 py-2 bg-slate-800 hover:bg-slate-900 text-white rounded-xl text-sm font-semibold transition shadow-sm"
                >
                  Đóng
                </button>
              </div>
            </div>
          </div>
        );
      })()}

      {/* App Footer */}
      <footer className="bg-white border-t border-[#ECEFF1] py-4 mt-12">
        <div className="max-w-none w-full mx-auto px-6 flex flex-col sm:flex-row justify-between items-center text-xs text-[#455A64] gap-2">
          <div>
            &copy; 2026 Surveillance BOQ Engine. Bản quyền thuộc Ban Nghiên cứu Phát triển Kỹ thuật Công nghệ.
          </div>
          <div className="flex items-center gap-4">
            <a href="#" className="hover:underline hover:text-[#1A237E]" onClick={(e) => { e.preventDefault(); addToast("Hệ thống hoạt động mượt mà", "info"); }}>Hệ thống</a>
            <a href="#" className="hover:underline hover:text-[#1A237E]" onClick={(e) => { e.preventDefault(); addToast("Chính sách bảo mật nội bộ", "info"); }}>Chính sách</a>
            <span className="text-slate-300">|</span>
            <span className="font-mono text-[11px] text-slate-400">V.2026.06.26_UTC</span>
          </div>
        </div>
      </footer>

    </div>
  );
}
