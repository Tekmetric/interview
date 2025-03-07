export default interface Author {
    id: number | null;
    firstName: string | null ;
    lastName: string | null;
    photoUrl: string | null;
}

export interface PageOfAuthors {
    currentPage: number;
    totalPage: number;
    totalItems: number;
    authors : Author[];
}