type DataResponse<T> = {
  data: T
}

type PagedResponse<T> = {
  data: T[]
  page: number
  totalPages: number
  totalCount: number
}

export type { DataResponse, PagedResponse }
