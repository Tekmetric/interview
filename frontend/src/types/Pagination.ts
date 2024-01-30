class Pagination{
    empty: boolean;
    first: boolean;
    last: boolean;
    numberOfElements: number;
    totalElements: number;
    totalPages: number;
    pageSize: number;
    pageNumber: number;

    constructor(pageNumber: number, pageSize: number) {
        this.empty = true;
        this.first = true;
        this.last = true;
        this.numberOfElements = 0;
        this.totalElements = 0;
        this.totalPages = 0;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
    }
}

export default Pagination;