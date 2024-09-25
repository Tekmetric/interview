import { AppBarProps } from '@mui/material/AppBar';
import { IUserMenuProps } from '../UserMenu/UserMenu.interface';

export interface IAppBarProps extends AppBarProps {
  open: boolean;
}

export interface ITopBarProps extends IUserMenuProps {}
