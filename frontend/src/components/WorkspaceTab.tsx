import React from "react";
import { Building, Activity, Plus, RefreshCw, Trash2, ArrowRight } from "lucide-react";
import { Project, Tower, DynamicCategory, FloorData, ManualGroup } from "../types";
import { TowerBOMTable } from "./TowerBOMTable";
import { FloorCalculatorTable } from "./FloorCalculatorTable";
import { SummaryBOMTab } from "./SummaryBOMTab";
import { CableDetailsTable } from "./CableDetailsTable";

interface WorkspaceTabProps {
  activeProject: Project;
  isSummaryTabActive: boolean;
  setIsSummaryTabActive: (val: boolean) => void;
  activeTowerId: string;
  setActiveTowerId: (val: string) => void;
  handleDeleteTower: (id: string) => void;
  handleCreateTower: (name: string) => void;
  tempFloors: number;
  setTempFloors: (val: number) => void;
  tempBasements: number;
  setTempBasements: (val: number) => void;
  tempHasRoof: boolean;
  setTempHasRoof: (val: boolean) => void;
  tempH: number;
  setTempH: (val: number) => void;
  tempV: number;
  setTempV: (val: number) => void;
  tempRack: "2U" | "6U" | "10U";
  setTempRack: (val: "2U" | "6U" | "10U") => void;
  tempQuantity2U: number;
  setTempQuantity2U: (val: number) => void;
  handleRecalculate: () => void;
  handleResetBOQ: () => void;
  activeTower: Tower | null;
  dynamicCategories: DynamicCategory[];
  flattenCategoryTree: (cats: DynamicCategory[]) => any[];
  selectedProducts: Record<string, Record<string, string>>;
  setSelectedProducts: React.Dispatch<React.SetStateAction<Record<string, Record<string, string>>>>;
  customBOMOverrides: Record<string, Record<string, number>>;
  setCustomBOMOverrides: React.Dispatch<React.SetStateAction<Record<string, Record<string, number>>>>;
  bomData: any;
  leftTableNotes: Record<string, string>;
  setLeftTableNotes: React.Dispatch<React.SetStateAction<Record<string, string>>>;
  selectedFloorIndexes: number[];
  setSelectedFloorIndexes: React.Dispatch<React.SetStateAction<number[]>>;
  activeCabinetIndex: number | null;
  setActiveCabinetIndex: React.Dispatch<React.SetStateAction<number | null>>;
  manualGroups: ManualGroup[];
  setManualGroups: React.Dispatch<React.SetStateAction<ManualGroup[]>>;
  setViewingFloorConnectionDetail: (index: number | null) => void;
  setEditingCabinetIndex: (index: number | null) => void;
  setTempCabinets: (cabs: any[]) => void;
  cabinetPlacements: number[];
  handleSelectAllFloors: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleToggleSelectFloor: (index: number, e: React.MouseEvent) => void;
  handleToggleCabinet: (index: number) => void;
  handleUpdateFloorCell: (index: number, field: string, val: any) => void;
  handleDeleteFloor: (index: number) => void;
  handleExportCSV: () => void;
  updateTowerFloorsData: (floors: FloorData[], groups: ManualGroup[], mode?: "auto" | "manual") => void;
  fetchCabinetPlacement: (
    floors: number,
    basements: number,
    hasRoof: boolean,
    hDist: number,
    vDist: number,
    rackType: string,
    floorsData: FloorData[],
    mode: "auto" | "manual",
    groups: ManualGroup[],
    qty2U: number
  ) => void;
  syncFloorsWithManualGroups: (floors: FloorData[], groups: ManualGroup[]) => FloorData[];
  addToast: (msg: string, type: "success" | "error" | "info") => void;
  isXl: boolean;
  leftWidth: number;
  setLeftWidth: React.Dispatch<React.SetStateAction<number>>;
  isDragging: boolean;
  setIsDragging: (val: boolean) => void;
  totalCamerasCount: number;
  totalDomeCount: number;
  totalBulletCount: number;
  totalSw24: number;
  totalSw16: number;
  totalRacks: number;
  totalUPS1K: number;
  totalUPS2K: number;
  totalPDU: number;
  totalConv: number;
  selectedTowersSummary: Record<string, boolean>;
  setSelectedTowersSummary: React.Dispatch<React.SetStateAction<Record<string, boolean>>>;
  isCalculatingSummary: boolean;
  handleCalculateSummary: () => void;
  isExportingExcel: boolean;
  handleExportExcel: () => void;
  summaryBomData: any;
  stickyHeaderStyle?: React.CSSProperties;
  API_BASE: string;
  DEFAULT_SITE_PARAMS: any;
  DEFAULT_HARDWARE_LOGIC: any;
  calculationMode: "auto" | "manual";
  setCalculationMode: (mode: "auto" | "manual") => void;
  fetchProjects: (activeId?: string) => Promise<void>;
  calculateProjectBOQ: (
    floors: number,
    hDist: number,
    vDist: number,
    rackType: string,
    siteParams: any,
    hardwareLogic: any,
    manualFloors?: any[],
    basements?: number,
    hasRoof?: boolean
  ) => any[];
}

