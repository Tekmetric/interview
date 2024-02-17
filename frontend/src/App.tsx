import './App.css';
import { ResultType } from './types/apiData';
import axiosClient from './services/axios';
import { useRequestProcessor } from './services/reactQuery';

function App() {
  const { query } = useRequestProcessor();

  const {
    data,
    isLoading,
    isError
  } = query<ResultType>(
    'cars',
    () => axiosClient.get('/getallmanufacturers').then((res) => res.data),
    {
      enabled: true
    }
  );

  if (isLoading) return <p>Loading...</p>;
  if (isError) return <p>Error :(</p>;

  console.log(data);

  return (
    <section className="flex flex-1 flex-col">
      <h1 className="text-2xl md:text-6xl text-black">App Component</h1>
      <span>{data?.Count}</span>
      {data && data.Results.map((el) => {
        return (
            <span>{el.Mfr_CommonName}</span>
        );
      })}
    </section>
  );
}

export default App;
