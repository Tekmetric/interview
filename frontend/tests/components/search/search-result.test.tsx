import { describe, it, expect } from "vitest";
import { MemoryRouter } from "react-router";
import { render } from "@testing-library/react";
import SearchResult from "~/components/search/search-result";
import type { SearchResultLocation } from "~/types/search";

describe("SearchResult Component", () => {
  const mockLocation: SearchResultLocation = {
    Key: '12345',
    LocalizedName: 'Test City',
    Country: {
      LocalizedName: 'Test Country'
    },
    AdministrativeArea: {
      LocalizedName: 'Test State'
    }
  }

  it("should render large/default size correctly", () => {
    const { container } = render(
      <MemoryRouter>
        <SearchResult
          location={mockLocation}
        />
      </MemoryRouter>
    );
    expect(container).toMatchSnapshot();
  });

  it("should render small size correctly", () => {
    const { container } = render(
      <MemoryRouter>
        <SearchResult
          location={mockLocation}
          size="small"
        />
      </MemoryRouter>
    );
    expect(container).toMatchSnapshot();
  });
});
