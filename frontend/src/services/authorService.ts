import Author from "../types/author";
import { DEFAULT_PAGE_SIZE, HTTP_DELETE, HTTP_POST } from "../util/constants";
import { getParamStr } from "../util/utils";
import callApi from "./apiService";

export const getAuthors = async (keyword: string = "", page: number = 0, size: number = DEFAULT_PAGE_SIZE, orderBy: string = "", orderDirection: string = "") => {
    const paramStr = getParamStr(keyword, page, size, orderBy, orderDirection);
    return callApi(`/authors?${paramStr}`);
  };
  
export const getBooksOfAuthor = async(author: Author) => {
    return callApi(`/authors/${author.id}/books`);
}

export const saveAuthor = async(author : Author) => {
  return callApi('/authors', HTTP_POST, author);
}

export const deleteAuthor = async(id : number) => {
  return callApi(`/authors/${id}`, HTTP_DELETE, id);
}
