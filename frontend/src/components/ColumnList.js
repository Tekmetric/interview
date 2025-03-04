import React from 'react';
import ColumnItem from './ColumnItem';
import { Link } from 'react-router-dom';

const ColumnList = ({ columnListData }) => {
  const { data: columnData, filter, title } = columnListData;
  const columnFilter = filter ? filter : 'all-time';

  return (
    <div className='w-1/3 bg-slate-100'>
      <header className='flex justify-between bg-slate-300 h-8 px-3 font-bold mb-6'>
        <span>{title}</span>
        <Link to={`/top/${columnFilter}`} className='hover:underline'>More</Link>
      </header>
      <ul className='flex flex-col'>
        {columnData.map((anime, index) => <ColumnItem key={index} data={anime} index={index + 1} /> )}
      </ul>
    </div>
  );
}

export default ColumnList;
