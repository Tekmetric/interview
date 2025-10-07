import React, { FC } from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Button from '@mui/material/Button';

const pages = ['All Animes', 'Pricing', 'Blog'];

export const ResponsiveAppBar: FC = () => (
  <AppBar position="absolute">
    <Toolbar>
      <img src="/images/anime_icon.png" alt="icon" style={{ width: 32, height: 32 }} />

      <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
        {pages.map((page) => (
          <Button
            key={page}
            sx={{ mx: 2, color: 'white', display: 'block' }}
            href={`/${page.toLowerCase().replace(' ', '-')}`}
            variant="text"
          >
            {page}
          </Button>
        ))}
      </Box>
    </Toolbar>
  </AppBar>
);

export default ResponsiveAppBar;
