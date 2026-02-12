import { useEffect } from "react";
import { useProductStore } from "../store/useProductStore";
import Loader from "../components/Loader";
import ProductsList from "../components/ProductsList";
import PaginationBar from "../components/PaginationBar";
import ProductDrawer from "../components/ProductDrawer";

export default function ProductsGrid() {
  const page    = useProductStore((s) => s.page);
  const pages   = useProductStore((s) => s.pages);
  const status  = useProductStore((s) => s.status); 
  const setPage = useProductStore((s) => s.setPage);

  const fetchPage = useProductStore.getState().fetchPage;

  useEffect(() => {
    fetchPage(page);
  }, [page]);

  const handlePageChange = (_: React.ChangeEvent<unknown>, value: number) => {
    setPage(value);             
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  return (
    <div className="p-6 relative">
      {status === "loading" ? <Loader /> : <ProductsList />}

      <PaginationBar page={page} pages={pages} onChange={handlePageChange} />

      <ProductDrawer
        open={useProductStore((s) => s.openDrawer)}
        onClose={useProductStore((s) => s.closeDrawer)}
        product={useProductStore((s) => s.selectedProduct)}
      />
    </div>
  );
}
