# Tech Interview Project - Raul's Shop

## Description

### Overview
Raul's Shop is a frontend application designed to fetch and display data from a backend API. The primary objective of this project is to demonstrate the ability to interact with APIs, manage state, and apply styling to create a visually appealing user interface.

### Features
- **Data Fetching**: The application fetches products and cart data from [dummyjson.com](https://dummyjson.com/docs/products) public API.
- **Data Display**: The fetched data is displayed on the Products page in a paginated grid structure.
  - Users can search products by name.
  - I'm using [@tanstack/react-query](https://tanstack.com/query/latest/docs/framework/react/overview) to fetch and cache paginated data from the API.
  - In oder to improve the performance of the application, I'm using [react-window](https://react-window.vercel.app/#/examples/grid/fixed-size) to render the products in a virtualized grid.
- **Add to Cart**: Users can add products to their cart from product detail page. Each product has an "Add to cart" button that, when clicked, adds the product to the user's cart.
- **Manage Cart Data**: The application allows users to view and manage the items in their cart. Users can:
  - View the products in their cart.
  - Increase or decrease the quantity of a product in their cart.
  - Remove a product from their cart.
- **Styling**: The application uses [tailwindcss](https://tailwindcss.com/docs/styling-with-utility-classes) to style the user interface. The styling is minimalistic and focuses on providing a clean and modern look. Additionally, the layout is responsive, ensuring that the application is accessible and visually appealing on various devices and screen sizes.



## Steps to get started:

#### Let's install the project locally
`npm install`

#### Let's start the project locally
`npm run dev`

### Goals
1. Fetch Data from the backend Crud API you created or from a public API
2. Display data from API onto your page (Table, List, etc.)
3. Apply a styling solution of your choice to make your page look different (CSS, SASS, CSS-in-JS)
4. Have fun

### Submitting your coding exercise
Once you have finished the coding exercise please create a PR into Tekmetric/interview
