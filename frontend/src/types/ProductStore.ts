import type { Product } from "../types/Product";
import type { Status } from "../types/Status";

export type ProductStore = {
  products: Product[];
  page: number;
  pages: number;
  status: Status;
  error: string | null;
  selectedProduct: Product | null;
  openDrawer: boolean;

  setPage: (page: number) => void;
  fetchPage: (page: number) => Promise<void>;
  selectProduct: (p: Product) => void;
  closeDrawer: () => void;
};