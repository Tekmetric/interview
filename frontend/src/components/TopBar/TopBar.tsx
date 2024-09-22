import * as React from 'react';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import RedPandaLogo from "../../assets/panda-logo.png";
import { Avatar, Box } from '@mui/material';
import AccountIcon from '@mui/icons-material/AccountCircle';
import { orange } from '@mui/material/colors';
import { AppBar, Toolbar } from './TopBar.style';
import UserMenu from '../UserMenu/UserMenu';
import { ITopBarProps } from './TopBar.interface';
import { getTheme } from '../../themes/theme.helper';

export default function TopBar({ open, setOpen, setTheme, theme } : ITopBarProps) {
  const [showUserMenu, setShowUserMenu] = React.useState<boolean>(true);

  return (
    <AppBar position="fixed" open={open}>
      <Toolbar>
        <IconButton
          color="inherit"
          aria-label="open drawer"
          onClick={() => setOpen(false)}
          edge="start"
          sx={[
            {
              marginRight: 5,
            },
            open && { display: 'none' },
          ]}
        >
          <img src={RedPandaLogo} height={32}/>
        </IconButton>

        <Box sx={{ flexGrow: 1 }}>
          <Typography variant="h6" noWrap component="div">
            Monitor red panda sightings
          </Typography>
        </Box>

        <Box sx={{ flexGrow: 0 }}>
          <Avatar sx={{ bgcolor: getTheme(theme).palette.background.paper, color: orange[600] }} onMouseEnter={() => setShowUserMenu(true)}>
            <AccountIcon />
          </Avatar>
        </Box>
      </Toolbar>

      <UserMenu open={showUserMenu} setOpen={setShowUserMenu} setTheme={setTheme} theme={theme} />
    </AppBar>
  );
}
