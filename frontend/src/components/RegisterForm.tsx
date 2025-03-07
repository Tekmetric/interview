import React, { useState } from "react";
import { TextField, Button, Typography, CircularProgress } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../services/authService";
import { z } from "zod";
import { UserRegistrationData } from "../types/user";

const userRegistrationSchema = z.object({
    email: z.string().email().min(1, "Email is required!"),
    password: z.string().min(1, "Password is required!").min(3, "Password must be at least 3 characters!"),
    firstName: z.string().min(1, "First Name is required!"),
    lastName: z.string().min(1, "Last Name is required!")
});

const RegisterForm = () => {
  const [userRegistration, setUserRegistration] = useState<UserRegistrationData>({
    email: null,
    password: null,
    firstName: null,
    lastName: null
  });
  const [loading, setLoading] = useState(false);
  const [validationErrors, setValidationErrors] = useState<{ email?: string; password?: string; firstName?: string; lastName?: string }>({});
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleRegister = async () => {
    const validation = userRegistrationSchema.safeParse(userRegistration);
    if (!validation.success) {
      const fieldErrors: any = {};
      validation.error.errors.forEach((err) => {
        if (err.path.includes("email")) fieldErrors.email = err.message;
        if (err.path.includes("password")) fieldErrors.password = err.message;
        if (err.path.includes("firstName")) fieldErrors.firstName = err.message;
        if (err.path.includes("lastName")) fieldErrors.lastName = err.message;
      });
      setValidationErrors(fieldErrors);
      return;
    }

    try {
        await registerUser(userRegistration);
        navigate("/login");
        setValidationErrors({});
        setError("");
    } catch (error) {
        console.error(error);
        setError("Registration failed! Try another email address!");
    } finally {
        setLoading(false);
    }
  };


  return (
    <div className="flex justify-center items-center">
      <div className="bg-white p-8 shadow-lg rounded-md w-full max-w-[500px] max-h-[400px]">
        <Typography variant="h5" className="text-center">
          Register
        </Typography>
        <TextField
            label="First Name"
            fullWidth
            value={userRegistration?.firstName ?? ""}
            onChange={(e) => setUserRegistration({ ...userRegistration!, firstName: e.target.value })}
            required
            error={!!validationErrors.firstName}
            helperText={validationErrors.firstName}
          />
        <TextField
            label="Last Name"
            fullWidth
            value={userRegistration?.lastName ?? ""}
            onChange={(e) => setUserRegistration({ ...userRegistration!, lastName: e.target.value })}
            error={!!validationErrors.lastName}
            helperText={validationErrors.lastName}
          />
        <TextField
          label="Email"
          type="email"
          fullWidth
          value={userRegistration?.email ?? ""}
          onChange={(e) => setUserRegistration({ ...userRegistration!, email: e.target.value })}
          required
          error={!!validationErrors.email}
          helperText={validationErrors.email}
      />
        <TextField
          label="Password"
          type="password"
          fullWidth
          value={userRegistration?.password ?? ""}
          onChange={(e) => setUserRegistration({ ...userRegistration!, password: e.target.value })}
          required
          error={!!validationErrors.password}
          helperText={validationErrors.password}
        />
        {error && <Typography color="error" className="mb-4">{error}</Typography>}
        <Button variant="contained" color="primary" type="submit" fullWidth disabled={loading} onClick={handleRegister}>
          {loading ? <CircularProgress size={24} color="inherit" /> : "Register"}
        </Button>
      </div>
    </div>
  );
};

export default RegisterForm;
