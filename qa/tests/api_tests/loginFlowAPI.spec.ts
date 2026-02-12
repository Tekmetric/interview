import {test, expect} from "@playwright/test"

test.describe('Login Verification', () => {
    const USER = {
        email: "ektestAPI@test.com",
        password: "APItest123",
        incorrectPassword: "123APItest"
    }
    
    test('API Login - Valid Details', async ({request}) => {
        const response = await request.post('https://automationexercise.com/api/verifyLogin',{
            form:{
                "email": USER.email,
                "password": USER.password
            }
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(200)
        expect(body.message).toBe('User exists!');
    });

    test('API Login - Missing Email', async ({request}) => {
        const response = await request.post('https://automationexercise.com/api/verifyLogin',{
            params:{
                "password": USER.password
            }
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(400);
        expect(body.message).toBe('Bad request, email or password parameter is missing in POST request.');
    });

    test('API Login - DELETE request', async({request}) => {
        const response = await request.delete('https://automationexercise.com/api/verifyLogin',{});

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(405);
        expect(body.message).toBe('This request method is not supported.');
    });

    test('API Login - Invalid Details', async ({request}) => {
        const response = await request.post('https://automationexercise.com/api/verifyLogin',{
            form:{
                "email": USER.email,
                "password": USER.incorrectPassword
            }
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(404)
        expect(body.message).toBe('User not found!');
    });

});