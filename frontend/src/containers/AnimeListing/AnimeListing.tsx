import React, { FC } from 'react';
import { useParams } from 'react-router';

import { AnimeListingContainer, CardContainer, Details } from './AnimeListing.styled';
import NotFound from '../NotFound/NotFound';
import { useGetAnimeByIdQuery } from '../../api/api.slice';

export const AnimeListing: FC = () => {
  const params = useParams();
  const { data: anime, error } = useGetAnimeByIdQuery(params.id, { skip: !params.id });

  if (!params.id) {
    return <NotFound title="You must enter a valid anime id" />;
  }

  if (error) {
    return <h2>An error occured. Try again later</h2>;
  }

  return (
    <AnimeListingContainer>
      <CardContainer>
        <img src={anime?.data?.images?.jpg?.image_url} alt="Anime" />
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
