import {test, expect} from '@playwright/test';

//API Test: 1 - Create Account API Test on 'https://www.automationexercise.com/'
test('Create Account API Test', async ({request}) => {
   
    //Creating a unique email using timestamp
    const timestamp = Date.now();
    const uniqueEmail = `Account${timestamp}@testing.com`;
    const password = '1234567890';

    //Making a POST request to create an account
    const response = await request.post('https://www.automationexercise.com/api/createAccount', {
        form: {
            name: 'Creating',
            email:uniqueEmail,
            password: '1234567890',
            title:'Mrs',
            birth_date:'15',
            birth_month:'December',
            birth_year:'1997',
            firstname:'Creating',
            lastname:'Account',
            company:'Account Creating Company',
            address1:'123 Test avenue',
            address2:'1st floor',
            country:'United States',
            zipcode:'77001',
            state:'Texas',
            city:'Houston',
            mobile_number:'1234567890'

        }//end of form
    })//end of post request

//Verify Account Creation API Test on url
expect(response.status()).toBe(200);
console.log('Account created' , uniqueEmail);

 //API Test 2 - Login with the account created via API Test 1
 const loginResponse = await request.post('https://automationexercise.com/api/verifyLogin', {
        form: {
            email: uniqueEmail,
            password: password
        }//end of form
    });//end of post request

    expect(loginResponse.status()).toBe(200);
    const loginBody = await loginResponse.json();
    expect(loginBody.message).toBe('User exists!');

    console.log('Login successful with new account');
});//end of test 1 

//Post account for test 2 for login functionality
test('Setup - Create Test Account via API', async ({request}) => {
    
    const response = await request.post('https://automationexercise.com/api/createAccount', {
        form: {
            name: 'Lenny',
            email: 'lenny@testing.com',
            password: '1234567890',
            title: 'Mrs',
            birth_date: '15',
            birth_month: 'December',
            birth_year: '1997',
            firstname: 'Lenny',
            lastname: 'Smith',
            company: 'Test Company',
            address1: '123 Test St',
            address2: 'Apt 1',
            country: 'United States',
            zipcode: '77002',
            state: 'Texas',
            city: 'Houston',
            mobile_number: '1234567890'
        }//end of form
    });//end of post request
    
    expect(response.status()).toBe(200);
    console.log('Test account created via API for UI tests');
});//end of test 2

//API Test 3 -Create and Delete Account API Test on 'https://www.automationexercise.com/'
test('Delete Account API Test', async ({request}) => {
    
    const timestamp = Date.now();
    const email = `Delete${timestamp}@testing.com`;
    const password = '1234567890';
    
    // Create account first
    await request.post('https://automationexercise.com/api/createAccount', {
        form: {
            name: 'Test',
            email: email,
            password: password,
            title: 'Mr',
            birth_date: '1',
            birth_month: 'January',
            birth_year: '2000',
            firstname: 'Test',
            lastname: 'User',
            company: 'Test',
            address1: '123 St',
            address2: '',
            country: 'United States',
            zipcode: '12345',
            state: 'Texas',
            city: 'Austin',
            mobile_number: '1111111111'
        }//end of form
    });//end of post request
    
    console.log('Test account created for deletion');
    
    // Delete the account that was created 
    const deleteResponse = await request.delete('https://automationexercise.com/api/deleteAccount', {
        form: {
            email: email,
            password: password
        }//end of form
    });//end of delete request
    
    expect(deleteResponse.status()).toBe(200);
    const deleteBody = await deleteResponse.json();
    expect(deleteBody.message).toBe('Account deleted!');
    console.log('Account deleted successfully');
});//end of test 3