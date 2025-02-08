import { test, expect } from '@playwright/test'

test.describe('SpaceX Launch Dashboard', () => {
  test('should display all sections and rocket details', async ({ page }) => {
    // Navigate to the home page and wait for it to fully load
    await page.goto('/', { waitUntil: 'load' })
    await page.waitForTimeout(2000)

    // Check for the presence of all main sections
    await expect(page.getByTestId('header')).toBeVisible()
    await expect(page.getByTestId('next-launch-section')).toBeVisible()
    await expect(page.getByTestId('latest-launch-section')).toBeVisible()
    await expect(page.getByTestId('launches-section')).toBeVisible()
    await expect(page.getByTestId('rockets-section')).toBeVisible()

    // Interact with the rockets section
    const rocketsSection = page.getByTestId('rockets-section')
    await expect(rocketsSection).toBeVisible()

    // Get all rocket cards
    const rocketCards = await rocketsSection.getByTestId('rocket-card').all()
    expect(rocketCards.length).toBeGreaterThan(0)

    // Select a random rocket card
    const randomIndex = Math.floor(Math.random() * rocketCards.length)
    const randomRocketCard = rocketCards[randomIndex]

    // Click on the random rocket card to open details
    await randomRocketCard.getByRole('button', { name: 'View Details' }).click()

    // Wait for the rocket details modal to appear
    const rocketDetailsModal = page.getByTestId('rocket-modal-content')
    await expect(rocketDetailsModal).toBeVisible()

    // Check for the presence of rocket data
    await expect(
      rocketDetailsModal.getByTestId('rocket-modal-title')
    ).toBeVisible()
    await expect(
      rocketDetailsModal.getByTestId('rocket-description')
    ).toBeVisible()

    // Close the rocket details modal
    await rocketDetailsModal.getByRole('button', { name: 'Close' }).click()
    await expect(rocketDetailsModal).not.toBeVisible()
  })
})
