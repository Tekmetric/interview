import { useContext, useEffect, useState } from 'react';
import { GlobalContext } from '../../shared/context/global';
import { useGetChapters } from '../../shared/hooks/api/use-get-chapters';
import Chapter from '../../features/chapters/chapter';
import { ChapterType } from '../../shared/types/chapter';
import { Typography } from '@mui/material';
import Loader from '../../components/loader';
import BackToTop from '../../components/back-to-top';
import './chapter-list.css';
import Logout from '../../components/logout';

const ChapterList = () => {
  const { globalState } = useContext(GlobalContext);
  const storageToken = localStorage.getItem('token') ?? '';

  const [chapters, setChapters] = useState<ChapterType[]>();

  const { data, refetch: getChapters, isLoading, isError } = useGetChapters(globalState.token || storageToken);

  useEffect(() => {
    getChapters();
  }, []);

  useEffect(() => {
    if (data) {
      setChapters(data);
    }
  }, [data]);

  return (
    <>
      <header>
        <Typography variant="h1" component="h1" className="text-6xl text-center mb-12 py-6 bg-slate-300">
          Bhagavad Gita Chapters
        </Typography>
        <Logout />
      </header>
      <div className="chapter-list-wrapper">
        {isError && <p>Failed to load chapters</p>}
        {isLoading ? (
          <Loader />
        ) : (
          chapters?.map((chapter: ChapterType, index: number) => {
            return (
              <div key={index}>
                <Chapter
                  chapter_number={chapter.chapter_number}
                  name_meaning={chapter.name_meaning}
                  chapter_summary={chapter.chapter_summary}
                />
              </div>
            );
          })
        )}
        <BackToTop />
      </div>
    </>
  );
};

export default ChapterList;
