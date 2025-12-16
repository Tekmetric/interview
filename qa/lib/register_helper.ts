/**
 * A helper class for registering, retrieving, updating, and deleting user accounts via API.
 */
export class RegisterHelper {
    readonly randomNumber: string;
    readonly name: string;
    readonly email: string;
    readonly password: string;
    readonly title: string;
    readonly birth_date: string;
    readonly birth_month: string;
    readonly birth_year: string;
    readonly firstname: string;
    readonly lastname: string;
    readonly company: string;
    readonly address: string;
    readonly address2: string;
    readonly country: string;
    readonly state: string;
    readonly city: string;
    readonly zipcode: string;
    readonly phone: string;

    
    constructor() {
        this.randomNumber = Math.random().toString().slice(2);
        this.name = `qatester${this.randomNumber}`;
        this.email = `qatest${this.randomNumber}@tester.com`;
        this.password = 'Testing12';
        this.title = 'Mr';
        this.birth_date = '1';
        this.birth_month = 'January';
        this.birth_year = '2000';
        this.firstname = 'first';
        this.lastname = 'last';
        this.company = '';
        this.address = '370 Congress St';
        this.address2 = '';
        this.country = 'United States';
        this.state = 'Massachusetts';
        this.city = 'Boston';
        this.zipcode = '02210';
        this.phone = '6174445555';
    }


