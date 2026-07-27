import React from "react";
import { Check, Building, Warehouse, SlidersHorizontal, Activity } from "lucide-react";
import { Tower, BasePreset } from "../types";

interface StandardsTabProps {
  activeTower: Tower | null;
  BASE_PRESETS: BasePreset[];
  handleSelectPreset: (preset: BasePreset) => void;
}

export const StandardsTab: React.FC<StandardsTabProps> = ({
  activeTower,
  BASE_PRESETS,
  handleSelectPreset,
}) => {
  return (
    <div className="flex flex-col gap-6 text-left font-sans">
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
  );
};
