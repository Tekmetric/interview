export interface PaginatedResponsePayload<T> {
  data: T[];
  items: number;
  pages: number;
}
