import { useFetch } from '../helpers/useFetch';

const Column = () => {
  const {
    data: dogs,
    loading,
    error,
  } = useFetch('https://api.thedogapi.com/v1/breeds?limit=10&page=1');

  return (
    <>
      {loading && <div>Loading...</div>}
      {error && <div>Error: {error}</div>}
      {dogs && (
        <div>
          {dogs.map((dog: any) => {
            return (
              <div key={dog.id}>
                <span>{dog.name}</span>
                <img src={dog.image?.url} alt={dog.name}></img>
              </div>
            );
          })}
        </div>
      )}
    </>
  );
};

export default Column;
