import React from "react";
import { Tower, ManualGroup } from "../types";

interface CabinetConfigModalProps {
  editingCabinetIndex: number | null;
  setEditingCabinetIndex: (index: number | null) => void;
  activeTower: Tower | null;
  tempCabinets: any[];
  setTempCabinets: (cabs: any[]) => void;
  selectedAllocIds: string[];
  setSelectedAllocIds: React.Dispatch<React.SetStateAction<string[]>>;
  setLastSelectedAllocId: (val: string | null) => void;
  handleToggleSelectAlloc: (cabIdx: number, allocIdx: number, e: React.MouseEvent) => void;
  manualGroups: ManualGroup[];
  setManualGroups: (groups: ManualGroup[]) => void;
  syncFloorsWithManualGroups: (floors: any[], groups: ManualGroup[]) => any[];
  updateTowerFloorsData: (floors: any[], groups: ManualGroup[]) => void;
  addToast: (msg: string, type: "success" | "error" | "info") => void;
}

export const CabinetConfigModal: React.FC<CabinetConfigModalProps> = ({
  editingCabinetIndex,
  setEditingCabinetIndex,
  activeTower,
  tempCabinets,
  setTempCabinets,
  selectedAllocIds,
  setSelectedAllocIds,
  setLastSelectedAllocId,
  handleToggleSelectAlloc,
  manualGroups,
  setManualGroups,
  syncFloorsWithManualGroups,
  updateTowerFloorsData,
  addToast,
}) => {
  if (editingCabinetIndex === null) return null;
  const floorDataRow = activeTower?.floorsData.find(fd => fd.floorIndex === editingCabinetIndex);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-xs font-sans">
      <div className="bg-white rounded-xl shadow-2xl border border-slate-200 w-full max-w-4xl max-h-[85vh] flex flex-col overflow-hidden animate-in fade-in zoom-in-95 duration-150 text-left">
        {/* Modal Header */}
        <div className="px-6 py-4 border-b border-slate-100 flex items-center justify-between bg-slate-50">
          <div>
            <h3 className="text-lg font-bold text-[#1A237E]">
              Cấu hình tủ điện tại {floorDataRow?.label || `Tầng ${editingCabinetIndex + 1}`}
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
                const totalDome = cab.allocations.reduce((sum: number, a: any) => sum + a.domeCount, 0);
                const totalBullet = cab.allocations.reduce((sum: number, a: any) => sum + a.bulletCount, 0);
                const totalCam = totalDome + totalBullet;

                return (
                  <div key={cab.id || cabIdx} className="bg-white border border-slate-200 rounded-xl shadow-xs overflow-hidden">
                    {/* Cabinet Header */}
                    <div className="px-5 py-3 bg-slate-50 border-b border-slate-100 flex justify-between items-center">
                      <div className="flex flex-wrap items-center gap-3">
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
                          {cab.allocations.map((alloc: any, allocIdx: number) => {
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
  );
};

interface FloorConnectionDetailModalProps {
  viewingFloorConnectionDetail: number | null;
  setViewingFloorConnectionDetail: (index: number | null) => void;
  activeTower: Tower | null;
  getFloorConnections: (floorIndex: number) => any[];
  manualGroups: ManualGroup[];
}

export const FloorConnectionDetailModal: React.FC<FloorConnectionDetailModalProps> = ({
  viewingFloorConnectionDetail,
  setViewingFloorConnectionDetail,
  activeTower,
  getFloorConnections,
  manualGroups,
}) => {
  if (viewingFloorConnectionDetail === null) return null;
  const floorIndex = viewingFloorConnectionDetail;
  const targetFloor = activeTower?.floorsData.find(fl => fl.floorIndex === floorIndex);
  if (!targetFloor) return null;

  const connections = getFloorConnections(floorIndex);
  const hostedGroup = manualGroups.find(g => g.cabinetIndex === floorIndex);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-xs font-sans">
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
};
