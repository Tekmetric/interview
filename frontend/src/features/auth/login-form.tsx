import { TextField, Button, Box } from '@mui/material';
import { LoginDataType, FormDataType } from '../../shared/types/login';
import { ChangeEvent, FormEvent, useState } from 'react';

type LoginFormPropsType = {
  isError: boolean;
  setAuthData: React.Dispatch<React.SetStateAction<LoginDataType>>;
};

const LoginForm = ({ isError, setAuthData }: LoginFormPropsType) => {
  const [formData, setFormData] = useState<FormDataType>({
    client_id: '',
    client_secret: '',
  });

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setAuthData(prevAuthData => ({
      ...prevAuthData,
      client_id: formData.client_id,
      client_secret: formData.client_secret,
    }));
  };

  const handleIdChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData(prevFormData => ({
      ...prevFormData,
      client_id: event.target.value,
    }));
  };

  const handleSecureChange = (event: ChangeEvent<HTMLInputElement>) => {
    setFormData(prevFormData => ({
      ...prevFormData,
      client_secret: event.target.value,
    }));
  };

  return (
    <Box
      className="max-w-sm px-6 py-8 bg-slate-200 rounded-md shadow-md shadow-gray-400"
      onSubmit={handleSubmit}
      component="form"
      noValidate
    >
      <p className="text-center text-xl font-medium mb-2">
        Sign in to Bhagavad Gita
      </p>
      <p className="text-center text-md mb-4">
        Enter you details below
      </p>
      <TextField
        margin="normal"
        required
        fullWidth
        id="client_id"
        label="Client ID"
        name="client_id"
        autoComplete="client_id"
        autoFocus
        value={formData?.client_id}
        onChange={handleIdChange}
        inputProps={{ 'data-testid': 'client-id-input' }}
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
        value={formData?.client_secret}
        onChange={handleSecureChange}
        inputProps={{ 'data-testid': 'client-secret-input' }}
      />
      {isError && <p className='text-red-700 text-center font-semibold'>Login failed !</p>}
      <Button
        className="font-bold"
        type="submit"
        fullWidth
        variant="contained"
        sx={{ mt: 3, pt: 1.5, pb: 1.5 }}
      >
        Sign In
      </Button>
      <p className="text-center mt-2">
        <a className="text-sky-600" href="https://bhagavadgita.io/api/" target="_blank" rel="noopener noreferrer">
          Don&apos;t have an account?
        </a>
      </p>
    </Box>
  );
};

export default LoginForm;
