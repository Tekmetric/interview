export default class FormUtil {
    loading: boolean;
    message: string;

    public constructor(loading: boolean, message: string) {
        this.loading = loading;
        this.message = message;
    }
}