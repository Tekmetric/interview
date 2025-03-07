import LoginResponse from "../types/loginResponse";
import User, { UserRegistrationData } from "../types/user";
import { LOGIN_DATA } from "../util/constants";
import callApi from "./apiService";

const getLoginData = () : LoginResponse | null => {
    const loginResponseStr : string | null = localStorage.getItem(LOGIN_DATA);
    if (loginResponseStr) {
        return JSON.parse(loginResponseStr);
    }
    return null;
}

export const getActiveUser = () : User | null => {
    const loginData = getLoginData();
    if (loginData) {
        return loginData.user;
    }
    return null;
}

export const getAuthToken = () : string | null => {
    const loginData = getLoginData();
    if (loginData) {
        return loginData.token;
    }
    return null;
};
  
export const removeLoginData = () => {
    localStorage.removeItem(LOGIN_DATA);
}
  
export const saveLoginData = (loginResponse : LoginResponse) => {
    localStorage.setItem(LOGIN_DATA, JSON.stringify(loginResponse));
}
  
export const loginUser = async (username: string, password: string) => {
    return callApi('/auth/login', "POST", { username, password });
};

export const registerUser = async (userRegistration: UserRegistrationData) => {
    console.log(userRegistration);
    return callApi('/auth/register', "POST", userRegistration);
}
  

