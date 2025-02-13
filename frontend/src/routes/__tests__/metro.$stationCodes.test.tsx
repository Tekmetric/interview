import { metroArrivalTimesOptions } from '@api/wmata';
import { QueryClient, QueryClientProvider, useQuery } from '@tanstack/react-query';
import { cleanup, renderHook, waitFor } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import { getPredictionsMockResponse, trainsByCode } from '../../tests/data/wmata';

afterAll(() => {
  vi.resetAllMocks();
  window.history.replaceState(null, 'root', '/');
  cleanup();
});

function useMetroArrivalTimes(stationCodes: string) {
  return useQuery(metroArrivalTimesOptions(stationCodes));
}

describe('metro.$stationCodes route', () => {
  it.each([['All'], ['A01'], ['B01']])('queries for stationCodes "%s" as expected', async (stationCodes: string) => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: {
          retry: false
        }
      }
    });
    const wrapper = ({ children }: { children: any }) => (
      <QueryClientProvider client={ queryClient }>{children}</QueryClientProvider>
    );

    const { result: allResult } = renderHook(() => useMetroArrivalTimes(stationCodes), { wrapper });
    await waitFor(() => expect(allResult.current.isSuccess).toBe(true));
    // If the stationCodes is 'All', we expect the full mock response. Otherwise, filter down to just the truthy trains.
    const expectedResponse = stationCodes === 'All' ? getPredictionsMockResponse : { Trains: stationCodes.split(',').map(stationCode => trainsByCode[stationCode as keyof typeof trainsByCode]).filter(Boolean) };
    expect(allResult.current.data).toStrictEqual(expectedResponse);
  });
});
