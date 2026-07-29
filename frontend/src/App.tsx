/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from "react";
import { 
  Layers, 
  Settings, 
  HelpCircle, 
  Activity, 
  FileSpreadsheet, 
  Download, 
  Plus, 
  Trash2, 
  Copy, 
  Save, 
  RefreshCw, 
  Sliders, 
  Cpu, 
  Coins, 
  FileText, 
  Check, 
  AlertCircle, 
  Briefcase, 
  Warehouse, 
  SlidersHorizontal,
  ChevronRight,
  ChevronDown,
  Info,
  Building,
  ArrowRight,
  FileDown,
  Printer,
  CheckCircle2,
  Trash
} from "lucide-react";
import { motion, AnimatePresence } from "motion/react";
import { Header } from "./components/Header";
import { ProjectsTab } from "./components/ProjectsTab";
import { SettingsTab } from "./components/SettingsTab";
import { WorkspaceTab } from "./components/WorkspaceTab";
import { CabinetConfigModal, FloorConnectionDetailModal } from "./components/CabinetConfigModal";
import { StandardsTab } from "./components/StandardsTab";

import { BASE_PRESETS, BASE_INVENTORY, DEFAULT_SITE_PARAMS, DEFAULT_HARDWARE_LOGIC, calculateProjectBOQ, INITIAL_PROJECTS, localCalculateCabinetPlacement } from "./data";
import { Project, Tower, FloorData, SiteParameters, HardwareLogic, InventoryItem, StandardPreset, SystemConfig, DynamicCategory, DynamicProductType, ProductTypeOption } from "./types";

interface BOMItem {
  stt: string;
  name: string;
  desc?: string;
  unit: string;
  field?: string;
  noteKey: string;
  subLevel?: boolean;
}

const BOM_ITEMS: BOMItem[] = [
  // I. HẠNG MỤC VẬT TƯ CHÍNH VÀ GIÁM SÁT VÀ ĐỊNH TUYẾN
  { stt: "", name: "I. HẠNG MỤC VẬT TƯ CHÍNH VÀ GIÁM SÁT VÀ ĐỊNH TUYẾN", unit: "", noteKey: "" },
  { stt: "1", name: "Camera IP Dome 2MP HIKVISION DS-2CD1121G0-I", unit: "Cái", field: "camDomeQuantity", noteKey: "cat1_1" },
  { stt: "2", name: "Camera IP thân 2MP HIKVISION DS-2CD1021G0-I", unit: "Cái", field: "camBulletQuantity", noteKey: "cat1_2" },
  { stt: "3", name: "Đầu ghi hình camera IP 32 kênh HIKVISION DS-7732NXI-K4", unit: "Cái", field: "recorder32Quantity", noteKey: "cat1_3" },
  { stt: "4", name: "Đầu ghi hình camera IP 16 kênh", unit: "Cái", field: "recorder16Quantity", noteKey: "cat1_4" },
  { stt: "5", name: "Ổ Cứng 10T WESTERN", unit: "Cái", field: "hardDiskQuantity", noteKey: "cat1_5" },
  { stt: "6", name: "Switch Hikvision POE 24 cổng DS-3E1326P-EI", unit: "Cái", field: "swich24POEQuantity", noteKey: "cat1_6" },
  { stt: "7", name: "Switch Hikvision POE 16 cổng DS-3E1318P-EI", unit: "Cái", field: "swich16POEQuantity", noteKey: "cat1_7" },
  { stt: "8", name: "Switch 16 port CISCO CBS110-16T-EU", unit: "Cái", field: "swich16CISCOQuantity", noteKey: "cat1_8" },
  { stt: "9", name: "Switch 24 port CISCO", unit: "Cái", field: "swich24CISCOQuantity", noteKey: "cat1_9" },
  { stt: "10", name: "Màn hình quan sát 43 inch SamSung(khung kê + HDMI (15m))", unit: "Bộ", field: "obserScreenQuantity", noteKey: "cat1_10" },

  // II. HẠNG MỤC TRUYỀN DẪN
  { stt: "", name: "II. HẠNG MỤC TRUYỀN DẪN", unit: "", noteKey: "" },
  { stt: "1", name: "Cáp quang 4FO", unit: "Mét", field: "fiberCableQuantity", noteKey: "cat2_1" },
  { stt: "2", name: "Cáp mạng Cat5E", unit: "Mét", field: "cableQuantity", noteKey: "cat2_2" },
  { stt: "3", name: "Bộ chuyển đổi quang điện Gigabit GNETCOM 10/100/1000M GNC-2111S-20A/B", unit: "Bộ", field: "converterQuantity", noteKey: "cat2_3" },
  { stt: "4", name: "Tủ mạng rack 2U", unit: "Bộ", field: "cabinet2UQuantity", noteKey: "cat2_4" },
  { stt: "5", name: "Tủ mạng rack 6U", unit: "Bộ", field: "cabinet6UQuantity", noteKey: "cat2_5" },
  { stt: "6", name: "Tủ mạng rack 10U (Có bánh xe)", unit: "Bộ", field: "cabinet10UQuantity", noteKey: "cat2_6" },
  { stt: "7", name: "Tủ mạng rack 32U", unit: "Bộ", field: "cabinet32UQuantity", noteKey: "cat2_8" },
  { stt: "8", name: "Tủ mạng rack 42U", unit: "Bộ", field: "cabinet42UQuantity", noteKey: "cat2_9" },
  { stt: "9", name: "ODF 12FO SC/UPC (Full Phụ kiện)", unit: "Cái", field: "odf12FOQuantity", noteKey: "cat2_10" },
  { stt: "10", name: "ODF 24FO SC/UPC (Full Phụ kiện)", unit: "Cái", field: "odf24FOQuantity", noteKey: "cat2_11" },

  // III. HẠNG MỤC ĐIỆN
  { stt: "", name: "III. HẠNG MỤC ĐIỆN", unit: "", noteKey: "" },
  { stt: "1", name: "Dây điện CVV 2x2.5", unit: "Mét", field: "cvvCable", noteKey: "cat3_1" },
  { stt: "2", name: "Thanh nguồn PDU đa năng 6 ổ cắm 3 chấu chuẩn 19\"", unit: "Cái", field: "pduQuantity", noteKey: "cat3_2" },

  // IV. HẠNG MỤC NGUỒN DỰ PHÒNG
  { stt: "", name: "IV. HẠNG MỤC NGUỒN DỰ PHÒNG", unit: "", noteKey: "" },
  { stt: "1", name: "Nguồn lưu điện UPS ARES Model AR610 1000VA/800W", unit: "Bộ", field: "ups1000Quantity", noteKey: "cat4_1" },
  { stt: "3", name: "Nguồn lưu điện UPS ARES Model AR630 3000VA-2400W", unit: "Bộ", field: "ups3000Quantity", noteKey: "cat4_3" },

  // V. VẬT TƯ PHỤ
  { stt: "", name: "V. VẬT TƯ PHỤ", unit: "", noteKey: "" },
  { stt: "1", name: "Vật tư phụ", desc: "Bao gồm ống điện, ruột gà, vít, tacke...", unit: "Gói", noteKey: "cat5_1" },
  { stt: "1.1", name: "Vật tư phụ kết nối", unit: "Gói", noteKey: "cat5_1_1" },
  { stt: "-", name: "Đầu mạng AMP Cat 5", unit: "Cái", field: "ampCatQuantity", noteKey: "cat5_1_1_sub1", subLevel: true },
  { stt: "-", name: "Dây nhảy quang SC/UPC SC/UPC 3M", unit: "Sợi", field: "fiberOpticalPatchQuantity", noteKey: "cat5_1_1_sub2", subLevel: true },
  { stt: "-", name: "ODF 4FO SC/UPC - SC/UPC (Full phụ kiện)", unit: "Bộ", field: "odf4FOQuantity", noteKey: "cat5_1_1_sub3", subLevel: true },
  { stt: "-", name: "Dây nhảy mạng Cat5", unit: "Sợi", field: "patchCordQuantity", noteKey: "cat5_1_1_sub4", subLevel: true },
  { stt: "-", name: "Thanh quản lý cáp mạng 19inch", unit: "Cái", field: "cablemanageQuantity", noteKey: "cat5_1_1_sub5", subLevel: true },
  { stt: "1.2", name: "Vật tư phụ thi công", unit: "Gói", noteKey: "cat5_1_2" },
  { stt: "1.2.1", name: "Ruột gà phi 20", unit: "Mét", field: "chickenTubeQuantity", noteKey: "cat5_1_2_1", subLevel: true },
  { stt: "1.2.2", name: "Ống điện D20", unit: "Mét", field: "electricTubeQuantity", noteKey: "cat5_1_2_2", subLevel: true },

  // VI. CHI PHÍ LẮP ĐẶT
  { stt: "", name: "VI. CHI PHÍ LẮP ĐẶT", unit: "", noteKey: "" },
  { stt: "1", name: "Chi phí lắp đặt", desc: "Thi công trọn gói và hướng dẫn vận hành", unit: "Gói", noteKey: "cat6_1" },
  { stt: "1.1", name: "Nhân công Cấu hình thiết lập", unit: "Công", noteKey: "cat6_1_1" },
  { stt: "-", name: "Thiết lập cấu hình", unit: "Công", noteKey: "cat6_1_1_sub1", subLevel: true },
  { stt: "-", name: "Hồ sơ hướng dẫn", unit: "Công", noteKey: "cat6_1_1_sub2", subLevel: true },
  { stt: "-", name: "Kiểm thử T&C", unit: "Công", noteKey: "cat6_1_1_sub3", subLevel: true },
  { stt: "-", name: "Dự trù Thay đổi cấu hình phát sinh", unit: "Công", noteKey: "cat6_1_1_sub4", subLevel: true },
  { stt: "-", name: "Nghiệm thu", unit: "Công", noteKey: "cat6_1_1_sub5", subLevel: true },
  { stt: "-", name: "Bảo hành thiết lập", unit: "Công", noteKey: "cat6_1_1_sub6", subLevel: true },
  { stt: "1.2", name: "Triển khai", unit: "Công", noteKey: "cat6_1_2" }
];

