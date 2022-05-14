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

describe('App', () => {
  it('renders loading spinner first', async () => {
    render(appComponent);
    await waitFor(() => screen.getByTestId('id-loading-spinner'));
  });

  it('renders two character cards (Rick, Morty) after loading', async () => {
    render(appComponent);
    await waitForElementToBeRemoved(() => screen.getByTestId('id-loading-spinner'));
    const cards = screen.getAllByRole('ccard');
    expect(cards).toHaveLength(2);
    expect(within(cards[0]).getByText(/Rick Sanchez/i)).toBeInTheDocument();
    expect(within(cards[1]).getByText(/Morty Smith/i)).toBeInTheDocument();
  });

  it('renders two pagination controls', async () => {
    render(appComponent);
    await waitForElementToBeRemoved(() => screen.getByTestId('id-loading-spinner'));
    const paginations = screen.getAllByLabelText('pagination navigation');
    expect(paginations).toHaveLength(2);
  });

  it('renders one card (Summer) in the next page', async () => {
    render(appComponent);
    await waitForElementToBeRemoved(() => screen.getByTestId('id-loading-spinner'));
    const paginations = screen.getAllByLabelText('pagination navigation');
    fireEvent.click(within(paginations[0]).getByLabelText('Go to page 2'));
    await waitForElementToBeRemoved(() => screen.getByTestId('id-loading-spinner'));
    const card = screen.getByRole('ccard');
    expect(card).toBeInTheDocument();
    expect(within(card).getByText(/Summer Smith/i)).toBeInTheDocument();
  });
});
