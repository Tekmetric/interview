import { When } from '@cucumber/cucumber';
import 'dotenv/config';
import BookOnlineApi from '../Pages/BookOnlineApi';



When(/^I Create, Verify and Delete a room via api using firstname '(.*?)', lastname '(.*?)', totalPrice '(.*?)', depositPaid '(.*?)', checkIn '(.*?)', checkOut '(.*?)', additionalNeeds '(.*?)'$/,
    async function (firstName, lastName, totalPrice, depositPaid, checkIn, checkOut, additionalNeeds) {
        const createResponse = await new BookOnlineApi().createApi(firstName, lastName, totalPrice, depositPaid, checkIn, checkOut, additionalNeeds);
        const bookingId = createResponse.data.bookingid;
        await new BookOnlineApi().verifyApi(bookingId, firstName, lastName)
        await new BookOnlineApi().deleteApi(bookingId)
    });



