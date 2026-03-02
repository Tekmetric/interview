import {Given, When, Then,} from '@cucumber/cucumber';
import BookOnlinePage from "../Pages/BookOnlinePage";
import {appConfig} from "../Utilities/env";

// ... existing code ...

// setDefaultTimeout(60 * 1000); // Set the default timeout to 60 seconds

let bookOnlinePage;

Given(/^I navigate to the Booking url$/, async function () {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.navigateToURL(appConfig.baseUiURL);
    await this.page.waitForLoadState('networkidle');
});

// Match the feature's wording (and do it case-insensitively to avoid future casing breakages)
When(/^I enter the check-in date '([^']+)'$/i, async function (date: string) {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.interactWithPage('clickNselect', 'checkInDate' );
    await this.bookOnlinePage.interactWithPage('fill', 'checkInDate', date );
});

When(/^I enter the check-out date '([^']+)'$/i, async function (date: string) {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.interactWithPage('clickNselect', 'checkOutDate' );
    await this.bookOnlinePage.interactWithPage('fill', 'checkOutDate', date);
});

When(/^I click the '([^']+)' button$/i, async function (button: string) {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.interactWithPage('click', button);
});

When(/^I enter the first name as '([^']+)'$/i, async function (fname: string) {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.interactWithPage('click', 'firstNameText');
    await this.bookOnlinePage.interactWithPage('fill', 'firstNameText', fname);
});

When(/^I enter the last name as '([^']+)'$/i, async function (lname: string) {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.interactWithPage('click', 'lastNameText');
    await this.bookOnlinePage.interactWithPage('fill', 'lastNameText', lname);
});

When(/^I enter the email as '([^']+)'$/i, async function (email: string) {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.interactWithPage('click', 'emailText');
    await this.bookOnlinePage.interactWithPage('fill', 'emailText', email);
});

When(/^I enter the phone as '([^']+)'$/i, async function (phone: string) {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.interactWithPage('click', 'phoneText');
    await this.bookOnlinePage.interactWithPage('fill', 'phoneText',  phone);
});

Then(/^I verify the booking is confirmed with the following message '([^']+)'$/i, async function (message: string) {
    this.bookOnlinePage = this.bookOnlinePage || bookOnlinePage || new BookOnlinePage(this.page);
    await this.bookOnlinePage.getTextFieldValue('confirmDialogue', message);
});
