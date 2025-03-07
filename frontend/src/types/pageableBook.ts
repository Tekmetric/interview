import Book from "./book";

export interface PageableBook {
    currentPage: number;
    totalPage: number;
    totalItems: number;
    books : Book[];
}
  