import axios from 'axios';
import { TaskResponse } from '@/store/data/task/types.ts';

// Helper function to simulate a delay
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

const get = async (url: string): Promise<TaskResponse> => {
  await delay(1000);

  const { data } = await axios.get<TaskResponse>(url);

  return data;
};

export default get;
