import React, { useState } from 'react';
import { Header } from './Header';
import { Navbar } from './Navbar';
import { Footer } from './Footer';

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const [navbarOpen, setNavbarOpen] = useState(true);

  const toggleNavbar = () => {
    setNavbarOpen(!navbarOpen);
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Header toggleNavbar={toggleNavbar} />

      <div className="flex flex-1">
        <Navbar isOpen={navbarOpen} />

        <main className={`flex-1 transition-all duration-300 ${navbarOpen ? 'md:ml-64' : ''}`}>
          <div className="container mx-auto px-4 py-6">{children}</div>
        </main>
      </div>

      <Footer />
    </div>
  );
};
