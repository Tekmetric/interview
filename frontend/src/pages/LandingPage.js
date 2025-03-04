import React from 'react';
import { useLandingPage } from '../hooks/useLandingPage';
import ColumnList from '../components/ColumnList';
import { Loader } from '../components/Loader';

const LandingPage = () => {
  const { data: landingPageData, isLoading } = useLandingPage();

  return (
    <div className='flex flex-col justify-between min-h-screen'>
      {isLoading && <Loader />}
      <div className='flex mx-6 [&>*:nth-child(2n)]:mx-6'>
        {!isLoading && landingPageData.map((columnData, idx) => <ColumnList key={idx} columnListData={columnData} /> )}
      </div>
    </div>
  );
}

export default LandingPage;
