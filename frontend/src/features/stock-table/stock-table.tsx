import { Fragment, MouseEvent, useState } from "react";
import { SymbolData } from "@/lib/api/hooks/get/useFetchSymbols";
import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
  Typography,
} from "@mui/material";
import useFetchSymbolInsight from "@/lib/api/hooks/get/useFetchSymbolInsight";
import { getComparator, Order } from "./utils/sort";
import EnhancedTableToolbar from "./components/toolbar";
import EnhancedTableHead from "./components/table-head";
import CustomTableRow from "./components/table-row";

type StockTableProps = {
  data: SymbolData[];
  setSelectedSymbols: (symbol: string[]) => void;
  onDelete: (symbol: string) => void;
  onDeleteAll: () => void;
};

const StockTable = ({
  data,
  setSelectedSymbols,
  onDelete,
  onDeleteAll,
}: StockTableProps) => {
  const [order, setOrder] = useState<Order>("asc");
  const [orderBy, setOrderBy] = useState<keyof SymbolData>("description");
  const [selected, setSelected] = useState<readonly string[]>([]);
  const [dense] = useState(true);
  const { data: insights, isLoading } = useFetchSymbolInsight(
    data.map((d) => d.symbol)
  );
  const rows = [...data].sort(getComparator(order, orderBy));

  const handleRequestSort = (
    _event: MouseEvent<unknown>,
    property: keyof SymbolData
  ) => {
    const isAsc = orderBy === property && order === "asc";
    setOrder(isAsc ? "desc" : "asc");
    setOrderBy(property);
  };

  const handleSelectAllClick = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.checked) {
      const newSelected = data.map((n) => n.symbol);
      setSelected(newSelected);
      setSelectedSymbols(newSelected);

      return;
    }
    setSelected([]);
    setSelectedSymbols([]);
  };

  const handleSelect = (id: string) => {
    const selectedIndex = selected.indexOf(id);
    let newSelected: string[] = [];

    if (selectedIndex === -1) {
      newSelected = newSelected.concat(selected, id);
    } else if (selectedIndex === 0) {
      newSelected = newSelected.concat(selected.slice(1));
    } else if (selectedIndex === selected.length - 1) {
      newSelected = newSelected.concat(selected.slice(0, -1));
    } else if (selectedIndex > 0) {
      newSelected = newSelected.concat(
        selected.slice(0, selectedIndex),
        selected.slice(selectedIndex + 1)
      );
    }
    setSelected(newSelected);
    setSelectedSymbols(newSelected);
  };

  const handleDelete = (event: MouseEvent<unknown>, symbol: string) => {
    const filteredSymbols = selected.filter((s) => s !== symbol);
    event.stopPropagation();

    onDelete(symbol);
    setSelected(filteredSymbols);
    setSelectedSymbols(filteredSymbols);
  };

  const handleDeleteAll = () => {
    onDeleteAll();
    setSelected([]);
    setSelectedSymbols([]);
  };

  const getIndicators = (symbol: string) => {
    const insight = insights?.find((insight) => insight.symbol === symbol);

    return insight ? insight.indicators : null;
  };

  return (
    <Box sx={{ width: "100%" }}>
      <Paper sx={{ width: "100%", mb: 2 }}>
        <EnhancedTableToolbar
          numSelected={selected.length}
          onDeleteAll={handleDeleteAll}
        />
        <TableContainer sx={{ overflowX: "auto" }}>
          <Table aria-labelledby="tableTitle" size={dense ? "small" : "medium"}>
            <EnhancedTableHead
              numSelected={selected.length}
              order={order}
              orderBy={orderBy}
              onSelectAllClick={handleSelectAllClick}
              onRequestSort={handleRequestSort}
              rowCount={data.length}
            />
            <TableBody>
              {rows.map((row: SymbolData, index) => {
                const isItemSelected = selected.includes(row.symbol);
                const labelId = `enhanced-table-checkbox-${index}`;
                const indicators = getIndicators(row.symbol);

                return (
                  <Fragment key={row.symbol}>
                    <CustomTableRow
                      isItemSelected={isItemSelected}
                      labelId={labelId}
                      indicators={indicators}
                      isLoading={isLoading}
                      row={row}
                      handleSelect={handleSelect}
                      handleDelete={handleDelete}
                    />
                  </Fragment>
                );
              })}
              {!isLoading && rows.length === 0 ? (
                <TableRow sx={{ height: 50 }}>
                  <TableCell colSpan={10}>
                    <Typography align="center" data-testid="no-data-label">
                      No data to display
                    </Typography>
                  </TableCell>
                </TableRow>
              ) : (
                <></>
              )}
            </TableBody>
          </Table>
        </TableContainer>
      </Paper>
    </Box>
  );
};

export default StockTable;
