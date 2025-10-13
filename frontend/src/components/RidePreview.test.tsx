import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import RidePreview from './RidePreview';
import type { EntityLiveData } from '@/types/parks-api';

describe('RidePreview', () => {
  const mockRide = {
    id: '86a41273-5f15-4b54-93b6-829f140e5161',
    name: "Peter Pan's Flight",
    entityType: 'ATTRACTION',
    queue: {
      STANDBY: {
        waitTime: 65,
      },
    },
    status: 'OPERATING',
    lastUpdated: '2025-10-08T17:14:12Z',
    waitTime: 65,
    lastUpdatedMinutes: 5,
  } as EntityLiveData & { waitTime: number | null; lastUpdatedMinutes: number };

  it('renders the ride preview correctly', async () => {
    render(<RidePreview ride={mockRide} />);

    expect(await screen.getByTestId('ride-name').textContent).toBe("Peter Pan's Flight");
    expect(await screen.getByTestId('wait-time').textContent).toBe('65min');
    expect(await screen.getByTestId('last-updated').textContent).toBe('5 minutes ago');
  });

  describe('wait time variants', () => {
    it('renders red for long waits', async () => {
      render(<RidePreview ride={{ ...mockRide, waitTime: 75 }} />);
      expect(await screen.getByTestId('wait-time').classList.contains('bg-red-100')).toBe(true);
    });

    it('renders green for short waits', async () => {
      render(<RidePreview ride={{ ...mockRide, waitTime: 15 }} />);
      expect(await screen.getByTestId('wait-time').classList.contains('bg-green-100')).toBe(true);
    });

    it('renders yellow for medium waits', async () => {
      render(<RidePreview ride={{ ...mockRide, waitTime: 45 }} />);
      expect(await screen.getByTestId('wait-time').classList.contains('bg-yellow-100')).toBe(true);
    });

    it('renders gray for no wait time', async () => {
      render(<RidePreview ride={{ ...mockRide, waitTime: null }} />);
      expect(await screen.getByTestId('wait-time').classList.contains('bg-gray-100')).toBe(true);
    });
  });
});
