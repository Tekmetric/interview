import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import Results from "./Results";
import LoadingAnimation from "./LoadingAnimation";
import fetchArt from "../apiMethods/fetchArt";
const AUTHORS = [
  "Rembrandt van Rijn",
  "Johannes Vermeer",
  "Roelant Savery",
  "Gerard van Honthorst",
];

const ArtList = () => {
  const [requestParams, setRequestParams] = useState({
    title: "",
    artist: "",
  });

  const results = useQuery(["artList", requestParams], fetchArt);
  const artObjects = results?.data?.artObjects ?? [];

  return (
    <div className="w-10/12 mx-auto">
      <form
        className="bg-white shadow-md rounded p-8 mb-4 w-10/12 mx-auto grid grid-cols-5 gap-3 items-center"
        onSubmit={(e) => {
          e.preventDefault();
          const formData = new FormData(e.target);
          const obj = {
            artist: formData.get("artist") ?? "",
            title: formData.get("title") ?? "",
          };
          setRequestParams(obj);
        }}
      >
        <label htmlFor="title" className="block text-gray-700 text-sm font-bold mb-2 col-span-2">
          Title
          <input
            id="title"
            name="title"
            placeholder="Title"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
          />
        </label>

        <label htmlFor="artist" className="block text-gray-700 text-sm font-bold mb-2 col-span-2">
          Artist
          <select id="artist" name="artist" className="shadow border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
            <option />
            {AUTHORS.map((artist) => (
              <option key={artist} value={artist}>
                {artist}
              </option>
            ))}
          </select>
        </label>

        <button className="p-10 allign-middle mt-3 bg-transparent hover:bg-teal-500 text-teal-700 font-semibold hover:text-white py-2 px-4 border border-teal-500 hover:border-transparent rounded">
          Search
        </button>
      </form>
      {results?.isLoading ? (
        <LoadingAnimation />
      ) : (
        <Results artObjects={artObjects} />
      )}
    </div>
  );
};

export default ArtList;
