import React from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Plus, Building, ArrowRight, Copy, Trash } from "lucide-react";
import { Project, BasePreset } from "../types";

interface ProjectsTabProps {
  projects: Project[];
  activeProjectId: string;
  setActiveProjectId: (id: string) => void;
  setActiveTab: (tab: "app" | "projects" | "inventory" | "standards" | "settings") => void;
  isCreatingProject: boolean;
  setIsCreatingProject: (val: boolean) => void;
  newProjectName: string;
  setNewProjectName: (val: string) => void;
  newProjectFloors: number;
  setNewProjectFloors: (val: number) => void;
  newProjectDesc: string;
  setNewProjectDesc: (val: string) => void;
  newProjectPreset: string;
  setNewProjectPreset: (val: string) => void;
  BASE_PRESETS: BasePreset[];
  handleCreateProject: () => void;
  handleDeleteProject: (id: string) => void;
  addToast: (msg: string, type: "success" | "error" | "info") => void;
}

export const ProjectsTab: React.FC<ProjectsTabProps> = ({
  projects,
  activeProjectId,
  setActiveProjectId,
  setActiveTab,
  isCreatingProject,
  setIsCreatingProject,
  newProjectName,
  setNewProjectName,
  newProjectFloors,
  setNewProjectFloors,
  newProjectDesc,
  setNewProjectDesc,
  newProjectPreset,
  setNewProjectPreset,
  BASE_PRESETS,
  handleCreateProject,
  handleDeleteProject,
  addToast,
}) => {
  return (
    <div className="flex flex-col gap-6 font-sans text-left">
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
  );
};
