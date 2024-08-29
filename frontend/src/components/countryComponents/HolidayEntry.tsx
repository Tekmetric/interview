import React from "react";
import { HolidayInfo } from "../../interfaces/country";
import { TableRow, TableCell } from "@mui/material";
import CheckIcon from "@mui/icons-material/Check";
import { format } from "date-fns";

interface HolidayEntryProps {
  holiday: HolidayInfo;
}

export const HolidayEntry: React.FC<HolidayEntryProps> = ({ holiday }) => {
  return (
    <TableRow sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
      <TableCell component="th" scope="row">
        {format(new Date(holiday?.date), "PPP") || ""}
      </TableCell>
      <TableCell align="right">{holiday?.localName || "-"}</TableCell>
      <TableCell align="right">{holiday?.name || "-"}</TableCell>
      <TableCell align="right">{holiday?.types?.join(', ') || ""}</TableCell>
      <TableCell align="right">{holiday?.global && <CheckIcon />}</TableCell>
      <TableCell align="right">
        {holiday?.counties?.join(", ") || ""}
      </TableCell>
    </TableRow>
  );
};
