import React from 'react';
import { AppBar, Toolbar, Typography, Box, Menu, MenuItem, Button } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import Brightness4Icon from '@mui/icons-material/Brightness4';
import Brightness7Icon from '@mui/icons-material/Brightness7';
import LanguageIcon from '@mui/icons-material/Language';
import ChecklistRtlIcon from '@mui/icons-material/ChecklistRtl';
import useTranslationMenu from '@/hooks/useTranslationMenu.ts';
import LanguageTypes from '@/types/Language';
import { Link, useLocation } from 'react-router-dom';
import RouterUrl from '@/types/RouterUrl.ts';

interface NavbarProps {
  darkMode: boolean;
  setDarkMode: (mode: boolean) => void;
}

const Navbar: React.FC<NavbarProps> = ({ darkMode, setDarkMode }) => {
  const { translate, selectedLanguage, menuEl, handleLanguageClose, handleOnClickLanguage, handleLanguageChange } =
    useTranslationMenu();

  const { pathname } = useLocation();

  return (
    <AppBar position="static">
      <Toolbar sx={{ justifyContent: 'space-between' }}>
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'row',
            alignItems: 'center',
            gap: '4px',
          }}
        >
          <ChecklistRtlIcon />
          <Typography variant="h6">{translate('app.title')}</Typography>
        </Box>
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'row',
            alignItems: 'center',
          }}
        >
          {pathname !== RouterUrl.Home && (
            <Button
              component={Link}
              data-testid="home-link-button"
              to={RouterUrl.Home}
              aria-label={translate('app.translation.button.Home')}
              color="inherit"
            >
              {translate('app.translation.button.Home')}
            </Button>
          )}
          <IconButton
            color="inherit"
            data-testid="dark-mode-toggle-button"
            aria-label={translate(
              `app.translation.header.ariaLabel.${darkMode ? 'SwitchToLightMode' : 'SwitchToDarkMode'}`,
            )}
            onClick={() => setDarkMode(!darkMode)}
          >
            {darkMode ? <Brightness4Icon /> : <Brightness7Icon />}
          </IconButton>
          <IconButton
            color="inherit"
            data-testid="language-button"
            onClick={handleOnClickLanguage}
            id="language-button"
            aria-label={translate('app.translation.button.LanguageSelector')}
            aria-haspopup="true"
            aria-expanded={menuEl ? 'true' : undefined}
            aria-controls={menuEl ? 'language-menu' : undefined}
          >
            <LanguageIcon />
          </IconButton>
          <Menu
            id="language-menu"
            data-testid="language-menu"
            aria-labelledby="language-button"
            anchorEl={menuEl}
            open={!!menuEl}
            onClose={handleLanguageClose}
          >
            <MenuItem
              data-testid="language-en"
              onClick={() => handleLanguageChange(LanguageTypes.EN)}
              selected={selectedLanguage === LanguageTypes.EN}
            >
              {translate('app.translation.language.en')}
            </MenuItem>
            <MenuItem
              onClick={() => handleLanguageChange(LanguageTypes.FR)}
              selected={selectedLanguage === LanguageTypes.FR}
            >
              {translate('app.translation.language.fr')}
            </MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  );
};

export default Navbar;
