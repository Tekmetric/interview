import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import { removeLoginData } from "../services/authService";
import { Typography, IconButton, Dialog, DialogTitle, DialogContent, DialogActions, Button } from "@mui/material";
import { LogoutOutlined } from "@mui/icons-material"; // Logout ikonu eklendi
import { useState } from "react";

const Header = () => {
    const { user, setUser } = useAuth();
    const navigate = useNavigate();
    const [openDialog, setOpenDialog] = useState(false);

    const handleLogin = () => {
        navigate("/login");
    }

    const handleRegister = () => {
        navigate("/register");
    }

    const handleLogoutConfirm = () => {
      setUser(null);
      removeLoginData();
      navigate("/home");
      setOpenDialog(false); // Logout işlemi sonrası diyalogu kapat
    };

    return (
        <header className="bg-blue-900 p-4 m-0.5">
            <div className="flex justify-between items-center">
                <div className="flex-1 text-white font-bold">
                    <Typography variant="h4">Reading List App</Typography>
                </div>
                {user ? (
                    <div className="flex-1 text-right text-white font-bold flex items-center justify-end">
                        <Typography>{user.firstName} {user.lastName}</Typography>
                        <IconButton onClick={() => setOpenDialog(true)} sx={{ color: "white", ml: 1 }}>
                            <LogoutOutlined />
                        </IconButton>
                    </div>
                ) : (
                    <div className="flex gap-4 text-right text-white cursor-pointer hover:underline">
                        <div onClick={handleLogin}>Login</div>
                        <div onClick={handleRegister}>Register</div>
                    </div>
                )}
            </div>

            <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
                <DialogTitle>Logout</DialogTitle>
                <DialogContent>
                    <Typography>Are you sure you want to logout?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDialog(false)} color="secondary">
                        Cancel
                    </Button>
                    <Button onClick={handleLogoutConfirm} color="primary">
                        Yes, Logout
                    </Button>
                </DialogActions>
            </Dialog>
        </header>
    )
}

export default Header;
