import { Task, TaskItemResponse, TaskPayload, TaskResponse } from '@/store/data/task/types.ts';
import dayjs from 'dayjs';

const mapRawTasks = (tasks: TaskItemResponse[]): Task[] =>
  tasks.map(t => {
    return {
      ...t,
      dueDate: dayjs(t.creationDate).format('DD-MM-YYYY'),
      creationDate: dayjs(t.creationDate).format('DD-MM-YYYY'),
    };
  });

export const mapTasks = (data: TaskResponse): TaskPayload => ({
  data: mapRawTasks(data.data),
  count: data.items,
  totalPages: data.pages,
  currentPage: data.next ? data.next - 1 : data.pages,
  firstPage: data.first,
  lastPage: data.last,
  nextPage: data.next,
  prevPage: data.prev,
});
