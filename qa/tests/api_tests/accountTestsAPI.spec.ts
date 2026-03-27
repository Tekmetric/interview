import {test, expect} from "@playwright/test"
import { faker } from "@faker-js/faker"

test.describe('User Account CRUD', () => {
    test.describe.configure({ mode: 'serial' });

    // Generate user acct info using Faker

    const USER = {
        name: faker.person.firstName(),
        email: faker.internet.email(),
        password: "APItest123",
        title: "Mr",
        birth_day: "1",
        birth_month: "January",
        birth_year: "2000",
        first_name: faker.person.firstName(),
        last_name: faker.person.lastName(),
        company: faker.company.name(),
        address1: faker.location.streetAddress(),
        address2: faker.location.secondaryAddress(),
        country: "United States",
        state: faker.location.state(),
        city: faker.location.city(),
        zipcode: faker.location.zipCode(),
        mobile_phone: faker.phone.number()
    }

    test('API Post Create Account', async ({request}) => {
        const response = await request.post('https://automationexercise.com/api/createAccount',{
            form:{
                "name": USER.name,
                "email": USER.email,
                "password": USER.password,
                "title": USER.title,
                "birth_date": USER.birth_day,
                "birth_month": USER.birth_month,
                "birth_year": USER.birth_year,
                "firstname": USER.first_name,
                "lastname": USER.last_name,
                "company": USER.company,
                "address1": USER.address1,
                "address2": USER.address2,
                "country": USER.country,
                "zipcode": USER.zipcode,
                "state": USER.state,
                "city": USER.city,
                "mobile_number": USER.mobile_phone
            }
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(201);
        expect(body.message).toBe('User created!');
    });

    test('API Read Account Detail', async ({request}) => {
        const response = await request.get('https://automationexercise.com/api/getUserDetailByEmail',{
            params:{
                "email": USER.email
            }
        });
        
        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(200);
        expect(body.user).toHaveProperty("email",USER.email);
        expect(body.user).toHaveProperty("first_name",USER.first_name);
        expect(body.user).toHaveProperty("last_name",USER.last_name);
        expect(body.user).toHaveProperty("company",USER.company);
    });

     test('API Update Account Detail', async ({request}) => {
        const response = await request.put('https://automationexercise.com/api/updateAccount',{
            form:{
                "name": USER.name,
                "email": USER.email,
                "password": USER.password,
                "title": USER.title,
                "birth_date": USER.birth_day,
                "birth_month": USER.birth_month,
                "birth_year": USER.birth_year,
                "firstname": faker.person.firstName(),
                "lastname": USER.last_name,
                "company": USER.company,
                "address1": USER.address1,
                "address2": USER.address2,
                "country": USER.country,
                "zipcode": USER.zipcode,
                "state": USER.state,
                "city": USER.city,
                "mobile_number": USER.mobile_phone
            }
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(200);
        expect(body.message).toBe('User updated!');
    });

    test('API Delete Account', async ({request}) => {
        const response = await request.delete('https://automationexercise.com/api/deleteAccount',{
            form:{
                "email": USER.email,
                "password": USER.password
            }
        });

        expect(response.status()).toBe(200);
        const body = await response.json();
        expect(body.responseCode).toBe(200);
        expect(body.message).toBe('Account deleted!');
    });

});