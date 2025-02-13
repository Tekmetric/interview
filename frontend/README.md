# Tekmetric Frontend Interview

## Metro Buddy, a WMATA Metro Tracker

Inspired by (but nowhere near enough features as) [MetroHero](https://dcmetrohero.net/dashboard).

### Steps to get started:

1. Go to https://developer.wmata.com/signup and create an account to get your own WMATA API key.
2. Run `cp .env.template .env.local` to create a new, local environment file.
3. Add your API key to the `.env.local` file as `VITE_WMATA_API_KEY`.
4. Install the dependencies of the project by running `yarn`.
5. Run the project locally by running `yarn start`.

### Running tests

Simply run `yarn test` to run the tests.