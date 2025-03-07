import Book from "../types/book";
import { DEFAULT_PAGE_SIZE, HTTP_DELETE, HTTP_POST } from "../util/constants";
import { getParamStr } from "../util/utils";
import callApi from "./apiService";

export const getBooks = async (keyword: string = "", page: number = 0, size: number = DEFAULT_PAGE_SIZE, orderBy: string = "", orderDirection: string = "") => {
    const paramStr = getParamStr(keyword, page, size, orderBy, orderDirection);
    return callApi(`/books?${paramStr}`);
};

export const saveBook = async(book : Book) => {
    return callApi('/books', HTTP_POST, book);
}

export const deleteBook = async(id : number) => {
    return callApi(`/books/${id}`, HTTP_DELETE, id);
}
