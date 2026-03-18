import * as dotenv from 'dotenv';
dotenv.config();

export const appConfig = {
    baseUiURL:
        'https://automationintesting.online',
    baseApiAuthURL:
        'https://restful-booker.herokuapp.com/auth',
    createApiUrl:
        'https://restful-booker.herokuapp.com/booking',
    updateApiUrl:
        'https://restful-booker.herokuapp.com/booking/',
    verifyApiUrl:
        'https://restful-booker.herokuapp.com/booking/',
    deleteApiUrl:
        'https://restful-booker.herokuapp.com/booking/',
    username:
        'admin',
    password:
        'password123',
};