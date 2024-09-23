import React from 'react';
import RedPanda from "../../assets/panda-menu.png";
import { Avatar, FormControlLabel, Slide, Typography } from '@mui/material';
import { AvatarContainer, FlexColumnContainer, FlexRowContainer, MaterialUISwitch, Paper, RPImage, UserMenuPopover } from './UserMenu.style';
import { IUserMenuProps } from './UserMenu.interface';
import AccountIcon from '@mui/icons-material/AccountCircle';
import { Themes } from '../../types/Theme';
import { orange } from '@mui/material/colors';
import { getTheme } from '../../themes/theme.helper';

export default function UserMenu({ open, setOpen, setTheme, theme} : IUserMenuProps) {

  return (
    <UserMenuPopover sx={{ display: open ? 'flex' : 'none' }} onMouseLeave={() => setOpen(false)}>
      <Slide direction='down' in={open}>
        <Paper elevation={3}>
          <FlexRowContainer>
            <FlexColumnContainer>
              <Typography variant="h6" noWrap component="div">
                Discover this week's new red pandas!
              </Typography>
              
              <Typography variant="h6" noWrap component="div">
                <a href="https://redpandanetwork.org/donate" target='_blank'>Make a difference! Donate now to RPN</a>
              </Typography>
              
              <Typography variant="h6" noWrap component="div">
                <a href="https://redpandanetwork.org/adopt" target='_blank'> Adopt a panda </a>
              </Typography>
            </FlexColumnContainer>

            <RPImage src={RedPanda} height={150} />

            <FlexColumnContainer>
              <AvatarContainer>
                  <Typography variant="h6" noWrap component="div">
                    Hi, [username] !
                  </Typography>

                  <Avatar sx={{ bgcolor: getTheme(theme).palette.background.paper, color: orange[600] }}>
                    <AccountIcon />
                  </Avatar>
              </AvatarContainer>

              <FlexColumnContainer>
                <Typography variant="h6" noWrap component="div">
                  My account
                </Typography>
                
                <Typography variant="h6" noWrap component="div">
                  Change password
                </Typography>
                
                <Typography variant="h6" noWrap component="div">
                  Settings
                </Typography>
              </FlexColumnContainer>

              <FormControlLabel
                control={<MaterialUISwitch checked={theme === Themes.Dark} />}
                label={ `${theme === Themes.Dark ? "Dark" : "Light"}  mode`}
                onChange={(_, checked) => setTheme(checked ? Themes.Dark : Themes.Light)}
              />
            </FlexColumnContainer>
          </FlexRowContainer>
        </Paper>
      </Slide>
    </UserMenuPopover>
  );
}
