import { Locator, Page } from '@playwright/test'

export class ContactComponent {
    readonly nameInput: Locator;
    readonly emailInput: Locator;
    readonly phoneInput: Locator;
    readonly subjectInput: Locator;
    readonly messageInput: Locator;
    readonly submitButton: Locator;
    readonly thanksConfirmationMessage: Locator;
    readonly validationError: Locator;

    constructor(private page: Page) {
        const section = page.locator('#contact')

        this.nameInput = section.getByTestId('ContactName')
        this.emailInput = section.getByTestId('ContactEmail')
        this.phoneInput = section.getByTestId('ContactPhone')
        this.subjectInput = section.getByTestId('ContactSubject')
        this.messageInput = section.getByTestId('ContactDescription')
        this.submitButton = section.getByRole('button', { name: 'Submit' })
        this.thanksConfirmationMessage = page.getByRole('heading', { name: /Thanks for getting in touch/ })
        this.validationError = section.locator('.alert.alert-danger')
    }

    async fillAndSubmit({ name, email, phone, subject, message }: {
        name: string;
        email: string;
        phone: string;
        subject: string;
        message: string;
    }) {
        await this.nameInput.fill(name)
        await this.emailInput.fill(email)
        await this.phoneInput.fill(phone)
        await this.subjectInput.fill(subject)
        await this.messageInput.fill(message)
        await this.submitButton.click()
    }
}