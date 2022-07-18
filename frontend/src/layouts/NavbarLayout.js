import React from 'react';
import { PropTypes } from 'prop-types';
import NavBar from '../components/navBar/NavBar';

import './NavbarLayout.scss';

function NavbarLayout({ children }) {
  return (
    <>
      <NavBar />
      <main className="main">
        { children }
      </main>
    </>
  );
}

NavbarLayout.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node,
  ]).isRequired,
};

export default NavbarLayout;
