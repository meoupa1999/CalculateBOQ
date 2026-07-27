import React from "react";
import { Activity, RefreshCw, FileSpreadsheet } from "lucide-react";
import { Project, DynamicCategory } from "../types";

const formatLabor = (val: number) => {
  if (val === undefined || val === null) return "0.0";
  if (val === 0) return "0.0";
  const str = val.toFixed(4);
  let trimmed = str.replace(/0+$/, '');
  if (trimmed.endsWith('.')) {
    trimmed += '0';
  }
  return trimmed;
};

const SYSTEM_CODES = [
  "CAMERA_DOME",
  "CAMERA_BULLET",
  "RECORDER_32",
  "RECORDER_16",
  "HARD_DISK_10T",
  "SWITCH_24_POE",
  "SWITCH_16_POE",
  "SWITCH_16_CISCO",
  "SWITCH_24_CISCO",
  "OBSERVER_SCREEN_43",
  "FIBER_CABLE_4FO",
  "LAN_CABLE_CAT5E",
  "CONVERTER_GIGABIT",
  "RACK_CABINET_2U",
  "RACK_CABINET_6U",
  "RACK_CABINET_10U",
  "RACK_CABINET_20U",
  "RACK_CABINET_32U",
  "RACK_CABINET_42U",
  "ODF_12FO",
  "ODF_24FO",
  "ELECTRIC_CABLE_CVV",
  "PDU_POWER_6",
  "UPS_1000VA",
  "UPS_3000VA",
  "ACCESSORIES_PACKAGE",
  "AMP_CAT5_CONNECTOR",
  "FIBER_PATCH_CORD_3M",
  "ODF_4FO",
  "LAN_PATCH_CORD",
  "CABLE_MANAGEMENT_19",
  "CONDUIT_FLEXIBLE_D20",
  "CONDUIT_RIGID_D20"
];

interface SummaryBOMTabProps {
  activeProject: Project;
  selectedTowersSummary: Record<string, boolean>;
  setSelectedTowersSummary: React.Dispatch<React.SetStateAction<Record<string, boolean>>>;
  isCalculatingSummary: boolean;
  handleCalculateSummary: () => void;
  isExportingExcel: boolean;
  handleExportExcel: () => void;
  summaryBomData: any;
  dynamicCategories: DynamicCategory[];
  flattenCategoryTree: (cats: DynamicCategory[]) => any[];
  leftTableNotes: Record<string, string>;
  customBOMOverrides: Record<string, Record<string, number>>;
}

export const SummaryBOMTab: React.FC<SummaryBOMTabProps> = ({
  activeProject,
  selectedTowersSummary,
  setSelectedTowersSummary,
  isCalculatingSummary,
  handleCalculateSummary,
  isExportingExcel,
  handleExportExcel,
  summaryBomData,
  dynamicCategories,
  flattenCategoryTree,
  leftTableNotes,
  customBOMOverrides,
}) => {
  return (
    <div className="space-y-6 mt-6 font-sans">
      <div className="bg-white border border-[#ECEFF1] rounded-lg p-6 shadow-xs text-left">
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
          {/* Table Card */}
          <div className="bg-white border border-[#ECEFF1] rounded-lg shadow-xs overflow-hidden w-full text-left">
            <div className="px-6 py-4 border-b border-[#ECEFF1] bg-slate-50/50">
              <h3 className="font-sans font-bold text-base text-[#191c1e]">
                Khung BOM Tổng Hợp Thiết Bị &amp; Vật Tư
              </h3>
              <p className="text-xs text-[#455A64]">
                Khung sườn danh mục vật tư chính, phụ kiện tổng cộng từ các tháp đã chọn
              </p>
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
                    {flattenCategoryTree(dynamicCategories).map((item, idx) => {
                      if (item.type === "category" || item.type === "subcategory") {
                        return (
                          <tr key={idx} className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                            <td colSpan={8} className="py-2.5 px-4 uppercase tracking-wide">
                              {item.name}
                            </td>
                          </tr>
                        );
                      }

                      let quantity = 0;
                      if (item.code) {
                        if (!SYSTEM_CODES.includes(item.code)) {
                          activeProject.towers.forEach(t => {
                            if (selectedTowersSummary[t.id]) {
                              quantity += (customBOMOverrides[t.id]?.[item.code] ?? 0);
                            }
                          });
                        } else {
                          quantity = summaryBomData?.[item.code] ?? 0;
                        }
                      }
                      
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

                      const noteKey = `summary_${item.code}`;

                      return (
                        <tr key={idx} className={rowClass}>
                          <td className={sttClass}>{item.stt}</td>
                          <td className={nameClass}>{item.name}</td>
                          <td className="py-2.5 px-2 text-slate-600">{item.desc || ""}</td>
                          <td className={unitClass}>{item.unit || ""}</td>
                          <td className="py-2.5 px-1 text-center font-mono">{quantity}</td>
                          <td className="py-2.5 px-1 text-center font-mono">{formatLabor(item.labor)}</td>
                          <td className="py-2.5 px-1 text-center font-mono">{formatLabor((item.labor || 0) * quantity)}</td>
                          <td className="py-2.5 px-2 text-slate-600">{leftTableNotes[noteKey] || item.note || ""}</td>
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
  );
};
