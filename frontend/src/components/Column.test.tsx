// import '@testing-library/jest-dom';
// import { screen, render, waitFor } from '@testing-library/react';
// import Column from './Column';

// describe.skip('testing Column', () => {
//   afterEach(() => {
//     jest.resetAllMocks();
//   });
//   it('should render a list of dogs', async () => {
//     render(<Column />);
//     await waitFor(async () => {
//       const dogList = await screen.findByTestId('dog-list');

//       expect(dogList).toBeInTheDocument();
//     });
//   });

//   it('should not fail rendering', async () => {
//     global.fetch = jest.fn(() =>
//       Promise.resolve({
//         json: () => {
//           throw new Error('Testing the Error');
//         },
//       })
//     ) as jest.Mock;
//     render(<Column />);

//     await waitFor(async () => {
//       const dogList = await screen.queryByText('Dogs');
//       expect(dogList).not.toBeInTheDocument();
//     });
//   });
// });
