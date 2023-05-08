# Tech Interview Project

## Steps to get started:

#### Clone the poject locally
`git clone https://github.com/tfuqua/interview.git`

#### Create your own git branch
`git checkout -b [name_of_your_new_branch]`

#### Push your branch up to git
`git push origin [name_of_your_new_branch]`

#### Let's install the project locally
- `cd ./frontend`
- `npm install`

#### Let's start the project locally
`npm run dev`

### Goals
1. Fetch Data from the backend Crud API you created or from a public API
2. Display data from API onto your page (Table, List, etc.)
3. Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)
4. Have fun


## Rijksmuseum virtual tour

The API we picked for this challenge is the [Rijks data API](https://data.rijksmuseum.nl/object-metadata/api).

We created a simple React Application that allows the user to view Art pieces from the Rijksmuseum by providing the following features:

- Displaying a custom set of art objects based on the user input
  - Navigate to the [homepage](http://localhost:5173/) (by default we display the first objects returned by the API)
  - The list can be customized by selecting from a fixed list of Authors or by inputting a title(substrings also work)
  - We use the [Collection API](https://data.rijksmuseum.nl/object-metadata/api/#collection-api) for all operations described in this feature
- Displaying a detailed view of a certain art object
  - By clicking on an art object from the homepage we navigate to the `/details` page of that art object
  - The user can see more information about the art piece like the date of creation and a brief analysis.
  - We use the [Collection Details API](https://data.rijksmuseum.nl/object-metadata/api/#collection-details-api) to fetch the information referenced above

