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
import { Project, Tower, FloorData, SiteParameters, HardwareLogic, InventoryItem, StandardPreset } from "./types";

export default function App() {
  // Navigation active state
  // "dashboard" | "parameters" | "logic" | "cost" | "reports"
  const [activeNav, setActiveNav] = useState<"dashboard" | "parameters" | "logic" | "cost" | "reports">("dashboard");

  // Top header tabs state
  // "app" | "projects" | "inventory" | "standards"
  const [activeTab, setActiveTab] = useState<"app" | "projects" | "inventory" | "standards">("app");

  // Inventory form states
  const [newItemCode, setNewItemCode] = useState("");
  const [newItemName, setNewItemName] = useState("");
  const [newItemCategory, setNewItemCategory] = useState<"Camera" | "Switch" | "Rack" | "UPS" | "PDU" | "Converter" | "Cable" | "Accessories">("Camera");
  const [newItemSpec, setNewItemSpec] = useState("");
  const [newItemUnit, setNewItemUnit] = useState("Cái");
  const [newItemPrice, setNewItemPrice] = useState(100000);

  const API_BASE = "http://localhost:8080/api";

  // Load projects from backend
  const [projects, setProjects] = useState<Project[]>([]);
  const [activeProjectId, setActiveProjectId] = useState<string>("");
  const [activeTowerId, setActiveTowerId] = useState<string>("");

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

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
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
  const activeTower = activeProject?.towers?.find((t) => t.id === activeTowerId) || activeProject?.towers?.[0];

  // Temporary edit states for current tower top inputs (to be committed on "Tính toán BOQ" click)
  const [tempFloors, setTempFloors] = useState(activeTower?.floorsCount || 5);
  const [tempBasements, setTempBasements] = useState(activeTower?.basementsCount || 0);
  const [tempHasRoof, setTempHasRoof] = useState(activeTower?.hasRoof || false);
  const [tempH, setTempH] = useState(activeTower?.horizontalDistance || 50);
  const [tempV, setTempV] = useState(activeTower?.verticalDistance || 4);
  const [tempRack, setTempRack] = useState<"2U" | "6U" | "9U" | "12U">(activeTower?.rackType || "2U");

  // State to store cabinet placement floors (indices of upper floors that have cabinets)
  const [cabinetPlacements, setCabinetPlacements] = useState<number[]>([]);

  // Fetch cabinet placement from API
  const fetchCabinetPlacement = async (
    floorsCount: number,
    basementsCount: number,
    hasRoof: boolean,
    horizontalDistance: number,
    verticalDistance: number,
    rackType: string,
    floorsData: FloorData[]
  ) => {
    if (floorsCount <= 0) {
      setCabinetPlacements([]);
      return;
    }
    try {
      const sortedFloors = [...floorsData]
        .sort((a, b) => a.floorIndex - b.floorIndex)
        .map(f => ({
          floorIndex: f.floorIndex,
          label: f.label,
          camerasCount: f.camerasCount
        }));

      const res = await fetch(`${API_BASE}/calculate/cabinet-placement`, {
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
          floors: sortedFloors
        })
      });
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
                      if (backendInfo.isCabinetPlaced) {
                        return {
                          ...f,
                          sw24Count: backendInfo.sw24Count ?? 0,
                          sw16Count: backendInfo.sw16Count ?? 0,
                          upsType: backendInfo.upsCount === 1 ? "1K" : (backendInfo.upsCount === 2 ? "2K" : "None"),
                          pduCount: backendInfo.pduCount ?? 0,
                          convCount: backendInfo.convCount ?? 0,
                          cameraQuantityInCabinet: backendInfo.cameraQuantityInCabinet ?? 0,
                          isCabinetPlaced: true,
                          fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                          toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                        };
                      }
                    }
                    // Non-cabinet floor: clear cabinet equipment quantities
                    return {
                      ...f,
                      sw24Count: 0,
                      sw16Count: 0,
                      upsType: "None",
                      pduCount: 0,
                      convCount: 0,
                      cameraQuantityInCabinet: 0,
                      isCabinetPlaced: false,
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

  // Sync temp values when active tower changes
  useEffect(() => {
    if (activeTower) {
      setTempFloors(activeTower?.floorsCount);
      setTempBasements(activeTower?.basementsCount || 0);
      setTempHasRoof(activeTower?.hasRoof || false);
      setTempH(activeTower?.horizontalDistance);
      setTempV(activeTower?.verticalDistance);
      setTempRack(activeTower?.rackType);
      
      // Load cabinet placement for current active tower
      fetchCabinetPlacement(
        activeTower?.floorsCount || 0,
        activeTower?.basementsCount || 0,
        activeTower?.hasRoof || false,
        activeTower?.horizontalDistance || 0,
        activeTower?.verticalDistance || 0,
        activeTower?.rackType || "2U",
        activeTower?.floorsData || []
      );
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
    const localPlacements = localCalculateCabinetPlacement(tempFloors, tempH, tempV);
    const newCabinetPlacements = localPlacements.map(lvl => tempBasements + lvl - 1);
    setCabinetPlacements(newCabinetPlacements);

    // Perform BOQ calculation
    const updatedFloorsData = calculateProjectBOQ(
      tempFloors,
      tempH,
      tempV,
      tempRack,
      activeTower?.siteParams,
      activeTower?.hardwareLogic,
      activeTower?.floorsData, // pass current to try to retain customized floor cam numbers if within bounds
      tempBasements,
      tempHasRoof,
      newCabinetPlacements
    );

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
                floorsData: updatedFloorsData,
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

    // Call PUT /api/towers/{id} to update tower in backend
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
          heightLength: tempV
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
            updatedFloorsData
          );
        }
      })
      .catch(err => console.error("Error saving recalculated tower to backend", err));
    }

    addToast("Tính toán lại BOQ thành công!", "success");
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

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
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
    let rack9u = 0;
    let rack12u = 0;
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
      const match = f.label.match(/Tầng\s+(\d+)/);
      const isUpperFloor = match !== null && !f.label.includes("Mái");
      const physicalFloorNum = isUpperFloor ? parseInt(match[1]) : null;
      const needsCabinet = physicalFloorNum !== null && cabinetPlacements.includes(physicalFloorNum);
      if (needsCabinet) {
        if (activeTower?.rackType === "2U") rack2u += 1;
        else if (activeTower?.rackType === "6U") rack6u += 1;
        else if (activeTower?.rackType === "9U") rack9u += 1;
        else if (activeTower?.rackType === "12U") rack12u += 1;
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
      "item-rack-9u": rack9u,
      "item-rack-12u": rack12u,
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
      const match = f.label.match(/Tầng\s+(\d+)/);
      const isUpperFloor = match !== null && !f.label.includes("Mái");
      const physicalFloorNum = isUpperFloor ? parseInt(match[1]) : null;
      const isCabinetPlaced = physicalFloorNum !== null && cabinetPlacements.includes(physicalFloorNum);
      const cabinetCol = isCabinetPlaced 
        ? (f.cableLength > 0 
          ? `Tủ (${f.cameraQuantityInCabinet ?? 0} Cam) - ${f.cableLength}m` 
          : `Tủ (${f.cameraQuantityInCabinet ?? 0} Cam)`)
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
  const totalRacks = activeTower?.floorsData?.filter((f) => {
    const match = f.label.match(/Tầng\s+(\d+)/);
    const isUpperFloor = match !== null && !f.label.includes("Mái");
    const physicalFloorNum = isUpperFloor ? parseInt(match[1]) : null;
    return physicalFloorNum !== null && cabinetPlacements.includes(physicalFloorNum);
  }).length || 0;

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
                          }}
                          className={`px-4 py-2.5 text-sm font-semibold border-t-2 border-x rounded-t-lg transition-all duration-150 flex items-center gap-2 ${
                            activeTowerId === t.id
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
                    
                    <button
                      onClick={() => {
                        const name = prompt("Nhập tên Tháp (Tower) mới:", `Tháp ${String.fromCharCode(65 + (activeProject?.towers?.length || 0))}`);
                        if (name) {
                          handleCreateTower(name);
                        }
                      }}
                      className="px-3 py-1.5 text-xs font-bold text-[#1A237E] hover:bg-[#E8EAF6] rounded transition flex items-center gap-1 ml-2 border border-[#1A237E]/20"
                    >
                      <Plus className="w-3.5 h-3.5" />
                      <span>Thêm Tháp</span>
                    </button>
                  </div>

                  {!activeTower ? (
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
                        <div>
                          <label className="block text-left text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                            Loại tủ
                          </label>
                          <select
                            value={tempRack}
                            onChange={(e) => setTempRack(e.target.value as "2U" | "6U" | "9U" | "12U")}
                            className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-base font-semibold text-center focus:border-[#1A237E] focus:outline-none transition"
                          >
                            <option value="2U">2U</option>
                            <option value="6U">6U</option>
                            <option value="9U">9U</option>
                            <option value="12U">12U</option>
                          </select>
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
                                heightLength: tempV
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
                              defaultFloors
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
                      {/* Top Calculator Input Section */}
                      <div className="bg-white border border-[#ECEFF1] rounded-lg p-4 shadow-xs">
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 lg:grid-cols-7 gap-4 items-end">
                          
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
                              <option value="9U">9U</option>
                              <option value="12U">12U</option>
                            </select>
                          </div>

                          {/* Compute Trigger Button */}
                          <div>
                            <button
                              onClick={handleRecalculate}
                              className="w-full bg-[#1A237E] hover:bg-[#1A237E]/95 text-white py-2 px-4 rounded text-sm font-semibold shadow-xs transition flex items-center justify-center gap-2 h-[38px]"
                            >
                              <RefreshCw className="w-4 h-4" />
                              <span>Tính toán BOQ</span>
                            </button>
                          </div>

                        </div>
                      </div>

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

                      {/* Grid Container for Excel Table (Left) and BOQ Table (Right) */}
                      <div className="grid grid-cols-1 xl:grid-cols-2 gap-6 items-start w-full">
                        
                        {/* Excel-like BOQ Template Table (Left) */}
                        <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs overflow-hidden w-full">
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
                          <div className="overflow-x-auto p-4 bg-slate-50/30">
                            <div className="border border-slate-200 rounded overflow-hidden shadow-xs bg-white min-w-[850px]">
                              
                              {/* Spreadsheet Title Block */}
                              <div className="border-b border-slate-200 bg-[#F8F9FA] px-4 py-3 text-center">
                                <div className="text-sm font-sans font-bold text-[#1A237E] uppercase tracking-wide">
                                  KHỐI LƯỢNG BOQ GÓI 62 CAMERA HÀNH LANG LÔ E2 (BLOCK H)
                                </div>
                              </div>

                              <table className="w-full text-xs text-left border-collapse font-sans">
                                <thead>
                                  {/* Main Table Column Titles */}
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
                                  {/* Category I Header Row */}
                                  <tr className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                                    <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                                      I. HẠNG MỤC VẬT TƯ CHÍNH VÀ GIÁM SÁT VÀ ĐỊNH TUYẾN
                                    </td>
                                  </tr>

                                  {/* Section I Items */}
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Camera IP Dome 2MP HIKVISION DS-2CD1121G0-I
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">2</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Camera IP thân 2MP HIKVISION DS-2CD1021G0-I
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_2")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">3</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Đầu ghi hình camera IP 32 kênh HIKVISION DS-7732NXI-K4
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_3")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">4</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Đầu ghi hình camera IP 16 kênh
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_4")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">5</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Ổ Cứng 10T WESTERN
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_5")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">6</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Switch Hikvision POE 24 cổng DS-3E1326P-EI
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_6")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">7</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Switch Hikvision POE 16 cổng DS-3E1318P-EI
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_7")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">8</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Switch 16 port CISCO CBS110-16T-EU
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_8")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">9</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Switch 24 port CISCO
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_9")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">10</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Màn hình quan sát 43 inch SamSung(khung kê + HDMI (15m))
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat1_10")}
                                  </tr>

                                  {/* Category II Header Row */}
                                  <tr className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                                    <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                                      II. HẠNG MỤC TRUYỀN DẪN
                                    </td>
                                  </tr>

                                  {/* Section II Items */}
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Cáp quang 4FO
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Mét</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">2</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Cáp mạng Cat5E
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Mét</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_2")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">3</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Bộ chuyển đổi quang điện Gigabit GNETCOM 10/100/1000M GNC-2111S-20A/B
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_3")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">4</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Tủ mạng rack 2U
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_4")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">5</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Tủ mạng rack 6U
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_5")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">6</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Tủ mạng rack 10U (Có bánh xe)
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_6")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">7</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Tủ mạng rack 20U (Có bánh xe)
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_7")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">8</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Tủ mạng rack 32U
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_8")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">9</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Tủ mạng rack 42U
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_9")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">10</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      ODF 12FO SC/UPC (Full Phụ kiện)
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat2_10")}
                                  </tr>

                                  {/* Category III Header Row */}
                                  <tr className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                                    <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                                      III. HẠNG MỤC ĐIỆN
                                    </td>
                                  </tr>

                                  {/* Section III Items */}
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Dây điện CVV 2x2.5
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Mét</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat3_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">2</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Thanh nguồn PDU đa năng 6 ổ cắm 3 chấu chuẩn 19"
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat3_2")}
                                  </tr>

                                  {/* Category IV Header Row */}
                                  <tr className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                                    <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                                      IV. HẠNG MỤC NGUỒN DỰ PHÒNG
                                    </td>
                                  </tr>

                                  {/* Section IV Items */}
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Nguồn lưu điện UPS ARES Model AR610 1000VA/800W
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat4_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">3</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Nguồn lưu điện UPS ARES Model AR630 3000VA-2400W
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat4_3")}
                                  </tr>

                                  {/* Category V Header Row */}
                                  <tr className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                                    <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                                      V. VẬT TƯ PHỤ
                                    </td>
                                  </tr>

                                  {/* Section V Items */}
                                  <tr className="divide-x divide-slate-200 bg-yellow-100/70 hover:bg-yellow-100 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-700">1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Vật tư phụ
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-700 text-xs">
                                      Bao gồm ống điện, ruột gà, vít, tacke...
                                    </td>
                                    <td className="py-2.5 px-1 text-center text-slate-700 font-semibold">Gói</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 bg-yellow-100/70 hover:bg-yellow-100 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-700">1.1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Vật tư phụ kết nối
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-700"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700 font-semibold">Gói</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Đầu mạng AMP Cat 5
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_1_sub1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Dây nhảy quang SC/UPC SC/UPC 3M
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Sợi</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_1_sub2")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      ODF 4FO SC/UPC - SC/UPC (Full phụ kiện)
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Bộ</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_1_sub3")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Dây nhảy mạng Cat5
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Sợi</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_1_sub4")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Thanh quản lý cáp mạng 19inch
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Cái</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_1_sub5")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 bg-yellow-100/70 hover:bg-yellow-100 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-700">1.2</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Vật tư phụ thi công
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-700"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_2")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">1.2.1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Ruột gà phi 20
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Mét</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_2_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">1.2.2</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Ống điện D20
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Mét</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat5_1_2_2")}
                                  </tr>

                                  {/* Category VI Header Row */}
                                  <tr className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                                    <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                                      VI. CHI PHÍ LẮP ĐẶT
                                    </td>
                                  </tr>

                                  {/* Section VI Items */}
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-600">1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Chi phí lắp đặt
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600 text-xs">
                                      Thi công trọn gói và hướng dẫn vận hành
                                    </td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Gói</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 bg-yellow-100/70 hover:bg-yellow-100 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-700">1.1</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Nhân công Cấu hình thiết lập
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-700"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700 font-semibold">Công</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1_1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Thiết lập cấu hình
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Công</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1_1_sub1")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Hồ sơ hướng dẫn
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Công</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1_1_sub2")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Kiểm thử T&C
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Công</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1_1_sub3")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Dự trù Thay đổi cấu hình phát sinh
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Công</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1_1_sub4")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Nghiệm thu
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Công</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1_1_sub5")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                                    <td className="py-2.5 px-1 text-center text-slate-400">-</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight pl-6">
                                      Bảo hành thiết lập
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-600"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700">Công</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1_1_sub6")}
                                  </tr>
                                  <tr className="divide-x divide-slate-200 bg-yellow-100/70 hover:bg-yellow-100 transition">
                                    <td className="py-2.5 px-1 text-center font-semibold text-slate-700">1.2</td>
                                    <td className="py-2.5 px-2 font-semibold text-slate-800 leading-tight">
                                      Triển khai
                                    </td>
                                    <td className="py-2.5 px-2 text-slate-700"></td>
                                    <td className="py-2.5 px-1 text-center text-slate-700 font-semibold">Công</td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    <td className="py-2.5 px-1 text-center font-mono"></td>
                                    {renderNoteCell("cat6_1_2")}
                                  </tr>
                                </tbody>
                              </table>
                            </div>
                          </div>
                        </div>

                        {/* Detailed Interactive BOQ Sheet */}
                        <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs overflow-hidden w-full">
                        <div className="px-6 py-4 border-b border-[#ECEFF1] flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-slate-50/50">
                          <div>
                            <h3 className="font-sans font-bold text-base text-[#191c1e]">
                              Bảng tính BOQ chi tiết
                            </h3>
                            <p className="text-xs text-[#455A64]">
                              Nhấp trực tiếp vào ô để thay đổi số lượng camera của từng tầng hoặc chọn nhiều tầng để đồng bộ nhanh
                            </p>
                          </div>

                          {/* Bulk editing banner */}
                          {selectedFloorIndexes.length > 0 ? (
                            <div className="flex items-center gap-3 bg-[#E8EAF6] px-3 py-1.5 rounded-lg border border-[#1A237E]/20 self-start sm:self-auto">
                              <span className="text-xs font-semibold text-[#1A237E]">
                                Đã chọn {selectedFloorIndexes.length} tầng
                              </span>
                              <div className="flex items-center gap-1.5">
                                <input
                                  id="bulk-camera-input"
                                  type="number"
                                  min="0"
                                  placeholder="Số cam"
                                  className="w-16 bg-white border border-[#ECEFF1] rounded px-2 py-1 text-xs font-semibold text-center focus:border-[#1A237E] focus:outline-none"
                                  onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                      const val = parseInt((e.target as HTMLInputElement).value);
                                      if (!isNaN(val) && val >= 0) {
                                        handleBulkUpdateCamera(val);
                                      }
                                    }
                                  }}
                                />
                                <button
                                  onClick={() => {
                                    const el = document.getElementById("bulk-camera-input") as HTMLInputElement;
                                    if (el) {
                                      const val = parseInt(el.value);
                                      if (!isNaN(val) && val >= 0) {
                                        handleBulkUpdateCamera(val);
                                      } else {
                                        alert("Vui lòng nhập số camera hợp lệ!");
                                      }
                                    }
                                  }}
                                  className="bg-[#1A237E] hover:bg-[#1A237E]/90 text-white text-xs font-bold px-2.5 py-1.5 rounded transition shadow-sm"
                                >
                                  Đồng bộ
                                </button>
                              </div>
                              <button
                                onClick={() => setSelectedFloorIndexes([])}
                                className="text-xs text-slate-500 hover:text-red-600 font-medium ml-1 transition"
                              >
                                Hủy
                              </button>
                            </div>
                          ) : (
                            <button
                              onClick={handleExportCSV}
                              title="Tải về file excel CSV"
                              className="p-1.5 text-[#455A64] hover:text-[#1A237E] hover:bg-slate-100 rounded transition self-end sm:sm:self-auto"
                            >
                              <Download className="w-5 h-5" />
                            </button>
                          )}
                        </div>

                        <div className="overflow-x-auto">
                          <table className="w-full text-left border-collapse min-w-[900px]">
                            <thead>
                              <tr className="bg-slate-50 border-b border-[#ECEFF1] text-[11px] font-bold text-[#455A64] uppercase tracking-wider">
                                <th className="py-3 px-4 w-12 text-center">
                                  <input
                                    type="checkbox"
                                    checked={selectedFloorIndexes.length === activeTower?.floorsData.length && activeTower?.floorsData.length > 0}
                                    onChange={handleSelectAllFloors}
                                    className="rounded text-[#1A237E] focus:ring-[#1A237E] w-4 h-4 cursor-pointer"
                                  />
                                </th>
                                <th className="py-3 px-4 w-28">TẦNG</th>
                                <th className="py-3 px-3 w-32">SỐ CAMERA</th>
                                <th className="py-3 px-3 w-28">CAM DOME</th>
                                <th className="py-3 px-3 w-28">CAM THÂN</th>
                                <th className="py-3 px-3 w-32">TỦ & SỐ CAM</th>
                                <th className="py-3 px-3 w-20">SW24</th>
                                <th className="py-3 px-3 w-20">SW16</th>
                                <th className="py-3 px-3 w-24">UPS 1K/2K</th>
                                <th className="py-3 px-3 w-20">PDU</th>
                                <th className="py-3 px-3 w-28">CONVERTER</th>
                              </tr>
                            </thead>
                            <tbody className="divide-y divide-[#ECEFF1] text-sm">
                              {(() => {
                                const floors = activeTower?.floorsData || [];
                                const roofFloors = floors.filter(f => f.label.includes("Mái"));
                                const upperFloors = floors.filter(f => f.label.startsWith("Tầng") && !f.label.includes("Mái"));
                                const basementFloors = floors.filter(f => f.label.startsWith("B"));

                                const sortedUpperFloors = [...upperFloors].sort((a, b) => {
                                  const numA = parseInt(a.label.replace("Tầng", "").trim()) || 0;
                                  const numB = parseInt(b.label.replace("Tầng", "").trim()) || 0;
                                  return numB - numA;
                                });

                                const sortedBasementFloors = [...basementFloors].sort((a, b) => {
                                  const numA = parseInt(a.label.replace("B", "").trim()) || 0;
                                  const numB = parseInt(b.label.replace("B", "").trim()) || 0;
                                  return numA - numB;
                                });

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
                                  { bg: 'bg-indigo-50 hover:bg-indigo-100/70', border: 'border-l-4 border-indigo-500/80', labelBg: 'bg-indigo-100 text-indigo-700 border border-indigo-200' },
                                  { bg: 'bg-teal-50 hover:bg-teal-100/70', border: 'border-l-4 border-teal-500/80', labelBg: 'bg-teal-100 text-teal-700 border border-teal-200' },
                                  { bg: 'bg-amber-50 hover:bg-amber-100/70', border: 'border-l-4 border-amber-500/80', labelBg: 'bg-amber-100 text-amber-700 border border-amber-200' },
                                  { bg: 'bg-rose-50 hover:bg-rose-100/70', border: 'border-l-4 border-rose-500/80', labelBg: 'bg-rose-100 text-rose-700 border border-rose-200' },
                                  { bg: 'bg-sky-50 hover:bg-sky-100/70', border: 'border-l-4 border-sky-500/80', labelBg: 'bg-sky-100 text-sky-700 border border-sky-200' },
                                  { bg: 'bg-violet-50 hover:bg-violet-100/70', border: 'border-l-4 border-violet-500/80', labelBg: 'bg-violet-100 text-violet-700 border border-violet-200' },
                                  { bg: 'bg-emerald-50 hover:bg-emerald-100/70', border: 'border-l-4 border-emerald-500/80', labelBg: 'bg-emerald-100 text-emerald-700 border border-emerald-200' }
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

                                const renderRow = (f: FloorData) => {
                                  const isCabinetPlaced = cabinetPlacements.includes(f.floorIndex);
                                  const styleGroup = getRangeStyle(f);

                                  return (
                                    <tr 
                                      key={f.floorIndex} 
                                      className={`transition ${
                                        selectedFloorIndexes.includes(f.floorIndex) 
                                          ? 'bg-[#E8EAF6]/30' 
                                          : styleGroup.bg
                                      } ${styleGroup.border}`}
                                    >
                                    <td className="py-2 px-4 text-center">
                                      <input
                                        type="checkbox"
                                        checked={selectedFloorIndexes.includes(f.floorIndex)}
                                        onClick={(e) => handleToggleSelectFloor(f.floorIndex, e)}
                                        onChange={() => {}}
                                        className="rounded text-[#1A237E] focus:ring-[#1A237E] w-4 h-4 cursor-pointer"
                                      />
                                    </td>
                                    <td className="py-2 px-4 font-semibold text-[#191c1e]">
                                      <div className="flex items-center gap-2">
                                        <span>{f.label}</span>
                                        {isCabinetPlaced && (
                                          <span className="inline-flex items-center gap-1 px-1.5 py-0.5 text-[10px] font-bold rounded bg-[#1A237E]/10 text-[#1A237E] border border-[#1A237E]/20" title="Tầng đặt tủ rack">
                                            <svg className="w-3.5 h-3.5 text-[#1A237E]" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                                              <path strokeLinecap="round" strokeLinejoin="round" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01" />
                                            </svg>
                                            RACK
                                          </span>
                                        )}
                                      </div>
                                    </td>
                                    
                                    {/* Editable Camera Count */}
                                    <td className="py-2 px-3">
                                      <input
                                        type="number"
                                        min="0"
                                        placeholder="0"
                                        value={f.camerasCount === 0 ? "" : f.camerasCount}
                                        onFocus={(e) => e.target.select()}
                                        onChange={(e) => handleUpdateFloorCell(f.floorIndex, "camerasCount", Math.max(0, parseInt(e.target.value) || 0))}
                                        className="w-20 bg-[#f8f9fb] border border-[#ECEFF1] hover:border-slate-300 focus:border-[#1A237E] rounded px-2 py-1 text-center font-mono font-semibold focus:outline-none transition"
                                      />
                                    </td>

                                    {/* Editable Dome Count */}
                                    <td className="py-2 px-3">
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
                                    <td className="py-2 px-3">
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

                                    {/* Calculated Cab length / Total ports column */}
                                    <td className="py-2 px-3 text-center">
                                      {isCabinetPlaced ? (
                                        <div className="px-2 py-1 font-mono text-xs font-semibold text-center border rounded inline-block min-w-[130px] text-[#1A237E] bg-[#E8EAF6] border-[#1A237E]/20">
                                          {f.cableLength > 0
                                            ? `Tủ (${f.cameraQuantityInCabinet ?? 0} Cam) - ${f.cableLength}m` 
                                            : `Tủ (${f.cameraQuantityInCabinet ?? 0} Cam)`}
                                        </div>
                                      ) : (
                                        <span className="text-slate-300">-</span>
                                      )}
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
                                  </tr>
                                );
                              };

                                return (
                                  <>
                                    {/* Group 3: Tầng Mái */}
                                    {roofFloors.length > 0 && (
                                      <>
                                        <tr className="bg-slate-100/90 border-y border-[#ECEFF1] text-[11px] font-bold text-[#1A237E] select-none">
                                          <td colSpan={11} className="py-2.5 px-4">
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
                                          <td colSpan={11} className="py-2.5 px-4">
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
                                          <td colSpan={11} className="py-2.5 px-4">
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

        </main>
      </>
    )}
  </div>

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
