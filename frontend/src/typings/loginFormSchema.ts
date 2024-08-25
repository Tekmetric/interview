import * as Yup from "yup";

export const validationSchema = Yup.object({
  username: Yup.string()
    .min(6, "Username should be at least 6 characters")
    .required("Required"),
  password: Yup.string()
    .min(6, "Password must be at least 6 characters")
    .required("Required"),
});
