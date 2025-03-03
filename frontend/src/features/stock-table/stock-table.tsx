import { MouseEvent, useState } from "react";
import { SymbolData } from "@/lib/api/hooks/get/useFetchSymbols";
import {
  Box,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableRow,
  Checkbox,
  IconButton,
  LinearProgress,
  Typography,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import useFetchSymbolInsight from "@/lib/api/hooks/get/useFetchSymbolInsight";
import { getComparator, Order } from "./utils/sort";
import EnhancedTableToolbar from "./components/toolbar";
import EnhancedTableHead from "./components/table-head";

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
                  <TableRow
                    hover
                    onClick={() => handleSelect(row.symbol)}
                    role="checkbox"
                    aria-checked={isItemSelected}
                    tabIndex={-1}
                    key={row.symbol}
                    selected={isItemSelected}
                    sx={{ cursor: "pointer" }}
                  >
                    <TableCell padding="checkbox">
                      <Checkbox color="primary" checked={isItemSelected} />
                    </TableCell>
                    <TableCell
                      component="th"
                      id={labelId}
                      scope="row"
                      padding="none"
                    >
                      {row.description}
                    </TableCell>
                    <TableCell align="left">{row.symbol}</TableCell>
                    <TableCell align="left">{row.type}</TableCell>
                    <TableCell align="left">{indicators?.bestEntry}</TableCell>
                    <TableCell align="left">{indicators?.risk3Y}</TableCell>
                    <TableCell align="left">
                      {indicators?.estimated3Y}
                    </TableCell>
                    <TableCell align="left">{indicators?.yield}</TableCell>
                    <TableCell align="left">{indicators?.sentiment}</TableCell>
                    <TableCell padding="checkbox">
                      <IconButton
                        aria-label="Remove symbol"
                        data-testid="remove-symbol"
                        onClick={(event) => handleDelete(event, row.symbol)}
                      >
                        <DeleteIcon />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                );
              })}
              {isLoading ? (
                <TableRow>
                  <TableCell colSpan={10}>
                    <LinearProgress data-testid="is-loading-component" />
                  </TableCell>
                </TableRow>
              ) : rows.length === 0 ? (
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
