/**
 * This returns a future date in an object {year, month, day}
 * @param daysFromNow - how many days from the future
 */
function getFutureDate(daysFromNow: number): {
  year: number;
  month: number;
  day: number;
} {
  const date = new Date();
  date.setDate(date.getDate() + daysFromNow);
  return {
    year: date.getFullYear(),
    month: date.getMonth() + 1,
    day: date.getDate(),
  };
}

/**
 * @param checkInOffset default 
 * @param checkOutOffset default
 * @returns check-in and check-out pair
 */
export function getBookingDates(checkInOffset = 30, checkOutOffset = 31){
    return {
        checkIn: getFutureDate(checkInOffset),
        checkOut: getFutureDate(checkOutOffset)
    };
}

/**
 * Formatting date object as YYYY-MM-DD for handling API payloads
 */
export function formatDate({year, month, day}:{year: number; month: number; day: number}): string {
    return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2,'0')}`
}
