import { AppBarProps } from '@mui/material/AppBar';

export interface IAppBarProps extends AppBarProps {
  open: boolean;
}

export interface ITopBarProps {
  open: boolean;
  setOpen: (isOpen: boolean) => void;
}
