import {test, expect} from '../../fixtures'
import { generateGuestDetails, generateBookingDates, formatDate } from '../../helpers/test-data'


test.describe('Booking Flow End to End', () => {
    test('should be able to complete end to end room booking', async ({homePage, reservationPage}) =>{
    
        const {checkIn, checkOut} = generateBookingDates()

        const guest = generateGuestDetails()

        //selecting a room
        await homePage.bookRoom('Single')

        //selecting date range
        await reservationPage.navigatingToMonth(checkIn)
        
        await reservationPage.selectDateRange(checkIn.day, checkOut.day)

        await expect(reservationPage.selectedDateRange).toBeVisible()

        //open guest detail form
        await reservationPage.openGuestDetailsForm()
        
        //filling out guest details
        await reservationPage.fillOutGuestDetails(guest)

        //submit booking
        await reservationPage.submitBooking()

        await expect(reservationPage.confirmationHeading).toBeVisible()

        const expectedDates = `${formatDate(checkIn)} - ${formatDate(checkOut)}`
        await expect(reservationPage.confirmedDates).toContainText(expectedDates)

        await expect(reservationPage.returnHomeButton).toBeVisible()  
    })
})