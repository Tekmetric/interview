@booking
Feature: this feature navigates to the booking website and makes a reservation

  @Test1
  Scenario: Navigate to the webpage and make a booking
    Given I navigate to the following url 'https://automationintesting.online/'
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
    When I click the 'returnHome' button


