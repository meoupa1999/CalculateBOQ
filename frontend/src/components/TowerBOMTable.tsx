import React from "react";
import { Plus, Trash2, ChevronDown } from "lucide-react";
import { Tower, DynamicCategory } from "../types";

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

interface TowerBOMTableProps {
  activeTower: Tower;
  dynamicCategories: DynamicCategory[];
  flattenCategoryTree: (cats: DynamicCategory[]) => any[];
  selectedProducts: Record<string, Record<string, string>>;
  setSelectedProducts: React.Dispatch<React.SetStateAction<Record<string, Record<string, string>>>>;
  customBOMOverrides: Record<string, Record<string, number>>;
  setCustomBOMOverrides: React.Dispatch<React.SetStateAction<Record<string, Record<string, number>>>>;
  bomData: any;
  leftTableNotes: Record<string, string>;
  setLeftTableNotes: React.Dispatch<React.SetStateAction<Record<string, string>>>;
  stickyHeaderStyle?: React.CSSProperties;
}

export const TowerBOMTable: React.FC<TowerBOMTableProps> = ({
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
  stickyHeaderStyle,
}) => {
  return (
    <div className="border border-slate-200 rounded overflow-visible shadow-xs bg-white min-w-[850px] text-left font-sans">
      {/* Spreadsheet Title Block */}
      <div className="border-b border-slate-200 bg-[#F8F9FA] px-4 py-3 text-center">
        <div className="text-sm font-sans font-bold text-[#1A237E] uppercase tracking-wide">
          KHỐI LƯỢNG BOQ GÓI VẬT TƯ CỦA {activeTower?.name?.toUpperCase()}
        </div>
      </div>

      <table className="w-full text-xs text-left border-collapse font-sans">
        <thead className="bg-[#E8EAED] shadow-xs">
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

            const selectedProductId = selectedProducts[activeTower?.id || ""]?.[item.code || ""] || item.products?.[0]?.id;
            const selectedProduct = item.products?.find((p: any) => p.id === selectedProductId) || item.products?.[0];
            const displayDesc = selectedProduct?.description || "";

            const quantity = item.code
              ? (customBOMOverrides[activeTower?.id || ""]?.[item.code] ?? bomData?.[item.code] ?? 0)
              : 0;

            const isEditableField = [
              "FIBER_CABLE_4FO",
              "RACK_CABINET_32U",
              "RACK_CABINET_42U",
              "UPS_3000VA",
              "ELECTRIC_CABLE_CVV",
              "CONDUIT_FLEXIBLE_D20",
              "CONDUIT_RIGID_D20"
            ].includes(item.code || "") || (item.code && !SYSTEM_CODES.includes(item.code));
            
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

            const noteKey = item.code;

            return (
              <tr key={idx} className={rowClass}>
                <td className={sttClass}>{item.stt}</td>
                <td className={nameClass}>
                  {item.products && item.products.length > 0 ? (
                    <div className="relative w-full">
                      {/* Styled custom display box that allows text wrapping */}
                      <div className="w-full bg-[#f8f9fb] border border-slate-200 rounded px-2 py-1 text-xs text-slate-700 flex items-center justify-between gap-1 cursor-pointer hover:bg-slate-100/50 transition pr-6 relative min-h-[26px]">
                        <span className="text-wrap break-words text-slate-800 font-semibold leading-normal block py-0.5">
                          {selectedProduct?.name || "Chọn sản phẩm..."}
                        </span>
                        <div className="absolute right-2 top-1/2 -translate-y-1/2 pointer-events-none">
                          <ChevronDown className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                        </div>
                      </div>
                      {/* Actual invisible select dropdown overlays the box */}
                      <select
                        value={selectedProductId || ""}
                        onChange={(e) => {
                          const prodId = e.target.value;
                          setSelectedProducts(prev => {
                            const towerId = activeTower?.id || "";
                            const towerProds = prev[towerId] || {};
                            return {
                              ...prev,
                              [towerId]: {
                                ...towerProds,
                                [item.code || ""]: prodId
                              }
                            };
                          });
                        }}
                        className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                      >
                        {item.products.map((p: any) => (
                          <option key={p.id} value={p.id}>{p.name}</option>
                        ))}
                      </select>
                    </div>
                  ) : (
                    item.name
                  )}
                </td>
                <td className="py-2.5 px-2 text-slate-600">{displayDesc}</td>
                <td className={unitClass}>{item.unit || ""}</td>
                <td className="py-1 px-1 text-center font-mono">
                  {isEditableField && activeTower ? (
                    <input
                      type="number"
                      value={quantity === 0 ? "" : quantity}
                      placeholder="0"
                      onChange={(e) => {
                        const val = Math.max(0, parseFloat(e.target.value) || 0);
                        setCustomBOMOverrides(prev => {
                          const towerId = activeTower.id;
                          const towerOverrides = prev[towerId] || {};
                          return {
                            ...prev,
                            [towerId]: {
                              ...towerOverrides,
                              [item.code]: val
                            }
                          };
                        });
                      }}
                      className="w-20 bg-white border border-slate-200 focus:border-[#1A237E] rounded px-1 text-center font-mono text-xs focus:outline-none py-0.5 focus:ring-1 focus:ring-[#1A237E]"
                    />
                  ) : (
                    quantity
                  )}
                </td>
                <td className="py-2.5 px-1 text-center font-mono">{formatLabor(item.labor)}</td>
                <td className="py-2.5 px-1 text-center font-mono">{formatLabor((item.labor || 0) * quantity)}</td>
                <td className="py-2.5 px-2 text-slate-600">
                  <input
                    type="text"
                    value={leftTableNotes[noteKey] || item.note || ""}
                    onChange={(e) => setLeftTableNotes(prev => ({ ...prev, [noteKey]: e.target.value }))}
                    placeholder="Nhập ghi chú..."
                    className="w-full bg-[#f8f9fb] border border-transparent hover:border-slate-200 focus:border-[#1A237E] rounded px-2 py-0.5 transition focus:outline-none text-xs"
                  />
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};
