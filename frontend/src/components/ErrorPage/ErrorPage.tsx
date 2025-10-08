import React, { FC } from 'react';
import { ErrorPageContainer } from './ErrorPage.styled';

type ErrorPageProps = {
  message?: string;
};

export const ErrorPage: FC<ErrorPageProps> = ({ message = 'Unexpected error. Please try again later.' }) => {
  return (
    <ErrorPageContainer>
      <img src="/images/error_icon.png" alt="error" style={{ width: 150 }} />
      {message}
    </ErrorPageContainer>
  );
};

export default ErrorPage;
