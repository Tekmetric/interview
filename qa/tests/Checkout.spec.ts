import { test, expect } from '@playwright/test';
import { fillForm, clickElement } from '../util/Reusable_Methods/Reusable_Methods';


//Checkout functionality  test on 'https://www.automationexercise.com/'
test('Single Item Shopper', async ({ page }) => {
    
    //navigate to the website
    await page.goto('https://www.automationexercise.com/');
    //click on the 'Signup / Login' button
    await clickElement(page, '[href="/login"]');
    //filling the email and password fields
    await fillForm(page, '[data-qa="login-email"]', 'lenny@testing.com');
    await fillForm(page, '[data-qa="login-password"]', '1234567890');
    //clicking on login button
    await clickElement(page, '[data-qa="login-button"]');
    //Verify Login successful
    await expect(page.locator('text=Logged in as')).toBeVisible();
    console.log('Login successful');
    //scroll to to womens section
    const btn = page.locator('[class="badge pull-right"]').first();
    await btn.scrollIntoViewIfNeeded();
    await btn.click();
   //clicking on the 'Womens' category
    await clickElement(page, '[href="/category_products/1"]');
    //Clicking Add to cart button for 3rd product Rose Pink Embroidered Maxi Dress
    const thirdProduct = page.locator('[class="btn btn-default add-to-cart"]').nth(4);
    await thirdProduct.scrollIntoViewIfNeeded();
    await thirdProduct.click();
    //clicking on view cart button
    await page.locator('[href="/view_cart"]').nth(1).click();
    //clicking on proceed to checkout button
    await clickElement(page, '[class="btn btn-default check_out"]')
    //Capturing the text of billing address and shipping address
    const Address = await page.locator('[data-qa="checkout-info"]').textContent();
    console.log('Address verified');
    //Capturing Item Decription, Price, Quantity, and Total
    const CartInfo = await page.locator('[id="cart_info"]').textContent();
    console.log('Cart info captured'); 
    //Inputting text into the comment box
    await fillForm(page, '[name="message"]', 'Please deliver between 9 AM to 5 PM');
    //Clicking on Place Order button
    await clickElement(page, '[class="btn btn-default check_out"]');
    //Fill in payment details
    await fillForm(page, '[name="name_on_card"]', 'Lenny Smith');
    await fillForm(page, '[name="card_number"]', '1234 5678 9123 0000');
    await fillForm(page, '[name="cvc"]', '123');
    await fillForm(page, '[name="expiry_month"]', '12');
    await fillForm(page, '[name="expiry_year"]', '2025');
    //clicking on Pay and Confirm Order button
    await clickElement(page, '[id="submit"]');
    //verifying the Order Placed message
    const orderPlaced = await page.locator('[data-qa="order-placed"]').textContent();
    expect(orderPlaced).toContain('Order Placed!');
    console.log('Order Placed successfully');

});//end of test 1 


//Checkout functionality with several items test on 'https://www.automationexercise.com/
test('Checkout Several Items', async ({ page }) => {
    
    //navigate to the website
    await page.goto('https://www.automationexercise.com/');
    //click on the 'Signup / Login' button
    await clickElement(page, '[href="/login"]');
    //filling the email and password fields
    await fillForm(page, '[data-qa="login-email"]', 'lenny@testing.com');
    await fillForm(page, '[data-qa="login-password"]', '1234567890');
    //clicking on login button
    await clickElement(page, '[data-qa="login-button"]');
    //Verify Login successful
    await expect(page.locator('text=Logged in as')).toBeVisible();
    console.log('Login successful');

    //initialize an array with the index of products to be added to cart
    const productsindex = [2, 4, 6];
    //create a loop to add multiple products to cart by index and scrolling into view
    for (let i = 0; i < productsindex.length; i++) {
        const productButton = page.locator('[class="btn btn-default add-to-cart"]').nth(productsindex[i]);
        await productButton.scrollIntoViewIfNeeded();
        await productButton.click();
        //clicking on continue shopping button
        await clickElement(page, '[class="btn btn-success close-modal btn-block"]');
        
    }//end of for loop
       
        //scroll to view cart button and click it
        const cartBtn = page.locator('[href="/view_cart"]').nth(0);
        await cartBtn.scrollIntoViewIfNeeded();
        await cartBtn.click();
        //clicking on proceed to checkout button
        await clickElement(page, '[class="btn btn-default check_out"]')
        //Verifying the total price of all items in the cart
        await expect(page.locator('text=Total Amount')).toBeVisible();
        console.log('Total amount verified');
        //clicking on Place Order button
        await clickElement(page, '[class="btn btn-default check_out"]');
        //Fill in payment details
        await fillForm(page, '[name="name_on_card"]', 'Lenny Smith');
        await fillForm(page, '[name="card_number"]', '1234 5678 9123 0000');
        await fillForm(page, '[name="cvc"]', '123');
        await fillForm(page, '[name="expiry_month"]', '12');
        await fillForm(page, '[name="expiry_year"]', '2025');
        //Paying and confirming the order
        await clickElement(page, '[id="submit"]');
        //Verifying the order was placed
        const orderPlaced = await page.locator('[data-qa="order-placed"]').textContent();
        expect(orderPlaced).toContain('Order Placed!');
        console.log('Order Placed successfully');

});//end of test2

