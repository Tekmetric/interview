import React, { useState } from 'react';
import { Column, Header } from './components';

const App = () => {
  // const [data, setData] = useState<Array<any>>([]);

  return (
    <div className="relative mx-[100px] flex min-h-screen flex-col justify-center py-6 sm:py-12">
      <div className="relative mx-auto min-w-full rounded-xl bg-gray-600 bg-opacity-50 px-6 pb-8 pt-10 shadow-xl ring-1 ring-gray-900/5 sm:px-1">
        <Header />
        <div className='flex gap-[100px]'>
          <Column />
          <Column />
        </div>
      </div>
    </div>
  );
};

export default App;
