import React, { useEffect, useState, useRef, useCallback } from 'react';
import { Container, Header, type Dog, type DogLists } from './components';
import { useFetch } from './helpers/useFetch';
import {
  addElementToList,
  removeElementFromList,
} from './helpers/listOperations';

const App = () => {
  const [dogsPetted, setDogsPetted] = useState<number>(0);

  const [dogs, setDogs] = useState<DogLists>({
    afterPet: [],
    beforePet: [],
  });

  const [inital, setInitial] = useState<boolean>(true);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const addDogs = async () => {
    const randNumber = Math.floor(Math.random() * (30 + 1));

    const data = await fetchData(randNumber);

    if (data) {
      // setDogs();
      const newDogs = [...dogs.beforePet, ...data];
      console.log(newDogs);
      setDogs({
        afterPet: dogs.afterPet,
        beforePet: newDogs,
      });
    }
  };

  const fetchData = useCallback(async (page: number) => {
    const response = await fetch(
      `https://api.thedogapi.com/v1/breeds?limit=5&page=${page}`
    );
    const data = await response.json();
    if (response.status !== 200) {
      setError('Error fetching data.');
    }
    return data as Array<any>;
  }, []);

  useEffect(() => {
    const fetchDogs = async () => {
      const data = await fetchData(0);
      if (data) {
        const newDogs = [...dogs.beforePet, ...data];
        console.log(newDogs);
        setDogs({
          afterPet: dogs.afterPet,
          beforePet: newDogs,
        });
      }
    };
    if (inital) {
      fetchDogs();
      setInitial(false);
    }
  }, [fetchData, inital, dogs]);

  const onDragEnd = (element: any) => {
    // if the element is not dropped in a droppable area, do nothing
    if (!element.destination) {
      console.log(element);
      return;
    }

    const sourceColumnName: 'afterPet' | 'beforePet' =
      element.source.droppableId;
    const destinationColumnName: 'afterPet' | 'beforePet' =
      element.destination.droppableId;
    
    const shallowDogLists: DogLists = { ...dogs };

    const originalColumn = shallowDogLists[sourceColumnName]; 
    const [removedElement, newDogsColumn] = removeElementFromList(
      originalColumn,
      element.source.index
    );
    shallowDogLists[sourceColumnName] = newDogsColumn;

    const destinationList = shallowDogLists[destinationColumnName];
    shallowDogLists[destinationColumnName] = addElementToList(
      destinationList,
      element.destination.index,
      removedElement
    );

    setDogs(shallowDogLists);
    setDogsPetted(shallowDogLists.afterPet.length);
  };

  return (
    <div className="relative mx-[100px] flex min-h-screen flex-col justify-center py-6 sm:py-12">
      <div className="relative mx-auto min-w-full rounded-xl bg-gray-600 bg-opacity-50 px-6 pb-8 pt-10 shadow-xl ring-1 ring-gray-900/5 sm:px-1">
        <Header onAddDogsClick={addDogs} dogsPetted={dogsPetted} />
        <div className="flex max-w-full justify-center gap-[100px]">
          <Container list={dogs} onDragEnd={onDragEnd} />
        </div>
      </div>
    </div>
  );
};

export default App;
