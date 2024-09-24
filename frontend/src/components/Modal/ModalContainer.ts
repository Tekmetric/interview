import { Box, styled } from "@mui/material";

export const StyledModalContainer = styled(Box)(({ theme }) => ({
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)',
  width: "80%",
  background: theme.palette.background.paper,
  border: '2px solid #000',
  boxShadow: "24",
  padding: 16,
  position: "absolute"
}));
