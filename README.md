# Alex Manzo FE Interview App

## Disney wait times

Deployed app: https://interview-sage-beta.vercel.app/

Note: The API powering this application is rate limited (300 requests per minute per client). There should be no reason that should be an issue here, but noting just in case.

Disney wait times is a simple web app that leverages the [themeparks.wiki API](https://themeparks.wiki/api) to display current wait times in the various parks at Disney World. This app was built for active park visitors in mind, so is designed for a mobile display.

The landing page allows the users to choose between the different parks available at Walt Disney World in Florida, then see a page of operating rides and their standby wait times. Users can sort the list by last updated time, wait time, or ride name. Additionally, they can search to filter the list by wait time.

## Use of AI tools

I had Github Copilot running in my IDE while developing this project and occasionally used autocompletions/corrections. If Copilot autocompleted an entire function or block of logic, I reviewed and in most cases rewrote it.

I did not use Copilot's chat feature or ChatGPT for this project.

Addtionally, I personally wrote this README without the use of AI.

## Local development

No API key is necessary for this API, so you can spin the application up without needing to set any ENV vars.

This runs on Node v22.20.0. You can automatically set your Node version by using `nvm use`.

You can run locally with `npm run dev`.

## Technology & libraries used

### Typescript, Vite

I initialized a new project usng Typescript and Vite at its core. I regularly use Typescript in my current role and prefer it in personal projects as it allows me to move more quickly with autocompletions in addition to more effectively catching potential errors.

Vite is the go-to build tooling in the indstury right now and what I used everyday. Outside of using a framework like Next, React recommends using Vite or a similar build tool for boostrapping projects.

### Tanstack Router & Query

I leveraged filed-based routing and used Tanstack Query to handle API calls.

I used [Tanstack's router quick start](https://tanstack.com/router/latest/docs/framework/react/quick-start) to scaffold the entire project with the file-router template. This sped along many of the chores in getting started.

### Components & styling

- **Components**: [shadcn](https://ui.shadcn.com/)
- **Styling**: Tailwind
- **Icons**: Lucide React

Tailwind is my personal favorite CSS solution, so I often use tools that integrate well with it.

I chose shadcn as a component library because it can easily be leveraged as a lightweight, low-level library with common-sense defaults. I appreciate that it's built on top of RadixUI primitives, which prioritize accessibility. With shadcn you can install components as-needed, avoiding needing to manage a heavy & opinionated library. Finally, I simply wanted to try it out and this seemed like the perfect opportunity.

By default, shadcn used Lucide for icons, so I opted into it.

## Improvements and next steps

The current state of this application can be classified as an MVP. If I were to continue, I'd prioritize addressing some technical debt before digging into new features.

In addition to items outlined below, as the app scaled I would also adopt a more pragmatic directory structure than the current state.

### Technical debt

#### Testing

I wrote a simple unit test for one component, but the project lacks testing coverage right now. My next step in development would be to write comprehensive unit tests (including a strong mock routing setup) for all views, components, and functions.

Additionally, I would eventually look to implement Playwright tests to cover full end-to-end testing. The flows in this applications aren't particularly complex, but I'd prefer to begin building them early and then iterating as the app scales.

#### Loading and error states

On first pass, I haven't given great care to error or loading states when it comes to fetching data. Before expanding functionality, I would go back to implemenet skeleton loading components and error states, as well as improved error handling.

#### Fine tune retries & polling

Since this app is meant to serve live data, it's set to refetch every 5 minutes per the API documentation's suggestion. I would like to spend more time on revalidation and retry settings as well as evaluating the refetch interval.

#### Repo improvements and developer tools

Currently the repo has no linting, which I would like to implement immediately. I frequently implement a linting step in with a [lint staged](https://github.com/lint-staged/lint-staged) pre-commit hook, along with Prettier to ensure consistent formatting across all developers.

#### SEO
I didn't address much in the way of SEO on the first pass, but would prioritize it more when building for deployment.

### Features

Initially, I'd like to add Park Detail and Ride detail pages. The API provided additional data such a `forecast` of wait times for a particular ride, operating hours, and specific status on if a ride is open, closed, or in refurbishment.

I'd also like to prioritize more user-friendly URL slugs, so a user could go directly to `/parks/magic-kingdom` for example.

Finally, the API powering this is robust, providing data not just for Disney, but for all major theme parks. In its current state, you could actually provide any `parkId` on the `/parks/:parkId` route and the data would (probably) load. It would be fun to expand the app to include other theme parks! The API also offers data for restaurants and shows, which would be great to provide even if the scope of the app remained Disney.
