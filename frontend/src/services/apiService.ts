import { HTTP_GET } from "../util/constants";
import { getAuthToken, removeLoginData } from "./authService";

const API_URL = import.meta.env.VITE_baseApiUrl; 

const callApi = async (endpoint: string, method: string = HTTP_GET, body: any = null) => {
  const token = getAuthToken();

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...(token && { "Authorization": `Bearer ${token}` }), 
  };

  const response = await fetch(`${API_URL}${endpoint}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null,
  });

  if (!response.ok) {
    const errorData = await response.json();  
    const errorMessage = errorData?.message || `Error: ${response.statusText}`;
    throw new Error(errorMessage);  
  }

  if (response.status === 401) {
    removeLoginData();
    window.location.href = "/login"; 
  }
  else if (response.status === 409) {
    alert("This record is being used by other records and cannot be deleted!");
    return null;
  }
  else if (response.status === 204) {
    return null;  
  }
  else if (!response.ok) {
    throw new Error(`Error: ${response.statusText}`);
  }

  return response.json();
  
};

export default callApi;
