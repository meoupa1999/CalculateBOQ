import React from "react";
import { Building, Info } from "lucide-react";
import { Project } from "../types";

interface HeaderProps {
  activeTab: string;
  setActiveTab: (tab: "app" | "projects" | "inventory" | "standards" | "settings") => void;
  projectsCount: number;
}

export const Header: React.FC<HeaderProps> = ({
  activeTab,
  setActiveTab,
  projectsCount,
}) => {
  return (
    <header className="bg-white border-b border-[#ECEFF1] sticky top-0 z-30 font-sans">
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
              Quản lý Dự án ({projectsCount})
            </button>
            <button
              onClick={() => { setActiveTab("settings"); }}
              className={`px-4 py-2 text-sm font-medium rounded transition ${
                activeTab === "settings" ? "text-[#1A237E] bg-[#E8EAF6]" : "text-[#455A64] hover:text-[#191c1e] hover:bg-slate-100"
              }`}
            >
              Cấu hình hệ thống
            </button>
            <button
              disabled
              className="px-4 py-2 text-sm font-medium rounded text-slate-400 opacity-50 cursor-not-allowed"
            >
              Kho thiết bị (0)
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
  );
};
