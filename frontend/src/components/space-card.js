import React from "react";
import Card from "@material-ui/core/Card";
import CardMedia from "@material-ui/core/CardMedia";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import PropTypes from "prop-types";

import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles({
  card: {
    maxWidth: "500px",
  },
});

const SpaceCard = ({
  imageDetails: { hdurl = "", title = "", explanation = "" },
}) => {
  const classes = useStyles();
  return (
    <Card className={classes.card}>
      <CardMedia component="img" image={hdurl} title={title} />
      <CardContent>
        <Typography variant="h5">{title}</Typography>
        <Typography variant="body2">{explanation}</Typography>
      </CardContent>
    </Card>
  );
};

SpaceCard.propTypes = {
  imageDetails: PropTypes.shape({
    hdurl: PropTypes.string,
    title: PropTypes.string,
    explanation: PropTypes.string,
  }),
};

export default SpaceCard;
