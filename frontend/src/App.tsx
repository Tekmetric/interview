import { CircularProgress, Grid } from '@mui/material';

import './App.css';
import axiosClient from './services/axios';
import { useRequestProcessor } from './services/reactQuery';
import CardItem from './components/Card';
import { ManufacturersRespData } from './interfaces/api';

function App() {
  const { query } = useRequestProcessor();

  const { data, isLoading } = query<ManufacturersRespData>(
    'manufacturers',
    () => axiosClient.get('/getallmanufacturers').then((res) => res.data),
    {
      enabled: true
    }
  );

  if (isLoading) return <CircularProgress />;

  return (
    <section className="flex flex-1 flex-col">
      <h1 className="text-center text-2xl sm:text-4xl md:text-6xl text-black">
        Car manufacturers
      </h1>

      <Grid container spacing={6} padding={4}>
        {data &&
          data.Results.map((el) => {
            return  el.Mfr_CommonName && (
              <Grid
                key={el.Mfr_ID}
                item
                xs={6}
                md={4}
                lg={3}
                justifySelf="center"
              >
                <CardItem cardData={el} />
              </Grid>
            );
          })}
      </Grid>
    </section>
  );
}

export default App;
