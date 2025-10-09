import React, { FC } from 'react';
import { Link } from 'react-router';

import { NotFoundContainer } from './NotFound.styled';
import { NotFoundProps } from './NotFound.types';

export const NotFound: FC<(NotFoundProps)> = ({ title = 'Ooops! Resource not found' }) => {
  return (
    <NotFoundContainer>
      <h2>{title}</h2>
      <Link to="/">Go back</Link>
    </NotFoundContainer>
  );
};

export default NotFound;
