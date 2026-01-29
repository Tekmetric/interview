# Playwright Automation Framework Exercise

## Testing Website
https://automationintesting.online/

## Repository
https://github.com/mwinteringham/restful-booker-platform  
https://github.com/mwinteringham/restful-booker  

## API Docs
https://restful-booker.herokuapp.com/apidoc/index.html  



## Purpose of This Exercise

This exercise is designed to evaluate how you **design, structure, and reason about test automation**, not just how many tests you write.

We care about:
- Framework design and maintainability
- Thoughtful test strategy and layering
- Reliable state and data management
- Debuggability and CI readiness
- Your ability to explain tradeoffs and decisions

Quality and clarity matter more than volume.

Time expectation: 3 days. Do not overbuild.




## UI Automation Goals

### Booking Flow End to End
1. Select a room  
2. Pick check in and check out dates  
3. Enter guest details  
4. Submit booking  
5. Assert confirmation state in the UI  

### One Additional UI Scenario That Adds Real Coverage
Pick any additional scenario(s) of your choosing. This is an opportunity to demonstrate your QA judgement and how you think about risk and coverage.



## API Automation Goals

### Booking API Coverage
Automate at least two of the following operations:
1. Create booking  
2. Get booking  
3. Update booking  
4. Delete booking  



## Bonus Goal (Optional)

### Admin Authentication via API and Browser Session Reuse
Authenticate as an admin via API, store the authenticated session in the browser, and access an admin only URL without performing a UI login flow.

Notes:
- This application uses cookie based authentication with a `token` cookie
- The goal is to demonstrate clean session reuse and state management
- You do not need to automate the admin login UI

---

## Architecture and Design Expectations

We are intentionally leaving parts of this exercise open ended to understand how you approach automation design.

As part of your solution, be prepared to discuss:
- How your framework is structured and why
- What logic lives in tests vs fixtures vs helpers
- How authentication and state are handled
- How your approach reduces flake and maintenance cost
- What tradeoffs you made due to time constraints
- How this framework would scale with more tests and contributors



## Required Documentation

Include a README section in your submission that covers:

- High level framework architecture  
- Key design decisions and reasoning  
- How UI and API tests interact in your approach  
- How test data and dates are managed  
- How tests are executed locally  
- How you would run this in CI  
- What you would improve next with more time  

Keep this concise. One to two pages max.


## Interview Discussion Topics

During the interview, you should be prepared to walk through:

- Framework structure and extensibility  
- Fixture and setup strategy  
- Authentication and state control  
- Test strategy and layering decisions  
- Flake prevention and waiting strategy  
- Debugging a failing test using available artifacts  
- How this would fit into a CI pipeline  

You may be asked to make a small change or explain how you would extend the framework.

