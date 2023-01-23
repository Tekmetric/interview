import LoginForm from "components/auth/LoginForm";
import Layout from "layout/Layout";

export default function LoginPage() {
  return (
    <Layout disabledFooter disabledHeader>
        <LoginForm />
    </Layout>
  );
}