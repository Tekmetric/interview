import React from 'react';
import { Container } from '@mui/material';

import Footer from './Footer';
import Header from './Header';
import { PageComponent } from '../interfaces/components';

const Page: React.FC<PageComponent> = ({
  children,
  hideFooter = false,
  hideHeader = false
}) => {
  return (
    <Container disableGutters className="flex flex-1 flex-col font-roboto">
      <Container disableGutters component="section" sx={{ height: '5vh' }}>
        {!hideHeader && <Header />}
      </Container>
      <Container disableGutters component="section" sx={{ minHeight: '90vh' }}>
        {children}
      </Container>
      <Container disableGutters component="section" sx={{ height: '5vh' }}>
        {!hideFooter && <Footer />}
      </Container>
    </Container>
  );
};

export default Page;
