import { Button, ButtonProps, styled } from "@mui/material";

export const PrimaryGradientButton = styled(Button)<ButtonProps>(({ theme }) => ({
  padding: theme.spacing(2, 3),
  fontSize: 16,
  fontWeight: 600
}));

export const SecondaryGradientButton = styled(Button)<ButtonProps>(({ theme }) => ({
  background: "linear-gradient(to right, #ee0979, #ff6a00)",
  padding: theme.spacing(2, 3),
  fontSize: 16,
  color: "white",
  fontWeight: 600
}));
