import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import notFound from "../assets/notfound.jpg";
import LoadingAnimation from "./LoadingAnimation";
import fetchArtDetails from "../apiMethods/fetchArtDetails";

const ArtObjectDetails = () => {
  const { id } = useParams();
  const results = useQuery(["details", id], fetchArtDetails);
  const artDetail = results?.data?.artObject;

  return results.isLoading ? (
    <LoadingAnimation />
  ) : (
    <div className="m-20">
      <div className="flex items-center">
        <img
          src={artDetail.hasImage ? artDetail.webImage?.url : notFound}
          alt={artDetail.label?.title}
          className="max-w-xl"
        />
        <div className="text-center p-4">
          <div className="p-4 text-3xl">{artDetail.label?.title}</div>
          <h2 className="p-4 text-xl italic">{artDetail.label?.makerLine}</h2>
          <h2 className="p-4">{artDetail.label?.description}</h2>
        </div>
      </div>
    </div>
  );
};

export default ArtObjectDetails;
