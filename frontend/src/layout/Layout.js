import React from "react";
import PropTypes from "prop-types";
import Header from "layout/Header";
import Footer from "layout/Footer";

Layout.propTypes = {
  children: PropTypes.node,
  disabledFooter: PropTypes.bool,
  disabledHeader: PropTypes.bool,
};

export default function Layout({ children, disabledHeader, disabledFooter }) {
  return (
    <>
      {disabledHeader ? null : <Header />}

      {children}

      {disabledFooter ? null : <Footer />}
    </>
  );
}
