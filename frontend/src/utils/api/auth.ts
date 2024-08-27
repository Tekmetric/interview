import { LoginResponse, LoginForm } from "../../typings/auth";
import { buildUrl, request } from "../request";

export async function login(data: LoginForm): Promise<LoginResponse> {
  const requestInit: RequestInit = {
    method: "POST",
    headers: {
      accept: "application/json",
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
    credentials: "include",
  };
  return fetch(buildUrl("/api/login/"), requestInit).then((response) =>
    response.json()
  );
}

export async function logout() {
  return request("POST", "/api/logout/");
}
