import { create } from "zustand";
import { fetchProducts } from "../api/productsApi";
import type { ProductStore } from "../types/ProductStore";

export const useProductStore = create<ProductStore>()((set) => ({
  products: [],
  page: 1,
  pages: 1,
  status: "idle",
  error: null,
  selectedProduct: null,
  openDrawer: false,

  async fetchPage(page: number) {
    set({ status: "loading", error: null });

    try {
      const data = await fetchProducts(page);
      set({
        products: data.products,
        pages: data.pages,
        status: "success",
        error: null,
      });
    } catch (e: any) {
      set({ status: "error", error: e?.message ?? "Failed to load products" });
    }
  },

  setPage(page: number) {
    set({ page });
  },

  selectProduct(p) {
    set({ selectedProduct: p, openDrawer: true });
  },

  closeDrawer() {
    set({ selectedProduct: null, openDrawer: false });
  },
}));
