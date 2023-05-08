import { Link } from "react-router-dom";

const ArtObject = (props) => {
  const { title, artist, image, id } = props;

  return (
    <div className="flex justify-center items-center">
      <Link to={`/details/${id}`}>
        <div className="mx-auto">
          <img src={image} alt={title} />
        </div>
        <div>
          <h1 className="text-center bg-teal-500 text-white">{title}</h1>
          <h2>{artist}</h2>
        </div>
      </Link>
    </div>
  );
};

export default ArtObject;
