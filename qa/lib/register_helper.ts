export class RegisterHelper {
    readonly randomNumber: string;
    readonly name: string;
    readonly email: string;
    readonly password: string;
    readonly firstname: string;
    readonly lastname: string;
    readonly address: string;
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
        this.firstname = 'first';
        this.lastname = 'last';
        this.address = '370 Congress St';
        this.country = 'United States';
        this.state = 'Massachusetts';
        this.city = 'Boston';
        this.zipcode = '02210';
        this.phone = '6174445555';
    }

    async api_create_account() {
        const params = new URLSearchParams();
        params.append("name", this.name);
        params.append("email", this.email);
        params.append("password", this.password);
        params.append("title", "Mr");
        params.append("birth_date", "1");
        params.append("birth_month", "January");
        params.append("birth_year", "2000");
        params.append("firstname", this.firstname);
        params.append("lastname", this.lastname);
        params.append("lastname", this.lastname);
        params.append("company", "");
        params.append("address1", this.address);
        params.append("address2", "");
        params.append("country", this.country);
        params.append("zipcode", this.zipcode);
        params.append("state", this.state);
        params.append("city", this.city);
        params.append("mobile_number", this.phone)

        const response = await fetch("https://automationexercise.com/api/createAccount", {
            method: 'POST',
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Accept": "application/json"
            },
            body: params.toString()
        });
    }
}