import { API_HOST } from "../configs/apis";

export const fetchProducts = async (page: number = 1) => {
  const res = await fetch(`${API_HOST}/products?page=${page}`);
  if (!res.ok) throw new Error("Failed to fetch products");
  const data = await res.json();
  return {
    products: data.data,
    total: data.total,
    page: data.page,
    pages: data.pages,
  };
};
