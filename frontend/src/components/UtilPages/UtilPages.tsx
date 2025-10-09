import React, { FC } from 'react';
import { UtilPagesContainer } from './UtilPages.styled';

type UtilsPageProps = {
  type: UtilPageType;
  message?: string;
};

const UTILS_PAGE_TYPE = {
  ERROR: {
    message: 'Unexpected error. Please try again later.',
    imageSrc: '/images/error_icon.png',
    altText: 'Error',
  },
  NOT_FOUND: {
    message: 'No items found',
    imageSrc: '/images/empty_icon.png',
    altText: 'Not Found',
  },
};

type UtilPageType = keyof typeof UTILS_PAGE_TYPE;

export const UtilsPage: FC<UtilsPageProps> = ({ type, message }) => {
  const { imageSrc, altText, message: defaultMessage } = UTILS_PAGE_TYPE[type];

  return (
    <UtilPagesContainer>
      <img src={imageSrc} alt={altText} style={{ width: 150 }} />
      {message || defaultMessage}
    </UtilPagesContainer>
  );
};

export default UtilsPage;
