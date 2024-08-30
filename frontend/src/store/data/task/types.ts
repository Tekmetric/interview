export interface TaskItemResponse {
  id: string;
  title: string;
  description: string;
  status: string;
  dueDate: string;
  creationDate: string;
}

export interface TaskPayload {
  data: Task[];
  totalPages: number;
  count: number;
  currentPage: number;
  firstPage: number;
  lastPage: number;
  nextPage: number | null;
  prevPage: number | null;
}

export interface TaskResponse {
  data: TaskItemResponse[];
  pages: number;
  items: number;
  first: number;
  last: number;
  next: number | null;
  prev: number | null;
}

export interface Task {
  id: string;
  title: string;
  description: string;
  status: string;
  dueDate: string;
  creationDate: string;
}

export interface TaskState {
  list: TaskPayload;
  isLoading: boolean;
}
