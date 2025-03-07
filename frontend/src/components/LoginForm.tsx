import React, { useState } from "react";
import { TextField, Button, Typography, CircularProgress } from "@mui/material";
import { loginUser, saveLoginData } from "../services/authService";
import { useNavigate, Link } from "react-router-dom";
import LoginResponse from "../types/loginResponse";
import { useAuth } from "../hooks/useAuth";


const LoginForm = () => {
  const { setUser } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const data: LoginResponse = await loginUser(email, password);
      saveLoginData(data);
      setUser(data.user);
      navigate("/home");
    } catch (error) {
      console.error(error);
      setError("Invalid credentials. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex justify-center items-center">
      <form onSubmit={handleSubmit} className="bg-white p-8 shadow-lg rounded-md w-full max-w-sm">
        <Typography variant="h5" className="text-center mb-4">
          Login
        </Typography>
        <TextField
          label="Email"
          type="email"
          fullWidth
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className="mb-4"
        />
        <TextField
          label="Password"
          type="password"
          fullWidth
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className="mb-4"
        />
        {error && <Typography color="error" className="mb-4">{error}</Typography>}
        <Button variant="contained" color="primary" type="submit" fullWidth disabled={loading}>
          {loading ? <CircularProgress size={24} color="inherit" /> : "Login"}
        </Button>
        <Typography variant="body2" className="text-center mt-6">
          Don't have an account? <Link to="/register" className="text-blue-500">Register</Link>
        </Typography>
        </form>
    </div>
  );
};

export default LoginForm;
