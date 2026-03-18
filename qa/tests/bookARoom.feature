@booking
Feature: this feature navigates to the booking website and makes a reservation

  @ui
  Scenario: Navigate to the webpage and make a booking
    Given I navigate to the Booking url
    When I enter the check-in date '25/03/2026'
    When I enter the check-out date '28/03/2026'
    When I click the 'checkAvailability' button
    When I click the 'bookNow1' button
    When I click the 'reserveNow1' button
    When I enter the first name as 'Jae'
    When I enter the last name as 'Choe'
    When I enter the email as 'zzz@zzz.com'
    When I enter the phone as '123-456-7890'
    When I click the 'reserveNow2' button
    Then I verify the booking is confirmed with the following message 'Booking Confirmed'


  @api
  Scenario: API Calls to Book a Room
    Given I Create, Verify and Delete a room via api using firstname 'Jae', lastname 'Choe', totalPrice '100', depositPaid 'true', checkIn '2026-04-01', checkOut '2026-04-05', additionalNeeds 'breakfast'