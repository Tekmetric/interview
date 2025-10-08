import React, { FC } from 'react';
import { EmptyPageContainer } from './EmptyPage.styled';

type EmptyPageProps = {
  message?: string;
};

export const EmptyPage: FC<EmptyPageProps> = ({ message = 'No items found' }) => {
  return (
    <EmptyPageContainer>
      <img src="/images/empty_icon.png" alt="error" style={{ width: 150 }} />
      {message}
    </EmptyPageContainer>
  );
};

export default EmptyPage;
