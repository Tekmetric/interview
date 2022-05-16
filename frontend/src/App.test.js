import React from 'react';
import {
  render,
  fireEvent,
  waitFor,
  waitForElementToBeRemoved,
  screen,
  within,
} from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';

import App from './App';

const appComponent = (
  <BrowserRouter>
    <App />
  </BrowserRouter>
);

test('renders and searches characters, resets search results properly', async () => {
  render(appComponent);

  // renders loading spinner at first
  await waitForElementToBeRemoved(() => screen.getByTestId('id-loading-spinner'));

  // renders two character cards (Rick and Morty) after loading
  let cards = screen.getAllByRole('ccard');
  expect(cards).toHaveLength(2);
  expect(within(cards[0]).getByText(/Rick Sanchez/i)).toBeInTheDocument();
  expect(within(cards[1]).getByText(/Morty Smith/i)).toBeInTheDocument();

  // renders two pagination controls
  let paginations = screen.getAllByLabelText('pagination navigation');
  expect(paginations).toHaveLength(2);

  // renders one card (Summer) in the next page
  fireEvent.click(within(paginations[0]).getByLabelText('Go to page 2'));
  await waitForElementToBeRemoved(() => screen.getByTestId('id-loading-spinner'));
  const card = screen.getByRole('ccard');
  expect(card).toBeInTheDocument();
  expect(within(card).getByText(/Summer Smith/i)).toBeInTheDocument();

  // searches by name "Smith" to show two characters
  const searchInput = screen.getByTestId('id-input-character-name');
  fireEvent.change(searchInput, { target: { value: 'Smith' } });
  await waitFor(() => {
    cards = screen.getAllByRole('ccard');
    expect(cards).toHaveLength(2);
  });
  expect(within(cards[0]).getByText(/Morty Smith/i)).toBeInTheDocument();
  expect(within(cards[1]).getByText(/Summer Smith/i)).toBeInTheDocument();

  // searches by name "Wax" to display nobody message
  fireEvent.change(searchInput, { target: { value: 'Wax' } });
  await waitFor(() => expect(screen.getByTestId('id-message-nobody')).toBeInTheDocument());

  // taps clear button to reset results
  fireEvent.click(screen.getByTestId('id-button-clear-search'));
  await waitForElementToBeRemoved(() => screen.getByTestId('id-loading-spinner'));
  cards = screen.getAllByRole('ccard');
  expect(within(cards[0]).getByText(/Rick Sanchez/i)).toBeInTheDocument();
  expect(within(cards[1]).getByText(/Morty Smith/i)).toBeInTheDocument();
});
