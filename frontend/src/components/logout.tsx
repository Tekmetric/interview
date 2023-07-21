import { IconButton } from "@mui/material";
import LogoutIcon from '@mui/icons-material/Logout';
import { useLogout } from "../shared/hooks/utils/use-logout";

const Logout = () => {
    const { logout } = useLogout('token');

    return (
        <IconButton
            className='fixed top-9 right-5'
            aria-label="Logout button"
            title="Logout"
            onClick={logout}
        >
            <LogoutIcon />
        </IconButton>
    )
}

export default Logout;