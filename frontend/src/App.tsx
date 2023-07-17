import React, { useEffect, useState, useRef } from 'react';
import { Container, Header, type Dog, type DogLists } from './components';
import { useFetch } from './helpers/useFetch';

const App = () => {
  const [page, setPage] = useState<number>(0);
  const dogs = useRef<DogLists>({ afterPet: [], beforePet: [] });

  const {
    data,
    loading,
    error,
  }: { data: Array<Dog>; loading: boolean; error: string | null } = useFetch(
    `https://api.thedogapi.com/v1/breeds?limit=5&page=${page}`
  );

  const addDogs = () => {
    const randNumber = Math.floor(Math.random() * 30);
    console.log(randNumber);
    setPage(randNumber);
  };

  useEffect(() => {
    if (data && !loading && !error) {
      const foundDuplicate = dogs.current.beforePet.find(
        (dog: Dog) => dog.id === data[0].id
      );
      if (!foundDuplicate) {
        const newDogs = [...dogs.current.beforePet, ...data];
        console.log(newDogs);

        dogs.current = { beforePet: newDogs, afterPet: dogs.current.afterPet };
      } else {
        console.log('Duplicate found, retrying...');
        // This is commented because on dev it will fetch twice
        // Since React 18, tries to double render when StrictMode is on
        // setPage(Math.floor(Math.random() * 30));
      }
    }
  }, [data, loading, error, dogs]);

  const onDragEnd = (element: any) => { };

  return (
    <div className="relative mx-[100px] flex min-h-screen flex-col justify-center py-6 sm:py-12">
      <div className="relative mx-auto min-w-full rounded-xl bg-gray-600 bg-opacity-50 px-6 pb-8 pt-10 shadow-xl ring-1 ring-gray-900/5 sm:px-1">
        <Header onAddDogsClick={addDogs} />
        <div className="flex gap-[100px] justify-center max-w-full">
          <Container list={dogs.current} onDragEnd={onDragEnd} />
        </div>
      </div>
    </div>
  );
};

export default App;
