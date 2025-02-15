import { MetroStationCodes } from '@components/routes/MetroStationCodes';
import { screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { otherTrains } from '../../../tests/data/wmata';
import { renderWithQueryClient } from '../../../tests/utils';

const paramsObject = {
  stationCodes: 'All'
};
const searchObject = {
  line: ''
};
describe('metroStationCodes component', () => {
  beforeAll(() => {
    vi.mock('@tanstack/react-router', async () => {
      const actual = await vi.importActual('@tanstack/react-router');
      return {
        ...actual,
        createFileRoute: () => () => ({
          useParams: () => paramsObject,
          useSearch: () => searchObject
        })
      };
    });
    vi.mock('@tanstack/react-query', async () => {
      const actual = await vi.importActual('@tanstack/react-query');
      return {
        ...actual,
        useSuspenseQuery: () => ({
          data: {
            Trains: [{
              LocationCode: 'Testing',
              Line: 'BL',
              Destination: 'Testing',
              DestinationName: 'Testing',
              LocationName: 'Testing',
              ArrivalTime: 'Testing',
              Min: '5'
            }, ...otherTrains]
          }
        })
      };
    });
  });

  it.each([['RD', 4, 'Red', 'Blue'], ['BL', 1, 'Blue', 'Red']])('uses the line filter "%s" to filter down the results to only %s %s line train(s)', async (line: string, numberOfTrains: number, fullLineName: string, nonExistentTrainsQuery: string) => {
    searchObject.line = line;
    renderWithQueryClient(<MetroStationCodes />);

    expect(screen.getAllByText(new RegExp(fullLineName)).length).toBe(numberOfTrains);
    // Expect to throw since there won't be any elements found, resulting in a TestingLibraryElementError
    expect(() => screen.getAllByText(new RegExp(nonExistentTrainsQuery))).toThrow('Unable to find an element with the text:');
  });
});
