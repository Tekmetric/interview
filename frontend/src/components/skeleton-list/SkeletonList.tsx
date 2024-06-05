import { Skeleton } from "@mui/material";
import { makeStyles } from "tss-react/mui";

const useStyles = makeStyles()((theme) => ({
  skeletonContainer: {
    display: "flex",
    flexDirection: "column",
    gap: theme.spacing(1),
  },
}));

export const SkeletonList = () => {
  const { classes } = useStyles();

  return (
    <div className={classes.skeletonContainer}>
      <Skeleton variant="rounded" height={60} />
      <Skeleton variant="rounded" height={60} />
      <Skeleton variant="rounded" height={60} />
    </div>
  );
};
