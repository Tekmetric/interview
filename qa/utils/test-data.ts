function getRandomInt(min: number, max: number): number {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

export interface GuestDetails {
  firstname: string;
  lastname: string;
  email: string;
  phone: string;
}

export interface BookingDates {
  checkin: string;
  checkout: string;
}

export function generateGuestDetails(): GuestDetails {
  const randomId = getRandomInt(1000, 9999);
  const phoneNum = `555${getRandomInt(10000000, 99999999)}`;
  return {
    firstname: `FirstName${randomId}`,
    lastname: `LastName${randomId}`,
    email: `test${randomId}@example.com`,
    phone: phoneNum,
  };
}

function getRandomDateWithinRange(startDate: Date, endDate: Date): Date {
  const randomTime = startDate.getTime() + Math.random() * (endDate.getTime() - startDate.getTime());
  return new Date(randomTime);
}


export function generateBookingDates(): BookingDates {
  const currentDate = new Date();


  const oneMonthLater = new Date(currentDate);
  oneMonthLater.setMonth(currentDate.getMonth() + 1);


  const checkin = getRandomDateWithinRange(currentDate, oneMonthLater);

  
  const checkout = new Date(checkin);
  checkout.setDate(checkout.getDate() + 1); 

  return {
    checkin: checkin.toISOString().split('T')[0], 
    checkout: checkout.toISOString().split('T')[0], 
  };
}
