import { Box } from "@mui/material";
import { Outlet } from "react-router-dom";

export default function RedPandaPage() {
  return (
    <Box sx={{ width: '100%', height: '100%'}}>
      <Outlet />
    </Box>
  );
}
