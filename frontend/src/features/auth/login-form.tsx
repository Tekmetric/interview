import { TextField, Button, Box } from '@mui/material';

const LoginForm = () => {
    return (
        <Box component="form" noValidate sx={{ mt: 1 }}>
            <TextField
                margin="normal"
                required
                fullWidth
                id="client_id"
                label="Client ID"
                name="client_id"
                autoComplete="client_id"
                autoFocus
            />
            <TextField
                margin="normal"
                required
                fullWidth
                name="client_secure"
                label="Client Secret"
                type="password"
                id="client_secure"
                autoComplete="current-password"
            />
            <Button
                className='bg-red font-extrabold'
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
            >
                Sign In
            </Button>
        </Box>
    );
}

export default LoginForm;