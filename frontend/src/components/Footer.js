import { Copyright, Facebook, Instagram, X } from '@mui/icons-material';
import { Link } from 'react-router-dom';

export const Footer = () => {
  return (
    <footer className='h-24 w-full bg-blue-600 text-white flex items-center flex-col justify-center'>
      <p>This website is the property of Kudor-Dani Jenő, <Copyright />2025 All Rights Reserved</p>
    </footer>
  )
};
