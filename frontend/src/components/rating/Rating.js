import React, { useMemo } from 'react';
import { Box, Icon } from '@chakra-ui/react';
import { StarIcon } from '@chakra-ui/icons';

const Rating = ({ rating }) => {
  const { rate, count } = rating;

  const renderStars = useMemo(() => {
    return Array.from({ length: 5 }, (_, index) => (
      <Icon
        key={index}
        as={StarIcon}
        color={index < rate ? 'yellow.500' : 'gray.300'}
      />
    ));
  }, [rate]);

  return (
    <Box>
      {renderStars} ({count})
    </Box>
  );
};

export default Rating;
