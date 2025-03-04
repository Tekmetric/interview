import { useParams } from "react-router-dom";

import { AnimeDetails } from "../components/AnimeDetails";
import { AnimeSpecs } from "../components/AnimeSpecs";
import { Error } from "../components/Error";
import { Loader } from '../components/Loader';
import { useIndividualAnime } from "../hooks/useIndividualAnime";

const IndividualAnimePage = () => {
  const { id } = useParams();
  const { data, characters, staff, reviews, isLoading, error } = useIndividualAnime(id);

  return (
    <>
      {error && <Error />}
      {isLoading && <Loader />}
      {!isLoading && (
        <div className="border">
          <div className="bg-slate-200 p-2">
            <h1 className="font-bold text-xl">{data.title}</h1>
            <h2 className="font-bold text-lg text-slate-500">{data.title_english}</h2>
          </div>
          <div className="flex">
            <AnimeSpecs data={data} />
            <AnimeDetails data={data} characters={characters} staff={staff} reviews={reviews} />
          </div>
        </div>
      )}
    </>
  )
};

export default IndividualAnimePage;