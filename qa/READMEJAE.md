### High level framework architecture
    I created a framework by separating out the page objects / helpers, the test steps, and the BDD Cucumber Feature 
### Key design decisions and reasoning
    It's easier to maintain, easier to understand with self documenting steps, and easier to scale
### How UI and API tests interact in your approach
    They are separated out and can be run independently
### How test data and dates are managed
    URL and key data is stored in a config file, if you need to obfuscate the data, you can add it to gitingore
### How tests are executed locally
    You can run the tests locally in the terminal with  npx cucumber-js --tags @api and @ui
### How you would run this in CI
    I would create a paramaeterized Jenkins Job that runs the test in a pipeline, with downstream jobs for data resetting and version control