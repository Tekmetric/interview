# Tech Interview Project

## Steps to get started:

#### Fork the repository and clone it locally
- https://github.com/Tekmetric/interview.git

#### Let's install the project locally
`npm install`

#### Let's start the project locally
`npm start`

### Goals
1. Fetch Data from the backend Crud API you created or from a public API
2. Display data from API onto your page (Table, List, etc.)
3. Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)
4. Have fun

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview

## How I approaced the test.
I've tried to create as many talking points with the data as possible, around front end problems/patterns we might come up against. 

Firstly I've used a hook to grab the data. I've also stored some state in there, had it been more complex it would have deserved context, but it just required a calculated value that would never change.

There's a bunch of examples of changing UI with data/state. The UI/text on the page based on a single rockets current state, or the actual rendering of the widths and heights of the rocket model.

A small rendering cycle issue showed I set the rockets max calcualtions as undefined and check that in the component, as order of rendering meant I would render a rocket before that was set. If it was 0 it would render a base model rocket and then grow it to the right size, next render.

I've added a unit test to check the hook. I didn't unit test components as I'd normally cover them with e2e testing. Though happy to check they render and we're not breaking much. 

I stook with the tech stack provided (after some vulnerability updates) but really toyed with the idea of updating React, to give me access to the newer testing libraries (comment left in the test, around the act method)

