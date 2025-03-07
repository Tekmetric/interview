import { Link } from "react-router-dom";
import { List, ListItem, ListItemButton, ListItemIcon } from "@mui/material";
import { Home, Book, People, List as ListIcon, Help } from "@mui/icons-material";
import { useAuth } from "../hooks/useAuth";

const Sidebar = () => {
  const { user } = useAuth();
  
  return (
    <List>
        <ListItem key="Home" disablePadding>
          <ListItemButton component={Link} to="/home">
              <ListItemIcon title="Home"><Home /></ListItemIcon>
            </ListItemButton>
        </ListItem>
        {user ? (
          <>
          <ListItem key="Books" disablePadding>
            <ListItemButton component={Link} to="/books">
              <ListItemIcon title="Books"><Book /></ListItemIcon>
            </ListItemButton>
          </ListItem>
          <ListItem key="Authors" disablePadding>
            <ListItemButton component={Link} to="/authors">
              <ListItemIcon title="Authors"><People /></ListItemIcon>
            </ListItemButton>
          </ListItem>
          <ListItem key="ReadingLists" disablePadding>
            <ListItemButton component={Link} to="/reading-lists">
              <ListItemIcon title="My Reading Lists"><ListIcon /></ListItemIcon>
            </ListItemButton>
          </ListItem>
          </>
        ) : null}
        <ListItem key="About" disablePadding>
          <ListItemButton component={Link} to="/about">
              <ListItemIcon title="About"><Help /></ListItemIcon>
            </ListItemButton>
        </ListItem>
    </List>
  );
};

export default Sidebar;
