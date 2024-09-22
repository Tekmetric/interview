import { Box, Typography } from "@mui/material";
import SleepyPanda from "../../assets/panda-sleep.png";

export default function Fallback404Page () {
  return (
    <Box sx={{ display: 'flex', justifyContent: "space-evenly", alignItems: "center", width: '100vw'}}>
      <img src={SleepyPanda} />

      <Box sx={{ display: 'flex', justifyContent: "center", alignItems: "center", flexDirection: "column"}}>
        <Typography variant="h1">
          404
        </Typography>
        <Typography variant="h2">
          Nothing to see here!
        </Typography>
      </Box>
    </Box>
  )
}
