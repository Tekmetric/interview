import theme from "@/theme";
import { alpha, Box, LinearProgress } from "@mui/material";

type LoadingOverlayProps = {
  isLoading: boolean;
};

const LoadingOverlay = (props: LoadingOverlayProps) => {
  const { isLoading } = props;

  if (!isLoading) {
    return null;
  }

  return (
    <Box
      position="absolute"
      top={0}
      left={0}
      right={0}
      bottom={0}
      display="flex"
      justifyContent="center"
      alignItems="center"
      zIndex={1}
      bgcolor={alpha(theme.palette.background.default, 0.5)}
    >
      <LinearProgress sx={{ width: "100%" }} />
    </Box>
  );
};

export default LoadingOverlay;
