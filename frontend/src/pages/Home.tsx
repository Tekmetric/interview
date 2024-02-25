import { useReducer } from 'react';
import { CircularProgress, Container, Grid } from '@mui/material';

import axiosClient from '../services/axios';
import { useRequestProcessor } from '../services/reactQuery';
import CardWrapper from '../components/cards/CardWrapper';
import ManufacturerCard from '../components/cards/ManufacturerCard';
import Filter from '../components/Filter';
import { ManufacturersRespData } from '../interfaces/api';
import { FiltersI, ReactComponentI } from '../interfaces/components';

const itemsReducer = (state: FiltersI, newState: Partial<FiltersI>) => {
  return {
    ...state,
    ...newState
  };
};

const filterData = (data: ManufacturersRespData, filters: FiltersI) => {
  return data.Results.filter((el) => {
    if (!el.Country || !el.Mfr_CommonName) return false;
    if (
      filters.name &&
      !el.Mfr_CommonName.toLowerCase().includes(filters.name.toLowerCase())
    ) {
      return false;
    }
    if (
      filters.country &&
      !el.Country.toLowerCase().includes(filters.country.toLowerCase())
    ) {
      return false;
    }
    return true;
  });
};

const Home: React.FC<ReactComponentI> = () => {
  const { query } = useRequestProcessor();

  const [filters, setFilters] = useReducer(itemsReducer, {
    name: '',
    country: '',
    tags: []
  });


  const { data, isLoading } = query<ManufacturersRespData>(
    ['manufacturers'],
    () => axiosClient.get('/getallmanufacturers').then((res) => res.data),
    { enabled: true }
  );

  if (isLoading) return <CircularProgress />;

  const filteredData = (data && filterData(data, filters)) || [];

  return (
    <Container className="flex flex-1 flex-col">
      <h1 className="text-center text-2xl sm:text-4xl md:text-6xl text-black">
        Car manufacturers
      </h1>

      <Filter filters={filters} setFilters={setFilters} />

      <Grid container spacing={6} padding={4} marginTop={0}>
        {filteredData.map((el) => {
          return (
            el.Mfr_CommonName && (
              <Grid
                key={el.Mfr_ID}
                item
                xs={6}
                md={4}
                lg={3}
                justifySelf="center"
              >
                <CardWrapper children={<ManufacturerCard cardData={el} />} />
              </Grid>
            )
          );
        })}
      </Grid>
    </Container>
  );
};

export default Home;
