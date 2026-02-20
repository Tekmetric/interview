import { faker } from '@faker-js/faker';
import { Booking } from './api-helper';
import { formatDate, getBookingDates } from './date-helper';

export function generateBookingData(overrides: Partial<Booking> = {}): Booking {
    const { checkIn, checkOut } = getBookingDates();

    return {
        firstname: faker.person.firstName(),
        lastname: faker.person.lastName(),
        totalprice: faker.number.int({ min: 50, max: 500 }),
        depositpaid: faker.datatype.boolean(),
        bookingdates: {
            checkin: formatDate(checkIn),
            checkout: formatDate(checkOut),
        },
        additionalneeds: faker.helpers.arrayElement(['Breakfast', 'Lunch', 'Dinner', 'None']),
        ...overrides,
    };
}


export function generateGuestDetails() {
    return {
        firstName: faker.person.firstName(),
        lastName: faker.person.lastName(),
        email: faker.internet.email(),
        phone: '07' + faker.string.numeric(9),
    };
}


export function generateContactFormData() {
    return {
        name: `${faker.person.firstName()} ${faker.person.lastName()}`,
        email: faker.internet.email(),
        phone: faker.string.numeric(11),
        subject: faker.lorem.sentence({ min: 3, max: 6 }),
        message: faker.lorem.paragraphs(1),
    };
}

export function generateBookingDates() {
    const offset = faker.number.int({ min: 60, max: 180 })
    return getBookingDates(offset, offset + 1)
}

export { formatDate, getBookingDates } from './date-helper'