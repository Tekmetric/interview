import Superscript from "@/components/superscript";
import {
  TableRow,
  TableCell,
  Checkbox,
  LinearProgress,
  IconButton,
} from "@mui/material";

import DeleteIcon from "@mui/icons-material/Delete";
import { Indicator } from "@/lib/api/hooks/get/useFetchSymbolInsight";
import { SymbolData } from "@/lib/api/hooks/get/useFetchSymbols";

type CustomTableRowProps = {
  isItemSelected: boolean;
  labelId: string;
  indicators: Indicator | null;
  isLoading: boolean;
  row: SymbolData;
  handleSelect: (symbol: string) => void;
  handleDelete: (
    event: React.MouseEvent<HTMLButtonElement>,
    symbol: string
  ) => void;
};

const CustomTableRow = ({
  isItemSelected,
  labelId,
  indicators,
  isLoading,
  row,
  handleSelect,
  handleDelete,
}: CustomTableRowProps) => {
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
      <TableCell component="th" id={labelId} scope="row" padding="none">
        {row.description}
      </TableCell>
      <TableCell align="left">{row.symbol}</TableCell>
      <TableCell align="left">{row.type}</TableCell>
      <TableCell align="left">
        {isLoading ? <LinearProgress /> : indicators?.bestEntry}
        {indicators?.bestEntry && <Superscript>[AI]</Superscript>}
      </TableCell>
      <TableCell align="left">
        {isLoading ? <LinearProgress /> : indicators?.risk3Y}
        {indicators?.risk3Y && <Superscript>[AI]</Superscript>}
      </TableCell>
      <TableCell align="left">
        {isLoading ? <LinearProgress /> : indicators?.estimated3Y}
        {indicators?.estimated3Y && <Superscript>[AI]</Superscript>}
      </TableCell>
      <TableCell align="left">
        {isLoading ? <LinearProgress /> : indicators?.yield}
        {indicators?.yield && <Superscript>[AI]</Superscript>}
      </TableCell>
      <TableCell align="left">
        {isLoading ? <LinearProgress /> : indicators?.sentiment}
        {indicators?.sentiment && <Superscript>[AI]</Superscript>}
      </TableCell>
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
};

export default CustomTableRow;
