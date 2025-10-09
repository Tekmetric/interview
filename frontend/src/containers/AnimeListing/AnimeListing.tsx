import React, { FC } from 'react';
import { useParams } from 'react-router';
import Skeleton from '@mui/material/Skeleton';

import UtilPages from '../../components/UtilPages/UtilPages';
import { AnimeListingContainer, CardContainer, Details } from './AnimeListing.styled';
import NotFound from '../NotFound/NotFound';
import { useGetAnimeByIdQuery } from '../../states/api/api.slice';

export const AnimeListing: FC = () => {
  const params = useParams();
  const { data: anime, error, isFetching } = useGetAnimeByIdQuery(params.id, { skip: !params.id });

  if (!params.id) {
    return <NotFound title="You must enter a valid anime id" />;
  }

  if (error) {
    return (
      <AnimeListingContainer>
        <CardContainer>
          <UtilPages type={'status' in error && error?.status === 404 ? 'NOT_FOUND' : 'ERROR'} />
        </CardContainer>
      </AnimeListingContainer>
    );
  }

  if (isFetching) {
    return (
      <AnimeListingContainer>
        <CardContainer>
          <Skeleton variant="rounded" width={240} height={360} />
          <Details>
            <Skeleton variant="text" sx={{ fontSize: '20px' }} />
          </Details>
        </CardContainer>
      </AnimeListingContainer>
    );
  }

  return (
    <AnimeListingContainer>
      <CardContainer>
        <img src={anime?.data?.images?.jpg?.image_url} alt="Anime" style={{ width: 240, height: 360 }} />
        <Details>
          <h2>
            {anime?.data.title}
            {anime?.data.title_japanese && ` (${anime.data.title_japanese})`}
          </h2>
        </Details>
      </CardContainer>
    </AnimeListingContainer>
  );
};

export default AnimeListing;
