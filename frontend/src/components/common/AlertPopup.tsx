import { Alert, Snackbar } from "@mui/material";
import React from "react";
import { AlertTypes, useAlert } from "../../context/AlertContext";

function AlertPopup() {
  const { message, type, setAlert } = useAlert();

  if (message && type) {
    return (
      <Snackbar
        open={true}
        anchorOrigin={{ vertical: "top", horizontal: "center" }}
        autoHideDuration={50000}
        onClose={() => setAlert("")}
      >
        <Alert
          onClose={() => setAlert("")}
          severity={type === AlertTypes.SUCCESS ? "success" : "error"}
          variant="filled"
          sx={{ width: "100%" }}
        >
          {message}
        </Alert>
      </Snackbar>
    );
  } else {
    return <></>;
  }
}

export default AlertPopup;
