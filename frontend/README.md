# Anime List

## Stack
- FE app:
  - React v19
  - TS v5
  - React DOM v19
  - RTK v2
  - MUI v7
  - styled-components v6
  - proxy-memoize v3

## Instructions
- Install the dependencies
  - npm install
- Start the FE app:
  - npm run dev 

## Functionality 
- Infinite scroll data grid that fetches paginated anime listings data from a public API
- Supports paginated sorting on 3 columns
- Clicking on a row will redirect to another page, that contains the Anime Listing
- Test setup with testing-library
- Theme passed through styled-components's ThemeProvider

## Resources
- Public API: 
  - https://jikan.moe/ 
  - https://docs.api.jikan.moe/ 
- Images:
  - anime_background: https://www.freepik.com/free-ai-image/anime-style-clouds_94937388.htm#fromView=keyword&page=1&position=10&uuid=f9c8c631-8d99-434c-8e99-2e231e868e2b&query=Anime+background 
  - anime_icon: https://www.flaticon.com/free-icon/animate_5261267?term=anime&page=1&position=4&origin=tag&related_id=5261267 
  -  error_icon: https://www.freepik.com/icon/error_10633473#fromView=keyword&page=1&position=41&uuid=b64a59c8-fc9e-4241-8be0-915b66762ecf
  - empty_icon: https://www.freepik.com/icon/delete_1388968#fromView=search&page=1&position=38&uuid=b375e5dd-8d03-45e4-9692-4e0bb167bac4
- Color pallete generator from image:
  - https://color.adobe.com/create/image 

## Further Improvements
- Enhance table scroll
- Add table virtualization
- Table sorting: changing sorting order or column does not currently reset the scrollTop
- Expand the theme tokens, add more themes and a theme selector
- Expand Anime Listing Page
- Make the table responsive
- Add specific errors for each API
- Add ErrorBoundary
- Refactor TableRow
- Use const and enums for all the magic strings
- Refactor types