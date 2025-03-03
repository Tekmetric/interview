import { Box, Button, ButtonGroup } from "@mui/material";

type RangeSelectProps = {
  isVisible: boolean;
  setRange: (range: string) => void;
  range: string;
};

const RangeSelect = (props: RangeSelectProps) => {
  const { isVisible, range, setRange } = props;

  if (!isVisible) {
    return null;
  }

  return (
    <Box display="flex" justifyContent="flex-end" mb={2}>
      <ButtonGroup variant="outlined" color="primary">
        {["1D", "1W", "1M", "6M", "1Y", "3Y", "MAX"].map((rangeOption) => (
          <Button
            key={rangeOption}
            onClick={() => setRange(rangeOption)}
            variant={range === rangeOption ? "contained" : "outlined"}
          >
            {rangeOption}
          </Button>
        ))}
      </ButtonGroup>
    </Box>
  );
};

export default RangeSelect;
