import { AuthResponse, LoginForm } from "../../typings/auth";
import { buildUrl, send } from "../send";

export async function login(data: LoginForm) {
  return send("POST", "/api/login/", {
    ...data,
  });
}

export async function logout() {
  return send("POST", "/api/logout/");
}

export async function isAuthenticated(): Promise<AuthResponse> {
  return fetch(buildUrl("/api/is-authenticated/", undefined), {
    method: "GET",
    credentials: "include",
  }).then((response) => {
    return response.json();
  });
}
