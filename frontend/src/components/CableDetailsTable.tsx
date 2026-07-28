import React from "react";
import { Tower, FloorData } from "../types";

interface CableDetailsTableProps {
  activeTower: Tower;
  stickyHeaderStyle?: React.CSSProperties;
}

export const CableDetailsTable: React.FC<CableDetailsTableProps> = ({
  activeTower,
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

  const allSortedFloors = [
    ...roofFloors,
    ...sortedUpperFloors,
    ...sortedBasementFloors
  ];

  return (
    <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs w-full text-left font-sans mt-6">
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
        <div className="text-xs font-mono text-[#2E7D32] bg-[#E8F5E9] px-2.5 py-1 rounded font-bold border border-[#2E7D32]/20 font-sans">
          CABLE DETAILS
        </div>
      </div>
      
      <div className="overflow-x-auto xl:overflow-visible p-4 bg-slate-50/30">
        <table className="w-full text-xs text-center border-collapse font-sans border border-slate-200 min-w-[900px] bg-white rounded shadow-xs overflow-hidden">
          <thead className="bg-[#1A237E]">
            <tr className="bg-[#1A237E] text-white font-bold border-b border-slate-300 divide-x divide-slate-200 select-none uppercase tracking-wider text-[10px]">
              <th className="py-3 px-2 w-24">Tủ</th>
              <th className="py-3 px-2 w-28 text-left pl-4">Tầng camera</th>
              <th className="py-3 px-2 w-28">Tầng đặt tủ</th>
              <th className="py-3 px-2 w-24">Số camera</th>
              <th className="py-3 px-2 w-32">Mét AutoCAD</th>
              <th className="py-3 px-2 w-28">Thông tầng</th>
              <th className="py-3 px-2 w-28">Xuống tủ</th>
              <th className="py-3 px-2 w-28">Trong tủ</th>
              <th className="py-3 px-2 w-32 font-semibold text-white bg-indigo-900 border-indigo-950 border-r">Tổng cáp/tầng</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-200">
            {allSortedFloors.length === 0 ? (
              <tr>
                <td colSpan={9} className="py-8 text-slate-400 italic text-center">
                  Không có dữ liệu cho tháp này. Vui lòng bấm Tính toán để bắt đầu.
                </td>
              </tr>
            ) : (
              <>
                {allSortedFloors.map((f) => {
                  const cabinetFloor = floors.find(fl => fl.floorIndex === f.cabinetIndex);
                  const cabinetLabel = cabinetFloor ? cabinetFloor.label : (f.cabinetIndex !== undefined ? `Tầng ${f.cabinetIndex}` : "");

                  return (
                    <tr 
                      key={f.floorIndex} 
                      className="divide-x divide-slate-100 hover:bg-slate-50 transition font-sans text-xs"
                    >
                      <td className="py-2.5 px-2 font-mono font-semibold text-[#1A237E]">
                        {f.cabinetIndex !== undefined 
                          ? `Tủ ${cabinetFloor ? cabinetFloor.label.replace("Tầng ", "").replace("Tầng", "").trim() : (f.cabinetIndex + 1)}`
                          : "-"}
                      </td>
                      <td className="py-2.5 px-2 text-left pl-4 font-semibold text-[#191c1e]">
                        {f.label}
                      </td>
                      <td className="py-2.5 px-2 text-slate-600">
                        {cabinetLabel}
                      </td>
                      <td className="py-2.5 px-2 font-mono text-center">
                        {f.camerasCount || 0}
                      </td>
                      <td className="py-2.5 px-2 font-mono text-slate-500">
                        {f.cableLengthInput || 0} m
                      </td>
                      <td className="py-2.5 px-2 font-mono text-slate-500">
                        {f.atrium || 0} m
                      </td>
                      <td className="py-2.5 px-2 font-mono text-slate-500">
                        {f.downCabinet || 0} m
                      </td>
                      <td className="py-2.5 px-2 font-mono text-slate-500">
                        {f.inCabinet || 0} m
                      </td>
                      <td className="py-2.5 px-2 font-mono font-bold text-center bg-indigo-50 text-[#1A237E] text-[13px] border-r">
                        {f.cableLength || 0} m
                      </td>
                    </tr>
                  );
                })}
                <tr className="bg-slate-50 border-t border-slate-300 font-bold divide-x divide-slate-200">
                  <td colSpan={3} className="py-3 px-4 text-left uppercase text-[#1A237E]">Tổng cộng:</td>
                  <td className="py-3 px-2 font-mono text-center text-[#191c1e] text-[13px]">
                    {allSortedFloors.reduce((sum, f) => sum + (f.camerasCount || 0), 0)} cam
                  </td>
                  <td colSpan={4} className="bg-slate-100"></td>
                  <td className="py-3 px-2 font-mono text-center text-indigo-900 bg-indigo-100/90 text-sm font-extrabold border-r">
                    {allSortedFloors.reduce((sum, f) => sum + (f.cableLength || 0), 0)} m
                  </td>
                </tr>
              </>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
