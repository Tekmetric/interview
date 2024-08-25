import { LoginForm } from "../../typings/auth";
import { send } from "../send";

export async function login(data: LoginForm) {
  return send("POST", "/api/login/", {
    ...data,
  });
}

export async function logout() {
  return send("POST", "/api/logout/");
}
