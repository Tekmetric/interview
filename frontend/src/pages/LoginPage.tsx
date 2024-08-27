import React from "react";
import { useFormik } from "formik";
import { TextField, Button, Box, Typography } from "@mui/material";
import { validationSchema } from "../typings/loginFormSchema";
import { login } from "../utils/api/auth";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import styled from "@emotion/styled";
import LoginImage from "../assets/images/LoginImage.jpg";

const PageContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  background-image: url(${LoginImage});
  background-size: cover;
  background-position: center;
  justify-content: center;
  height: 100vh;
  width: 100%;
`;

const FormContainer = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  max-width: 300px;
  margin: 0 auto;
  justify-content: center;
  height: 100vh;
`;

const FormElements = styled(Box)`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
  justify-content: center;
  gap: 24px;
  background-color: white;
  padding: 20px;
  border-radius: 16px;
  max-height: 500px;
  max-width: 300px;
  width: 300px;
`;

const FormButton = styled(Button)`
  width: 100%;
  min-height: 40px;
`;

const FormField = styled(TextField)`
  width: 100%;
`;

function LoginPage() {
  const navigate = useNavigate();
  const { setToken } = useAuth();
  const formik = useFormik({
    initialValues: {
      username: "",
      password: "",
    },
    validationSchema,
    onSubmit: (values) => {
      login(values).then((response) => {
        setToken(response.sessionToken);
        navigate("/");
      });
    },
  });

  return (
    <>
      <PageContainer>
        <form
          style={{ height: "100%", width: "100%" }}
          onSubmit={formik.handleSubmit}
        >
          <FormContainer>
            <FormElements>
              <Typography
                variant="h5"
                style={{ color: "#454140", marginBottom: "20px" }}
              >
                Login to your account
              </Typography>
              <FormField
                id="username"
                name="username"
                label="Username"
                value={formik.values.username}
                onChange={formik.handleChange}
                error={formik.touched.username && !!formik.errors.username}
                helperText={formik.touched.username && formik.errors.username}
                required
              />
              <FormField
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
              <FormButton variant="contained" type="submit">
                Login
              </FormButton>
            </FormElements>
          </FormContainer>
        </form>
      </PageContainer>
    </>
  );
}

export default LoginPage;
