# Tech Interview Project

## Art Institute of Chicago artwork gallery

The application tries to mimic some museum-like vibe via:
- pseudo-3d styling
- glass-reflection parallax effect
- infinity horizontal scrolling (though it is a bad thing in terms of accessibility, I decided to keep horizontal scrolling to mimic a museum-like vibe + parallax effect)

The app uses https://api.artic.edu/docs/#introduction API to display images

Some technical challenges that were solved:
- artwork frame glass parallax effect
- avoiding layout shifts while images are loading by precalculating their sizes
- for some reason, some images requested via IDs from their API are broken, the app handles such broken images via a gray fallback image with a special icon. I decided to still output such images at least to show their data like author, date, etc.

## Steps to get started:

#### Let's install the project locally

`yarn`

#### Let's start the project locally

`yarn start`

#### Test project locally

`yarn test`
