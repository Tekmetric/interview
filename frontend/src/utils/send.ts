import Cookies from "universal-cookie";

const cookies = new Cookies();

function buildUrl(pathname: string, params?: URLSearchParams) {
  const url = new URL(pathname, "http://localhost:8000");
  if (params) {
    url.search = params.toString();
  }
  return url.toString();
}

type JsonValue = boolean | number | string | null | JsonArray | JsonObject;
type JsonObject = { [key: string]: JsonValue };
type JsonArray = Array<JsonValue>;

export async function send<T extends JsonValue>(
  method: "GET" | "POST" | "PUT" | "DELETE",
  pathname: string,
  body?: JsonObject,
  params?: URLSearchParams
): Promise<T> {
  const url = buildUrl(pathname, params);
  let contentType;
  let encodedBody;
  if (body) {
    contentType = "application/json";
    encodedBody = JSON.stringify(body);
  }
  const requestInit: RequestInit = {
    method,
    headers: {
      accept: "application/json",
      ...(contentType && { "Content-Type": "application/json" }),
      "X-CSRFToken": cookies.get("csrftoken"),
    },
    body: encodedBody || undefined,
    credentials: "include",
  };
  return fetch(url, requestInit).then((response) => {
    if (response.status === 403 || response.status === 401) {
      // Redirect to login page in case of authentication failure
      window.location.href = "/login";
    }
    if (!response.ok) {
      throw new Error("Network response was not ok " + response.statusText);
    }
    if (response.status === 204) {
      return null;
    }
    return response.json();
  });
}
