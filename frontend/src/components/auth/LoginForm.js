import * as Yup from "yup";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup/dist/yup";
import { useLocation, useNavigate } from "react-router-dom";
import { authService } from "api/authService";
import { useAuth } from "contexts/authContext";
import toast from "react-hot-toast";
import { LOGO_URL } from "constants";

const FormSchema = Yup.object().shape({
  email: Yup.string()
    .required("Email is required")
    .email("That is not an email"),
  password: Yup.string()
    .required("Password is required")
    .min(6, "Password should be of minimum 6 characters length"),
});

export default function LoginForm() {
  const { state } = useLocation();
  const navigate = useNavigate();
  const { login } = useAuth();

  const {
    reset,
    register,
    handleSubmit,
    formState: { isSubmitting, isValid, errors },
  } = useForm({
    mode: "onChange",
    resolver: yupResolver(FormSchema),
    defaultValues: { email: "", password: "" },
  });

  const onSubmit = async (formData) => {
    try {
      const { status, data } = await authService.login(formData);
      if (status === 200) {
        login(data);
        navigate(state?.from ? state.from : "/", {
          replace: true,
        });
        toast.success("Welcome to Tekmetric!");
      } else {
        console.warn(status, data.message);
      }
      reset();
    } catch (error) {
      toast.error(error.response.data.message);
      console.error(error.message);
    }
  };
  return (
    <div className="flex-col flex items-center justify-center mt-20">
      <img className="items-center w-48" src={LOGO_URL} alt="Tekmetric" />
      <div className="mt-10 bg-stone-100 border p-5 w-full max-w-md mx-auto shadow-lg">
        <div className="mx-8 my-5 mb-1 text-lg text-stone-500 text-center">
          <h1>Sign in to Tekmetric</h1>
        </div>
        <form className="space-y-5" onSubmit={handleSubmit(onSubmit)}>
          <div className="py-5">
            <input
              className="w-full border rounded h-12 px-4 focus:outline-none"
              {...register("email")}
              placeholder="Email"
              type="email"
              autoFocus
              required
            />
            <p className="mt-2 text-red-700 text-sm">
              {errors?.email?.message}
            </p>
            <br />

            <input
              className="w-full border rounded h-12 px-4 focus:outline-none"
              {...register("password")}
              placeholder="Password"
              type="password"
              required
            />
            <p className="mt-2 text-red-700 text-sm">
              {errors?.password?.message}
            </p>
            <br />
            <button
              className={`btn w-full bg-sky-800 font-bold text-white p-2 no-animation rounded-none hover:bg-sky-700 active:bg-sky-900 disabled:bg-slate-200 ${
                isSubmitting ? "loading" : null
              }`}
              disabled={!isValid}
            >
              SIGN IN
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
