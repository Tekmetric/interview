# Project for Tech Interviews

## Near Earth Object (NEO) Viewer

NEO Viewer is a simple app that displays all asteroids on a given day that come within 1.3 AU of Earth, and thus are classified by NASA as "Near Earth Objects". More information about NEOs can be found [here](https://cneos.jpl.nasa.gov/about/basics.html).

This app uses NASA's NeoW (Near Earth Object Web Service) REST API which provides data from the NASA JPL Asteroid team. The API is maintained by the [SpaceRocks Team](https://github.com/SpaceRocks/).

### Running Locally

1. Clone repo.
2. Run `npm install` within the `frontend` directory.
3. Run `npm start`
4. Optionally, obtain an API key from https://api.nasa.gov/, or ask Claire for their key. (The included DEMO_KEY has a lower rate limit.)
5. Enjoy!
