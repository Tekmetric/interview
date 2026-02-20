import {test, expect} from '../../fixtures'

test.describe('Admin Session', () => {
    test('should get access to admin panel with injected session cookie without UI login', async ({adminPage, page}) => {
        await expect(page).toHaveURL(/admin/)
        await expect(page.getByRole('button', { name: 'Logout' })).toBeVisible()
        await expect(page.getByText('Room #')).toBeVisible()
    })
})