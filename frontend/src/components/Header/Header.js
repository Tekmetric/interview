import React from 'react';
import { Link } from 'react-router-dom';
import './Header.scss';

const Header = ({ title, subtitle, showBackButton = false, backUrl = "/" }) => {
  return (
    <header className="header">
      <div className="header-ribbon"><p><strong>Tekmetric</strong> Visual Vehicle Inspection Tool</p></div>
      <div className="header-content">
        <div className="header-left">
          {showBackButton && (
            <Link to={backUrl} className="back-button">
              ← Back
            </Link>
          )}
        </div>
        <div className="header-center">
          <h1 className="header-title">
            {title}
          </h1>
          {subtitle && (
            <p className="header-subtitle">{subtitle}</p>
          )}
        </div>
        <div className="header-right">
          <Link to="/" className="home-link">
            Home
          </Link>
        </div>
      </div>
    </header>
  );
};

export default Header;
