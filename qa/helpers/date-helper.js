function addDays(date, days) {
  const next = new Date(date);
  next.setUTCDate(next.getUTCDate() + days);
  return next;
}

function toIsoDate(date) {
  return date.toISOString().slice(0, 10);
}

function toUiDate(isoDate) {
  const [year, month, day] = isoDate.split('-');
  return `${day}/${month}/${year}`;
}

function buildBookingWindow(checkinIso, nights) {
  const checkout = addDays(new Date(`${checkinIso}T00:00:00.000Z`), nights);
  const checkoutIso = toIsoDate(checkout);

  return {
    checkinIso,
    checkoutIso,
    checkinUi: toUiDate(checkinIso),
    checkoutUi: toUiDate(checkoutIso)
  };
}

module.exports = {
  addDays,
  toIsoDate,
  toUiDate,
  buildBookingWindow
};