export default function App() {
  // Navigation active state
  // "dashboard" | "parameters" | "logic" | "cost" | "reports"
  const [activeNav, setActiveNav] = useState<"dashboard" | "parameters" | "logic" | "cost" | "reports">("dashboard");

  // Top header tabs state
  // "app" | "projects" | "inventory" | "standards" | "settings"
  const [activeTab, setActiveTab] = useState<"app" | "projects" | "inventory" | "standards" | "settings">("app");

  // Inventory form states
  const [newItemCode, setNewItemCode] = useState("");
  const [newItemName, setNewItemName] = useState("");
  const [newItemCategory, setNewItemCategory] = useState<"Camera" | "Switch" | "Rack" | "UPS" | "PDU" | "Converter" | "Cable" | "Accessories">("Camera");
  const [newItemSpec, setNewItemSpec] = useState("");
  const [newItemUnit, setNewItemUnit] = useState("Cái");
  const [newItemPrice, setNewItemPrice] = useState(100000);

  const API_BASE = "/api";

  // Configuration settings state
  const CONFIG_ID = "a2b0a797-8ff2-4a79-ac5d-78525bd25e90";
  const [systemConfig, setSystemConfig] = useState<SystemConfig | null>(null);
  const [isLoadingConfig, setIsLoadingConfig] = useState(false);

  const fetchSystemConfig = async () => {
    setIsLoadingConfig(true);
    try {
      const res = await fetch(`${API_BASE}/configs/${CONFIG_ID}`);
      if (res.ok) {
        const data = await res.json();
        setSystemConfig(data);
      } else {
        console.error("Failed to fetch system config");
      }
    } catch (err) {
      console.error("Error fetching system config", err);
    } finally {
      setIsLoadingConfig(false);
    }
  };

  const [isSavingConfig, setIsSavingConfig] = useState(false);

  const handleSaveConfig = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!systemConfig) return;
    setIsSavingConfig(true);
    try {
      const res = await fetch(`${API_BASE}/configs/${CONFIG_ID}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          conditionLength: systemConfig.conditionLength,
          sw24ConditionQuanity: systemConfig.sw24ConditionQuanity,
          sw16ConditionQuanity: systemConfig.sw16ConditionQuanity,
          ups: systemConfig.ups,
          pdu: systemConfig.pdu,
          converter: systemConfig.converter
        })
      });
      if (res.ok) {
        addToast("Cập nhật cấu hình hệ thống thành công!", "success");
        fetchSystemConfig();
      } else {
        addToast("Lỗi khi cập nhật cấu hình!", "error");
      }
    } catch (err) {
      console.error(err);
      addToast("Lỗi kết nối khi cập nhật cấu hình!", "error");
    } finally {
      setIsSavingConfig(false);
    }
  };

  // Load projects from backend
  const [projects, setProjects] = useState<Project[]>([]);
  const [activeProjectId, setActiveProjectId] = useState<string>("");
  const [activeTowerId, setActiveTowerId] = useState<string>("");
  const [isSummaryTabActive, setIsSummaryTabActive] = useState<boolean>(false);
  const [selectedTowersSummary, setSelectedTowersSummary] = useState<Record<string, boolean>>({});
  const [summaryBomData, setSummaryBomData] = useState<any>(null);
  const [isCalculatingSummary, setIsCalculatingSummary] = useState<boolean>(false);
  const [isExportingExcel, setIsExportingExcel] = useState<boolean>(false);

  // BOM Configuration states
  const [dynamicCategories, setDynamicCategories] = useState<DynamicCategory[]>([]);
  const [tempCategories, setTempCategories] = useState<DynamicCategory[]>([]);
  const [deletedProductTypeIds, setDeletedProductTypeIds] = useState<string[]>([]);
  const [isSavingBOMConfig, setIsSavingBOMConfig] = useState<boolean>(false);
  const [activeSettingsTab, setActiveSettingsTab] = useState<"config" | "add_product" | "bom_config">("config");
  const [selectedProducts, setSelectedProducts] = useState<Record<string, Record<string, string>>>({});

  // Dynamic Product creation states
  const [productTypes, setProductTypes] = useState<ProductTypeOption[]>([]);
  const [selectedProductTypeId, setSelectedProductTypeId] = useState<string>("");
  const [newProductName, setNewProductName] = useState<string>("");
  const [newProductDesc, setNewProductDesc] = useState<string>("");
  const [isCreatingProduct, setIsCreatingProduct] = useState<boolean>(false);

  const fetchCategories = async () => {
    try {
      const res = await fetch(`${API_BASE}/categories`);
      if (res.ok) {
        const data = await res.json();
        setDynamicCategories(data);
      }
    } catch (err) {
      console.error("Failed to fetch categories", err);
    }
  };

  const fetchProductTypes = async () => {
    try {
      const res = await fetch(`${API_BASE}/products/types`);
      if (res.ok) {
        const data = await res.json();
        setProductTypes(data);
        if (data.length > 0) {
          setSelectedProductTypeId(data[0].id);
        }
      }
    } catch (err) {
      console.error("Failed to fetch product types", err);
    }
  };

  useEffect(() => {
    fetchCategories();
    fetchProductTypes();
  }, []);

  useEffect(() => {
    if (activeSettingsTab === "bom_config" && dynamicCategories.length > 0) {
      setTempCategories(JSON.parse(JSON.stringify(dynamicCategories)));
      setDeletedProductTypeIds([]);
    }
  }, [activeSettingsTab, dynamicCategories]);

  const updateProductTypeField = (catId: string, ptId: string, field: keyof DynamicProductType, value: any) => {
    setTempCategories(prev => {
      const updateInList = (list: DynamicCategory[]): DynamicCategory[] => {
        return list.map(cat => {
          if (cat.id === catId) {
            return {
              ...cat,
              productTypes: (cat.productTypes || []).map(pt => {
                if (pt.id === ptId) {
                  return { ...pt, [field]: value };
                }
                return pt;
              })
            };
          }
          if (cat.children && cat.children.length > 0) {
            return {
              ...cat,
              children: updateInList(cat.children)
            };
          }
          return cat;
        });
      };
      return updateInList(prev);
    });
  };

  const deleteProductType = (catId: string, ptId: string) => {
    if (!ptId.startsWith("temp-")) {
      setDeletedProductTypeIds(prev => [...prev, ptId]);
    }
    setTempCategories(prev => {
      const deleteInList = (list: DynamicCategory[]): DynamicCategory[] => {
        return list.map(cat => {
          if (cat.id === catId) {
            return {
              ...cat,
              productTypes: (cat.productTypes || []).filter(pt => pt.id !== ptId)
            };
          }
          if (cat.children && cat.children.length > 0) {
            return {
              ...cat,
              children: deleteInList(cat.children)
            };
          }
          return cat;
        });
      };
      return deleteInList(prev);
    });
  };

  const addProductTypeAtEnd = (catId: string) => {
    const tempId = `temp-${Date.now()}`;
    setTempCategories(prev => {
      const addInList = (list: DynamicCategory[]): DynamicCategory[] => {
        return list.map(cat => {
          if (cat.id === catId) {
            const nextIndex = (cat.productTypes || []).length > 0
              ? Math.max(...(cat.productTypes || []).map(p => p.orderIndex || 0)) + 1
              : 1;
            const newPt: DynamicProductType = {
              id: tempId,
              code: `NEW_ITEM_${Date.now()}`,
              name: "Vật tư mới",
              unit: "Cái",
              labor: 0.0,
              orderIndex: nextIndex,
              note: "",
              formula: "",
              products: []
            };
            return {
              ...cat,
              productTypes: [...(cat.productTypes || []), newPt]
            };
          }
          if (cat.children && cat.children.length > 0) {
            return {
              ...cat,
              children: addInList(cat.children)
            };
          }
          return cat;
        });
      };
      return addInList(prev);
    });
  };

  const addProductTypeAfter = (catId: string, currentPtId: string) => {
    const tempId = `temp-${Date.now()}`;
    setTempCategories(prev => {
      const addInList = (list: DynamicCategory[]): DynamicCategory[] => {
        return list.map(cat => {
          if (cat.id === catId) {
            const listPts = cat.productTypes || [];
            const currentIndex = listPts.findIndex(p => p.id === currentPtId);
            if (currentIndex === -1) return cat;

            const currentPt = listPts[currentIndex];
            const nextPt = listPts[currentIndex + 1];

            let newOrderIndex = 1.0;
            if (currentPt.orderIndex !== undefined && currentPt.orderIndex !== null) {
              if (nextPt && nextPt.orderIndex !== undefined && nextPt.orderIndex !== null) {
                newOrderIndex = (currentPt.orderIndex + nextPt.orderIndex) / 2;
              } else {
                newOrderIndex = currentPt.orderIndex + 1.0;
              }
            }

            const newPt: DynamicProductType = {
              id: tempId,
              code: `NEW_ITEM_${Date.now()}`,
              name: "Vật tư mới",
              unit: "Cái",
              labor: 0.0,
              orderIndex: newOrderIndex,
              note: "",
              formula: "",
              products: []
            };

            const newList = [...listPts];
            newList.splice(currentIndex + 1, 0, newPt);

            return {
              ...cat,
              productTypes: newList
            };
          }
          if (cat.children && cat.children.length > 0) {
            return {
              ...cat,
              children: addInList(cat.children)
            };
          }
          return cat;
        });
      };
      return addInList(prev);
    });
  };

  const flattenTempCategories = (cats: DynamicCategory[]): any[] => {
    const rows: any[] = [];
    cats.forEach(cat => {
      const isSub = cat.description === "SUBCATEGORY" || cat.name.match(/^[\d\.]+/);
      rows.push({
        type: isSub ? 'subcategory' : 'category',
        id: cat.id,
        name: cat.name
      });

      if (cat.productTypes && cat.productTypes.length > 0) {
        cat.productTypes.forEach(pt => {
          rows.push({
            type: 'product_type',
            id: pt.id,
            code: pt.code,
            name: pt.name,
            unit: pt.unit,
            labor: pt.labor,
            formula: pt.formula,
            note: pt.note,
            orderIndex: pt.orderIndex,
            categoryId: cat.id
          });
        });
      }

      if (cat.children && cat.children.length > 0) {
        const childRows = flattenTempCategories(cat.children);
        rows.push(...childRows);
      }
    });
    return rows;
  };

  const flattenCategoryTree = (cats: DynamicCategory[]): any[] => {
    const rows: any[] = [];
    cats.forEach(cat => {
      const isSub = cat.description === "SUBCATEGORY" || cat.name.match(/^[\d\.]+/);
      rows.push({
        stt: isSub ? "-" : "",
        name: cat.name,
        type: isSub ? "subcategory" : "category"
      });

      if (cat.productTypes && cat.productTypes.length > 0) {
        cat.productTypes.forEach(pt => {
          rows.push({
            stt: pt.orderIndex ? pt.orderIndex.toString() : "-",
            name: pt.name,
            code: pt.code,
            unit: pt.unit,
            labor: pt.labor,
            note: pt.note,
            formula: pt.formula,
            products: pt.products || [],
            subLevel: true,
            type: "product_type",
            id: pt.id
          });
        });
      }

      if (cat.children && cat.children.length > 0) {
        const childRows = flattenCategoryTree(cat.children);
        rows.push(...childRows);
      }
    });
    return rows;
  };

  const handleSaveBOMConfig = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSavingBOMConfig(true);
    try {
      const createList: any[] = [];
      const updateList: any[] = [];

      let sequentialOrder = 1;
      const traverse = (cats: DynamicCategory[]) => {
        cats.forEach(cat => {
          if (cat.productTypes && cat.productTypes.length > 0) {
            cat.productTypes.forEach(pt => {
              const currentOrder = sequentialOrder++;
              if (pt.id.startsWith("temp-")) {
                createList.push({
                  name: pt.name,
                  categoryId: cat.id,
                  unit: pt.unit,
                  labor: pt.labor,
                  orderIndex: currentOrder,
                  formula: pt.formula,
                  note: pt.note,
                  code: pt.code
                });
              } else {
                updateList.push({
                  id: pt.id,
                  name: pt.name,
                  unit: pt.unit,
                  labor: pt.labor,
                  orderIndex: currentOrder,
                  formula: pt.formula,
                  note: pt.note,
                  code: pt.code
                });
              }
            });
          }
          if (cat.children && cat.children.length > 0) {
            traverse(cat.children);
          }
        });
      };

      traverse(tempCategories);

      const payload = {
        create: createList,
        update: updateList,
        delete: deletedProductTypeIds
      };

      const res = await fetch(`${API_BASE}/configs/bom`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      if (res.ok) {
        addToast("Cập nhật cấu hình bảng BOM thành công!", "success");
        await fetchCategories();
      } else {
        addToast("Lỗi khi cập nhật cấu hình bảng BOM!", "error");
      }
    } catch (err) {
      console.error("Failed to save BOM config", err);
      addToast("Lỗi kết nối khi cập nhật cấu hình bảng BOM!", "error");
    } finally {
      setIsSavingBOMConfig(false);
    }
  };

  const handleCreateProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newProductName.trim()) return;
    setIsCreatingProduct(true);
    try {
      const res = await fetch(`${API_BASE}/products`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          productTypeId: selectedProductTypeId,
          product: {
            name: newProductName,
            description: newProductDesc
          }
        })
      });
      if (res.ok) {
        addToast("Tạo sản phẩm thành công!", "success");
        setNewProductName("");
        setNewProductDesc("");
        await fetchCategories();
      } else {
        addToast("Lỗi khi tạo sản phẩm!", "error");
      }
    } catch (err) {
      console.error(err);
      addToast("Lỗi kết nối khi tạo sản phẩm!", "error");
    } finally {
      setIsCreatingProduct(false);
    }
  };

  const [leftWidth, setLeftWidth] = useState<number>(50);
  const [isDragging, setIsDragging] = useState<boolean>(false);
  const [isXl, setIsXl] = useState<boolean>(window.innerWidth >= 1280);

  useEffect(() => {
    const media = window.matchMedia("(min-width: 1280px)");
    const listener = () => setIsXl(media.matches);
    media.addEventListener("change", listener);
    return () => media.removeEventListener("change", listener);
  }, []);

  useEffect(() => {
    if (!isDragging) return;

    const handleMove = (clientX: number) => {
      const container = document.getElementById("bom-split-container");
      if (!container) return;
      const rect = container.getBoundingClientRect();
      const newWidth = ((clientX - rect.left) / rect.width) * 100;
      setLeftWidth(Math.min(Math.max(newWidth, 20), 80));
    };

    const handleMouseMove = (e: MouseEvent) => {
      handleMove(e.clientX);
    };

    const handleTouchMove = (e: TouchEvent) => {
      if (e.touches.length > 0) {
        handleMove(e.touches[0].clientX);
      }
    };

    const handleMouseUp = () => {
      setIsDragging(false);
    };

    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);
    document.addEventListener("touchmove", handleTouchMove);
    document.addEventListener("touchend", handleMouseUp);

    document.body.style.cursor = "col-resize";
    document.body.style.userSelect = "none";

    return () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
      document.removeEventListener("touchmove", handleTouchMove);
      document.removeEventListener("touchend", handleMouseUp);
      document.body.style.cursor = "";
      document.body.style.userSelect = "";
    };
  }, [isDragging]);

  const startDrag = (e: React.MouseEvent | React.TouchEvent) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const fetchProjects = async (selectNewestId?: string) => {
    try {
      const response = await fetch(`${API_BASE}/projects?page=1&size=100`);
      if (!response.ok) throw new Error("Failed to fetch projects");
      const pageData = await response.json();
      const backendList = pageData.content || [];

      // Map backend list to Project list
      const mappedList: Project[] = await Promise.all(
        backendList.map(async (p: any) => {
          let mappedTowers: Tower[] = [];
          try {
            const towersRes = await fetch(`${API_BASE}/towers?projectId=${p.id}&page=1&size=100`);
            if (towersRes.ok) {
              const towersPage = await towersRes.json();
              const backendTowers = towersPage.content || [];
              mappedTowers = backendTowers.map((t: any) => {
                const selectedPreset = BASE_PRESETS.find(pr => pr.id === t.configId) || BASE_PRESETS[0];
                
                // Try to load custom floor configs from specialName JSON if present
                let parsedFloors = [];
                if (t.specialName) {
                  try {
                    parsedFloors = JSON.parse(t.specialName);
                  } catch (e) {
                    console.error("Error parsing specialName floorsData", e);
                  }
                }
                
                if ((!parsedFloors || parsedFloors.length === 0) && t.floorCount > 0) {
                  // Fallback: calculate default floors only if floorCount > 0
                  const projectParams: SiteParameters = {
                    ...DEFAULT_SITE_PARAMS,
                    cableFactor: selectedPreset.cableFactor,
                  };
                  const projectLogic: HardwareLogic = {
                    ...DEFAULT_HARDWARE_LOGIC,
                    switchPreference: selectedPreset.switchPreference,
                  };
                  parsedFloors = calculateProjectBOQ(
                    t.floorCount,
                    t.widthLength ?? 50,
                    t.heightLength ?? 4,
                    "2U",
                    projectParams,
                    projectLogic,
                    [],
                    t.basementCount ?? 0,
                    t.hasRoof ?? false
                  );
                }

                return {
                  id: t.id,
                  name: t.name,
                  description: t.description || "Tháp chính của dự án",
                  createdAt: t.audit?.createdAt || new Date().toISOString(),
                  floorsCount: t.floorCount ?? 0,
                  basementsCount: t.basementCount ?? 0,
                  hasRoof: t.hasRoof ?? false,
                  horizontalDistance: t.widthLength ?? 50,
                  verticalDistance: t.heightLength ?? 4,
                  rackType: "2U" as const,
                  quantity2U: t.quantity2U || 1,
                  standardPresetId: selectedPreset.id,
                  siteParams: {
                    ...DEFAULT_SITE_PARAMS,
                    cableFactor: selectedPreset.cableFactor,
                  },
                  hardwareLogic: {
                    ...DEFAULT_HARDWARE_LOGIC,
                    switchPreference: selectedPreset.switchPreference,
                  },
                  floorsData: parsedFloors,
                  customPrices: {},
                };
              });
            }
          } catch (e) {
            console.error("Error fetching towers for project", p.id, e);
          }

          return {
            id: p.id,
            name: p.name,
            description: p.description || "Dự án giám sát hạ tầng mới",
            createdAt: p.audit?.createdAt || new Date().toISOString(),
            towers: mappedTowers,
          };
        })
      );
      setProjects(mappedList);

      if (mappedList.length > 0) {
        let activeProjId = "";
        if (selectNewestId) {
          activeProjId = selectNewestId;
        } else {
          activeProjId = activeProjectId && mappedList.some((item) => item.id === activeProjectId)
            ? activeProjectId
            : mappedList[0].id;
        }
        setActiveProjectId(activeProjId);
        
        const activeProjObj = mappedList.find(p => p.id === activeProjId);
        if (activeProjObj && activeProjObj.towers.length > 0) {
          setActiveTowerId((prev) => {
            if (prev && activeProjObj.towers.some((t) => t.id === prev)) {
              return prev;
            }
            return activeProjObj.towers[0].id;
          });
        } else {
          setActiveTowerId("");
        }
      } else {
        setActiveProjectId("");
        setActiveTowerId("");
      }
    } catch (error) {
      console.error("Error fetching projects", error);
      addToast("Không thể kết nối đến Backend Server!", "error");
    }
  };

  useEffect(() => {
    fetchProjects();
    fetchSystemConfig();
  }, []);

  // Global base inventory (can be edited globally under Inventory tab)
  const [globalInventory, setGlobalInventory] = useState<InventoryItem[]>(BASE_INVENTORY);

  // New project creation state
  const [isCreatingProject, setIsCreatingProject] = useState(false);
  const [newProjectName, setNewProjectName] = useState("");
  const [newProjectDesc, setNewProjectDesc] = useState("");
  const [newProjectFloors, setNewProjectFloors] = useState(5);
  const [newProjectPreset, setNewProjectPreset] = useState("std-commercial");

  // State for left template table notes (Ghi chú)
  const [leftTableNotes, setLeftTableNotes] = useState<Record<string, string>>({
    cat2_2: "LS/Panduit/Comspose",
    cat3_2: "Dintek/Vietrack/TMC",
    cat5_1_1_sub4: "Dintek/AMP/Panduit",
    cat5_1_1_sub5: "Dintek/AMP/Panduit",
    cat5_1_2: "Dự trù 7 triệu chưa bao gồm 1.2.1",
    cat6_1_1: "6 Ngày hoặc chạy song song với triển khai hạ tầng",
  });

  // Toast alert notifications
  const [toasts, setToasts] = useState<{ id: string; message: string; type: "success" | "info" | "error" }[]>([]);

  // Selected floors for bulk editing
  const [selectedFloorIndexes, setSelectedFloorIndexes] = useState<number[]>([]);
  const [lastSelectedFloorIndex, setLastSelectedFloorIndex] = useState<number | null>(null);

  // Clear selected floors when switching projects
  useEffect(() => {
    setSelectedFloorIndexes([]);
    setLastSelectedFloorIndex(null);
  }, [activeProjectId]);

  const handleSelectAllFloors = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.checked) {
      setSelectedFloorIndexes(activeTower?.floorsData.map((f) => f.floorIndex));
    } else {
      setSelectedFloorIndexes([]);
    }
    setLastSelectedFloorIndex(null);
  };

  const handleToggleSelectFloor = (floorIndex: number, event?: React.MouseEvent) => {
    event?.stopPropagation();
    const isShiftKey = event ? event.shiftKey : false;
    
    setSelectedFloorIndexes((prev) => {
      let newSelection = [...prev];
      
      if (isShiftKey && lastSelectedFloorIndex !== null) {
        const floors = activeTower?.floorsData;
        const startIdx = floors.findIndex(f => f.floorIndex === lastSelectedFloorIndex);
        const endIdx = floors.findIndex(f => f.floorIndex === floorIndex);
        
        if (startIdx !== -1 && endIdx !== -1) {
          const minIdx = Math.min(startIdx, endIdx);
          const maxIdx = Math.max(startIdx, endIdx);
          
          const rangeFloorIndices = floors
            .slice(minIdx, maxIdx + 1)
            .map(f => f.floorIndex);
          
          const isSelecting = prev.includes(lastSelectedFloorIndex);
          
          if (isSelecting) {
            rangeFloorIndices.forEach(idx => {
              if (!newSelection.includes(idx)) {
                newSelection.push(idx);
              }
            });
          } else {
            newSelection = newSelection.filter(idx => !rangeFloorIndices.includes(idx));
          }
        }
      } else {
        if (prev.includes(floorIndex)) {
          newSelection = prev.filter((idx) => idx !== floorIndex);
        } else {
          newSelection = [...prev, floorIndex];
        }
      }
      
      return newSelection;
    });
    
    setLastSelectedFloorIndex(floorIndex);
  };

  const handleBulkUpdateCamera = (camsCount: number) => {
    if (selectedFloorIndexes.length === 0 || !activeTower) return;

    const baseFloors = activeTower.floorsData.map((f) => {
      if (selectedFloorIndexes.includes(f.floorIndex)) {
        const cams = camsCount;
        const dome = Math.round(cams * 0.5);
        const bullet = cams - dome;
        return {
          ...f,
          camerasCount: cams,
          domeCount: dome,
          bulletCount: bullet,
        };
      }
      return f;
    });

    const recalculatedFloors = calculateProjectBOQ(
      activeTower.floorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      activeTower.siteParams,
      activeTower.hardwareLogic,
      baseFloors,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      cabinetPlacements
    );

    // In manual mode, sync the camera updates to the manualGroups allocations
    let nextGroups = manualGroups;
    if (calculationMode === "manual") {
      nextGroups = manualGroups.map((g) => {
        let groupChanged = false;
        const newCabinets = g.cabinets.map((cab) => {
          let cabinetChanged = false;
          const newAllocations = cab.allocations.map((alloc) => {
            if (selectedFloorIndexes.includes(alloc.floorIndex)) {
              cabinetChanged = true;
              groupChanged = true;
              
              const cams = camsCount;
              const dome = Math.round(cams * 0.5);
              const bullet = cams - dome;
              
              return {
                ...alloc,
                domeCount: dome,
                bulletCount: bullet
              };
            }
            return alloc;
          });
          if (cabinetChanged) {
            return {
              ...cab,
              allocations: newAllocations
            };
          }
          return cab;
        });
        if (groupChanged) {
          return {
            ...g,
            cabinets: newCabinets
          };
        }
        return g;
      });
      setManualGroups(nextGroups);
    }

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                floorsData: recalculatedFloors,
                manualGroups: nextGroups,
              };
            }
            return t;
          });

          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    fetchCabinetPlacement(
      activeTower.floorsCount,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      recalculatedFloors,
      calculationMode,
      nextGroups,
      activeTower.quantity2U || 1
    );

    addToast(`Đồng bộ ${camsCount} camera cho ${selectedFloorIndexes.length} tầng thành công!`, "success");
    setSelectedFloorIndexes([]); // Clear selection after apply
  };

  const addToast = (message: string, type: "success" | "info" | "error" = "success") => {
    const id = Date.now().toString() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4000);
  };

  // Find active project
  const activeProject = projects.find((p) => p.id === activeProjectId) || projects[0];

  // Find active tower
  const activeTower = isSummaryTabActive ? null : (activeProject?.towers?.find((t) => t.id === activeTowerId) || activeProject?.towers?.[0]);

  // Temporary edit states for current tower top inputs (to be committed on "Tính toán BOQ" click)
  const [tempFloors, setTempFloors] = useState(activeTower?.floorsCount || 5);
  const [tempBasements, setTempBasements] = useState(activeTower?.basementsCount || 0);
  const [tempHasRoof, setTempHasRoof] = useState(activeTower?.hasRoof || false);
  const [tempH, setTempH] = useState(activeTower?.horizontalDistance || 50);
  const [tempV, setTempV] = useState(activeTower?.verticalDistance || 4);
  const [tempRack, setTempRack] = useState<"2U" | "6U" | "10U" | "20U">(activeTower?.rackType || "2U");
  const [tempQuantity2U, setTempQuantity2U] = useState<number>(activeTower?.quantity2U || 1);

  // State to store cabinet placement floors (indices of upper floors that have cabinets)
  const [cabinetPlacements, setCabinetPlacements] = useState<number[]>([]);
  const [bomData, setBomData] = useState<any>(null);
  const [customBOMOverrides, setCustomBOMOverrides] = useState<Record<string, Record<string, number>>>({});

  // Dual-mode calculation state
  const [calculationMode, setCalculationMode] = useState<"auto" | "manual">("auto");
  const [manualGroups, setManualGroups] = useState<{
    cabinetIndex: number;
    cabinets: {
      id: string;
      type: string;
      quantity2U?: number;
      allocations: {
        floorIndex: number;
        domeCount: number;
        bulletCount: number;
      }[];
    }[];
  }[]>([]);
  const [activeCabinetIndex, setActiveCabinetIndex] = useState<number | null>(null);
  const [editingCabinetIndex, setEditingCabinetIndex] = useState<number | null>(null);
  const [viewingFloorConnectionDetail, setViewingFloorConnectionDetail] = useState<number | null>(null);
  const [tempCabinets, setTempCabinets] = useState<{
    id: string;
    type: string;
    quantity2U?: number;
    allocations: {
      floorIndex: number;
      domeCount: number;
      bulletCount: number;
    }[];
  }[]>([]);

  // Bulk selection states for linked floors within the cabinet config popup
  const [selectedAllocIds, setSelectedAllocIds] = useState<string[]>([]);
  const [lastSelectedAllocId, setLastSelectedAllocId] = useState<string | null>(null);

  useEffect(() => {
    setSelectedAllocIds([]);
    setLastSelectedAllocId(null);
  }, [editingCabinetIndex]);

  const handleToggleSelectAlloc = (cabIdx: number, allocIdx: number, event?: React.MouseEvent) => {
    event?.stopPropagation();
    const id = `${cabIdx}_${allocIdx}`;
    const isShiftKey = event ? event.shiftKey : false;

    setSelectedAllocIds((prev) => {
      let newSelection = [...prev];

      if (isShiftKey && lastSelectedAllocId !== null) {
        const allAllocs: { cabIdx: number; allocIdx: number; id: string }[] = [];
        tempCabinets.forEach((c, cI) => {
          c.allocations.forEach((a, aI) => {
            allAllocs.push({ cabIdx: cI, allocIdx: aI, id: `${cI}_${aI}` });
          });
        });

        const startIdx = allAllocs.findIndex(item => item.id === lastSelectedAllocId);
        const endIdx = allAllocs.findIndex(item => item.id === id);

        if (startIdx !== -1 && endIdx !== -1) {
          const minIdx = Math.min(startIdx, endIdx);
          const maxIdx = Math.max(startIdx, endIdx);

          const rangeIds = allAllocs
            .slice(minIdx, maxIdx + 1)
            .map(item => item.id);

          const isSelecting = prev.includes(lastSelectedAllocId);

          if (isSelecting) {
            rangeIds.forEach(rId => {
              if (!newSelection.includes(rId)) {
                newSelection.push(rId);
              }
            });
          } else {
            newSelection = newSelection.filter(rId => !rangeIds.includes(rId));
          }
        }
      } else {
        if (newSelection.includes(id)) {
          newSelection = newSelection.filter(item => item !== id);
        } else {
          newSelection.push(id);
        }
      }

      return newSelection;
    });

    setLastSelectedAllocId(id);
  };

  const syncFloorsWithManualGroups = (currentFloors: FloorData[], groups: any[]) => {
    const allocMap = new Map<number, { dome: number; bullet: number }>();
    groups.forEach((g) => {
      g.cabinets.forEach((c: any) => {
        c.allocations.forEach((a: any) => {
          const key = a.floorIndex;
          const existing = allocMap.get(key) || { dome: 0, bullet: 0 };
          allocMap.set(key, {
            dome: existing.dome + a.domeCount,
            bullet: existing.bullet + a.bulletCount,
          });
        });
      });
    });

    return currentFloors.map((f) => {
      if (allocMap.has(f.floorIndex)) {
        const alloc = allocMap.get(f.floorIndex)!;
        return {
          ...f,
          domeCount: alloc.dome,
          bulletCount: alloc.bullet,
          camerasCount: alloc.dome + alloc.bullet,
        };
      }
      return f;
    });
  };

  const getFloorConnections = (floorIndex: number) => {
    const connections: {
      cabinetFloorIndex: number;
      cabinetId: string;
      cabinetType: string;
      domeCount: number;
      bulletCount: number;
    }[] = [];

    const floors = activeTower?.floorsData || [];
    
    // 1. Try finding from API response data
    floors.forEach((fl) => {
      if (fl.isCabinetPlaced && fl.cabinets) {
        fl.cabinets.forEach((cab) => {
          if (cab.allocations) {
            const match = cab.allocations.find((a) => a.floorIndex === floorIndex);
            if (match) {
              connections.push({
                cabinetFloorIndex: fl.floorIndex,
                cabinetId: cab.cabinetId,
                cabinetType: cab.cabinetType,
                domeCount: match.domeCount,
                bulletCount: match.bulletCount,
              });
            }
          }
        });
      }
    });

    // 2. If no connections found (e.g. before API response), fall back to manualGroups state
    if (connections.length === 0) {
      manualGroups.forEach((g) => {
        if (g.cabinets) {
          g.cabinets.forEach((cab: any) => {
            if (cab.allocations) {
              const match = cab.allocations.find((a: any) => a.floorIndex === floorIndex);
              if (match) {
                connections.push({
                  cabinetFloorIndex: g.cabinetIndex,
                  cabinetId: cab.id,
                  cabinetType: cab.type,
                  domeCount: match.domeCount,
                  bulletCount: match.bulletCount,
                });
              }
            }
          });
        }
      });
    }

    return connections;
  };

  const updateTowerFloorsData = (newFloorsData: FloorData[], nextGroups = manualGroups, nextMode = calculationMode) => {
    if (!activeTower || !activeProject) return;
    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower.id) {
              return {
                ...t,
                floorsData: newFloorsData,
                manualGroups: nextGroups,
                calculationMode: nextMode,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    fetchCabinetPlacement(
      tempFloors,
      tempBasements,
      tempHasRoof,
      tempH,
      tempV,
      tempRack,
      newFloorsData,
      nextMode,
      nextGroups,
      tempRack === "2U" ? tempQuantity2U : 1
    );
  };

  const handleToggleCabinet = (floorIndex: number) => {
    if (!activeTower) return;
    const exists = manualGroups.some((g) => g.cabinetIndex === floorIndex);
    let nextGroups = [];
    if (exists) {
      if (activeCabinetIndex === floorIndex) {
        setActiveCabinetIndex(null);
      }
      nextGroups = manualGroups.filter((g) => g.cabinetIndex !== floorIndex);
    } else {
      const floorDataRow = activeTower.floorsData.find(fd => fd.floorIndex === floorIndex);
      const newGroup = {
        cabinetIndex: floorIndex,
        cabinets: [
          {
            id: crypto.randomUUID(),
            type: "6U",
            quantity2U: 1,
            allocations: [
              {
                floorIndex: floorIndex,
                domeCount: floorDataRow ? (floorDataRow.domeCount || 0) : 0,
                bulletCount: floorDataRow ? (floorDataRow.bulletCount || 0) : 0,
              }
            ]
          }
        ]
      };
      setActiveCabinetIndex(floorIndex);
      nextGroups = [...manualGroups, newGroup];
    }
    setManualGroups(nextGroups);
    
    const updatedFloorsData = syncFloorsWithManualGroups(activeTower.floorsData, nextGroups);
    updateTowerFloorsData(updatedFloorsData, nextGroups);
  };

  const handleCabinetRackTypeChange = (cabinetIndex: number, newRackType: string) => {
    // Deprecated: Managed inside the cabinet config modal
  };

  const handleCtrlClickFloor = (floorIndex: number) => {
    // Deprecated: Managed inside the cabinet config modal
  };

  // Fetch saved cabinet placement from DB
  const fetchSavedCabinetPlacement = async (towerId: string): Promise<boolean> => {
    if (!towerId) return false;
    const isUuid = (id: string) => /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(id);
    if (!isUuid(towerId)) return false;

    try {
      const res = await fetch(`${API_BASE}/calculate/cabinet-placement?towerId=${towerId}`);
      if (res.ok) {
        const data = await res.json();
        if (data && data.length > 0) {
          // Kiểm tra data DB có hợp lệ không: nếu có cabinets nhưng tất cả cameraQuantity = 0
          // thì data bị lỗi từ lần save trước → fallback sang tính lại
          const placedFloors = data.filter((item: any) => item.isCabinetPlaced);
          const allCabinetsZero = placedFloors.length > 0 && placedFloors.every((item: any) =>
            (item.cameraQuantityInCabinet ?? 0) === 0 &&
            (!item.cabinets || item.cabinets.every((c: any) => (c.cameraQuantityInCabinet ?? 0) === 0))
          );
          if (allCabinetsZero) {
            console.warn("DB data corrupt: all cabinet camera counts are 0, triggering recalculation");
            return false;
          }

          const hasManualAllocations = data.some((item: any) => 
            item.isCabinetPlaced && 
            item.cabinets && 
            item.cabinets.some((c: any) => c.allocations !== null && c.allocations !== undefined)
          );

          const reconstructedGroups: any[] = [];
          if (hasManualAllocations) {
            data.forEach((item: any) => {
              if (item.isCabinetPlaced && item.cabinets && item.cabinets.length > 0) {
                const manualCabs = item.cabinets.filter((cab: any) => cab.allocations !== null && cab.allocations !== undefined);
                if (manualCabs.length > 0) {
                  const allocationsFloors = manualCabs.flatMap((c: any) => (c.allocations || []).map((a: any) => a.floorIndex));
                  const minFloor = allocationsFloors.length > 0 ? Math.min(...allocationsFloors) : item.floorIndex;
                  const maxFloor = allocationsFloors.length > 0 ? Math.max(...allocationsFloors) : item.floorIndex;
                  
                  const floorRange: Record<number, number> = {};
                  floorRange[minFloor] = maxFloor;

                  reconstructedGroups.push({
                    cabinetIndex: item.floorIndex,
                    cabinets: manualCabs.map((cab: any) => {
                      const hasAlloc = cab.allocations && cab.allocations.length > 0;
                      const defaultAlloc = hasAlloc ? cab.allocations : [
                        {
                          floorIndex: item.floorIndex,
                          domeCount: item.domeCount || 0,
                          bulletCount: item.bulletCount || 0,
                        }
                      ];
                      return {
                        id: cab.cabinetId,
                        type: cab.cabinetType,
                        quantity2U: cab.quantity2U || 1,
                        allocations: defaultAlloc
                      };
                    }),
                    floorRange
                  });
                }
              }
            });
            setCalculationMode("manual");
            setManualGroups(reconstructedGroups);
          } else {
            setCalculationMode("auto");
            setManualGroups([]);
          }

          const cabinetFloorIndices = data
            .filter((item: any) => item.isCabinetPlaced)
            .map((item: any) => item.floorIndex);
          setCabinetPlacements(cabinetFloorIndices);

          const cabinetRanges = data
            .filter((item: any) => item.isCabinetPlaced)
            .map((item: any) => ({
              floorIndex: item.floorIndex,
              fromIndex: item.fromIndex,
              toIndex: item.toIndex,
            }));

          const backendMap = new Map<number, any>();
          data.forEach((item: any) => backendMap.set(item.floorIndex, item));

          setProjects((prev) =>
            prev.map((p) => {
              if (p.id === activeProjectId) {
                const updatedTowers = p.towers.map((t) => {
                  if (t.id === towerId) {
                    const updatedFloors = t.floorsData.map((f) => {
                      const coveringCabinet = cabinetRanges.find(
                        (c: any) => f.floorIndex >= c.fromIndex && f.floorIndex <= c.toIndex
                      );

                      if (backendMap.has(f.floorIndex)) {
                        const backendInfo = backendMap.get(f.floorIndex);
                        if (backendInfo.isCabinetPlaced) {
                          const matchingGroup = hasManualAllocations ? reconstructedGroups.find((g: any) => g.cabinetIndex === f.floorIndex) : null;
                          const mappedCabinets = (backendInfo.cabinets ?? []).map((cab: any) => {
                            const matchCab = matchingGroup?.cabinets?.find((c: any) => c.id === cab.cabinetId);
                            return {
                              ...cab,
                              quantity2U: hasManualAllocations ? (matchCab?.quantity2U || 1) : 1
                            };
                          });
                          return {
                            ...f,
                            camerasCount: backendInfo.camerasCount ?? f.camerasCount ?? 0,
                            domeCount: backendInfo.domeCount ?? f.domeCount ?? 0,
                            bulletCount: backendInfo.bulletCount ?? f.bulletCount ?? 0,
                            cableLengthInput: backendInfo.autocadLength !== undefined ? backendInfo.autocadLength : f.cableLengthInput,
                            sw24Count: backendInfo.sw24Count ?? 0,
                            sw16Count: backendInfo.sw16Count ?? 0,
                            upsType: backendInfo.upsCount === 1 ? "1K" : (backendInfo.upsCount === 2 ? "2K" : "None"),
                            pduCount: backendInfo.pduCount ?? 0,
                            convCount: backendInfo.convCount ?? 0,
                            cameraQuantityInCabinet: backendInfo.cameraQuantityInCabinet ?? 0,
                            isCabinetPlaced: true,
                            cabinetType: backendInfo.cabinetType,
                            cabinetIndex: backendInfo.cabinetIndex ?? undefined,
                            cableLength: backendInfo.cableLength ?? 0,
                            atrium: backendInfo.atrium ?? 0,
                            downCabinet: backendInfo.downCabinet ?? 0,
                            inCabinet: backendInfo.inCabinet ?? 0,
                            autocadLength: backendInfo.autocadLength ?? 0,
                            fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                            toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                            cabinets: mappedCabinets,
                          };
                        }
                        return {
                          ...f,
                          camerasCount: backendInfo.camerasCount ?? f.camerasCount ?? 0,
                          domeCount: backendInfo.domeCount ?? f.domeCount ?? 0,
                          bulletCount: backendInfo.bulletCount ?? f.bulletCount ?? 0,
                          cableLengthInput: backendInfo.autocadLength !== undefined ? backendInfo.autocadLength : f.cableLengthInput,
                          sw24Count: 0,
                          sw16Count: 0,
                          upsType: "None",
                          pduCount: 0,
                          convCount: 0,
                          cameraQuantityInCabinet: 0,
                          isCabinetPlaced: false,
                          cabinetType: undefined,
                          cabinetIndex: backendInfo.cabinetIndex ?? undefined,
                          cableLength: backendInfo.cableLength ?? 0,
                          atrium: backendInfo.atrium ?? 0,
                          downCabinet: backendInfo.downCabinet ?? 0,
                          inCabinet: backendInfo.inCabinet ?? 0,
                          autocadLength: backendInfo.autocadLength ?? 0,
                          fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                          toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                        };
                      }
                      return f;
                    });
                    return {
                      ...t,
                      floorsData: updatedFloors,
                      manualGroups: hasManualAllocations ? reconstructedGroups : [],
                      calculationMode: hasManualAllocations ? "manual" : "auto",
                    };
                  }
                  return t;
                });
                return {
                  ...p,
                  towers: updatedTowers,
                };
              }
              return p;
            })
          );
          return true;
        }
      }
    } catch (err) {
      console.error("Error fetching saved cabinet placement:", err);
    }
    return false;
  };

  // Fetch cabinet placement from API
  const fetchCabinetPlacement = async (
    floorsCount: number,
    basementsCount: number,
    hasRoof: boolean,
    horizontalDistance: number,
    verticalDistance: number,
    rackType: string,
    floorsData: FloorData[],
    mode: "auto" | "manual" = calculationMode,
    groups = manualGroups,
    qty2U: number = activeTower?.quantity2U || 1
  ) => {
    if (floorsCount <= 0) {
      setCabinetPlacements([]);
      return;
    }
    try {
      const isUuid = (id: string) => /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(id);
      const towerIdParam = activeTower?.id && isUuid(activeTower.id) ? `?towerId=${activeTower.id}` : "";

      const sortedFloors = [...floorsData]
        .sort((a, b) => a.floorIndex - b.floorIndex)
        .map(f => ({
          floorIndex: f.floorIndex,
          label: f.label,
          camerasCount: f.camerasCount,
          domeCount: f.domeCount,
          bulletCount: f.bulletCount,
          cableLength: f.cableLengthInput !== undefined ? f.cableLengthInput : 0,
        }));

      let res;
      if (mode === "manual") {
        const manualGroupsPayload = groups.map((g) => {
          // Gom đúng các floorIndex được allocate (bao gồm cabinetIndex + allocations)
          const allocatedFloors = Array.from(new Set([
            g.cabinetIndex,
            ...g.cabinets.flatMap((c: any) => c.allocations.map((a: any) => a.floorIndex))
          ])).sort((a, b) => a - b);

          // floorRange: gửi đúng min/max của các tầng thực sự được allocate
          const minF = allocatedFloors.length > 0 ? Math.min(...allocatedFloors) : g.cabinetIndex;
          const maxF = allocatedFloors.length > 0 ? Math.max(...allocatedFloors) : g.cabinetIndex;

          return {
            cabinetIndex: g.cabinetIndex,
            floorRange: { [minF]: maxF },
            cabinets: g.cabinets.map((c: any) => {
              const totalDome = c.allocations.reduce((sum: number, a: any) => sum + (a.domeCount || 0), 0);
              const totalBullet = c.allocations.reduce((sum: number, a: any) => sum + (a.bulletCount || 0), 0);
              return {
                id: c.id,
                type: c.type,
                totalDome,
                totalBullet,
                totalCamera: totalDome + totalBullet,
                allocations: c.allocations
              };
            })
          };
        });

        res = await fetch(`${API_BASE}/calculate/cabinet-placement-manual${towerIdParam}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            floorsCount,
            basementsCount,
            hasRoof,
            horizontalDistance,
            verticalDistance,
            rackType,
            quantity2U: qty2U,
            floors: sortedFloors,
            manualGroups: manualGroupsPayload,
          })
        });
      } else {
        res = await fetch(`${API_BASE}/calculate/cabinet-placement${towerIdParam}`, {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            floorsCount,
            basementsCount,
            hasRoof,
            horizontalDistance,
            verticalDistance,
            rackType,
            quantity2U: qty2U,
            floors: sortedFloors
          })
        });
      }

      if (res.ok) {
        const data = await res.json();
        
        // Extract floor indices where cabinets are placed
        const cabinetFloorIndices = data
          .filter((item: any) => item.isCabinetPlaced)
          .map((item: any) => item.floorIndex);
        setCabinetPlacements(cabinetFloorIndices);

        // Get cabinet ranges
        const cabinetRanges = data
          .filter((item: any) => item.isCabinetPlaced)
          .map((item: any) => ({
            floorIndex: item.floorIndex,
            fromIndex: item.fromIndex,
            toIndex: item.toIndex,
          }));

        // Map backend results by floorIndex
        const backendMap = new Map<number, any>();
        data.forEach((item: any) => backendMap.set(item.floorIndex, item));

        // Update active tower's floorsData with backend calculated equipment
        setProjects((prev) =>
          prev.map((p) => {
            if (p.id === activeProjectId) {
              const updatedTowers = p.towers.map((t) => {
                if (t.id === activeTowerId) {
                  const updatedFloors = t.floorsData.map((f) => {
                    const coveringCabinet = cabinetRanges.find(
                      (c: any) => f.floorIndex >= c.fromIndex && f.floorIndex <= c.toIndex
                    );

                    if (backendMap.has(f.floorIndex)) {
                      const backendInfo = backendMap.get(f.floorIndex);
                      if (backendInfo.floorIndex === 7) {
                        console.log("Floor 7 Backend Info:", JSON.stringify(backendInfo));
                      }
                      if (backendInfo.isCabinetPlaced) {
                        const matchingGroup = mode === "manual" ? groups.find(g => g.cabinetIndex === f.floorIndex) : null;
                        const mappedCabinets = (backendInfo.cabinets ?? []).map((cab: any) => {
                          const matchCab = matchingGroup?.cabinets?.find((c: any) => c.id === cab.cabinetId);
                          return {
                            ...cab,
                            quantity2U: mode === "manual" ? (matchCab?.quantity2U || 1) : (qty2U || 1)
                          };
                        });
                        return {
                          ...f,
                          camerasCount: backendInfo.camerasCount ?? f.camerasCount ?? 0,
                          domeCount: backendInfo.domeCount ?? f.domeCount ?? 0,
                          bulletCount: backendInfo.bulletCount ?? f.bulletCount ?? 0,
                          cableLengthInput: backendInfo.autocadLength !== undefined ? backendInfo.autocadLength : f.cableLengthInput,
                          sw24Count: backendInfo.sw24Count ?? 0,
                          sw16Count: backendInfo.sw16Count ?? 0,
                          upsType: backendInfo.upsCount === 1 ? "1K" : (backendInfo.upsCount === 2 ? "2K" : "None"),
                          pduCount: backendInfo.pduCount ?? 0,
                          convCount: backendInfo.convCount ?? 0,
                          cameraQuantityInCabinet: backendInfo.cameraQuantityInCabinet ?? 0,
                          isCabinetPlaced: true,
                          cabinetType: backendInfo.cabinetType,
                          cabinetIndex: backendInfo.cabinetIndex ?? undefined,
                          cableLength: backendInfo.cableLength ?? 0,
                          atrium: backendInfo.atrium ?? 0,
                          downCabinet: backendInfo.downCabinet ?? 0,
                          inCabinet: backendInfo.inCabinet ?? 0,
                          autocadLength: backendInfo.autocadLength ?? 0,
                          fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                          toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                          cabinets: mappedCabinets,
                        };
                      }
                      return {
                        ...f,
                        camerasCount: backendInfo.camerasCount ?? f.camerasCount ?? 0,
                        domeCount: backendInfo.domeCount ?? f.domeCount ?? 0,
                        bulletCount: backendInfo.bulletCount ?? f.bulletCount ?? 0,
                        cableLengthInput: backendInfo.autocadLength !== undefined ? backendInfo.autocadLength : f.cableLengthInput,
                        sw24Count: 0,
                        sw16Count: 0,
                        upsType: "None",
                        pduCount: 0,
                        convCount: 0,
                        cameraQuantityInCabinet: 0,
                        isCabinetPlaced: false,
                        cabinetType: undefined,
                        cabinetIndex: backendInfo.cabinetIndex ?? undefined,
                        cableLength: backendInfo.cableLength ?? 0,
                        atrium: backendInfo.atrium ?? 0,
                        downCabinet: backendInfo.downCabinet ?? 0,
                        inCabinet: backendInfo.inCabinet ?? 0,
                        autocadLength: backendInfo.autocadLength ?? 0,
                        fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                        toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                      };
                    }
                    // Non-cabinet floor fallback
                    return {
                      ...f,
                      sw24Count: 0,
                      sw16Count: 0,
                      upsType: "None",
                      pduCount: 0,
                      convCount: 0,
                      cameraQuantityInCabinet: 0,
                      isCabinetPlaced: false,
                      cabinetType: undefined,
                      cabinetIndex: undefined,
                      fromIndex: coveringCabinet ? coveringCabinet.fromIndex : undefined,
                      toIndex: coveringCabinet ? coveringCabinet.toIndex : undefined,
                    };
                  });
                  return {
                    ...t,
                    floorsData: updatedFloors,
                  };
                }
                return t;
              });
              return {
                ...p,
                towers: updatedTowers,
              };
            }
            return p;
          })
        );
      } else {
        const localPlacements = localCalculateCabinetPlacement(floorsCount, horizontalDistance, verticalDistance);
        setCabinetPlacements(localPlacements.map(lvl => basementsCount + lvl - 1));
      }
    } catch (err) {
      console.error("Error fetching cabinet placement", err);
      const localPlacements = localCalculateCabinetPlacement(floorsCount, horizontalDistance, verticalDistance);
      setCabinetPlacements(localPlacements.map(lvl => basementsCount + lvl - 1));
    }
  };

  const fetchBOM = async (tower: any) => {
    if (!tower || !tower.floorsData) return;
    try {
      const floorsData = tower.floorsData;
      const totalCamera = floorsData.reduce((acc: number, curr: any) => acc + (curr.camerasCount || 0), 0);
      const totalCamDome = floorsData.reduce((acc: number, curr: any) => acc + (curr.domeCount || 0), 0);
      const totalCamBullet = floorsData.reduce((acc: number, curr: any) => acc + (curr.bulletCount || 0), 0);
      const totalSw16 = floorsData.reduce((acc: number, curr: any) => acc + (curr.sw16Count || 0), 0);
      const totalSw24 = floorsData.reduce((acc: number, curr: any) => acc + (curr.sw24Count || 0), 0);
      const totalSwichPOE = totalSw16 + totalSw24;
      const totalCabinet = floorsData.filter((f: any) => f.isCabinetPlaced).length;
      const cabinets: Record<string, number> = {};
      floorsData.forEach((f: any) => {
        if (f.isCabinetPlaced) {
          if (f.cabinets && f.cabinets.length > 0) {
            f.cabinets.forEach((cab: any) => {
              const type = cab.cabinetType || cab.type || "2U";
              const qty2U = type === "2U" ? (cab.quantity2U || 1) : 1;
              cabinets[type] = (cabinets[type] || 0) + qty2U;
            });
          } else {
            const type = f.cabinetType || tower.rackType || "2U";
            const qty2U = type === "2U" ? (tower.quantity2U || 1) : 1;
            cabinets[type] = (cabinets[type] || 0) + qty2U;
          }
        }
      });
      if (Object.keys(cabinets).length === 0) {
        const type = tower.rackType || "2U";
        cabinets[type] = totalCabinet || 0;
      }
      const totalUPS = floorsData.filter((f: any) => f.isCabinetPlaced && f.upsType !== "None").length;
      const totalPDU = floorsData.reduce((acc: number, curr: any) => acc + (curr.pduCount || 0), 0);
      const totalConverter = floorsData.reduce((acc: number, curr: any) => acc + (curr.convCount || 0), 0);
      const totalCableLength = floorsData.reduce((acc: number, curr: any) => acc + (curr.cableLength || 0), 0);

      const floors = floorsData.map((f: any) => ({
        floorIndex: f.floorIndex,
        isCabinetPlaced: f.isCabinetPlaced || false,
        label: f.label,
        camerasCount: f.camerasCount || 0,
        domeCount: f.domeCount || 0,
        bulletCount: f.bulletCount || 0,
        cameraQuantityInCabinet: f.cameraQuantityInCabinet || 0,
        sw24Count: f.sw24Count || 0,
        sw16Count: f.sw16Count || 0,
        upsCount: f.upsType !== "None" ? 1 : 0,
        pduCount: f.pduCount || 0,
        convCount: f.convCount || 0,
        cabinetType: f.cabinetType
      }));

      const res = await fetch(`${API_BASE}/calculate/bom`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify([{
          towerId: tower.id, totalCamera,
          totalCamDome,
          totalCamBullet,
          totalSwichPOE,
          totalSw16,
          totalSw24,
          cabinets,

          totalUPS,
          totalPDU,
          totalConverter,
          totalCableLength,
          floors
        }])
      });
      if (res.ok) {
        const data = await res.json();
        setBomData(data);
      } else {
        console.error("Failed to fetch BOM", res.statusText);
        addToast("Lỗi khi tính toán BOM!", "error");
      }
    } catch (err) {
      console.error("Error fetching BOM data", err);
      addToast("Lỗi kết nối dịch vụ BOM!", "error");
    }
  };

  useEffect(() => {
    if (activeTower) {
      fetchBOM(activeTower);
    }
  }, [activeTower?.id, activeTower?.floorsData, activeTower?.rackType]);

  useEffect(() => {
    if (isSummaryTabActive && activeProject?.towers) {
      const initial: Record<string, boolean> = {};
      activeProject.towers.forEach(t => {
        initial[t.id] = true;
      });
      setSelectedTowersSummary(initial);
    }
  }, [isSummaryTabActive, activeProject?.towers]);

  const handleCalculateSummary = async () => {
    if (!activeProject?.towers) return;
    
    const selectedTowers = activeProject.towers.filter(t => selectedTowersSummary[t.id]);
    if (selectedTowers.length === 0) {
      addToast("Vui lòng chọn ít nhất một tháp để tính tổng BOM!", "error");
      return;
    }

    setIsCalculatingSummary(true);
    try {
      const payload = selectedTowers.map((t) => {
        const floorsData = t.floorsData;
        if (!floorsData || floorsData.length === 0) return null;
        const totalCamera = floorsData.reduce((acc: number, curr: any) => acc + (curr.camerasCount || 0), 0);
        const totalCamDome = floorsData.reduce((acc: number, curr: any) => acc + (curr.domeCount || 0), 0);
        const totalCamBullet = floorsData.reduce((acc: number, curr: any) => acc + (curr.bulletCount || 0), 0);
        const totalSw16 = floorsData.reduce((acc: number, curr: any) => acc + (curr.sw16Count || 0), 0);
        const totalSw24 = floorsData.reduce((acc: number, curr: any) => acc + (curr.sw24Count || 0), 0);
        const totalSwichPOE = totalSw16 + totalSw24;
        const totalCabinet = floorsData.filter((f: any) => f.isCabinetPlaced).length;
        const cabinets: Record<string, number> = {};
        floorsData.forEach((f: any) => {
          if (f.isCabinetPlaced) {
            if (f.cabinets && f.cabinets.length > 0) {
              f.cabinets.forEach((cab: any) => {
                const type = cab.cabinetType || cab.type || "2U";
                const qty2U = type === "2U" ? (cab.quantity2U || 1) : 1;
                cabinets[type] = (cabinets[type] || 0) + qty2U;
              });
            } else {
              const type = f.cabinetType || t.rackType || "2U";
              const qty2U = type === "2U" ? (t.quantity2U || 1) : 1;
              cabinets[type] = (cabinets[type] || 0) + qty2U;
            }
          }
        });
        if (Object.keys(cabinets).length === 0) {
          const type = t.rackType || "2U";
          cabinets[type] = totalCabinet || 0;
        }
        const totalUPS = floorsData.filter((f: any) => f.isCabinetPlaced && f.upsType !== "None").length;
        const totalPDU = floorsData.reduce((acc: number, curr: any) => acc + (curr.pduCount || 0), 0);
        const totalConverter = floorsData.reduce((acc: number, curr: any) => acc + (curr.convCount || 0), 0);
        const totalCableLength = floorsData.reduce((acc: number, curr: any) => acc + (curr.cableLength || 0), 0);

        const floors = floorsData.map((f: any) => ({
          floorIndex: f.floorIndex,
          isCabinetPlaced: f.isCabinetPlaced || false,
          label: f.label,
          camerasCount: f.camerasCount || 0,
          domeCount: f.domeCount || 0,
          bulletCount: f.bulletCount || 0,
          cameraQuantityInCabinet: f.cameraQuantityInCabinet || 0,
          sw24Count: f.sw24Count || 0,
          sw16Count: f.sw16Count || 0,
          upsCount: f.upsType !== "None" ? 1 : 0,
          pduCount: f.pduCount || 0,
          convCount: f.convCount || 0
        }));

        return {
          towerId: t.id,
          totalCamera,
          totalCamDome,
          totalCamBullet,
          totalSwichPOE,
          totalSw16,
          totalSw24,
          cabinets,

          totalUPS,
          totalPDU,
          totalConverter,
          totalCableLength,
          floors
        };
      }).filter(p => p !== null);

      if (payload.length === 0) {
        addToast("Không lấy được dữ liệu BOM cho tháp nào!", "error");
        return;
      }

      const res = await fetch(API_BASE + "/calculate/bom", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });

      if (res.ok) { setSummaryBomData(await res.json()); } else { addToast("Lỗi khi tính tổng BOM!", "error"); return; }
      addToast("Tính tổng BOM cho các tháp thành công!", "success");
    } catch (err) {
      console.error(err);
      addToast("Lỗi khi tính tổng BOM!", "error");
    } finally {
      setIsCalculatingSummary(false);
    }
  };

  const handleExportExcel = async () => {
    if (!activeProject) return;
    const selectedTowers = activeProject.towers.filter(t => selectedTowersSummary[t.id]);
    if (selectedTowers.length === 0) {
      addToast("Vui lòng chọn ít nhất một tháp để xuất Excel!", "error");
      return;
    }

    try {
      setIsExportingExcel(true);
      const towerIdsParam = selectedTowers.map(t => `towerIds=${t.id}`).join("&");
      const url = `${API_BASE}/calculate/export-excel?projectId=${activeProject.id}&${towerIdsParam}`;
      
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error("Không thể xuất file Excel");
      }
      
      const blob = await response.blob();
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = downloadUrl;
      
      const cleanProjectName = activeProject.name.replace(/\s+/g, "_");
      link.setAttribute("download", `BOQ_${cleanProjectName}_Export.xlsx`);
      
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(downloadUrl);
      addToast("Xuất file Excel thành công!", "success");
    } catch (err) {
      console.error(err);
      addToast("Lỗi khi xuất file Excel!", "error");
    } finally {
      setIsExportingExcel(false);
    }
  };

  // Sync temp values when active tower changes
  useEffect(() => {
    if (activeTower) {
      setTempFloors(activeTower?.floorsCount);
      setTempBasements(activeTower?.basementsCount || 0);
      setTempHasRoof(activeTower?.hasRoof || false);
      setTempH(activeTower?.horizontalDistance);
      setTempV(activeTower?.verticalDistance);
      setTempRack(activeTower?.rackType);
      setTempQuantity2U(activeTower?.quantity2U || 1);

      const nextMode = activeTower?.calculationMode || "auto";
      const nextGroups = activeTower?.manualGroups || [];

      setCalculationMode(nextMode);
      setManualGroups(nextGroups);
      
      // Chỉ load từ DB 1 lần khi tower chưa có kết quả tính toán (isCabinetPlaced chưa set)
      const isUuid = (id: string) => /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(id);
      const hasCabinetResult = activeTower?.floorsData?.some(f => f.isCabinetPlaced === true);
      
      if (!hasCabinetResult && activeTower?.id && isUuid(activeTower.id)) {
        // Lần đầu load tower từ DB (chưa có kết quả tính toán trong state)
        fetchSavedCabinetPlacement(activeTower.id).then((loaded) => {
          if (!loaded) {
            fetchCabinetPlacement(
              activeTower?.floorsCount || 0,
              activeTower?.basementsCount || 0,
              activeTower?.hasRoof || false,
              activeTower?.horizontalDistance || 0,
              activeTower?.verticalDistance || 0,
              activeTower?.rackType || "2U",
              activeTower?.floorsData || [],
              nextMode,
              nextGroups,
              activeTower?.rackType === "2U" ? (activeTower?.quantity2U || 1) : 1
            );
          }
        });
      } else if (!hasCabinetResult) {
        // Tower chưa có ID (mới tạo) → tính toán luôn
        fetchCabinetPlacement(
          activeTower?.floorsCount || 0,
          activeTower?.basementsCount || 0,
          activeTower?.hasRoof || false,
          activeTower?.horizontalDistance || 0,
          activeTower?.verticalDistance || 0,
          activeTower?.rackType || "2U",
          activeTower?.floorsData || [],
          nextMode,
          nextGroups,
          activeTower?.rackType === "2U" ? (activeTower?.quantity2U || 1) : 1
        );
      }
      // Nếu hasCabinetResult = true → đã có kết quả trong state, không cần gọi lại
    }
  }, [activeTower?.id]);

  // Report general meta details
  const [customerName, setCustomerName] = useState("Công ty TNHH Đầu tư & Phát triển Công nghệ");
  const [projectLocation, setProjectLocation] = useState("Khu Công nghệ cao Láng Hòa Lạc, Hà Nội");
  const [validDays, setValidDays] = useState(30);

  // Trigger recalculation of the active project
  const handleRecalculate = () => {
    if (!activeTower) return;
    if (tempFloors <= 0) {
      addToast("Số tầng nổi phải lớn hơn 0!", "error");
      return;
    }
    if (tempH <= 0 || tempV <= 0) {
      addToast("Khoảng cách ngang/dọc phải lớn hơn 0!", "error");
      return;
    }

    // Lưu tower settings vào backend trước
    const saveTowerAndRecalculate = (floorsDataToUse: FloorData[]) => {
      setProjects((prev) =>
        prev.map((p) => {
          if (p.id === activeProject.id) {
            const updatedTowers = p.towers.map((t) => {
              if (t.id === activeTower?.id) {
                return {
                  ...t,
                  floorsCount: tempFloors,
                  basementsCount: tempBasements,
                  hasRoof: tempHasRoof,
                  horizontalDistance: tempH,
                  verticalDistance: tempV,
                  rackType: tempRack,
                  quantity2U: tempRack === "2U" ? tempQuantity2U : 1,
                  floorsData: floorsDataToUse,
                  manualGroups: t.manualGroups || [],
                  calculationMode: t.calculationMode || "auto",
                };
              }
              return t;
            });
            return { ...p, towers: updatedTowers };
          }
          return p;
        })
      );

      if (activeTower) {
        fetch(`${API_BASE}/towers/${activeTower.id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            configId: "a2b0a797-8ff2-4a79-ac5d-78525bd25e90",
            name: activeTower.name,
            floorCount: tempFloors,
            basementCount: tempBasements,
            hasRoof: tempHasRoof,
            widthLength: tempH,
            heightLength: tempV,
            quantity2U: tempRack === "2U" ? tempQuantity2U : 1
          })
        })
        .then(res => {
          if (res.ok) {
            fetchCabinetPlacement(
              tempFloors,
              tempBasements,
              tempHasRoof,
              tempH,
              tempV,
              tempRack,
              floorsDataToUse,
              calculationMode,
              manualGroups,
              tempRack === "2U" ? tempQuantity2U : 1
            );
          }
        })
        .catch(err => console.error("Error saving recalculated tower to backend", err));
      }
    };

    if (calculationMode === "manual") {
      // Trong chế độ thủ công: giữ nguyên floorsData hiện tại, chỉ update settings tower
      // Không chạy calculateProjectBOQ để tránh reset isCabinetPlaced và gây flicker
      saveTowerAndRecalculate(activeTower.floorsData);
    } else {
      // Chế độ tự động: chạy local calculation trước để estimate cabinet placement
      const localPlacements = localCalculateCabinetPlacement(tempFloors, tempH, tempV);
      const newCabinetPlacements = localPlacements.map(lvl => tempBasements + lvl - 1);
      setCabinetPlacements(newCabinetPlacements);

      const updatedFloorsData = calculateProjectBOQ(
        tempFloors,
        tempH,
        tempV,
        tempRack,
        activeTower?.siteParams,
        activeTower?.hardwareLogic,
        activeTower?.floorsData,
        tempBasements,
        tempHasRoof,
        newCabinetPlacements
      );
      saveTowerAndRecalculate(updatedFloorsData);
    }

    addToast("Tính toán lại BOQ thành công!", "success");
  };

  const handleResetBOQ = () => {
    if (!activeTower) return;

    // Reset all floors data in the current active tower to 0 cameras and 0 equipment
    const resetFloors = activeTower.floorsData.map((f) => ({
      ...f,
      camerasCount: 0,
      domeCount: 0,
      bulletCount: 0,
      sw24Count: 0,
      sw16Count: 0,
      upsType: "None" as const,
      pduCount: 0,
      convCount: 0,
      cameraQuantityInCabinet: 0,
      isCabinetPlaced: false,
      cabinetType: undefined,
      fromIndex: undefined,
      toIndex: undefined,
      cableLengthInput: undefined,
    }));

    setManualGroups([]);
    setCabinetPlacements([]);
    setCalculationMode("auto");

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                floorsData: resetFloors,
                manualGroups: [],
                calculationMode: "auto",
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    // Call PUT /api/towers/{id} to save empty config to backend
    fetch(`${API_BASE}/towers/${activeTower.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        configId: "a2b0a797-8ff2-4a79-ac5d-78525bd25e90",
        name: activeTower.name,
        floorCount: activeTower.floorsCount,
        basementCount: activeTower.basementsCount,
        hasRoof: activeTower.hasRoof,
        widthLength: activeTower.horizontalDistance,
        heightLength: activeTower.verticalDistance,
        quantity2U: activeTower.quantity2U || 1
      })
    })
    .then((res) => {
      if (res.ok) {
        fetchCabinetPlacement(
          activeTower.floorsCount,
          activeTower.basementsCount || 0,
          activeTower.hasRoof || false,
          activeTower.horizontalDistance,
          activeTower.verticalDistance,
          activeTower.rackType,
          resetFloors,
          "auto",
          [],
          activeTower.quantity2U || 1
        );
      }
    })
    .catch(err => console.error("Error resetting tower in backend", err));

    addToast("Đã reset cấu hình camera và thiết bị về 0!", "info");
  };

  // Directly update specific cell value in the detailed floor sheet
  const handleUpdateFloorCell = (floorIndex: number, field: keyof FloorData, value: number | string) => {
    if (!activeTower) return;

    const isSelected = selectedFloorIndexes.includes(floorIndex);

    const baseFloors = activeTower.floorsData.map((f) => {
      const shouldUpdate = f.floorIndex === floorIndex || (isSelected && selectedFloorIndexes.includes(f.floorIndex));
      if (shouldUpdate) {
        const updatedRow = { ...f, [field]: value };
        
        // If cameras count, dome or bullet count is updated, we do automatic minor adjustment of children
        if (field === "camerasCount") {
          const cams = Number(value);
          const dome = Math.round(cams * 0.5);
          const bullet = cams - dome;
          updatedRow.camerasCount = cams;
          updatedRow.domeCount = dome;
          updatedRow.bulletCount = bullet;
        } else if (field === "domeCount") {
          const dome = Number(value);
          updatedRow.domeCount = dome;
          updatedRow.camerasCount = dome + updatedRow.bulletCount;
        } else if (field === "bulletCount") {
          const bullet = Number(value);
          updatedRow.bulletCount = bullet;
          updatedRow.camerasCount = updatedRow.domeCount + bullet;
        }
        return updatedRow;
      }
      return f;
    });

    const recalculatedFloors = calculateProjectBOQ(
      activeTower.floorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      activeTower.siteParams,
      activeTower.hardwareLogic,
      baseFloors,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      cabinetPlacements
    );

    // In manual mode, sync the camera updates to the manualGroups allocations
    let nextGroups = manualGroups;
    if (calculationMode === "manual") {
      nextGroups = manualGroups.map((g) => {
        let groupChanged = false;
        const newCabinets = g.cabinets.map((cab) => {
          let cabinetChanged = false;
          const newAllocations = cab.allocations.map((alloc) => {
            const shouldUpdateAlloc = alloc.floorIndex === floorIndex || (isSelected && selectedFloorIndexes.includes(alloc.floorIndex));
            if (shouldUpdateAlloc) {
              cabinetChanged = true;
              groupChanged = true;
              
              let newDome = alloc.domeCount;
              let newBullet = alloc.bulletCount;
              
              if (field === "camerasCount") {
                const cams = Number(value);
                newDome = Math.round(cams * 0.5);
                newBullet = cams - newDome;
              } else if (field === "domeCount") {
                newDome = Number(value);
              } else if (field === "bulletCount") {
                newBullet = Number(value);
              }
              
              return {
                ...alloc,
                domeCount: newDome,
                bulletCount: newBullet
              };
            }
            return alloc;
          });
          if (cabinetChanged) {
            return {
              ...cab,
              allocations: newAllocations
            };
          }
          return cab;
        });
        if (groupChanged) {
          return {
            ...g,
            cabinets: newCabinets
          };
        }
        return g;
      });
      setManualGroups(nextGroups);
    }

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                floorsData: recalculatedFloors,
                manualGroups: nextGroups,
              };
            }
            return t;
          });

          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    fetchCabinetPlacement(
      activeTower.floorsCount,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      recalculatedFloors,
      calculationMode,
      nextGroups,
      activeTower.quantity2U || 1
    );
  };

  const handleDeleteFloor = (deletedFloorIndex: number) => {
    if (!activeTower) return;

    const basementsCount = activeTower.basementsCount || 0;
    const floorsCount = activeTower.floorsCount || 0;
    const hasRoof = activeTower.hasRoof || false;

    let newBasementsCount = basementsCount;
    let newFloorsCount = floorsCount;
    let newHasRoof = hasRoof;

    if (deletedFloorIndex < basementsCount) {
      newBasementsCount = Math.max(0, basementsCount - 1);
    } else if (deletedFloorIndex >= basementsCount && deletedFloorIndex < basementsCount + floorsCount) {
      newFloorsCount = Math.max(0, floorsCount - 1);
    } else if (hasRoof && deletedFloorIndex === basementsCount + floorsCount) {
      newHasRoof = false;
    }

    setTempBasements(newBasementsCount);
    setTempFloors(newFloorsCount);
    setTempHasRoof(newHasRoof);

    const remainingFloors = activeTower.floorsData
      .filter((f) => f.floorIndex !== deletedFloorIndex)
      .map((f, idx) => ({
        ...f,
        floorIndex: idx,
      }));

    const newCabinetPlacements = cabinetPlacements
      .filter((idx) => idx !== deletedFloorIndex)
      .map((idx) => (idx > deletedFloorIndex ? idx - 1 : idx));
    setCabinetPlacements(newCabinetPlacements);

    const newManualGroups = manualGroups
      .filter((g) => g.cabinetIndex !== deletedFloorIndex)
      .map((g) => {
        const updatedCabinetIndex = g.cabinetIndex > deletedFloorIndex ? g.cabinetIndex - 1 : g.cabinetIndex;
        const updatedAssociatedFloors = g.associatedFloors
          .filter((fIdx) => fIdx !== deletedFloorIndex)
          .map((fIdx) => (fIdx > deletedFloorIndex ? fIdx - 1 : fIdx));
        return {
          ...g,
          cabinetIndex: updatedCabinetIndex,
          associatedFloors: updatedAssociatedFloors,
        };
      });
    setManualGroups(newManualGroups);

    const recalculatedFloors = calculateProjectBOQ(
      newFloorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      activeTower.siteParams,
      activeTower.hardwareLogic,
      remainingFloors,
      newBasementsCount,
      newHasRoof,
      newCabinetPlacements
    );

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                floorsCount: newFloorsCount,
                basementsCount: newBasementsCount,
                hasRoof: newHasRoof,
                floorsData: recalculatedFloors,
                manualGroups: newManualGroups,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    fetch(`${API_BASE}/towers/${activeTower.id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        configId: "a2b0a797-8ff2-4a79-ac5d-78525bd25e90",
        name: activeTower.name,
        floorCount: newFloorsCount,
        basementCount: newBasementsCount,
        hasRoof: newHasRoof,
        widthLength: activeTower.horizontalDistance,
        heightLength: activeTower.verticalDistance
      })
    })
    .then((res) => {
      if (res.ok) {
        fetchCabinetPlacement(
          newFloorsCount,
          newBasementsCount,
          newHasRoof,
          activeTower.horizontalDistance,
          activeTower.verticalDistance,
          activeTower.rackType,
          recalculatedFloors,
          calculationMode,
          manualGroups,
          activeTower.quantity2U || 1
        );
      }
    })
    .catch((err) => console.error("Error updating tower on floor deletion", err));

    addToast("Đã xóa tầng thành công và tính toán lại BOQ!", "success");
  };

  // Helper to render editable note input cell in the Excel-like BOQ Template Table (Left)
  const renderNoteCell = (key: string) => {
    return (
      <td className="py-1.5 px-2 text-slate-700">
        <input
          type="text"
          value={leftTableNotes[key] || ""}
          onChange={(e) => setLeftTableNotes(prev => ({ ...prev, [key]: e.target.value }))}
          placeholder="-"
          className="w-full bg-transparent hover:bg-slate-100/50 focus:bg-white border border-transparent hover:border-slate-300 focus:border-[#1A237E] rounded px-1.5 py-0.5 text-xs text-slate-700 focus:outline-none transition"
        />
      </td>
    );
  };

  const getLaborValue = (itemName: string): number | null => {
    const normalized = itemName.trim().toLowerCase();
    if (normalized.includes("camera ip dome 2mp hikvision ds-2cd1121g0-i")) return 0.085;
    if (normalized.includes("camera ip thân 2mp hikvision ds-2cd1021g0-i")) return 0.085;
    if (normalized.includes("đầu ghi hình camera ip 32 kênh hikvision ds-7732nxi-k4")) return 0.12;
    if (normalized.includes("đầu ghi hình camera ip 16 kênh")) return 0.12;
    if (normalized.includes("ổ cứng 10t western")) return 0.2;
    if (normalized.includes("switch hikvision poe 24 cổng ds-3e1326p-ei")) return 0.18;
    if (normalized.includes("switch hikvision poe 16 cổng ds-3e1318p-ei")) return 0.18;
    if (normalized.includes("switch 16 port cisco cbs110-16t-eu")) return 0.18;
    if (normalized.includes("switch 24 port cisco")) return 0.18;
    if (normalized.includes("màn hình quan sát 43 inch samsung")) return 0.34;
    if (normalized.includes("cáp quang 4fo")) return 0.007;
    if (normalized.includes("cáp mạng cat5e")) return 0.01;
    if (normalized.includes("bộ chuyển đổi quang điện gigabit gnetcom")) return 0.018;
    if (normalized.includes("tủ mạng rack 2u")) return 0.5;
    if (normalized.includes("tủ mạng rack 6u")) return 0.765;
    if (normalized.includes("tủ mạng rack 10u (có bánh xe)")) return 0.765;
    if (normalized.includes("tủ mạng rack 32u")) return 2;
    if (normalized.includes("tủ mạng rack 42u")) return 2;
    if (normalized.includes("odf 12fo sc/upc") || normalized.includes("odf 24fo sc/upc")) return 1;
    if (normalized.includes("dây điện cvv 2x2.5")) return 0.01;
    if (normalized.includes("thanh nguồn pdu đa năng")) return 0.04;
    if (normalized.includes("nguồn lưu điện ups ares model ar610")) return 0.245;
    if (normalized.includes("nguồn lưu điện ups ares model ar630")) return 0.245;
    if (normalized.includes("đầu mạng amp cat 5")) return 0.0267;
    if (normalized.includes("dây nhảy quang sc/upc sc/upc 3m") || normalized.includes("dây nhảy quang sc/upc")) return 0.013;
    if (normalized.includes("odf 4fo sc/upc")) return 0.165;
    if (normalized.includes("dây nhảy mạng cat5")) return 0.013;
    if (normalized.includes("thanh quản lý cáp mạng 19inch") || normalized.includes("thanh quản lý cáp mạng")) return 0.08;
    if (normalized.includes("ruột gà phi 20")) return 0.034;
    if (normalized.includes("ống điện d20")) return 0.034;
    return null;
  };

  const renderLaborCells = (itemName: string, quantity: number) => {
    const labor = getLaborValue(itemName);
    if (labor === null) {
      return (
        <>
          <td className="py-2.5 px-1 text-center font-mono"></td>
          <td className="py-2.5 px-1 text-center font-mono"></td>
        </>
      );
    }
    const totalLabor = quantity * labor;
    return (
      <>
        <td className="py-2.5 px-1 text-center font-mono">{labor}</td>
        <td className="py-2.5 px-1 text-center font-mono">{Number(totalLabor.toFixed(4))}</td>
      </>
    );
  };

  // Apply Selected Preset Standards
  const handleSelectPreset = (preset: StandardPreset) => {
    if (!activeTower) return;
    const updatedParams: SiteParameters = {
      ...activeTower?.siteParams,
      cableFactor: preset.cableFactor,
    };
    const updatedLogic: HardwareLogic = {
      ...activeTower?.hardwareLogic,
      switchPreference: preset.switchPreference,
    };
    
    // Recalculate whole floors with new parameters
    const recalculatedFloors = calculateProjectBOQ(
      activeTower?.floorsCount,
      activeTower?.horizontalDistance,
      activeTower?.verticalDistance,
      activeTower?.rackType,
      updatedParams,
      updatedLogic,
      activeTower?.floorsData,
      activeTower?.basementsCount || 0,
      activeTower?.hasRoof || false,
      cabinetPlacements
    );

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                standardPresetId: preset.id,
                siteParams: updatedParams,
                hardwareLogic: updatedLogic,
                floorsData: recalculatedFloors,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

    addToast(`Đã áp dụng tiêu chuẩn: ${preset.name}`, "success");
    setActiveTab("app");
  };

  // Update specific site parameters
  const handleUpdateSiteParam = (key: keyof SiteParameters, value: string | number) => {
    if (!activeTower) return;
    const updatedParams = { ...activeTower.siteParams, [key]: value };
    const recalculatedFloors = calculateProjectBOQ(
      activeTower.floorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      updatedParams,
      activeTower.hardwareLogic,
      activeTower.floorsData,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      cabinetPlacements
    );

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                siteParams: updatedParams,
                floorsData: recalculatedFloors,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

  };

  // Update specific hardware logic parameters
  const handleUpdateHardwareLogic = (key: keyof HardwareLogic, value: string | number) => {
    if (!activeTower) return;
    const updatedLogic = { ...activeTower.hardwareLogic, [key]: value };
    const recalculatedFloors = calculateProjectBOQ(
      activeTower.floorsCount,
      activeTower.horizontalDistance,
      activeTower.verticalDistance,
      activeTower.rackType,
      activeTower.siteParams,
      updatedLogic,
      activeTower.floorsData,
      activeTower.basementsCount || 0,
      activeTower.hasRoof || false,
      cabinetPlacements
    );

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                hardwareLogic: updatedLogic,
                floorsData: recalculatedFloors,
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );

  };

  // Reset current project custom price overrides
  const handleResetPrices = () => {
    if (!activeTower) return;
    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return { ...t, customPrices: {} };
            }
            return t;
          });
          return { ...p, towers: updatedTowers };
        }
        return p;
      })
    );
    addToast("Đã thiết lập lại giá gốc từ kho", "info");
  };

  // Set custom unit price for current project
  const handleSetCustomPrice = (itemId: string, price: number) => {
    if (!activeTower) return;
    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const updatedTowers = p.towers.map((t) => {
            if (t.id === activeTower?.id) {
              return {
                ...t,
                customPrices: {
                  ...t.customPrices,
                  [itemId]: price,
                },
              };
            }
            return t;
          });
          return {
            ...p,
            towers: updatedTowers,
          };
        }
        return p;
      })
    );
  };
const handleAddGlobalInventory = () => {
    if (!newItemCode || !newItemName) {
      addToast("Vui lòng nhập Mã vật tư và Tên thiết bị!", "error");
      return;
    }
    const item: InventoryItem = {
      id: "item-" + Date.now(),
      code: newItemCode,
      name: newItemName,
      category: newItemCategory,
      spec: newItemSpec,
      unit: newItemUnit,
      basePrice: newItemPrice,
    };
    setGlobalInventory((prev) => [...prev, item]);
    setNewItemCode("");
    setNewItemName("");
    setNewItemSpec("");
    setNewItemPrice(100000);
    addToast("Đã thêm thiết bị mới vào Kho vật tư!", "success");
  };

  const handleDeleteInventoryItem = (id: string) => {
    setGlobalInventory((prev) => prev.filter((item) => item.id !== id));
    addToast("Đã xóa thiết bị khỏi Kho", "info");
  };

  // Create Project handler
  const handleCreateProject = async () => {
    if (!newProjectName) {
      addToast("Vui lòng điền tên dự án!", "error");
      return;
    }

    try {
      const response = await fetch(`${API_BASE}/projects`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          name: newProjectName,
          description: newProjectDesc || "Dự án giám sát hạ tầng mới",
        }),
      });

      if (!response.ok) throw new Error("Failed to create project");

      addToast("Tạo dự án trên backend thành công!", "success");

      // Fetch projects again
      const listResponse = await fetch(`${API_BASE}/projects?page=1&size=100`);
      if (!listResponse.ok) throw new Error("Failed to fetch projects after creation");
      const pageData = await listResponse.json();
      const backendList = pageData.content || [];

      const oldIds = projects.map(p => p.id);
      const newProjectBackend = backendList.find((p: any) => !oldIds.includes(p.id)) || backendList[0];

      if (newProjectBackend) {
        await fetchProjects(newProjectBackend.id);
      }

      setIsCreatingProject(false);
      setNewProjectName("");
      setNewProjectDesc("");
      setNewProjectFloors(5);
      setActiveTab("app");
      setActiveNav("dashboard");
    } catch (error) {
      console.error("Error creating project", error);
      addToast("Không thể tạo dự án!", "error");
    }
  };

  const handleCreateTower = (name: string) => {
    if (!activeProject) return;
    const selectedPreset = BASE_PRESETS[0];

    // Create local placeholder tower — NOT saved to backend yet
    // The init form will show because floorsCount=0 and floorsData=[]
    // User fills in details then clicks "Khởi tạo tháp & Bắt đầu" which does POST /api/towers
    const newTower: Tower = {
      id: "tower-new-" + Date.now(),
      name: name,
      description: "Tháp giám sát mới",
      createdAt: new Date().toISOString(),
      floorsCount: 0,
      basementsCount: 0,
      hasRoof: false,
      horizontalDistance: 50,
      verticalDistance: 4,
      rackType: "2U",
      standardPresetId: selectedPreset.id,
      siteParams: { ...DEFAULT_SITE_PARAMS, cableFactor: selectedPreset.cableFactor },
      hardwareLogic: { ...DEFAULT_HARDWARE_LOGIC, switchPreference: selectedPreset.switchPreference },
      floorsData: [],
      customPrices: {},
    };

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          return { ...p, towers: [...p.towers, newTower] };
        }
        return p;
      })
    );
    setActiveTowerId(newTower.id);
    addToast(`Đã tạo tháp mới: ${name}. Vui lòng nhập thông số và bấm Khởi tạo.`, "success");
  };

  const handleDeleteTower = async (towerId: string) => {
    if (!activeProject) return;

    if (!confirm("Bạn có chắc chắn muốn xóa tháp này?")) {
      return;
    }

    // Call DELETE /api/towers/{id} if tower is persisted in backend
    if (!towerId.startsWith("tower-new-")) {
      try {
        const response = await fetch(`${API_BASE}/towers/${towerId}`, {
          method: "DELETE",
        });
        if (!response.ok) throw new Error("Failed to delete tower");
      } catch (err) {
        console.error("Error deleting tower", err);
        addToast("Lỗi khi xóa tháp trên backend!", "error");
        return;
      }
    }

    setProjects((prev) =>
      prev.map((p) => {
        if (p.id === activeProject.id) {
          const filteredTowers = p.towers.filter((t) => t.id !== towerId);
          return {
            ...p,
            towers: filteredTowers,
          };
        }
        return p;
      })
    );

    setActiveTowerId((prev) => {
      if (prev === towerId) {
        const remaining = activeProject.towers.filter((t) => t.id !== towerId);
        return remaining.length > 0 ? remaining[0].id : "";
      }
      return prev;
    });

    addToast("Đã xóa tháp thành công", "info");
  };

  const handleDeleteProject = async (id: string) => {
    if (projects.length <= 1) {
      addToast("Không thể xóa dự án duy nhất còn lại!", "error");
      return;
    }

    if (!confirm("Bạn có chắc chắn muốn xóa dự án này?")) {
      return;
    }

    try {
      const response = await fetch(`${API_BASE}/projects/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) throw new Error("Failed to delete project");

      addToast("Đã xóa dự án thành công", "info");
      await fetchProjects();
    } catch (error) {
      console.error("Error deleting project", error);
      addToast("Không thể xóa dự án!", "error");
    }
  };