    /**
     * Given user details, create an account via API. Defaults to the constructor values.
     * 
     * @param {string} name - The username of the account holder.
     * @param {string} email - The email of the account holder.
     * @param {string} password - The password of the account holder.
     * @param {string} title - The title of the account holder.
     * @param {string} birth_date - The birth date of the account holder.
     * @param {string} birth_month - The birth month of the account holder.
     * @param {string} birth_year - The birth year of the account holder.
     * @param {string} firstname - The first name of the account holder.
     * @param {string} lastname - The last name of the account holder.
     * @param {string} [company] - The company of the account holder. Optional.
     * @param {string} address - The address of the account holder.
     * @param {string} [address2] - The secondary address of the account holder. Optional.
     * @param {string} country - The country of the account holder.
     * @param {string} state - The state of the account holder.
     * @param {string} city - The city of the account holder.
     * @param {string} zipcode - The zipcode of the account holder.
     * @param {string} phone - The phone number of the account holder.
     * @returns A response message from the API call and a 201 response code.
     */
    async api_create_account(
        name: string = this.name,
        email: string = this.email,
        password: string = this.password,
        title: string = this.title,
        birth_date: string = this.birth_date,
        birth_month: string = this.birth_month,
        birth_year: string = this.birth_year,
        firstname: string = this.firstname,
        lastname: string = this.lastname,
        company?: string,
        address: string = this.address,
        address2?: string,
        country: string = this.country,
        state: string = this.state,
        city: string = this.city,
        zipcode: string = this.zipcode,
        phone: string = this.phone) {

        const params = new URLSearchParams();
        params.append("name", name);
        params.append("email", email);
        params.append("password", password);
        params.append("title", title);
        params.append("birth_date", birth_date);
        params.append("birth_month", birth_month);
        params.append("birth_year", birth_year);
        params.append("firstname", firstname);
        params.append("lastname", lastname);
        params.append("lastname", lastname);
        if (company) {
            params.append("company", company);
        }
        params.append("address1", address);
        if (address2) {
            params.append("address2", address2);
        }
        params.append("country", country);
        params.append("zipcode", zipcode);
        params.append("state", state);
        params.append("city", city);
        params.append("mobile_number", phone)

        const response = await fetch("https://automationexercise.com/api/createAccount", {
            method: 'POST',
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Accept": "application/json"
            },
            body: params.toString()
        });
        return response;
    }


    /**
     * Given an email, get the account details via API.
     * 
     * @param {string} email - The email of the account holder.
     * @returns The user details and a 200 response code.
     */
    async api_get_account_details(email: string) {

        const response = await fetch(`https://automationexercise.com/api/getUserDetailByEmail?email=${email}`, {
            method: 'GET',
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Accept": "application/json"
            },
        });

        return response;
    }


    /**
     * Given an email and password, update the account details via API.
     * 
     * @param {string} name - The username of the account holder.
     * @param {string} email - The email of the account holder.
     * @param {string} password - The password of the account holder.
     * @param {string} [title] - The title of the account holder. Optional.
     * @param {string} [birth_date] - The birth date of the account holder. Optional.
     * @param {string} [birth_month] - The birth month of the account holder. Optional.
     * @param {string} [birth_year] - The birth year of the account holder. Optional.
     * @param {string} [firstname] - The first name of the account holder. Optional.
     * @param {string} [lastname] - The last name of the account holder. Optional.
     * @param {string} [company] - The company of the account holder. Optional.
     * @param {string} [address] - The address of the account holder. Optional.
     * @param {string} [address2] - The secondary address of the account holder. Optional.
     * @param {string} [country] - The country of the account holder. Optional.
     * @param {string} [state] - The state of the account holder. Optional.
     * @param {string} [city] - The city of the account holder. Optional.
     * @param {string} [zipcode] - The zipcode of the account holder. Optional.
     * @param {string} [phone] - The phone number of the account holder. Optional.
     * @returns A response message from the API call and a 200 response code.
     */
    async api_update_account_details(
        name: string,
        email: string,
        password: string,
        title?: string,
        birth_date?: string,
        birth_month?: string,
        birth_year?: string,
        firstname?: string,
        lastname?: string,
        company?: string,
        address?: string,
        address2?: string,
        country?: string,
        state?: string,
        city?: string,
        zipcode?: string,
        phone?: string) {

        const params = new URLSearchParams();
        params.append("name", name);
        params.append("email", email);
        params.append("password", password);
        if (title) {
            params.append("title", title);
        }
        if (birth_date) {
            params.append("birth_date", birth_date);
        }
        if (birth_month) {
            params.append("birth_month", birth_month);
        }
        if (birth_year) {
            params.append("birth_year", birth_year);
        }
        if (firstname) {
            params.append("firstname", firstname);
        }
        if (lastname) {
            params.append("lastname", lastname);
        }
        if (company) {
            params.append("company", company);
        }
        if (address) {
            params.append("address1", address);
        }
        if (address2) {
            params.append("address2", address2);
        }
        if (country) {
            params.append("country", country);
        }
        if (state) {
            params.append("state", state);
        }
        if (city) {
            params.append("city", city);
        }
        if (zipcode) {
            params.append("zipcode", zipcode);
        }
        if (phone) {
            params.append("mobile_number", phone);
        }

        const response = await fetch("https://automationexercise.com/api/updateAccount", {
            method: 'PUT',
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Accept": "application/json"
            },
            body: params.toString()
        });
        return response;
    }


    /**
     * Given an email, delete the account via API.
     * 
     * @param {string} email - The email of the account holder.
     * @param {string} password - The password of the account holder.
     * @returns - A response message from the API call and a 200 response code.
     */
    async api_delete_account(email: string, password: string) {
        const params = new URLSearchParams();
        params.append("email", email);
        params.append("password", password);

        const response = await fetch("https://automationexercise.com/api/deleteAccount", {
            method: 'DELETE',
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Accept": "application/json"
            },
            body: params.toString()
        });
        return response
    }


    /**
     * Given an email and password, verify that the account can be logged in.
     * @param {string} email - The email of the account holder.
     * @param {string} password - The password of the account holder.
     * @returns - A response message from the API call and a 200 response code.
     */
    async api_login_account(email: string, password: string) {
        const params = new URLSearchParams();
        params.append("email", email);
        params.append("password", password);
        
        const response = await fetch("https://automationexercise.com/api/verifyLogin", {
            method: 'POST',
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Accept": "application/json"
            },
            body: params.toString()
        });
        return response;
    }

}