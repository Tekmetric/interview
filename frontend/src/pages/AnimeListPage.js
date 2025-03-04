import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { Star } from "@mui/icons-material";

import { AnimeFilterType } from "../utils/constants";
import { Error } from "../components/Error";
import { Loader } from "../components/Loader";
import { useTopAnimeQuery } from "../services/services";

export const AnimeListPage = () => {
  const { filter } = useParams();
  const [ pageNumber, setPageNumber ] = useState(1);
  const { isLoading, data: listData, error} = useTopAnimeQuery(AnimeFilterType[filter], pageNumber);

  const handleNextClick = () => {
    window.scrollTo(0,0);
    setPageNumber((prev) => prev + 1);
  }

  const handlePreviousClick = () => {
    window.scrollTo(0,0);
    setPageNumber((prev) => prev - 1);
  }

  const formattedMonth = (date) => {
    return date.toLocaleString('default', { month: 'short', year: 'numeric' });
  }

  return (
    <div className="flex flex-col">
      {error && <Error />}
      {isLoading && <Loader />}
      {!isLoading && (
        <>
          <div className="flex bg-blue-600 text-white text-center">
            <span className="w-28 p-2">Rank</span>
            <span className="w-full border-x p-2">Title</span>
            <span className="w-28 p-2">Score</span>
          </div>
          <ul className="[&>*:nth-child(2n)]:bg-slate-100">
            {listData.data.map((element, idx) => {
              const fromMonth = formattedMonth(new Date(element.aired.from));
              const toDate = element.aired.to ? new Date(element.aired.to) : '';
              const toMonth = toDate ? formattedMonth(toDate) : '';

              return (
                <li key={idx} className="flex items-center border border-t-0">
                  <span className="w-28 text-center text-4xl text-slate-600 font-bold">{idx + 1 + (pageNumber - 1)*25}</span>
                  <div className="flex w-full border-x p-4">
                    <img src={element.images.jpg.small_image_url} className='min-w-16 max-w-16 min-h-24 max-h-24 mr-2' alt="anime-image" />
                    <div className='flex flex-col max-w-72'>
                      <Link to={`/${element.mal_id}`} className='font-bold text-base hover:cursor-pointer hover:underline'>
                        {element.title}
                      </Link>
                      <p>{element.type} ({element.episodes} EPS)</p>
                      <p>{fromMonth} - {toMonth}</p>
                      <p>{element.members.toLocaleString()} members</p>
                    </div>
                  </div>
                  <div className="w-28 text-center">
                    <Star className={`mr-2 ${element.score ? 'stroke-orange-200 text-yellow-200' : 'stroke-slate-400 text-slate-300'}`} />
                    <span>{element.score ? element.score : 'N/A'}</span>
                  </div>
                </li>
              )
            })}
          </ul>
          <div className="flex justify-center">
            {
              listData.pagination.current_page > 1 && (
                <button className="bg-blue-600 text-white m-4 p-2" onClick={handlePreviousClick}>
                  {`< Prev 25`}
                </button>
              )
            }
            {
              listData.pagination.has_next_page && (
                <button className="bg-blue-600 text-white m-4 p-2" onClick={handleNextClick}>
                  {`Next 25 >`}
                </button>
              )
            }
          </div>
        </>
      )}
    </div>
  );
}