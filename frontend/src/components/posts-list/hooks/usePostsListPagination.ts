import { useState } from "react";

export function usePostsListPagination() {
  const [currentPage, setCurrentPage] = useState(1);

  const handleChangePage = (
    _event: React.ChangeEvent<unknown>,
    newPage: number
  ) => {
    setCurrentPage(newPage);
  };

  return {
    currentPage,
    handleChangePage,
  };
}