// BOQ Itemized Quantities Aggregation for Active Project
  const aggregateQuantities = () => {
    let totalDome = 0;
    let totalBullet = 0;
    let sw24Count = 0;
    let sw16Count = 0;
    let rack2u = 0;
    let rack6u = 0;
    let rack10u = 0;
    let rack20u = 0;
    let ups1k = 0;
    let ups2k = 0;
    let pduCount = 0;
    let convCount = 0;
    let cableMeters = 0;

    if (!activeProject || !activeTower || !activeTower?.floorsData) {
      return {};
    }

    activeTower?.floorsData.forEach((f) => {
      totalDome += f.domeCount;
      totalBullet += f.bulletCount;
      sw24Count += f.sw24Count;
      sw16Count += f.sw16Count;
      
      // Rack counts based on active project's rack size configuration
      if (f.isCabinetPlaced) {
        if (f.cabinets && f.cabinets.length > 0) {
          f.cabinets.forEach((cab: any) => {
            const type = cab.cabinetType || cab.type || "2U";
            const qty2U = type === "2U" ? (cab.quantity2U || 1) : 1;
            if (type === "2U") rack2u += qty2U;
            else if (type === "6U") rack6u += 1;
            else if (type === "10U") rack10u += 1;
            else if (type === "20U") rack20u += 1;
          });
        } else {
          const type = f.cabinetType || activeTower?.rackType || "2U";
          const qty2U = type === "2U" ? (activeTower?.quantity2U || 1) : 1;
          if (type === "2U") rack2u += qty2U;
          else if (type === "6U") rack6u += 1;
          else if (type === "10U") rack10u += 1;
          else if (type === "20U") rack20u += 1;
        }
      }

      if (f.upsType === "1K") ups1k += 1;
      else if (f.upsType === "2K") ups2k += 1;

      pduCount += f.pduCount;
      convCount += f.convCount;
      cableMeters += f.cableLength;
    });

    cableMeters = Math.round(cableMeters);

    // Build the quantified lines
    const quantities: Record<string, number> = {
      "item-cam-dome": totalDome,
      "item-cam-bullet": totalBullet,
      "item-sw-24": sw24Count,
      "item-sw-16": sw16Count,
      "item-rack-2u": rack2u,
      "item-rack-6u": rack6u,
      "item-rack-10u": rack10u,
      "item-rack-20u": rack20u,
      "item-ups-1k": ups1k,
      "item-ups-2k": ups2k,
      "item-pdu": pduCount,
      "item-converter": convCount,
      "item-cable-cat6": cableMeters,
      "item-accessories": activeTower?.floorsData.length, // Package of accessories per floor
    };

    return quantities;
  };

  const itemizedQuantities = aggregateQuantities();

  // Price calculations
  const getItemUnitPrice = (item: InventoryItem) => {
    if (activeProject && activeTower?.customPrices && activeTower?.customPrices[item.id] !== undefined) {
      return activeTower?.customPrices[item.id];
    }
    return item.basePrice;
  };

  const getSubTotal = () => {
    let subtotal = 0;
    globalInventory.forEach((item) => {
      const qty = itemizedQuantities[item.id] || 0;
      if (qty > 0) {
        subtotal += qty * getItemUnitPrice(item);
      }
    });
    return subtotal;
  };

  const subTotal = getSubTotal();
  const laborCost = Math.round(subTotal * 0.08); // 8% labor cost estimation
  const vatAmount = Math.round((subTotal + laborCost) * 0.1); // 10% VAT
  const totalProjectPrice = subTotal + laborCost + vatAmount;

  // Format currency helpers
  const formatVND = (value: number) => {
    return new Intl.NumberFormat("vi-VN", { style: "currency", currency: "VND" }).format(value);
  };

  // Export CSV / BOQ Table Excel Format trigger
  const handleExportCSV = () => {
    let csvContent = "data:text/csv;charset=utf-8,";
    csvContent += "Surveillance BOQ Engine Export - " + activeProject.name + "\r\n";
    csvContent += "Khach hang," + customerName + "\r\n";
    csvContent += "Dia diem," + projectLocation + "\r\n";
    csvContent += "Ngay xuat," + new Date().toLocaleDateString("vi-VN") + "\r\n\r\n";

    // Table detailed
    csvContent += "BANG TINH BOQ CHI TIET THEO TANG\r\n";
    csvContent += "Tang,So Camera,Cam Dome,Cam Than,Tu & So Cam,SW24,SW16,UPS,PDU,Converter\r\n";
    
    activeTower?.floorsData.forEach((f) => {
      const isCabinetPlaced = f.isCabinetPlaced;
      const cabinetCol = isCabinetPlaced 
        ? `Tủ ${f.cabinetType || ""} (${f.cameraQuantityInCabinet ?? 0} Cam)`
        : "-";
      csvContent += `"${f.label}",${f.camerasCount},${f.domeCount},${f.bulletCount},"${cabinetCol}",${f.sw24Count},${f.sw16Count},"${f.upsType}",${f.pduCount},${f.convCount}\r\n`;
    });

    csvContent += "\r\nBANG QUY CHUAN THIET BI TONG QUAN\r\n";
    csvContent += "Ma Vat Tu,Ten Thiet Bi,Thong So Ky Thuat,Don Vi,So Luong,Don Gia,Thanh Tien\r\n";

    globalInventory.forEach((item) => {
      const qty = itemizedQuantities[item.id] || 0;
      if (qty > 0) {
        const price = getItemUnitPrice(item);
        csvContent += `"${item.code}","${item.name}","${item.spec.replace(/"/g, '""')}","${item.unit}",${qty},${price},${qty * price}\r\n`;
      }
    });

    csvContent += `\r\n,,,,,Tong gia vat tu,${subTotal}\r\n`;
    csvContent += `,,,,,Chi phi thi cong lap dat (8%),${laborCost}\r\n`;
    csvContent += `,,,,,Thue gia tri gia tang VAT (10%),${vatAmount}\r\n`;
    csvContent += `,,,,,TONG CONG CHI PHI DU AN,${totalProjectPrice}\r\n`;

    const encodedUri = encodeURI(csvContent);
    const link = document.createElement("a");
    link.setAttribute("href", encodedUri);
    link.setAttribute("download", `BOQ_${activeProject.name.replace(/\s+/g, "_")}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    addToast("Xuất bảng tính BOQ thành công!", "success");
  };

  // Save Draft Action
  const handleSaveDraft = () => {
    addToast("Tính năng lưu cấu hình đang tạm khóa ở chế độ Read-only", "info");
  };

  // Helper values for summary cards
  const totalCamerasCount = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.camerasCount, 0) || 0;
  const totalDomeCount = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.domeCount, 0) || 0;
  const totalBulletCount = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.bulletCount, 0) || 0;
  const totalSw24 = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.sw24Count, 0) || 0;
  const totalSw16 = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.sw16Count, 0) || 0;
  const totalUPS1K = activeTower?.floorsData?.filter((f) => f.upsType === "1K").length || 0;
  const totalUPS2K = activeTower?.floorsData?.filter((f) => f.upsType === "2K").length || 0;
  const totalPDU = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.pduCount, 0) || 0;
  const totalConv = activeTower?.floorsData?.reduce((acc, curr) => acc + curr.convCount, 0) || 0;
  const totalRacks = activeTower?.floorsData?.filter((f) => f.isCabinetPlaced).length || 0;

  // Helper values for Summary BOM dashboard
  const selectedTowersForSummary = activeProject?.towers?.filter(t => selectedTowersSummary[t.id]) || [];
  
  const summaryTotalCamerasCount = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.camerasCount || 0), 0) || 0), 0);
  const summaryTotalDomeCount = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.domeCount || 0), 0) || 0), 0);
  const summaryTotalBulletCount = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.bulletCount || 0), 0) || 0), 0);
  const summaryTotalSw24 = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.sw24Count || 0), 0) || 0), 0);
  const summaryTotalSw16 = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.sw16Count || 0), 0) || 0), 0);
  const summaryTotalRacks = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.filter(f => f.isCabinetPlaced).length || 0), 0);
  const summaryTotalUPS1K = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.filter(f => f.isCabinetPlaced && f.upsType === "1K").length || 0), 0);
  const summaryTotalUPS2K = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.filter(f => f.isCabinetPlaced && f.upsType === "2K").length || 0), 0);
  const summaryTotalPDU = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.pduCount || 0), 0) || 0), 0);
  const summaryTotalConv = selectedTowersForSummary.reduce((acc, t) => acc + (t.floorsData?.reduce((fAcc, curr) => fAcc + (curr.convCount || 0), 0) || 0), 0);
  const stickyHeaderStyle: React.CSSProperties = {
    position: 'sticky',
    top: '64px',
    zIndex: 20,
    backgroundColor: 'inherit',
  };

  return (
    <div className="min-h-screen bg-[#F5F7F9] font-sans text-[#191c1e] antialiased flex flex-col selection:bg-[#1A237E]/10 selection:text-[#1A237E]">
      
      {/* Dynamic Toast Alert System */}
      <div className="fixed top-4 right-4 z-50 flex flex-col gap-2 max-w-sm">
        <AnimatePresence>
          {toasts.map((t) => (
            <motion.div
              key={t.id}
              initial={{ opacity: 0, y: -20, scale: 0.95 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className={`px-4 py-3 rounded-lg shadow-lg border text-sm flex items-center gap-3 backdrop-blur-md ${
                t.type === "success" 
                  ? "bg-[#E8EAF6]/95 border-[#1A237E]/20 text-[#1A237E]" 
                  : t.type === "error" 
                  ? "bg-red-50/95 border-red-200 text-red-800" 
                  : "bg-slate-50/95 border-slate-200 text-slate-800"
              }`}
            >
              {t.type === "success" && <CheckCircle2 className="w-5 h-5 flex-shrink-0 text-[#1A237E]" />}
              {t.type === "error" && <AlertCircle className="w-5 h-5 flex-shrink-0 text-red-600" />}
              {t.type === "info" && <Info className="w-5 h-5 flex-shrink-0 text-slate-600" />}
              <span>{t.message}</span>
            </motion.div>
          ))}
        </AnimatePresence>
      </div>

      {/* Main App Header */}
      <Header
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        projectsCount={projects.length}
      />

      {/* Main Area layout */}
      <div className="flex-1 max-w-none w-full mx-auto px-6 py-6 flex gap-6">
        {projects.length === 0 && activeTab === "app" ? (
          <div className="flex-1 flex flex-col items-center justify-center min-h-[400px] py-12">
            <div className="bg-white border border-[#ECEFF1] rounded-2xl p-8 max-w-md w-full shadow-lg text-center flex flex-col items-center gap-6">
              <div className="w-16 h-16 rounded-full bg-[#1A237E]/10 flex items-center justify-center text-[#1A237E]">
                <Building className="w-8 h-8" />
              </div>
              <div>
                <h2 className="font-bold text-xl text-[#191c1e] mb-2">Chưa có dự án nào</h2>
                <p className="text-sm text-[#455A64]">
                  Vui lòng tạo một dự án mới trước để bắt đầu thêm các tháp và tính toán BOQ.
                </p>
              </div>
              <button
                onClick={() => {
                  setActiveTab("projects");
                  setIsCreatingProject(true);
                }}
                className="w-full bg-[#1A237E] hover:bg-[#1A237E]/95 text-white font-semibold py-2.5 px-4 rounded-lg shadow-md transition flex items-center justify-center gap-2"
              >
                <Plus className="w-5 h-5" />
                <span>Tạo Dự Án Mới</span>
              </button>
            </div>
          </div>
        ) : (
          <main className="flex-1 flex flex-col gap-6 min-w-0">
            {activeTab === "app" && (
              <WorkspaceTab
                activeProject={activeProject}
                isSummaryTabActive={isSummaryTabActive}
                setIsSummaryTabActive={setIsSummaryTabActive}
                activeTowerId={activeTowerId}
                setActiveTowerId={setActiveTowerId}
                handleDeleteTower={handleDeleteTower}
                handleCreateTower={handleCreateTower}
                tempFloors={tempFloors}
                setTempFloors={setTempFloors}
                tempBasements={tempBasements}
                setTempBasements={setTempBasements}
                tempHasRoof={tempHasRoof}
                setTempHasRoof={setTempHasRoof}
                tempH={tempH}
                setTempH={setTempH}
                tempV={tempV}
                setTempV={setTempV}
                tempRack={tempRack}
                setTempRack={setTempRack}
                tempQuantity2U={tempQuantity2U}
                setTempQuantity2U={setTempQuantity2U}
                handleRecalculate={handleRecalculate}
                handleResetBOQ={handleResetBOQ}
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
                setLeftWidth={setLeftWidth}
                isDragging={isDragging}
                setIsDragging={setIsDragging}
                totalCamerasCount={totalCamerasCount}
                totalDomeCount={totalDomeCount}
                totalBulletCount={totalBulletCount}
                totalSw24={totalSw24}
                totalSw16={totalSw16}
                totalRacks={totalRacks}
                totalUPS1K={totalUPS1K}
                totalUPS2K={totalUPS2K}
                totalPDU={totalPDU}
                totalConv={totalConv}
                selectedTowersSummary={selectedTowersSummary}
                setSelectedTowersSummary={setSelectedTowersSummary}
                isCalculatingSummary={isCalculatingSummary}
                handleCalculateSummary={handleCalculateSummary}
                isExportingExcel={isExportingExcel}
                handleExportExcel={handleExportExcel}
                summaryBomData={summaryBomData}
                stickyHeaderStyle={stickyHeaderStyle}
                API_BASE={API_BASE}
                DEFAULT_SITE_PARAMS={DEFAULT_SITE_PARAMS}
                DEFAULT_HARDWARE_LOGIC={DEFAULT_HARDWARE_LOGIC}
                calculationMode={calculationMode}
                setCalculationMode={setCalculationMode}
                fetchProjects={fetchProjects}
                calculateProjectBOQ={calculateProjectBOQ}
              />
            )}

            {activeTab === "projects" && (
              <ProjectsTab
                projects={projects}
                activeProjectId={activeProjectId}
                setActiveProjectId={setActiveProjectId}
                setActiveTab={setActiveTab}
                isCreatingProject={isCreatingProject}
                setIsCreatingProject={setIsCreatingProject}
                newProjectName={newProjectName}
                setNewProjectName={setNewProjectName}
                newProjectFloors={newProjectFloors}
                setNewProjectFloors={setNewProjectFloors}
                newProjectDesc={newProjectDesc}
                setNewProjectDesc={setNewProjectDesc}
                newProjectPreset={newProjectPreset}
                setNewProjectPreset={setNewProjectPreset}
                BASE_PRESETS={BASE_PRESETS}
                handleCreateProject={handleCreateProject}
                handleDeleteProject={handleDeleteProject}
                addToast={addToast}
              />
            )}

            {activeTab === "standards" && (
              <StandardsTab
                activeTower={activeTower}
                BASE_PRESETS={BASE_PRESETS}
                handleSelectPreset={handleSelectPreset}
              />
            )}

            {activeTab === "settings" && (
              <SettingsTab
                systemConfig={systemConfig}
                setSystemConfig={setSystemConfig}
                isLoadingConfig={isLoadingConfig}
                isSavingConfig={isSavingConfig}
                handleSaveConfig={handleSaveConfig}
                productTypes={productTypes}
                selectedProductTypeId={selectedProductTypeId}
                setSelectedProductTypeId={setSelectedProductTypeId}
                newProductName={newProductName}
                setNewProductName={setNewProductName}
                newProductDesc={newProductDesc}
                setNewProductDesc={setNewProductDesc}
                isCreatingProduct={isCreatingProduct}
                handleCreateProduct={handleCreateProduct}
                tempCategories={tempCategories}
                setTempCategories={setTempCategories}
                isSavingBOMConfig={isSavingBOMConfig}
                handleSaveBOMConfig={handleSaveBOMConfig}
                activeSettingsTab={activeSettingsTab}
                setActiveSettingsTab={setActiveSettingsTab}
                dynamicCategories={dynamicCategories}
                setDeletedProductTypeIds={setDeletedProductTypeIds}
                fetchSystemConfig={fetchSystemConfig}
                flattenTempCategories={flattenTempCategories}
                updateProductTypeField={updateProductTypeField}
                addProductTypeAfter={addProductTypeAfter}
                deleteProductType={deleteProductType}
              />
            )}
          </main>
        )}
      </div>

      {/* Modal Cấu hình tủ điện và Phân bổ camera */}
      <CabinetConfigModal
        editingCabinetIndex={editingCabinetIndex}
        setEditingCabinetIndex={setEditingCabinetIndex}
        activeTower={activeTower}
        tempCabinets={tempCabinets}
        setTempCabinets={setTempCabinets}
        selectedAllocIds={selectedAllocIds}
        setSelectedAllocIds={setSelectedAllocIds}
        setLastSelectedAllocId={setLastSelectedAllocId}
        handleToggleSelectAlloc={handleToggleSelectAlloc}
        manualGroups={manualGroups}
        setManualGroups={setManualGroups}
        syncFloorsWithManualGroups={syncFloorsWithManualGroups}
        updateTowerFloorsData={updateTowerFloorsData}
        addToast={addToast}
      />

      {/* Modal Chi tiết liên kết và phân bổ camera (Manual Mode) */}
      <FloorConnectionDetailModal
        viewingFloorConnectionDetail={viewingFloorConnectionDetail}
        setViewingFloorConnectionDetail={setViewingFloorConnectionDetail}
        activeTower={activeTower}
        getFloorConnections={getFloorConnections}
        manualGroups={manualGroups}
      />

      {/* App Footer */}
      <footer className="bg-white border-t border-[#ECEFF1] py-4 mt-12">
        <div className="max-w-none w-full mx-auto px-6 flex flex-col sm:flex-row justify-between items-center text-xs text-[#455A64] gap-2">
          <div>
            &copy; 2026 Surveillance BOQ Engine. Bản quyền thuộc Ban Nghiên cứu Phát triển Kỹ thuật Công nghệ.
          </div>
          <div className="flex items-center gap-4">
            <a href="#" className="hover:underline hover:text-[#1A237E]" onClick={(e) => { e.preventDefault(); addToast("Hệ thống hoạt động mượt mà", "info"); }}>Hệ thống</a>
            <a href="#" className="hover:underline hover:text-[#1A237E]" onClick={(e) => { e.preventDefault(); addToast("Chính sách bảo mật nội bộ", "info"); }}>Chính sách</a>
            <span className="text-slate-300">|</span>
            <span className="font-mono text-[11px] text-slate-400">V.2026.06.26_UTC</span>
          </div>
        </div>
      </footer>

    </div>
  );
}
