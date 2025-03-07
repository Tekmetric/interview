export default interface User {
    firstName: string | null;
    lastName: string | null;
    email: string | null;
    admin: boolean | null;
}

export interface UserRegistrationData {
    firstName: string | null;
    lastName: string | null;
    email: string | null;
    password: string | null;
}
