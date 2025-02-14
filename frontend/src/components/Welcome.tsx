import { Subway } from '@phosphor-icons/react';

export default function Welcome() {
  return (
    <div className="bg-sky-50 dark:bg-gray-700 p-4 m-4 shadow-md rounded-md text-center">
      <Subway className="mx-auto" size={ 128 } />
      <h2 className="text-3xl">Welcome to Metro Buddy!</h2>
      <p className="mt-2">
        This is a simple app that helps you find the status of your favorite stations and lines of the DC Metro system, showing the next arrival from the WMATA API.
      </p>
      <p className="mt-2">
        Use the search bar in the top right to find your station, or press ⌘/ctrl + k to open the station selector.
      </p>
    </div>
  );
}
