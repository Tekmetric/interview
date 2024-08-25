import React from "react";
import { useFormik } from "formik";
import { TextField, Button, Box } from "@mui/material";
import { validationSchema } from "../typings/loginFormSchema";
import { login } from "../utils/api/auth";
import { useNavigate } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";

function LoginPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const formik = useFormik({
    initialValues: {
      username: "",
      password: "",
    },
    validationSchema,
    onSubmit: (values) => {
      login(values).then(() => {
        queryClient.invalidateQueries({ queryKey: ["auth-status"] });
        navigate("/");
      });
    },
  });

  return (
    <Box>
      <form onSubmit={formik.handleSubmit}>
        <TextField
          id="username"
          name="username"
          label="Username"
          value={formik.values.username}
          onChange={formik.handleChange}
          error={formik.touched.username && !!formik.errors.username}
          helperText={formik.touched.username && formik.errors.username}
          required
        />
        <TextField
          id="password"
          name="password"
          label="Password"
          type="password"
          value={formik.values.password}
          onChange={formik.handleChange}
          error={formik.touched.password && !!formik.errors.password}
          helperText={formik.touched.password && formik.errors.password}
          required
        />
        <Button variant="contained" type="submit">
          Login
        </Button>
      </form>
    </Box>
  );
}

export default LoginPage;
