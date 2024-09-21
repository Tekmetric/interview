import * as React from 'react';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import IconButton from '@mui/material/IconButton';
import RedPandaLogo from "../../assets/panda-logo.png";
import { Avatar, Box } from '@mui/material';
import AccountIcon from '@mui/icons-material/AccountCircle';
import { grey, orange } from '@mui/material/colors';
import { AppBar } from './TopBar.style';
import { ITopBarProps } from './TopBar.interface';

export default function TopBar({ open, setOpen } : ITopBarProps) {
  const [showUserMenu, setShowUserMenu] = React.useState<boolean>(false);

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
          <Avatar sx={{ bgcolor: orange[500], color: grey[900] }}>
            <AccountIcon />
          </Avatar>
        </Box>
      </Toolbar>
    </AppBar>
  );
}