export const WorkspaceTab: React.FC<WorkspaceTabProps> = ({
  activeProject,
  isSummaryTabActive,
  setIsSummaryTabActive,
  activeTowerId,
  setActiveTowerId,
  handleDeleteTower,
  handleCreateTower,
  tempFloors,
  setTempFloors,
  tempBasements,
  setTempBasements,
  tempHasRoof,
  setTempHasRoof,
  tempH,
  setTempH,
  tempV,
  setTempV,
  tempRack,
  setTempRack,
  tempQuantity2U,
  setTempQuantity2U,
  handleRecalculate,
  handleResetBOQ,
  activeTower,
  dynamicCategories,
  flattenCategoryTree,
  selectedProducts,
  setSelectedProducts,
  customBOMOverrides,
  setCustomBOMOverrides,
  bomData,
  leftTableNotes,
  setLeftTableNotes,
  selectedFloorIndexes,
  setSelectedFloorIndexes,
  activeCabinetIndex,
  setActiveCabinetIndex,
  manualGroups,
  setManualGroups,
  setViewingFloorConnectionDetail,
  setEditingCabinetIndex,
  setTempCabinets,
  cabinetPlacements,
  handleSelectAllFloors,
  handleToggleSelectFloor,
  handleToggleCabinet,
  handleUpdateFloorCell,
  handleDeleteFloor,
  handleExportCSV,
  updateTowerFloorsData,
  fetchCabinetPlacement,
  syncFloorsWithManualGroups,
  addToast,
  isXl,
  leftWidth,
  setLeftWidth,
  isDragging,
  setIsDragging,
  totalCamerasCount,
  totalDomeCount,
  totalBulletCount,
  totalSw24,
  totalSw16,
  totalRacks,
  totalUPS1K,
  totalUPS2K,
  totalPDU,
  totalConv,
  selectedTowersSummary,
  setSelectedTowersSummary,
  isCalculatingSummary,
  handleCalculateSummary,
  isExportingExcel,
  handleExportExcel,
  summaryBomData,
  stickyHeaderStyle,
  API_BASE,
  DEFAULT_SITE_PARAMS,
  DEFAULT_HARDWARE_LOGIC,
  calculationMode,
  setCalculationMode,
  fetchProjects,
  calculateProjectBOQ,
}) => {
  const containerRef = React.useRef<HTMLDivElement>(null);

  const [templates, setTemplates] = React.useState<any[]>([]);
  const [isSavingTemplate, setIsSavingTemplate] = React.useState(false);
  const [activeTemplateId, setActiveTemplateId] = React.useState<string>("");

  React.useEffect(() => {
    setActiveTemplateId("");
  }, [activeTower]);

  const fetchTemplates = async () => {
    try {
      const res = await fetch(`${API_BASE}/templates`);
      if (res.ok) {
        const data = await res.json();
        setTemplates(data);
      }
    } catch (err) {
      console.error("Failed to fetch templates", err);
    }
  };

  React.useEffect(() => {
    fetchTemplates();
  }, []);

  const getTemplateProductIds = () => {
    const ids: string[] = [];
    const rows = flattenCategoryTree(dynamicCategories);
    const towerId = activeTower?.id || "";
    const towerSelected = selectedProducts[towerId] || {};
    
    rows.forEach(item => {
      if (item.products && item.products.length > 0) {
        const selectedId = towerSelected[item.code] || item.products[0]?.id;
        if (selectedId) {
          ids.push(selectedId);
        }
      }
    });
    return ids;
  };

  const handleSaveTemplate = async () => {
    const name = prompt("Nhập tên Cấu hình mẫu (Template) mới:");
    if (!name) return;
    
    const productIds = getTemplateProductIds();
    if (productIds.length === 0) {
      addToast("Không có thiết bị nào để lưu thành mẫu!", "error");
      return;
    }
    
    setIsSavingTemplate(true);
    try {
      const res = await fetch(`${API_BASE}/templates`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          name,
          description: `Cấu hình mẫu lưu từ ${activeTower?.name} dự án ${activeProject.name}`,
          productIds
        })
      });
      if (res.ok) {
        addToast("Lưu cấu hình mẫu thành công!", "success");
        fetchTemplates();
      } else {
        addToast("Lỗi khi lưu cấu hình mẫu!", "error");
      }
    } catch (err) {
      console.error("Failed to save template", err);
      addToast("Lỗi kết nối khi lưu cấu hình mẫu!", "error");
    } finally {
      setIsSavingTemplate(false);
    }
  };

  const applyTemplate = (template: any) => {
    if (!template || !template.products) return;
    
    setSelectedProducts(prev => {
      const towerId = activeTower?.id || "";
      const updatedTowerProds = { ...(prev[towerId] || {}) };
      
      template.products.forEach((p: any) => {
        if (p.productTypeCode) {
          updatedTowerProds[p.productTypeCode] = p.id;
        }
      });
      
      return {
        ...prev,
        [towerId]: updatedTowerProds
      };
    });
    addToast(`Đã áp dụng mẫu "${template.name}" cho ${activeTower?.name}!`, "success");
  };

  const startResize = (e: React.MouseEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  React.useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      if (!isDragging || !containerRef.current) return;
      const containerRect = containerRef.current.getBoundingClientRect();
      const newWidth = ((e.clientX - containerRect.left) / containerRect.width) * 100;
      if (newWidth > 20 && newWidth < 80) {
        setLeftWidth(newWidth);
      }
    };

    const handleMouseUp = () => {
      setIsDragging(false);
    };

    if (isDragging) {
      window.addEventListener("mousemove", handleMouseMove);
      window.addEventListener("mouseup", handleMouseUp);
    }
    return () => {
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseup", handleMouseUp);
    };
  }, [isDragging]);

  return (
    <div className="flex flex-col gap-6 text-left font-sans">
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
        <SummaryBOMTab
          activeProject={activeProject}
          selectedTowersSummary={selectedTowersSummary}
          setSelectedTowersSummary={setSelectedTowersSummary}
          isCalculatingSummary={isCalculatingSummary}
          handleCalculateSummary={handleCalculateSummary}
          isExportingExcel={isExportingExcel}
          handleExportExcel={handleExportExcel}
          summaryBomData={summaryBomData}
          dynamicCategories={dynamicCategories}
          flattenCategoryTree={flattenCategoryTree}
          leftTableNotes={leftTableNotes}
          customBOMOverrides={customBOMOverrides}
        />
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
                  activeTower?.siteParams || {},
                  activeTower?.hardwareLogic || {},
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
              <div className="bg-white border-l-4 border-l-[#1A237E] border border-[#ECEFF1] rounded-r-lg p-5 flex flex-col justify-between">
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
              <div className="bg-white border-l-4 border-l-[#455A64] border border-[#ECEFF1] rounded-r-lg p-5">
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

              <div>
                <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                  Loại Tủ
                </label>
                <select
                  value={tempRack}
                  onChange={(e) => setTempRack(e.target.value as any)}
                  className="w-full bg-[#f8f9fb] border border-[#ECEFF1] rounded px-3 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition text-center cursor-pointer"
                >
                  <option value="2U">2U</option>
                  <option value="6U">6U</option>
                  <option value="10U">10U</option>
                </select>
              </div>

              {tempRack === "2U" && (
                <div>
                  <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                    Số lượng 2U
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

              <div className="flex gap-2">
                <button
                  onClick={handleRecalculate}
                  className="bg-[#1A237E] hover:bg-[#1A237E]/95 text-white py-2 px-4 rounded text-sm font-semibold shadow-xs transition flex items-center justify-center gap-1.5 h-[38px] whitespace-nowrap flex-1"
                >
                  <RefreshCw className="w-4 h-4" />
                  <span>Tính toán</span>
                </button>
                <button
                  onClick={handleResetBOQ}
                  className="bg-red-50 hover:bg-red-100 text-red-700 border border-red-200 py-2 px-4 rounded text-sm font-semibold shadow-xs transition flex items-center justify-center gap-1.5 h-[38px] whitespace-nowrap"
                  title="Reset dữ liệu"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>

          {/* Grid Container for Excel Table (Left) and BOQ Table (Right) */}
          <div ref={containerRef} id="bom-split-container" className="flex flex-col xl:flex-row gap-0 items-start w-full relative">
            <div 
              className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs w-full overflow-hidden"
              style={isXl ? { width: `calc(${leftWidth}% - 12px)`, flexShrink: 0 } : {}}
            >
              <div className="px-6 py-4 border-b border-[#ECEFF1] flex flex-col lg:flex-row lg:items-center justify-between gap-4 bg-slate-50/50">
                <div>
                  <h3 className="font-sans font-bold text-base text-[#191c1e]">
                    Khung BOQ Thiết bị &amp; Vật tư (Mẫu Excel)
                  </h3>
                  <p className="text-xs text-[#455A64]">
                    Khung sườn danh mục vật tư chính, phụ kiện và nhân công lắp đặt
                  </p>
                </div>
                
                <div className="flex items-center gap-2 flex-wrap">
                  {templates.length > 0 && (
                    <select
                      value={activeTemplateId}
                      onChange={(e) => {
                        const templateId = e.target.value;
                        if (templateId) {
                          const t = templates.find((temp: any) => temp.id === templateId);
                          applyTemplate(t);
                          setActiveTemplateId(templateId);
                        } else {
                          setActiveTemplateId("");
                        }
                      }}
                      className="bg-white border border-slate-200 rounded px-2 py-1 text-xs text-slate-700 font-semibold focus:border-[#1A237E] focus:outline-none transition cursor-pointer"
                    >
                      <option value="">-- Áp dụng Cấu hình mẫu --</option>
                      {templates.map((t: any) => (
                        <option key={t.id} value={t.id}>{t.name}</option>
                      ))}
                    </select>
                  )}

                  <button
                    onClick={handleSaveTemplate}
                    disabled={isSavingTemplate}
                    className="px-3 py-1 bg-emerald-600 hover:bg-emerald-700 disabled:bg-slate-300 text-white rounded text-xs font-bold transition flex items-center justify-center gap-1 h-[26px]"
                    title="Lưu cấu hình thiết bị hiện tại thành mẫu"
                  >
                    Lưu Mẫu (Template)
                  </button>
                </div>
              </div>

              <div className="p-4 bg-slate-50/30 max-h-[680px] overflow-y-auto overflow-x-auto relative">
                <TowerBOMTable
                  activeTower={activeTower}
                  dynamicCategories={dynamicCategories}
                  flattenCategoryTree={flattenCategoryTree}
                  selectedProducts={selectedProducts}
                  setSelectedProducts={setSelectedProducts}
                  customBOMOverrides={customBOMOverrides}
                  setCustomBOMOverrides={setCustomBOMOverrides}
                  bomData={bomData}
                  leftTableNotes={leftTableNotes}
                  setLeftTableNotes={setLeftTableNotes}
                  stickyHeaderStyle={{ ...stickyHeaderStyle, top: 0 }}
                />
              </div>
            </div>

            {isXl && (
              <div 
                className="group w-6 hover:w-6 flex-shrink-0 self-stretch flex items-center justify-center cursor-col-resize select-none relative z-10"
                onMouseDown={startResize}
              >
                <div className={`w-[2px] h-full bg-slate-200 group-hover:bg-[#1A237E] transition-colors ${isDragging ? 'bg-[#1A237E] w-[3px]' : ''}`} />
                <div className={`absolute top-1/2 -translate-y-1/2 w-4 h-8 bg-white border border-slate-200 rounded-md shadow-sm flex flex-col gap-[3px] items-center justify-center group-hover:border-indigo-400 transition-colors ${isDragging ? 'border-indigo-500 ring-1 ring-indigo-100' : ''}`}>
                  <div className="w-[1.5px] h-3 bg-slate-400 group-hover:bg-[#1A237E]" />
                  <div className="w-[1.5px] h-3 bg-slate-400 group-hover:bg-[#1A237E]" />
                </div>
              </div>
            )}

            <div 
              className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs w-full overflow-hidden"
              style={isXl ? { width: `calc(${100 - leftWidth}% - 12px)`, flexShrink: 0 } : {}}
            >
              <FloorCalculatorTable
                activeTower={activeTower}
                calculationMode={calculationMode}
                setCalculationMode={setCalculationMode}
                tempFloors={tempFloors}
                tempBasements={tempBasements}
                tempHasRoof={tempHasRoof}
                tempH={tempH}
                tempV={tempV}
                tempRack={tempRack}
                tempQuantity2U={tempQuantity2U}
                selectedFloorIndexes={selectedFloorIndexes}
                setSelectedFloorIndexes={setSelectedFloorIndexes}
                activeCabinetIndex={activeCabinetIndex}
                setActiveCabinetIndex={setActiveCabinetIndex}
                manualGroups={manualGroups}
                setManualGroups={setManualGroups}
                setViewingFloorConnectionDetail={setViewingFloorConnectionDetail}
                setEditingCabinetIndex={setEditingCabinetIndex}
                setTempCabinets={setTempCabinets}
                cabinetPlacements={cabinetPlacements}
                handleSelectAllFloors={handleSelectAllFloors}
                handleToggleSelectFloor={handleToggleSelectFloor}
                handleToggleCabinet={handleToggleCabinet}
                handleUpdateFloorCell={handleUpdateFloorCell}
                handleDeleteFloor={handleDeleteFloor}
                handleExportCSV={handleExportCSV}
                updateTowerFloorsData={updateTowerFloorsData}
                fetchCabinetPlacement={fetchCabinetPlacement}
                syncFloorsWithManualGroups={syncFloorsWithManualGroups}
                addToast={addToast}
                isXl={isXl}
                leftWidth={leftWidth}
                stickyHeaderStyle={{ ...stickyHeaderStyle, top: 0 }}
              />
            </div>
          </div>

          <CableDetailsTable
            activeTower={activeTower}
          />
        </>
      )}
    </div>
  );
};
