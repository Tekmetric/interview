import Author from "./author";

export default interface Book {
    id: number | null;
    name: string | null;
    publicationYear: number | null;
    author: Author | null;
}
  
export interface PageOfBooks {
    currentPage: number;
    totalPage: number;
    totalItems: number;
    books : Book[];
}