import React, { useEffect, useState } from "react";
import CircularProgress from "@material-ui/core/CircularProgress";
import Alert from "@material-ui/lab/Alert";
import { makeStyles } from "@material-ui/core/styles";

import SpaceCard from "../components/space-card";
import config from "../utils/config";
import { updateCache, checkCache } from "../utils/cache";

const {
  nasa: { apiKey, url },
} = config;

const useStyles = makeStyles({
  body: {
    display: "flex",
    flexWrap: "wrap",
    flexDirection: "column",
    maxWidth: "100%",
    width: "100%",
    margin: "0 auto",
    justifyContent: "center",
  },
});

const MainView = () => {
  const classes = useStyles();
  const today = new Date();
  const [date, setDate] = useState(today.toISOString().slice(0, 10));
  const [imageDetails, setImageDetails] = useState(null);
  const [loading, setIsLoading] = useState(false);
  const [alertMessage, setAlertMessage] = useState("");
  const fullUrl = `${url}?api_key=${apiKey}&date=${date}`;

  const fetchImage = async () => {
    try {
      setIsLoading(true);
      const response = await fetch(fullUrl).then((data) => data.json());
      if (response) {
        setImageDetails(response);
        setIsLoading(false);
        setAlertMessage("");
        updateCache(date, response);
      }
      if (response && response.code && response.code > 200 && response.msg) {
        setIsLoading(false);
        setAlertMessage(response.msg);
      }
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(
    () => {
      const inFuture = new Date(date) > today;
      if (inFuture) {
        setImageDetails(null);
        setAlertMessage("Date cannot be in the future.");
        return;
      }
      const cachedResult = checkCache(date);
      if (cachedResult) {
        setImageDetails(cachedResult);
      } else {
        fetchImage();
      }
    },
    [date]
  );

  if (loading) {
    return <CircularProgress />;
  }
  return (
    <div>
      <div className={classes.body}>
        <input
          type="date"
          value={date}
          onChange={(e) => setDate(e.currentTarget.value)}
        />
        {imageDetails ? <SpaceCard imageDetails={imageDetails} /> : <></>}
      </div>
      {alertMessage ? (
        <Alert variant="filled" severity={"error"}>
          {alertMessage}
        </Alert>
      ) : (
        <></>
      )}
    </div>
  );
};

export default MainView;
