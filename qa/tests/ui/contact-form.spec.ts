import {test, expect} from '../../fixtures'
import { generateContactFormData } from '../../helpers/test-data'

test.describe('Contact Form', () => {
    test('should successfully submit contact message form', async ({contactComponent}) => {
        const contact = generateContactFormData()
        await contactComponent.fillAndSubmit(contact)

        await expect(contactComponent.thanksConfirmationMessage).toBeVisible()
        await expect(contactComponent.thanksConfirmationMessage).toContainText('Thanks for getting in touch')
        await expect(contactComponent.thanksConfirmationMessage).toContainText(contact.name)
    })
    test('should show errors when trying to submit an empty form', async ({contactComponent}) => {
        await contactComponent.submitButton.click()
        await expect(contactComponent.validationError).toBeVisible()
        await expect(contactComponent.validationError).toContainText('Name may not be blank')
        await expect(contactComponent.validationError).toContainText('Email may not be blank')
        await expect(contactComponent.validationError).toContainText('Phone may not be blank')
        await expect(contactComponent.validationError).toContainText('Subject may not be blank')
        await expect(contactComponent.validationError).toContainText('Message may not be blank')
    })
})