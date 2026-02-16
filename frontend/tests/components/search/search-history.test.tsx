import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import SearchHistory from "~/components/search/search-history";
import type { SearchResultLocation } from "~/types/search";

vi.mock("@uidotdev/usehooks", () => ({
  useLocalStorage: vi.fn()
}));

import { useLocalStorage } from "@uidotdev/usehooks";

const mockSearchHistory: SearchResultLocation[] = [
  {
    Key: '12345',
    LocalizedName: 'New York',
    Country: {
      LocalizedName: 'United States'
    },
    AdministrativeArea: {
      LocalizedName: 'NY'
    }
  },
  {
    Key: '67890',
    LocalizedName: 'Los Angeles',
    Country: {
      LocalizedName: 'United States'
    },
    AdministrativeArea: {
      LocalizedName: 'CA'
    }
  }
];

describe("SearchHistory Component", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should render recent searches correctly", () => {
    vi.mocked(useLocalStorage).mockReturnValue([mockSearchHistory, vi.fn()] as any);

    const { container } = render(
      <MemoryRouter>
        <SearchHistory />
      </MemoryRouter>
    );

    expect(screen.getByText("Search History")).toBeTruthy();
    expect(screen.getByText("New York")).toBeTruthy();
    expect(screen.getByText("Los Angeles")).toBeTruthy();
    expect(screen.getByText("Clear")).toBeTruthy();
    expect(container).toMatchSnapshot();
  });

  it("should render correctly when no search history exists", () => {
    vi.mocked(useLocalStorage).mockReturnValue([[], vi.fn()] as any);

    const { container } = render(
      <MemoryRouter>
        <SearchHistory />
      </MemoryRouter>
    );

    expect(screen.getByText("Search History")).toBeTruthy();
    expect(screen.getByText("No search history yet.")).toBeTruthy();
    expect(screen.queryByText("Clear")).toBeNull();
    expect(container).toMatchSnapshot();
  });
});
