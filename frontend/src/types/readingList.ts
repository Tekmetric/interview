import Book from "./book";
import User from "./user";

export default interface ReadingList {
    id: number | null;
    name: string | null;
    owner: User | null;
    lastUpdate: Date | null;
    shared: boolean | null;
    books: Book[] | null;
}
  
export interface ReadingListRequest {
    id: number | null;
    name: string;
    shared: boolean;
    bookIds: number[] | null;
}