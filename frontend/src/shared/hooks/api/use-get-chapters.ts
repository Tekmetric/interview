import { useQuery } from '@tanstack/react-query';
import { ChapterType } from '../../types/chapter';

const baseUrl = process.env.REACT_APP_API_BASE_URL;
const apiPath = '/api/v1/chapters?access_token';

export const useGetChapters = (accessToken: string) => {
  return useQuery({
    queryKey: ['query:chapters'],
    queryFn: async () => {
      console.log('fetch');
      const response = await fetch(`${baseUrl}${apiPath}=${accessToken}`);

      if (!response.ok) {
        throw new Error('Failed to fetch chapters list.');
      }

      const chapters: ChapterType[] = await response.json();
      return chapters;
    },
  });
};
