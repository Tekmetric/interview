import React from 'react';
import PropTypes from 'prop-types';
import { Navigate } from 'react-router-dom';
import NavbarLayout from '../layouts/NavbarLayout';

function PrivateRoute({ children }) {
  return (localStorage.getItem('user'))
    ? (
      <NavbarLayout>
        {children}
      </NavbarLayout>
    )
    : <Navigate to="/" />;
}
PrivateRoute.propTypes = {
  children: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.node),
    PropTypes.node,
  ]).isRequired,
};

export default PrivateRoute;
