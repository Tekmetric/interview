import BasePage from "./BasePage";
import { expect, type Locator, type Page } from "@playwright/test";

type BookOnlineLocators = {
    checkInDate: Locator;
    checkOutDate: Locator;
    checkAvailability: Locator;
    bookNow1: Locator;
    bookNow2: Locator;
    bookNow3: Locator;
    reserveNow1: Locator;
    reserveNow2: Locator;
    firstNameText: Locator;
    lastNameText: Locator;
    emailText: Locator;
    phoneText: Locator;
    returnHome: Locator;
    confirmDialogue: Locator;
};

class BookOnlinePage extends BasePage {

    title: string = 'Restful-booker-platform demo';

    constructor(page: Page) {
        super(page); // Call the constructor of BasePage
    }

    getCurrentLocators(): BookOnlineLocators {
        // @ts-ignore
        // @ts-ignore
        return {
            checkInDate: this.page.getByRole('textbox').nth(0),
            checkOutDate: this.page.getByRole('textbox').nth(1),
            // checkAvailability: this.page.getByRole('button', { name:'Check Availability'}),
            checkAvailability: this.page.getByRole('button', { name: 'Check Availability' }),
            bookNow1: this.page.getByRole('link', { name:'Book now'}).nth(1),
            bookNow2: this.page.getByRole('link', { name:'Book now'}).nth(2),
            bookNow3: this.page.getByRole('link', { name:'Book now'}).nth(3),
            reserveNow1: this.page.locator('#doReservation'),
            reserveNow2: this.page.getByRole('button', {name: 'Reserve Now'}),
            firstNameText: this.page.locator('.room-firstname'),
            lastNameText: this.page.locator('.room-lastname'),
            emailText: this.page.locator('.room-email'),
            phoneText: this.page.locator('.room-phone'),
            returnHome: this.page.getByRole('button', { name:'Return home'}),
            confirmDialogue: this.page.getByRole('heading', { name: 'Booking Confirmed' }),
        };
    }

    async navigateToURL( url) {
        console.log(`Navigating to ${url}`);
        await this.navigateTo(url);
    }

    async interactWithPage(action, element ,value = null ) {
        await expect(this.page).toHaveTitle(this.title);
        const locators = this.getCurrentLocators();
        console.log(`Action: ${action}`);
        console.log(`Value: ${value}`);
        console.log(`Element: ${element}`);
        if (!locators[element]) {
            throw new Error(`Locator for "${element}" not found.`);
        }
        const cellLocator = locators[element];
        if (action === 'clickNselect') {
            await cellLocator.focus();
            await cellLocator.press('Control+A');
            await cellLocator.click();
        } else if (action === 'click') {
            await cellLocator.click();
        } else if (action === 'fill' && value !== null) {
            await cellLocator.fill(value);
        } else {
            throw new Error(`Invalid action "${action}" or missing
value for action "enter".`);
        }
    }

    async getTextFieldValue(element, value) {
        await expect(this.page).toHaveTitle(this.title);
        const locators = this.getCurrentLocators();
        console.log(`Value: ${value}`);
        console.log(`Element: ${element}`);
        if (!locators[element]) {
            throw new Error(`Locator for "${element}" not found.`);
        }
        const textValue = await locators[element].textContent();
        console.log(`text is : ${textValue}`);
        expect(textValue).toBe(value);
    }

}

export default BookOnlinePage;