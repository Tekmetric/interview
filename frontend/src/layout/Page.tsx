import React from 'react';
import { Container } from '@mui/material';

import Footer from './Footer';
import Header from './Header';
import { PageComponentI } from '../interfaces/components';

const Page: React.FC<PageComponentI> = ({
  children,
  hideFooter = false,
  hideHeader = false
}) => {
  return (
    <Container maxWidth={false} disableGutters className="flex flex-1 flex-col font-roboto">
      <Container maxWidth={false} disableGutters sx={{ height: '5vh' }}>
        {!hideHeader && <Header />}
      </Container>
      <Container maxWidth={false} disableGutters sx={{ minHeight: '90vh' }}>
        {children}
      </Container>
      <Container maxWidth={false} disableGutters sx={{ height: '5vh' }}>
        {!hideFooter && <Footer />}
      </Container>
    </Container>
  );
};

export default Page;
