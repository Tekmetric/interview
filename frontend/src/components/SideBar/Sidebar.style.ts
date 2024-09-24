import { 
  CSSObject,
  styled,
  Theme,
  Drawer as MuiDrawer,
  List,
  ListItemIcon as MuiListItemIcon,
  ListItemText as MuiListItemText
} from "@mui/material";
import { orange } from "@mui/material/colors";

export const drawerWidth = 240;

const openedMixin = (theme: Theme): CSSObject => ({
  width: drawerWidth,
  transition: theme.transitions.create('width', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.enteringScreen,
  }),
  overflowX: 'hidden',
});

const closedMixin = (theme: Theme): CSSObject => ({
  transition: theme.transitions.create('width', {
    easing: theme.transitions.easing.sharp,
    duration: theme.transitions.duration.leavingScreen,
  }),
  overflowX: 'hidden',
  width: `calc(${theme.spacing(7)} + 1px)`,
  [theme.breakpoints.up('sm')]: {
    width: `calc(${theme.spacing(8)} + 1px)`,
  },
});

export const DrawerHeader = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  padding: theme.spacing(0, 1),
  // necessary for content to be below app bar
  ...theme.mixins.toolbar,
}));

export const Drawer = styled(MuiDrawer, { shouldForwardProp: (prop) => prop !== 'open' })(
  ({ theme }) => ({
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: 'nowrap',
    boxSizing: 'border-box',
    justifyContent: "center",
    alignItems: "center",
    variants: [
      {
        props: ({ open }) => open,
        style: {
          ...openedMixin(theme),
          '& .MuiDrawer-paper': openedMixin(theme),
        },
      },
      {
        props: ({ open }) => !open,
        style: {
          ...closedMixin(theme),
          '& .MuiDrawer-paper': closedMixin(theme),
        },
      },
    ],
  }),
);

export const DrawerItems = styled(List)(() => ({
  display: 'flex',
  flexDirection: "column",
  alignItems: 'center',
  flexGrow: 1
}));

export const ListItemIcon = styled(MuiListItemIcon)(({ theme }) => ({
 color: theme.palette.primary.main
}));

export const ListItemText = styled(MuiListItemText)(({ theme }) => ({
  color: theme.palette.primary.main
}));
 
export const DevLogo = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: "center",
  justifyContent: "center",
  width: "100%",
  color: orange[500],
  padding: theme.spacing(2, 1)
}));