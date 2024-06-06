import { CopyAll } from "@mui/icons-material";
import {
  Alert,
  AlertTitle,
  Container,
  IconButton,
  Paper,
  Typography,
} from "@mui/material";
import { makeStyles } from "tss-react/mui";

const useStyles = makeStyles()((theme) => ({
  root: {
    padding: theme.spacing(2),
    marginTop: theme.spacing(10),
  },
  code: {
    marginTop: theme.spacing(2),
    display: "flex",
    alignItems: "center",
    backgroundColor: theme.palette.action.selected,
    borderRadius: theme.shape.borderRadius,
    padding: theme.spacing(1),
    justifyContent: "space-between",
  },
}));

interface Props {
  error: Error;
}

export const ErrorContainer = ({ error }: Props) => {
  const { classes } = useStyles();

  return (
    <Container maxWidth="md" component={Paper} className={classes.root}>
      <Alert severity="error">
        <AlertTitle>Something went wrong!</AlertTitle>Error: {error.message}
      </Alert>

      <Typography mt={2}>
        The error may occur due to no server running for handling requests. Did
        you forget to run it in another terminal?
      </Typography>

      <div className={classes.code}>
        <Typography>yarn server</Typography>
        <IconButton onClick={() => navigator.clipboard.writeText("yarn serve")}>
          <CopyAll />
        </IconButton>
      </div>
    </Container>
  );
};
