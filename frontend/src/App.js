import React from "react";
import { makeStyles } from "@material-ui/core/styles";

import MainView from "./views/main-view";

const useStyles = makeStyles({
  container: {
    display: "flex",
    justifyContent: "center",
  },
});

const App = () => {
  const classes = useStyles();
  return (
    <div className={classes.container}>
      <MainView />
    </div>
  );
};

export default App;
