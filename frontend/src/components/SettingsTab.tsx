import React from "react";
import { Save, Plus, Trash2, ChevronDown, CheckCircle2, SlidersHorizontal, Building } from "lucide-react";
import { SystemConfig, ProductType, DynamicCategory } from "../types";

interface SettingsTabProps {
  systemConfig: SystemConfig | null;
  setSystemConfig: React.Dispatch<React.SetStateAction<SystemConfig | null>>;
  isLoadingConfig: boolean;
  isSavingConfig: boolean;
  handleSaveConfig: (e: React.FormEvent) => void;
  productTypes: ProductType[];
  selectedProductTypeId: string;
  setSelectedProductTypeId: (id: string) => void;
  newProductName: string;
  setNewProductName: (val: string) => void;
  newProductDesc: string;
  setNewProductDesc: (val: string) => void;
  isCreatingProduct: boolean;
  handleCreateProduct: (e: React.FormEvent) => void;
  tempCategories: DynamicCategory[];
  setTempCategories: React.Dispatch<React.SetStateAction<DynamicCategory[]>>;
  isSavingBOMConfig: boolean;
  handleSaveBOMConfig: (e: React.FormEvent) => void;
  activeSettingsTab: "config" | "add_product" | "bom_config";
  setActiveSettingsTab: (tab: "config" | "add_product" | "bom_config") => void;
  dynamicCategories: DynamicCategory[];
  setDeletedProductTypeIds: React.Dispatch<React.SetStateAction<string[]>>;
  fetchSystemConfig: () => void;
  flattenTempCategories: (cats: DynamicCategory[]) => any[];
  updateProductTypeField: (catId: string, ptId: string, field: string, val: any) => void;
  addProductTypeAfter: (catId: string, ptId: string) => void;
  deleteProductType: (catId: string, ptId: string) => void;
}

