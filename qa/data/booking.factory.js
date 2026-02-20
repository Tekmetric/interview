const { addDays, toIsoDate } = require('../helpers/date-helper');

function buildBookingPayload(seed) {
  const checkin = addDays(new Date(), 30);
  const checkout = addDays(checkin, 2);

  return {
    firstname: `Api${seed}`,
    lastname: `User${seed}`,
    totalprice: 222,
    depositpaid: true,
    bookingdates: {
      checkin: toIsoDate(checkin),
      checkout: toIsoDate(checkout)
    },
    additionalneeds: 'Breakfast'
  };
}

module.exports = { buildBookingPayload };
