import { render, screen, fireEvent, act, within } from "@testing-library/react";
import "@testing-library/jest-dom";
import StockTable from "./stock-table";
import { SymbolData } from "@/lib/api/hooks/get/useFetchSymbols";

// Mock the useFetchSymbolInsight hook
vi.mock("@/lib/api/hooks/get/useFetchSymbolInsight", () => ({
  __esModule: true,
  default: () => ({
    data: [],
    isLoading: false,
  }),
}));

const mockData: SymbolData[] = [
  {
    symbol: "AAPL",
    description: "Apple Inc.",
    type: "Equity",
    displaySymbol: "AAPL",
  },
  {
    symbol: "GOOGL",
    description: "Alphabet Inc.",
    type: "Equity",
    displaySymbol: "GOOGL",
  },
];

const mockSetSelectedSymbols = vi.fn();
const mockOnDelete = vi.fn();
const mockOnDeleteAll = vi.fn();

describe("StockTable", () => {
  beforeEach(async () => {
    await act(async () => {
      render(
        <StockTable
          data={mockData}
          setSelectedSymbols={mockSetSelectedSymbols}
          onDelete={mockOnDelete}
          onDeleteAll={mockOnDeleteAll}
        />
      );
    });
  });

  test("Render table with data", () => {
    expect(screen.getByText("Apple Inc.")).toBeInTheDocument();
    expect(screen.getByText("Alphabet Inc.")).toBeInTheDocument();
  });

  test("Select and deselect a row", async () => {
    const firstRowCheckbox = screen.getAllByRole("checkbox")[1];
    await act(async () => {
      fireEvent.click(firstRowCheckbox);
    });
    expect(firstRowCheckbox).toBeChecked();
    await act(async () => {
      fireEvent.click(firstRowCheckbox);
    });
    expect(firstRowCheckbox).not.toBeChecked();
  });

  test("Select all rows", async () => {
    const selectAllCheckbox = screen.getAllByRole("checkbox")[0];
    await act(async () => {
      fireEvent.click(selectAllCheckbox);
    });
    expect(selectAllCheckbox).toBeChecked();
    expect(screen.getAllByRole("checkbox")[1]).toBeChecked();
    expect(screen.getAllByRole("checkbox")[2]).toBeChecked();
  });

  test("Remove a row", async () => {
    const row = screen.getByText("Apple Inc.").closest("tr");
    if (row) {
      const deleteButton = within(row).getByTestId("remove-symbol");
      await act(async () => {
        fireEvent.click(deleteButton);
      });
      expect(mockOnDelete).toHaveBeenCalledWith("AAPL");
    }
  });

  test("Display no data message", async () => {
    await act(async () => {
      render(
        <StockTable
          data={[]}
          setSelectedSymbols={mockSetSelectedSymbols}
          onDelete={mockOnDelete}
          onDeleteAll={mockOnDeleteAll}
        />
      );
    });
    expect(screen.getByTestId("no-data-label")).toBeInTheDocument();
  });
});
