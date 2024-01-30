import AuthService from "./AuthService"

export default function headers() {
    const headers = {
        'Authorization': '',
        'Content-Type': 'application/json'
    }

    const user = AuthService.getCurrentUser();
    if (user && user.token) {
        headers.Authorization = 'Bearer ' + user.token;
    }
    return headers;
}