import Superscript from "@/components/superscript";
import { useFetchAggregatedSentiment } from "@/lib/api/hooks/get/useFetchAggregatedSentiment";
import { Box, Typography } from "@mui/material";
import LinearProgress from "@mui/material/LinearProgress";

type StockAggregatedSentimentProps = {
  selectedSymbols: string[];
};

const StockAggregatedSentiment = ({
  selectedSymbols,
}: StockAggregatedSentimentProps) => {
  const { data, loading, error } = useFetchAggregatedSentiment(selectedSymbols);

  if (!selectedSymbols.length) {
    return null;
  }

  if (loading) {
    return <LinearProgress />;
  }

  if (error) {
    return (
      <Box
        sx={{
          border: "1px solid",
          borderColor: (theme) => theme.palette.divider,
          minHeight: "100%",
          p: 2,
        }}
      >
        <Typography variant="body1" color="error">
          Error: {error}
        </Typography>
      </Box>
    );
  }

  const highlightSymbols = (text: string) => {
    const symbolRegex = new RegExp(
      `\\b(${selectedSymbols.join("|")})\\b`,
      "gi"
    );
    return text.split(symbolRegex).map((part, index) =>
      selectedSymbols.includes(part.toUpperCase()) ? (
        <span key={index} style={{ fontWeight: "bold", color: "green" }}>
          {part}
        </span>
      ) : (
        part
      )
    );
  };

  return (
    <Box
      sx={{
        border: "1px solid",
        borderColor: (theme) => theme.palette.divider,
        minHeight: "100%",
        p: 2,
      }}
    >
      <Typography variant="h6" gutterBottom>
        Portfolio Evaluation
        <Superscript>[AI]</Superscript>
      </Typography>
      {data && (
        <Typography variant="body1">
          {highlightSymbols(data.evaluation)}
        </Typography>
      )}
    </Box>
  );
};

export default StockAggregatedSentiment;
