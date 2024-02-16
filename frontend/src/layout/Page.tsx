import React from 'react';
import Footer from './Footer';
import Header from './Header';
import ReactComponent from '../interfaces/ReactChildrenProps';

const Page: React.FC<ReactComponent> = ({
  children,
  props: { hideFooter, hideHeader } = { hideFooter: false, hideHeader: false }
}) => {
  return (
    <section className="flex flex-1 flex-col font-roboto">
      {!hideHeader && <Header />}
      {children}
      {!hideFooter && <Footer />}
    </section>
  );
};

export default Page;
