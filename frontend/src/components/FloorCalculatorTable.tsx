import React from "react";
import { Download, Trash2, RefreshCw } from "lucide-react";
import { Tower, FloorData, ManualGroup } from "../types";

interface FloorCalculatorTableProps {
  activeTower: Tower;
  calculationMode: "auto" | "manual";
  setCalculationMode: (mode: "auto" | "manual") => void;
  tempFloors: number;
  tempBasements: number;
  tempHasRoof: boolean;
  tempH: number;
  tempV: number;
  tempRack: "2U" | "6U" | "10U";
  tempQuantity2U: number;
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
  stickyHeaderStyle?: React.CSSProperties;
}

interface CellInputProps {
  value: string | number;
  type: "text" | "number";
  onUpdate: (val: any) => void;
  className?: string;
  placeholder?: string;
  min?: string;
}

const CellInput: React.FC<CellInputProps> = ({
  value,
  type,
  onUpdate,
  className,
  placeholder,
  min,
}) => {
  const [localValue, setLocalValue] = React.useState<string>(
    value === undefined || value === null || (type === "number" && value === 0) ? "" : String(value)
  );

  React.useEffect(() => {
    setLocalValue(value === undefined || value === null || (type === "number" && value === 0) ? "" : String(value));
  }, [value]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setLocalValue(e.target.value);
  };

  const handleBlur = () => {
    if (type === "number") {
      const parsed = Math.max(0, parseInt(localValue) || 0);
      onUpdate(parsed);
      setLocalValue(parsed === 0 ? "" : String(parsed));
    } else {
      onUpdate(localValue);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      if (type === "number") {
        const parsed = Math.max(0, parseInt(localValue) || 0);
        onUpdate(parsed);
        setLocalValue(parsed === 0 ? "" : String(parsed));
      } else {
        onUpdate(localValue);
      }
      e.currentTarget.blur();
    }
  };

  return (
    <input
      type={type}
      min={min}
      placeholder={placeholder}
      value={localValue}
      onFocus={(e) => type === "number" && e.target.select()}
      onChange={handleChange}
      onBlur={handleBlur}
      onKeyDown={handleKeyDown}
      onClick={(e) => e.stopPropagation()}
      className={className}
    />
  );
};

