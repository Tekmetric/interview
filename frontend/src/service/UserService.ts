import { AuthProvider } from "@toolpad/core";

const signIn: (provider: AuthProvider, formData: FormData, onLoginSuccess: () => void) => void = async (
  provider,
  formData,
  onLoginSuccess
) => {
  const promise = new Promise<void>((resolve) => {
    setTimeout(() => {
      console.log(
        `Signing in with "${provider.name}" and credentials: ${formData.get('email')}, ${formData.get('password')}`,
      );

      onLoginSuccess();
      resolve();
    }, 300);
  });
  return promise;
};

export const UserService = {
  signIn
};
