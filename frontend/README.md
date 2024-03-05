# Radu Baston - Frontend interview project

## About project

This project displays media content (movie/TV series/games) you can search by text. The displayed data will consist of the content data (name, year, poster) alongside
a random description fetched from a news api encapsulated together in a UI card. Features described are:

1. Search by text - the user can search the movie/series/game by text and card presenting content that matches that search will appear. Otherwise,
I chose to treat the `no results` case as an error, which would be shown below the `TextField` with an appropiate message.
2. Like/Dislike content - this feature allows the user to like/dislike different movies with persistance between sessions (persistance is done using local storage, so using incognito wouldn't work :) ). This is done by pressing the Like(d) button which would cause a like/dislike action (If the movie is liked and the user clicks on the text, it would cause a dislike on the content). The count of the total liked card is shown above the search input.
3. Share modal - th user can choose to share the IMDb url of the content selected by pressing the Share button which would cause a share modal to appear, where the user can copy the IMDb link (to clipboard).

## Technical description

Consumed APIs:

- [OMDB API](https://omdbapi.com/) - The Open Movie Database
- [News API](https://newsapi.org/) - Worldwide news

### Notes about implementation

- The fetch of the api is performed using `Promise.all` mechanism to benefit from concurrency on fetches.
- Tests were performed for only some of the components (I reckon other hooks/components are worth testing as well), but I was feeling I spent a lot of time on it (with building the features)

## Technical stack

**Technologies used**
 - Typescript
 - React
 - create-react-app
 - React Context
 - TailwindCSS
 - MaterialUI
 - React testing library
 - Eslint + Prettier
 - Pre commit hooks
 - yarn

## Get started

The project use yarn as package manager
Create a `.env.local` file in the `frontend/` folder and paste the following credentials 

```
export REACT_APP_MOVIE_API_URL='https://www.omdbapi.com'
export REACT_APP_MOVIE_API_KEY='f993c487'
export REACT_APP_MOVIE_API_PAGE_SIZE=10
export REACT_APP_DESCRIPTION_API_URL='https://newsapi.org/v2/everything'
export REACT_APP_DESCRIPTION_API_KEY='f9d6154053654ac0880d06bcdd9442b5'
```
The urls are needed as that, but if you want to create new credentials you are free to do so (or use those, otherwise).

Now you can run one of the commands available:
 - `yarn start` - starts the project locally
 - `yarn build` - build the project
 - `yarn test` - Runs the available test suites