export const FloorCalculatorTable: React.FC<FloorCalculatorTableProps> = ({
  activeTower,
  calculationMode,
  setCalculationMode,
  tempFloors,
  tempBasements,
  tempHasRoof,
  tempH,
  tempV,
  tempRack,
  tempQuantity2U,
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
  stickyHeaderStyle,
}) => {
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
    { bg: 'bg-indigo-100/40 hover:bg-indigo-200/80', border: 'border-l-4 border-indigo-600', labelBg: 'bg-indigo-200 text-indigo-900 border border-indigo-300 font-bold' },
    { bg: 'bg-amber-100/40 hover:bg-amber-200/80', border: 'border-l-4 border-amber-600', labelBg: 'bg-amber-200 text-amber-900 border border-amber-300 font-bold' },
    { bg: 'bg-teal-100/40 hover:bg-teal-200/80', border: 'border-l-4 border-teal-600', labelBg: 'bg-teal-200 text-teal-900 border border-teal-300 font-bold' },
    { bg: 'bg-rose-100/40 hover:bg-rose-200/80', border: 'border-l-4 border-rose-600', labelBg: 'bg-rose-200 text-rose-900 border border-rose-300 font-bold' },
    { bg: 'bg-emerald-100/40 hover:bg-emerald-200/80', border: 'border-l-4 border-emerald-600', labelBg: 'bg-emerald-200 text-emerald-900 border border-emerald-300 font-bold' },
    { bg: 'bg-sky-100/40 hover:bg-sky-200/80', border: 'border-l-4 border-sky-600', labelBg: 'bg-sky-200 text-sky-900 border border-sky-300 font-bold' },
    { bg: 'bg-purple-100/40 hover:bg-purple-200/80', border: 'border-l-4 border-purple-600', labelBg: 'bg-purple-200 text-purple-900 border border-purple-300 font-bold' }
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
    }

    let cameraExceeded = false;
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
        className={`transition select-none cursor-pointer divide-y divide-[#ECEFF1] ${
          selectedFloorIndexes.includes(f.floorIndex) 
            ? 'bg-slate-300 font-semibold text-slate-900' 
            : styleGroup.bg
        } ${styleGroup.border} ${isActiveCabinet ? 'ring-2 ring-emerald-500 ring-inset' : ''}`}
        onClick={(e) => {
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
              <CellInput
                type="text"
                value={f.label}
                onUpdate={(val) => handleUpdateFloorCell(f.floorIndex, "label", val)}
                className="bg-transparent border-0 hover:bg-slate-200/80 focus:bg-white focus:ring-1 focus:ring-[#1A237E]/30 focus:border-[#1A237E] rounded px-1.5 py-0.5 font-semibold text-[#191c1e] text-sm focus:outline-none transition w-36 text-left"
              />
              {calculationMode === "manual" ? (
                manualGroups.some(g => g.cabinetIndex === f.floorIndex) ? (
                  (() => {
                    const group = manualGroups.find(g => g.cabinetIndex === f.floorIndex);
                    const cabinetsStr = group?.cabinets.map((c: any) => c.type).join(", ") || "2U";
                    return (
                      <span className={`inline-flex items-center gap-1 px-1.5 py-0.5 text-[9px] font-bold rounded border transition-all whitespace-nowrap flex-shrink-0 ${
                        isActiveCabinet
                          ? "bg-emerald-600 text-white border-emerald-700 shadow-sm font-extrabold animate-pulse"
                          : "bg-emerald-100 text-emerald-800 border-emerald-200"
                      }`} title={isActiveCabinet ? "Tủ đang chọn để phân nhóm" : "Tủ đặt thủ công"}>
                        <svg className="w-3.5 h-3.5 font-bold text-current" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
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
                      const cabinetFloor = floors.find(fl => fl.floorIndex === associatedGroup.cabinetIndex);
                      const cabinetLabel = cabinetFloor ? cabinetFloor.label : `T.${associatedGroup.cabinetIndex + 1}`;
                      return (
                        <span className="inline-flex items-center gap-1 px-1.5 py-0.5 text-[9px] font-bold rounded bg-blue-100 text-blue-800 border border-blue-200 whitespace-nowrap flex-shrink-0">
                          Liên kết Tủ {cabinetLabel}
                        </span>
                      );
                    }
                    return (
                      <span className="inline-flex items-center gap-1 px-1.5 py-0.5 text-[9px] font-medium rounded bg-slate-100 text-slate-400 border border-slate-200 italic whitespace-nowrap flex-shrink-0">
                        Tự động tối ưu
                      </span>
                    );
                  })()
                )
              ) : (
                isCabinetPlaced && (
                  <span className="inline-flex items-center gap-1 px-1.5 py-0.5 text-[10px] font-bold rounded bg-[#1A237E]/10 text-[#1A237E] border border-[#1A237E]/20 whitespace-nowrap flex-shrink-0" title="Tầng đặt tủ rack">
                    <svg className="w-3.5 h-3.5 text-[#1A237E]" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M5 12h14M5 12a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v4a2 2 0 01-2 2M5 12a2 2 0 00-2 2v4a2 2 0 002 2h14a2 2 0 002-2v-4a2 2 0 00-2-2m-2-4h.01M17 16h.01" />
                    </svg>
                    RACK
                  </span>
                )
              )}
            </div>

            {isCabinetPlaced && (
              <div className="flex flex-wrap gap-1 mt-1 text-left">
                {(() => {
                  const floorCabs = f.cabinets || [];
                  const isManualGroup = calculationMode === "manual" && manualGroups.some(g => g.cabinetIndex === f.floorIndex);
                  if (floorCabs.length > 0) {
                    return floorCabs.map((c: any, cIdx: number) => {
                      const qty = isManualGroup ? (c.quantity2U || 1) : tempQuantity2U;
                      return (
                        <span key={cIdx} className="inline-flex items-center gap-2 px-2 py-0.5 text-xs font-bold rounded-md bg-[#E8EAF6] text-[#1A237E] border border-[#C5CAE9] shadow-sm whitespace-nowrap flex-shrink-0">
                          <span className="w-2 h-2 rounded-full bg-[#1A237E] animate-pulse"></span>
                          {c.cabinetType === "2U" && qty > 1 ? `${qty} ` : ""}Tủ {c.cabinetType || ""} ({c.cameraQuantityInCabinet ?? 0} Cam)
                        </span>
                      );
                    });
                  }
                  const fallbackQty = isManualGroup ? (activeTower?.quantity2U || 1) : tempQuantity2U;
                  return (
                    <span className="inline-flex items-center gap-2 px-2 py-0.5 text-xs font-bold rounded-md bg-[#E8EAF6] text-[#1A237E] border border-[#C5CAE9] shadow-sm whitespace-nowrap flex-shrink-0">
                      <span className="w-2 h-2 rounded-full bg-[#1A237E] animate-pulse"></span>
                      {f.cabinetType === "2U" && fallbackQty > 1 ? `${fallbackQty} ` : ""}Tủ {f.cabinetType || ""} ({f.cameraQuantityInCabinet ?? 0} Cam)
                    </span>
                  );
                })()}
              </div>
            )}

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
        <td className="py-2 px-3">
          <CellInput
            type="number"
            min="0"
            placeholder="0"
            value={f.cableLengthInput === undefined ? "" : f.cableLengthInput}
            onUpdate={(val) => handleUpdateFloorCell(f.floorIndex, "cableLengthInput", val)}
            className="w-20 bg-[#f8f9fb] border border-[#ECEFF1] hover:border-slate-300 focus:border-[#1A237E] rounded px-2 py-1 text-center font-mono font-semibold focus:outline-none transition"
          />
        </td>
        <td className="py-2 px-3">
          <CellInput
            type="number"
            min="0"
            placeholder="0"
            value={f.domeCount === 0 ? 0 : f.domeCount}
            onUpdate={(val) => handleUpdateFloorCell(f.floorIndex, "domeCount", val)}
            className="w-20 bg-[#f8f9fb] border border-[#ECEFF1] hover:border-slate-300 focus:border-[#1A237E] rounded px-2 py-1 text-center font-mono focus:outline-none transition"
          />
        </td>
        <td className="py-2 px-3">
          <CellInput
            type="number"
            min="0"
            placeholder="0"
            value={f.bulletCount === 0 ? 0 : f.bulletCount}
            onUpdate={(val) => handleUpdateFloorCell(f.floorIndex, "bulletCount", val)}
            className="w-20 bg-[#f8f9fb] border border-[#ECEFF1] hover:border-slate-300 focus:border-[#1A237E] rounded px-2 py-1 text-center font-mono focus:outline-none transition"
          />
        </td>
        <td className="py-2 px-3 text-center font-mono font-bold text-[#1A237E]">
          {f.cableLength !== undefined ? `${f.cableLength} m` : "-"}
        </td>
        <td className={`py-2 px-3 font-mono text-center ${isCabinetPlaced ? "text-[#191c1e]" : "text-slate-300"}`}>
          {f.sw24Count || "-"}
        </td>
        <td className={`py-2 px-3 font-mono text-center ${isCabinetPlaced ? "text-[#191c1e]" : "text-slate-300"}`}>
          {f.sw16Count || "-"}
        </td>
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
        <td className={`py-2 px-3 font-mono text-center ${isCabinetPlaced ? "text-[#191c1e]" : "text-slate-300"}`}>
          {f.pduCount || "-"}
        </td>
        <td className={`py-2 px-3 font-mono text-center ${isCabinetPlaced ? "text-[#191c1e]" : "text-slate-300"}`}>
          {f.convCount || "-"}
        </td>
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

  const allSortedFloors = [
    ...roofFloors,
    ...sortedUpperFloors,
    ...sortedBasementFloors
  ];

  return (
    <div className="w-full space-y-6">
      <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs w-full text-left font-sans">
        <div className="px-6 py-4 border-b border-[#ECEFF1] flex flex-col sm:flex-row sm:items-center justify-between gap-4 bg-slate-50/50">
          <div>
            <h3 className="font-sans font-bold text-base text-[#191c1e]">
              Bảng tính BOQ chi tiết
            </h3>
            <p className="text-xs text-[#455A64]">
              Nhấp trực tiếp vào ô để thay đổi số lượng camera của từng tầng hoặc chọn nhiều tầng để đồng bộ nhanh
            </p>
          </div>

          <div className="flex items-center gap-4 self-end sm:self-auto">
            <div className="flex items-center bg-slate-100 p-0.5 rounded-lg border border-slate-200/60 shadow-xs">
              <button
                onClick={() => {
                  setCalculationMode("auto");
                  updateTowerFloorsData(floors, manualGroups, "auto");
                  fetchCabinetPlacement(
                    tempFloors,
                    tempBasements,
                    tempHasRoof,
                    tempH,
                    tempV,
                    tempRack,
                    floors,
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
                  updateTowerFloorsData(floors, manualGroups, "manual");
                  fetchCabinetPlacement(
                    tempFloors,
                    tempBasements,
                    tempHasRoof,
                    tempH,
                    tempV,
                    tempRack,
                    floors,
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
              className="p-1.5 text-[#455A64] hover:text-[#1A237E] hover:bg-slate-100 rounded transition border border-slate-200 bg-white"
            >
              <Download className="w-5 h-5" />
            </button>
          </div>
        </div>

        <div className="max-h-[680px] overflow-y-auto overflow-x-auto relative">
          <table className="w-full text-left border-collapse min-w-[900px]">
            <thead className="bg-slate-50 shadow-xs">
              <tr className="bg-slate-50 border-b border-[#ECEFF1] text-[11px] font-bold text-[#455A64] uppercase tracking-wider">
                <th style={stickyHeaderStyle} className="py-3 px-4 w-12 text-center">
                  <input
                    type="checkbox"
                    checked={selectedFloorIndexes.length === floors.length && floors.length > 0}
                    onChange={handleSelectAllFloors}
                    className="rounded text-[#1A237E] focus:ring-[#1A237E] w-4 h-4 cursor-pointer"
                  />
                </th>
                {calculationMode === "manual" && (
                  <th style={stickyHeaderStyle} className="py-3 px-3 w-24 text-center">ĐẶT TỦ (MC)</th>
                )}
                <th style={stickyHeaderStyle} className="py-3 px-4 w-28">TẦNG</th>
                <th style={stickyHeaderStyle} className="py-3 px-3 w-32 font-semibold">KHOẢNG CÁCH DÂY (M)</th>
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
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
