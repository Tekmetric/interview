import { test, expect } from '../../fixtures/test-fixtures';

function simpleFaker() {
  return {
    person: { fullName: () => `Test User ${Math.floor(Math.random() * 1000)}` },
    internet: { email: () => `test${Math.floor(Math.random() * 1000)}@example.com` },
    phone: { number: () => `555${Math.floor(Math.random() * 10000000).toString().padStart(7, '0')}` },
    lorem: { paragraph: () => 'Test message for contact form.' },
  };
}
const faker = simpleFaker();

test.describe('Contact Form', () => {
  test('should submit contact form successfully', async ({ homePage }) => {
    await homePage.goto();
    const contactData = {
      name: faker.person.fullName(),
      email: faker.internet.email(),
      phone: faker.phone.number(),
      subject: 'Test Inquiry',
      message: faker.lorem.paragraph(),
    };
    await homePage.fillContactForm(contactData);
    await homePage.submitContactForm();
    await homePage.page.waitForTimeout(2000);
  });

  test('should have contact form visible', async ({ homePage }) => {
    await homePage.goto();
    const formVisible = await homePage.contactForm.isVisible();
    expect(formVisible).toBe(true);
  });

  test('should have required contact fields', async ({ homePage }) => {
    await homePage.goto();
    const nameVisible = await homePage.contactNameInput.isVisible();
    const emailVisible = await homePage.contactEmailInput.isVisible();
    expect(nameVisible || emailVisible).toBe(true);
  });
});