export const SettingsTab: React.FC<SettingsTabProps> = ({
  systemConfig,
  setSystemConfig,
  isLoadingConfig,
  isSavingConfig,
  handleSaveConfig,
  productTypes,
  selectedProductTypeId,
  setSelectedProductTypeId,
  newProductName,
  setNewProductName,
  newProductDesc,
  setNewProductDesc,
  isCreatingProduct,
  handleCreateProduct,
  tempCategories,
  setTempCategories,
  isSavingBOMConfig,
  handleSaveBOMConfig,
  activeSettingsTab,
  setActiveSettingsTab,
  dynamicCategories,
  setDeletedProductTypeIds,
  fetchSystemConfig,
  flattenTempCategories,
  updateProductTypeField,
  addProductTypeAfter,
  deleteProductType,
}) => {
  return (
    <div className="flex flex-col gap-6 w-full max-w-4xl mx-auto font-sans text-left">
      {/* Internal Settings Navigation Tabs */}
      <div className="flex items-center justify-between border-b border-slate-200 pb-3">
        <div className="flex gap-6">
          <button
            onClick={() => setActiveSettingsTab("config")}
            className={`pb-2.5 text-sm font-semibold border-b-2 transition-all duration-200 ${
              activeSettingsTab === "config"
                ? "border-[#1A237E] text-[#1A237E]"
                : "border-transparent text-[#455A64] hover:text-[#191c1e]"
            }`}
          >
            Cấu hình tham số hệ thống
          </button>
          <button
            onClick={() => setActiveSettingsTab("add_product")}
            className={`pb-2.5 text-sm font-semibold border-b-2 transition-all duration-200 ${
              activeSettingsTab === "add_product"
                ? "border-[#1A237E] text-[#1A237E]"
                : "border-transparent text-[#455A64] hover:text-[#191c1e]"
            }`}
          >
            Thêm sản phẩm
          </button>
          <button
            onClick={() => setActiveSettingsTab("bom_config")}
            className={`pb-2.5 text-sm font-semibold border-b-2 transition-all duration-200 ${
              activeSettingsTab === "bom_config"
                ? "border-[#1A237E] text-[#1A237E]"
                : "border-transparent text-[#455A64] hover:text-[#191c1e]"
            }`}
          >
            Cấu hình bảng BOM
          </button>
        </div>
      </div>

      {activeSettingsTab === "config" && (
        <div className="flex flex-col gap-6">
          <div>
            <h1 className="font-sans font-bold text-2xl text-[#191c1e] tracking-tight">
              Cấu hình tham số hệ thống
            </h1>
            <p className="text-sm text-[#455A64] mt-1">
              Thay đổi các tham số kỹ thuật mặc định và ngưỡng tối ưu hóa phân phối thiết bị (Switch, UPS, PDU)
            </p>
          </div>

          {isLoadingConfig || !systemConfig ? (
            <div className="bg-white border border-[#ECEFF1] rounded-xl p-12 text-center shadow-xs flex flex-col items-center justify-center gap-4 border-slate-200">
              <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[#1A237E]"></div>
              <span className="text-sm font-medium text-slate-500">Đang tải cấu hình hệ thống...</span>
            </div>
          ) : (
            <form onSubmit={handleSaveConfig} className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
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
                      className="w-full bg-[#f8f9fb] border border-slate-200 rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
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
                      className="w-full bg-[#f8f9fb] border border-slate-200 rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
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
                      className="w-full bg-[#f8f9fb] border border-slate-200 rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:outline-none transition font-mono"
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
                  onClick={fetchSystemConfig}
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

      {activeSettingsTab === "add_product" && (
        <div className="flex flex-col gap-6">
          <div>
            <h1 className="font-sans font-bold text-2xl text-[#191c1e] tracking-tight">
              Thêm sản phẩm động
            </h1>
            <p className="text-sm text-[#455A64] mt-1">
              Đăng ký thêm sản phẩm mới vào danh mục hệ thống để lựa chọn và hiển thị trong bảng báo giá BOM
            </p>
          </div>

          <form onSubmit={handleCreateProduct} className="bg-white border border-slate-200 rounded-xl shadow-sm overflow-hidden">
            <div className="p-6 border-b border-slate-100 bg-slate-50/50">
              <h3 className="font-sans font-bold text-base text-[#191c1e]">
                Thông tin sản phẩm mới
              </h3>
              <p className="text-xs text-[#455A64] mt-0.5">
                Chọn loại thiết bị và điền đầy đủ tên và mô tả chi tiết sản phẩm
              </p>
            </div>

            <div className="p-6 flex flex-col gap-5">
              <div>
                <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                  Loại thiết bị (Product Type) <span className="text-red-500">*</span>
                </label>
                {productTypes.length === 0 ? (
                  <div className="text-xs text-amber-600 bg-amber-50 p-3.5 rounded-lg border border-amber-100 font-medium">
                    Không tìm thấy loại sản phẩm nào. Vui lòng kiểm tra kết nối tới cơ sở dữ liệu.
                  </div>
                ) : (
                  <div className="relative">
                    <select
                      value={selectedProductTypeId}
                      onChange={(e) => setSelectedProductTypeId(e.target.value)}
                      className="w-full bg-[#f8f9fb] border border-slate-200 rounded-lg px-3.5 py-2.5 text-sm font-medium focus:border-[#1A237E] focus:ring-1 focus:ring-[#1A237E] focus:outline-none transition appearance-none cursor-pointer"
                    >
                      {productTypes.map((pt) => (
                        <option key={pt.id} value={pt.id}>
                          {pt.name} ({pt.code})
                        </option>
                      ))}
                    </select>
                    <div className="pointer-events-none absolute inset-y-0 right-0 flex items-center px-3.5 text-slate-400">
                      <ChevronDown className="w-4 h-4" />
                    </div>
                  </div>
                )}
              </div>

              <div>
                <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                  Tên sản phẩm thiết bị <span className="text-red-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={newProductName}
                  onChange={(e) => setNewProductName(e.target.value)}
                  placeholder="Ví dụ: Camera IP Dome 2MP Hikvision DS-2CD2121G0-I"
                  className="w-full bg-[#f8f9fb] border border-slate-200 rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:ring-1 focus:ring-[#1A237E] focus:outline-none transition"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-[#455A64] uppercase tracking-wide mb-1.5">
                  Mô tả chi tiết sản phẩm / Thông số kỹ thuật
                </label>
                <textarea
                  rows={4}
                  value={newProductDesc}
                  onChange={(e) => setNewProductDesc(e.target.value)}
                  placeholder="Nhập thông số kỹ thuật chi tiết của thiết bị (ví dụ: Full HD, H.265+, hồng ngoại 30m...)"
                  className="w-full bg-[#f8f9fb] border border-slate-200 rounded-lg px-3.5 py-2 text-sm font-medium focus:border-[#1A237E] focus:ring-1 focus:ring-[#1A237E] focus:outline-none transition"
                />
              </div>
            </div>

            <div className="px-6 py-4 bg-slate-50 border-t border-slate-100 flex justify-end gap-3">
              <button
                type="button"
                onClick={() => {
                  setNewProductName("");
                  setNewProductDesc("");
                }}
                className="px-4 py-2 border border-slate-200 text-sm font-semibold rounded-lg hover:bg-slate-100 transition text-[#455A64]"
              >
                Xóa dữ liệu nhập
              </button>
              <button
                type="submit"
                disabled={isCreatingProduct || productTypes.length === 0}
                className="px-5 py-2 bg-[#1A237E] hover:bg-[#1A237E]/95 disabled:bg-slate-300 disabled:cursor-not-allowed text-white text-sm font-semibold rounded-lg shadow-sm transition flex items-center gap-2"
              >
                {isCreatingProduct ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    <span>Đang tạo...</span>
                  </>
                ) : (
                  <>
                    <CheckCircle2 className="w-4 h-4" />
                    <span>Tạo sản phẩm</span>
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      )}

      {activeSettingsTab === "bom_config" && (
        <div className="flex flex-col gap-6">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="font-sans font-bold text-2xl text-[#191c1e] tracking-tight">
                Cấu hình bảng BOM
              </h1>
              <p className="text-sm text-[#455A64] mt-1">
                Chỉnh sửa trực tiếp tên vật tư, đơn vị tính, định mức nhân công, công thức số lượng và ghi chú cho bảng BOM
              </p>
            </div>
          </div>

          <form onSubmit={handleSaveBOMConfig} className="flex flex-col gap-6">
            <div className="bg-white border border-[#ECEFF1] rounded-xl shadow-sm overflow-hidden border-slate-200/80">
              <div className="overflow-x-auto max-h-[60vh]">
                <table className="w-full text-xs text-left border-collapse font-sans">
                  <thead>
                    <tr className="bg-[#E8EAED] text-[#3c4043] font-bold text-center border-b border-slate-300 divide-x divide-slate-200 select-none">
                      <th className="py-2.5 px-1 text-center w-14">STT</th>
                      <th className="py-2.5 px-2 text-left w-64">VẬT TƯ</th>
                      <th className="py-2.5 px-1 w-20">Đ.VỊ</th>
                      <th className="py-2.5 px-1 w-24">NHÂN CÔNG</th>
                      <th className="py-2.5 px-2 text-left w-36">CÔNG THỨC</th>
                      <th className="py-2.5 px-2 text-left w-40">GHI CHÚ</th>
                      <th className="py-2.5 px-1 w-24 text-center">THAO TÁC</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-200">
                    {flattenTempCategories(tempCategories).map((item, idx) => {
                      if (item.type === 'category' || item.type === 'subcategory') {
                        return (
                          <tr key={`cat-${item.id}`} className="bg-[#FFE0B2]/60 text-[#E65100] font-bold text-[11px] divide-x divide-slate-200">
                            <td colSpan={6} className="py-2.5 px-4 uppercase tracking-wide">
                              {item.name}
                            </td>
                            <td className="bg-[#FFE0B2]/60 border-l"></td>
                          </tr>
                        );
                      }

                      return (
                        <tr key={`pt-${item.id}`} className="divide-x divide-slate-200 hover:bg-slate-50/50 transition">
                          <td className="py-1 px-1 text-center font-mono text-slate-500">
                            {idx + 1}
                          </td>
                          <td className="py-1 px-2">
                            <input
                              type="text"
                              value={item.name}
                              onChange={(e) => {
                                const newName = e.target.value;
                                updateProductTypeField(item.categoryId, item.id, 'name', newName);
                                if (item.id.startsWith("temp-")) {
                                  const slug = newName
                                    .normalize("NFD")
                                    .replace(/[\u0300-\u036f]/g, "")
                                    .replace(/đ/g, "d").replace(/Đ/g, "D")
                                    .replace(/[^a-zA-Z0-9\s]/g, "")
                                    .trim()
                                    .toUpperCase()
                                    .replace(/\s+/g, "_");
                                  updateProductTypeField(item.categoryId, item.id, 'code', `${slug}_${item.id.split('-')[1] || Date.now()}`);
                                }
                              }}
                              className="w-full bg-[#f8f9fb] border border-slate-200 rounded px-2 py-1 focus:border-[#1A237E] focus:outline-none transition font-medium"
                            />
                          </td>
                          <td className="py-1 px-1">
                            <input
                              type="text"
                              value={item.unit || ''}
                              onChange={(e) => updateProductTypeField(item.categoryId, item.id, 'unit', e.target.value)}
                              className="w-full bg-[#f8f9fb] border border-slate-200 rounded px-2 py-1 focus:border-[#1A237E] focus:outline-none transition text-center font-medium"
                            />
                          </td>
                          <td className="py-1 px-1">
                            <input
                              type="number"
                              step="any"
                              value={item.labor ?? ''}
                              onChange={(e) => updateProductTypeField(item.categoryId, item.id, 'labor', e.target.value === '' ? null : parseFloat(e.target.value))}
                              placeholder="0.0"
                              className="w-full bg-[#f8f9fb] border border-slate-200 rounded px-2 py-1 focus:border-[#1A237E] focus:outline-none transition font-mono text-center"
                            />
                          </td>
                          <td className="py-1 px-2">
                            <input
                              type="text"
                              value={item.formula || ''}
                              onChange={(e) => updateProductTypeField(item.categoryId, item.id, 'formula', e.target.value)}
                              className="w-full bg-[#f8f9fb] border border-slate-200 rounded px-2 py-1 focus:border-[#1A237E] focus:outline-none transition font-mono"
                            />
                          </td>
                          <td className="py-1 px-2">
                            <input
                              type="text"
                              value={item.note || ''}
                              onChange={(e) => updateProductTypeField(item.categoryId, item.id, 'note', e.target.value)}
                              className="w-full bg-[#f8f9fb] border border-slate-200 rounded px-2 py-1 focus:border-[#1A237E] focus:outline-none transition"
                            />
                          </td>
                          <td className="py-1 px-1">
                            <div className="flex items-center justify-center gap-1.5">
                              <button
                                type="button"
                                onClick={() => addProductTypeAfter(item.categoryId, item.id)}
                                className="p-1 text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50 rounded transition"
                                title="Chèn thêm dòng phía dưới"
                              >
                                <Plus className="w-3.5 h-3.5" />
                              </button>
                              <button
                                type="button"
                                onClick={() => deleteProductType(item.categoryId, item.id)}
                                className="p-1 text-slate-400 hover:text-red-600 rounded hover:bg-red-50 transition"
                                title="Xóa dòng này"
                              >
                                <Trash2 className="w-3.5 h-3.5" />
                              </button>
                            </div>
                          </td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            </div>

            <div className="bg-white border border-[#ECEFF1] rounded-xl p-4 shadow-sm flex justify-end gap-3 border-slate-200/80">
              <button
                type="button"
                onClick={() => {
                  setTempCategories(JSON.parse(JSON.stringify(dynamicCategories)));
                  setDeletedProductTypeIds([]);
                }}
                className="px-4 py-2 border border-slate-200 text-sm font-semibold rounded-lg hover:bg-slate-100 transition text-[#455A64]"
              >
                Hủy &amp; Tải lại
              </button>
              <button
                type="submit"
                disabled={isSavingBOMConfig}
                className="px-5 py-2 bg-[#1A237E] hover:bg-[#1A237E]/95 disabled:bg-slate-300 disabled:cursor-not-allowed text-white text-sm font-semibold rounded-lg shadow-sm transition flex items-center gap-2"
              >
                {isSavingBOMConfig ? (
                  <>
                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                    <span>Đang lưu...</span>
                  </>
                ) : (
                  <>
                    <Save className="w-4 h-4" />
                    <span>Lưu cấu hình BOM</span>
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
