import { Box, Grid2, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { pandaMock } from "../../service/RedPandaService";
import { RedPanda, RedPandaSpecies } from "../../types/RedPanda";
import RedPandaImage from "../../assets/panda-tree.png";

export default function PandaDetail() {
  const { id } = useParams();

  const [panda, setPanda] = useState<RedPanda>();

  useEffect(() => {
    setPanda(pandaMock.find(panda => panda.id === id));
  }, []);

  return (
    <Grid2 container spacing={4}>
      { panda && (
        <>
          <Grid2 size={12}>
            <Typography variant="h6">
              {panda.name}'s details
            </Typography>
          </Grid2>

          <Grid2 container spacing={2} size={7}>
            <Grid2 size={4}>
              <Typography>
                Name
              </Typography>
            </Grid2>
            <Grid2 size={8}>
              <Typography>
                {panda.name}
              </Typography>
            </Grid2>

            <Grid2 size={4}>
              <Typography>
                Age
              </Typography>
            </Grid2>
            <Grid2 size={8}>
              <Typography>
                {panda.age}
              </Typography>
            </Grid2>

            <Grid2 size={4}>
              <Typography>
                Species
              </Typography>
            </Grid2>
            <Grid2 size={8}>
              <Typography>
                {panda.species === RedPandaSpecies.Chinese ? "Chinese" : "Himalayan"}
              </Typography>
            </Grid2>
            
            <Grid2 size={4}>
              <Typography>
                Has tracker
              </Typography>
            </Grid2>
            <Grid2 size={8}>
              <Typography>
                {panda.hasTracker ? "Yes" : "No"}
              </Typography>
            </Grid2>

            <Grid2 size={4}>
              <Typography>
                Colour
              </Typography>
            </Grid2>
            <Grid2 size={8}>
              <Box sx={{ width: '100%', height: "100%", background: panda.colour }} />
            </Grid2>

            <Grid2 size={4}>
              <Typography>
                Last seen on
              </Typography>
            </Grid2>
            <Grid2 size={8}>
              <Typography>
                {new Date().toLocaleString()}
              </Typography>
            </Grid2>
          </Grid2>

          <Grid2 size={4}>
            <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
              <img src={RedPandaImage} height={400} />
            </Box>
          </Grid2>
        </>
      )}

      {!panda && (
        <Typography variant="h6">
          Uh-oh, Panda not found!
        </Typography>
      )}
    </Grid2>
  );
}
