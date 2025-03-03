import { styled } from "@mui/material/styles";

const Superscript = styled("sup")(({ theme }) => ({
  color: theme.palette.success.main,
  fontSize: "0.6em",
  marginLeft: "0.2em",
  fontWeight: "bold",
}));

export default Superscript;
