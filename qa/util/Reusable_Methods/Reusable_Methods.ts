import { Page } from '@playwright/test';

export async function fillForm(page: Page, selector: string, value: string) {
  await page.locator(selector).fill(value);
}//end of fillForm

export async function clickElement(page: Page, selector: string) {
  await page.locator(selector).click();
}//end of clickElement 
