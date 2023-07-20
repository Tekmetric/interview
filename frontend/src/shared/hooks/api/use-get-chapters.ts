import { useQuery } from '@tanstack/react-query';
import { ChapterType } from '../../types/chapter';

export const useGetChapters = (accessToken: string) => {
  console.log('use-get-chapters');
  return useQuery({
    queryKey: ['query:chapters'],
    queryFn: async () => {
      console.log('fetch');
      const response = await fetch(`${process.env.REACT_APP_API_BASE_URL}/api/v1/chapters?access_token=${accessToken}`);

      if (!response.ok) {
        throw new Error('Failed to fetch chapters list.');
      }

      const chapters: ChapterType[] = await response.json();
      return chapters;
    },
  });
};
