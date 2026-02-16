import * as api from "~/api";
import * as hooks from "@uidotdev/usehooks";
import { describe, it, expect, beforeEach, vi } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router";
import SearchBar from "~/components/search/search-bar";
import "@testing-library/jest-dom";

vi.mock("~/api");
vi.mock("@uidotdev/usehooks");

describe("SearchBar Component", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(hooks.useDebounce).mockImplementation((value) => value);
    vi.mocked(hooks.useLocalStorage).mockReturnValue([[], vi.fn()]);
  });

  it("should render default state correctly", () => {
    const { container } = render(
      <MemoryRouter>
        <SearchBar />
      </MemoryRouter>
    );

    expect(container).toMatchSnapshot();
  });

  it("should display search results correctly", async () => {
    const mockResults = [
      { 
        Key: "1", 
        LocalizedName: "New York",
        Country: { LocalizedName: "United States" },
        AdministrativeArea: { LocalizedName: "New York" }
      },
      { 
        Key: "2", 
        LocalizedName: "New Orleans",
        Country: { LocalizedName: "United States" },
        AdministrativeArea: { LocalizedName: "Louisiana" }
      },
    ];

    vi.mocked(api.getLocations).mockResolvedValue(mockResults);

    const { container } = render(
      <MemoryRouter>
        <SearchBar />
      </MemoryRouter>
    );

    const input = screen.getByPlaceholderText("Search for a location...");
    fireEvent.change(input, { target: { value: "New" } });

    expect(await screen.findByRole("heading", { name: "New York" })).toBeInTheDocument();
    expect(await screen.findByRole("heading", { name: "New Orleans" })).toBeInTheDocument();

    expect(container).toMatchSnapshot();
  });

  it("should display correctly when no search results are returned", async () => {
    vi.mocked(api.getLocations).mockResolvedValue([]);

    const { container } = render(
      <MemoryRouter>
        <SearchBar />
      </MemoryRouter>
    );

    const input = screen.getByPlaceholderText("Search for a location...");
    fireEvent.change(input, { target: { value: "xyz" } });

    expect(await screen.findByText("No results found.")).toBeInTheDocument();
    expect(container).toMatchSnapshot();
  });
});
