import ReadingList from "../types/readingList";
import { DEFAULT_PAGE_SIZE, HTTP_DELETE, HTTP_POST, HTTP_PUT } from "../util/constants";
import { getParamStr } from "../util/utils";
import callApi from "./apiService";

export const getReadingLists = async (keyword: string = "", page: number = 0, size: number = DEFAULT_PAGE_SIZE, orderBy: string = "", orderDirection: string = "") => {
    const paramStr = getParamStr(keyword, page, size, orderBy, orderDirection);
    return callApi(`/reading-lists?${paramStr}`);
};

export const getSharedReadingLists = async (keyword: string = "", page: number = 0, size: number = DEFAULT_PAGE_SIZE, orderBy: string = "", orderDirection: string = "") => {
  const paramStr = getParamStr(keyword, page, size, orderBy, orderDirection);
  return callApi(`/reading-lists/shared?${paramStr}`);
};

export const saveReadingList = async (readingList: ReadingList) => {
    return callApi('/reading-lists', readingList.id ? HTTP_PUT : HTTP_POST, createPayload(readingList));
  };

export const deleteReadingList = async (id: number) => {
    return callApi(`/reading-lists/${id}`, HTTP_DELETE);
  };

const createPayload = (readingList: ReadingList) => {
  const payload = {
    id: readingList.id ? readingList.id : null,
    name: readingList.name ? readingList.name : "",
    shared: readingList.shared ? readingList.shared : false,
    bookIds: readingList.books ? readingList.books.map(book => book.id) : []
  };
  return payload;
};
  