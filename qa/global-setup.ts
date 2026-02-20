import { chromium } from '@playwright/test'
import { injectAdminSessionCookie } from './helpers/auth-helper'

async function globalSetup() {
    const browser = await chromium.launch()
    const context = await browser.newContext()
    const request = await context.request

    await injectAdminSessionCookie(request, context)
    await context.storageState({ path: 'storageState.json' })
    await browser.close()
}

export default globalSetup