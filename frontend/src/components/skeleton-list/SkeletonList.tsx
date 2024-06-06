import { Skeleton } from "@mui/material";
import { times } from "lodash";
import { makeStyles } from "tss-react/mui";

const useStyles = makeStyles()((theme) => ({
  skeletonContainer: {
    display: "flex",
    flexDirection: "column",
    gap: theme.spacing(2),
  },
  row: {
    display: "flex",
    gap: theme.spacing(1),
  },
  rowText: {
    width: "100%",
  },
}));

interface Props {
  rows?: number;
}

export const SkeletonList = ({ rows = 3 }: Props) => {
  const { classes } = useStyles();

  return (
    <div className={classes.skeletonContainer}>
      {times(rows).map((index) => (
        <div key={index} className={classes.row}>
          <Skeleton variant="circular" width={40} height={40} />
          <Skeleton variant="rounded" height={40} className={classes.rowText} />
        </div>
      ))}
    </div>
  );
};
