import { makeApiCall } from '../Utilities/apiCall';
import { appConfig } from "../Utilities/env";
import 'dotenv/config';
import {expect} from "@playwright/test";

class BookOnlineApi {

    async createApi(firstName:String, lastName: String, totalPrice, depositPaid: boolean, checkIn: Date, checkOut: Date, additionalNeeds: String) {
        const createEndpoint = appConfig.createApiUrl;
        const createMethod = 'POST';
        const createHeaders = {
            'Accept': 'application/json',
        };
        const createData = {
            firstname: firstName,
            lastname: lastName,
            totalprice: totalPrice,
            depositpaid: depositPaid,
            bookingdates: {
                checkin: checkIn,
                checkout: checkOut
                },
            additionalneeds : additionalNeeds
        };
        try {
            const response = await makeApiCall(createEndpoint, createMethod, createData, createHeaders);
            console.log('*** CREATE Data:', response.data);
            console.log('*** CREATE Status:', response.status);
            return response;
        } catch (error) {
            console.error('Error making API request:', error);
            throw error;
        }
    }

    async verifyApi(bookingId: String,firstName: String, lastName: String) {
        const verifyEndpoint = (appConfig.verifyApiUrl+bookingId);
        console.log('*** verify Endpoint is: ', verifyEndpoint);
        const verifyMethod = 'GET';
        const verifyHeaders = {
            'Accept': 'application/json',
        };
        const verifyData = {
        };
        try {
            const response = await makeApiCall(verifyEndpoint, verifyMethod, verifyData, verifyHeaders);
            const fName = response.data.firstname;
            const lName = response.data.lastname;
            const status = response.status;
            expect(fName).toContain(firstName);
            expect(lName).toContain(lastName);
            expect(status).toBe(200);
        } catch (error) {
            console.error('Error making API request:', error);
            throw error;
        }
    }

    async deleteApi(bookingId) {
        const deleteEndpoint = (appConfig.deleteApiUrl+bookingId);
        const token = await this.obtainAccessToken(appConfig.username, appConfig.password); // this will return the token
        console.log('*** delete Endpoint is: ', deleteEndpoint);
        const verifyMethod = 'DELETE';
        const verifyHeaders = {
            'Accept': 'application/json',
            'Cookie': `token=${token}`,
        };
        const verifyData = {
        };
        try {
            const response = await makeApiCall(deleteEndpoint, verifyMethod, verifyData, verifyHeaders);
            const status = response.status;
            const text = response.statusText;
            expect(status).toBe(201);
            expect(text).toBe('Created');

        } catch (error) {
            console.error('Error making API request:', error);
            throw error;
        }
    }


    async obtainAccessToken(userName, passWord) {
        const apiAuthURL = appConfig.baseApiAuthURL;
        const method = 'POST';
        const headers = {
            'Content-Type': 'application/json',
        };
        const body = {
                "username" : userName,
                "password" : passWord
        };
        try {
            const response = await makeApiCall(apiAuthURL, method, body, headers);
            const accessToken = response.data.token;
            console.log('**** Access Token:', accessToken);
            return accessToken;
        } catch (error) {
            console.error('Error obtaining access token:', error);
            throw error;
        }
    }




}

export default BookOnlineApi;