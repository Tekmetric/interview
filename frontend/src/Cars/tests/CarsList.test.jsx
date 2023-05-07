import renderer from 'react-test-renderer';
import CarsList from '../components/CarsList';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from 'react-query';
import { CARS } from '../../shared/constants';
import { render, fireEvent } from '@testing-library/react';
import nock from 'nock';
import axios from 'axios';
import httpAdapter from 'axios/lib/adapters/http';

const queryClient = new QueryClient();
// eslint-disable-next-line react/prop-types
const wrapper = ({ children }) => (
  <BrowserRouter>
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  </BrowserRouter>
);

const host = 'http://localhost:3001';
axios.defaults.host = host;
axios.defaults.adapter = httpAdapter;

nock('http://localhost:3001')
  .get('/cars')
  .query({ page: 1 })
  .reply(200, { cars: CARS, totalPages: 2 });

nock('http://localhost:3001')
  .get('/cars')
  .query({ page: 1, brand: 'bmw', color: 'all' })
  .reply(200, { cars: CARS.filter((car) => car.brand.toLowerCase() === 'bmw'), totalPages: 2 });

nock('http://localhost:3001')
  .get('/cars/brands')
  .reply(200, [
    { key: 'all', value: 'All' },
    { key: 'audi', value: 'Audi' },
    { key: 'bmw', value: 'BMW' }
  ]);

nock('http://localhost:3001')
  .get('/cars/colors')
  .reply(200, [
    { key: 'all', value: 'All' },
    { key: 'black', value: 'Black' },
    { key: 'gray', value: 'Gray' }
  ]);

describe('rendering list of cars', () => {
  it('matches the snapshot', () => {
    const component = renderer.create(wrapper(<CarsList />));

    let tree = component.toJSON();
    expect(tree).toMatchSnapshot();
  });

  it('renders a list of cars', async () => {
    const result = render(<CarsList />, { wrapper });

    for (let car of CARS) {
      await result.findByText(`${car.brand} - ${car.model}`);

      expect(result.getByText(`${car.brand} - ${car.model}`)).toBeTruthy();
    }
  });

  it('renders a list of cars filtered by brand', async () => {
    const result = render(<CarsList />, { wrapper });

    await result.findByLabelText('Brand');

    const brandSelector = document.querySelector('input.MuiSelect-nativeInput');
    fireEvent.change(brandSelector, { target: { value: 'bmw' } });

    await result.findAllByText('BMW - 3 Series');
    expect(result.getAllByText('BMW - 3 Series').length).toEqual(1);
  });
});
