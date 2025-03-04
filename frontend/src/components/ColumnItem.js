import React from 'react';
import { Link } from 'react-router-dom';

const ColumnItem = ({ data, index }) => {
  const { images, title, type, episodes, score, members } = data;
  const numberOfEpisodes = episodes ? episodes : 0;
  const displayScore = score ? score : 'N/A';

  return (
    <Link to={`/${data.mal_id}`} className='flex mb-4 h-32 hover:cursor-pointer hover:bg-slate-200 p-3'>
      <span className='font-bold text-4xl text-slate-600 w-12 min-w-12 flex items-center justify-center max-h-24 pr-2'>{index}</span>
      <img src={images.jpg.small_image_url} className='min-w-16 max-w-16 min-h-24 max-h-24 mr-2' alt="anime-image" />
      <div className='flex flex-col max-w-72 max-h-24'>
        <span className='font-bold text-base'>{title}</span>
        <p>{type}, {numberOfEpisodes} EPS, scored {displayScore}</p>
        <p>{members.toLocaleString()} members</p>
      </div>
    </Link>
  );
}

export default ColumnItem;
