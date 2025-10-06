# Pokédex Application

Vercel deployment [https://frontend-h0j5decnv-jonyens-projects.vercel.app/]

Project summary:

NOTE: this entire section was written by hand and not AI-generated

I initially built out this project manually without the use of AI, setting up a Promise-based fetch to the Pokemon API to retrieve the initial 150 Pokemon and displaying it in a simple table, showing the Pokedex number, the image of the Pokemon, and the name and type of the Pokemon. To display the data, I used React Table Library [https://react-table-library.com/?path=/story/getting-started-installation--page]. For the purpose of this coding exercise, I felt that would have been sufficient.

Then I spun up Claude CLI and that's when things got fun (and crazy):
* Requested that we implement virtualization to handle larger chunks of data (and subsequently changed the fetch to all 1025 Pokemon) - this put react-window in place of React Table Library
* Tweaked the CSS styling to be more visually appealing - padding and spacing of rows
* Moved table cell, header, body into their own respective components
* Added a background
* Added a loading spinner
* Internationalization of weight/height based on user location
* Displayed Pokemon stats in an inline BarChart
* Implemented caching of results into local storage
* Find-in-browser didn't work, so a search bar was added and modified Ctrl-F to redirect to the search bar
* Configured and implemented Tailwind CSS
* Abstracted out keyboard logic into a KeyboardNavigation wrapper so that it could potentially be used in other components
* Implemented unit tests
* Set up dark mode
* Added ErrorBoundary for improved error handling
* Storybook components for better testing
* Clean up code smells
* Internationalization of strings in the application
* Created a logger component so that console.logs don't show up in production
* Monitoring via Sentry
* Upgrade to React 18
* Migrated to TypeScript, added specific types in the app
* Set up Redux for state management
* 80%+ Code coverage
* Pre-commit checks via husky
* Github actions to deploy app into Vercel

Much of my process was based off of general intuition, considering best practices in a modern web application and from previous programming experience. I believe that there is much more that could be implemented, so out of consideration for your (the reviewer's) time, I decided that I needed to draw the line somewhere and submit what I have. Thank you very much for your time and effort in reviewing this.

