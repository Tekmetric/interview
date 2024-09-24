import React from "react";
import { Box, Button, Checkbox, FormControl, FormControlLabel, FormLabel, Grid2, Radio, RadioGroup, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { RedPanda, RedPandaSpecies, RedPandaSpeciesLabels } from "../../../types/RedPanda";
import { IPandaFormProps } from "./PandaForm.interface";
import { redPandaColours } from "../../../constants/panda.constants";
import { PandaValidator } from "../validators/Panda.validator";
import { ColourPickerIcon, ColourPickerIconChecked } from "./PandaForm.style";
import ConfirmationDialog from "../../ConfirmationDialog/ConfirmationDialog";
import { RedPandaService } from "../../../service/RedPandaService";
import ChinesePanda from "../../../assets/redpanda-landing.png";
import HimalayanPanda from "../../../assets/redpanda-landing-himalayan.png";
import Tracker from "../../../assets/tracker.png";

export default function PandaForm(props: IPandaFormProps) {
  const [name, setName] = useState<string | undefined>(props.panda?.name || "");
  const [age, setAge] = useState<number | undefined>(props.panda?.age);
  const [colour, setColour] = useState<string>(props.panda?.colour || redPandaColours[0]);
  const [hasTracker, setHasTracker] = useState<boolean>(props.panda?.hasTracker || false);
  const [species, setSpecies] = useState<RedPandaSpecies>(props.panda?.species || RedPandaSpecies.Himalayan);

  const [editedPanda, setEditedPanda] = useState<RedPanda>(RedPandaService.initFromPanda(props.panda));

  const [showDiscardChangesDialog, setShowDiscardChangesDialog] = useState(false);

  useEffect(() => {
    resetState(props.panda);
  }, [props.panda]);

  useEffect(() => {
    setEditedPanda(buildPanda());
  }, [name, age, colour, species, hasTracker]);

  const handleNameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setName(event.target.value);
  }

  const handleAgeChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    try {
      setAge(parseInt(event.target.value, 10));
    } catch {
      setAge(0);
    }
  }

  const handleSpeciesChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = (event.target as HTMLInputElement).value === RedPandaSpecies.Himalayan.toString()
      ? RedPandaSpecies.Himalayan
      : RedPandaSpecies.Chinese;
    setSpecies(value);
  };

  const handleTrackerChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setHasTracker(event.target.checked);
  };

  const handleColourChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setColour(event.target.value);
  };

  const resetState = (panda: RedPanda | undefined) => {
    setName(panda?.name || "");
    setAge(panda?.age);
    setColour(panda?.colour || redPandaColours[0]);
    setHasTracker(panda?.hasTracker || false);
    setSpecies(panda?.species || RedPandaSpecies.Himalayan);
    setEditedPanda(RedPandaService.initFromPanda(props.panda));
  }

  const buildPanda = () => RedPandaService.buildPanda(props.panda?.id, name, age, species, hasTracker, colour);

  const handleSave = () => {
    if (!PandaValidator.isValid(editedPanda)) {
      return;
    }
    
    props.onSave(editedPanda);
  }

  const onDiscardChangesConfirmed = () => {
    resetState(props.panda);
    setShowDiscardChangesDialog(false);
    props.onDiscard();
  }

  const handleDiscard = () => {
    if (PandaValidator.isDirty(RedPandaService.initFromPanda(props.panda), editedPanda)) {
      setShowDiscardChangesDialog(true);
    } else {
      onDiscardChangesConfirmed();
    }
  }

  return (
    <Grid2 container spacing={4}>
      <Grid2 container size={{ sm : 12, md: 5 }}>        
        <Grid2 container spacing={4}>
          <Grid2 size={12}>
            <Typography variant="h6">
              {props.panda ? "Edit" : "Add"} panda
            </Typography>
          </Grid2>

          <Grid2 container spacing={4}>
            <Grid2 size={12} flexGrow={1}>
              <TextField
                required
                id="outlined-required"
                label="Name"
                value={name}
                onChange={handleNameChange}
                error={name ? !PandaValidator.isNameValid(name).isValid : false}
                helperText={name && PandaValidator.isNameValid(name).errorMessage}
                fullWidth
              />
            </Grid2>
            <Grid2 size={12} flexGrow={1}>
              <TextField
                required
                id="outlined-required"
                label="Age"
                type="number"
                value={age || ""}
                onChange={handleAgeChange}
                error={age ? !PandaValidator.isAgeValid(age).isValid : false}
                helperText={age && PandaValidator.isAgeValid(age).errorMessage}
                fullWidth
              />
            </Grid2>
            <Grid2 size={12}>
              <FormControl>
                <FormLabel id="panda-species">Species</FormLabel>
                <RadioGroup
                  row
                  aria-labelledby="panda-species"
                  name="panda-species-radio-buttons-group"
                  value={species}
                  onChange={handleSpeciesChange}
                >
                  <FormControlLabel value={RedPandaSpecies.Himalayan.toString()} control={<Radio />} label={RedPandaSpeciesLabels[RedPandaSpecies.Himalayan]} /> 
                  <FormControlLabel value={RedPandaSpecies.Chinese.toString()} control={<Radio />} label={RedPandaSpeciesLabels[RedPandaSpecies.Chinese]} />
                </RadioGroup>
              </FormControl>
            </Grid2>

            <Grid2 size={12}>
              <FormControlLabel control={<Checkbox checked={hasTracker} onChange={handleTrackerChange} />} label="Has tracker" />
            </Grid2>

            <Grid2 size={12}>
              <FormControl>
                <FormLabel id="panda-Colour">Colour</FormLabel>
                <RadioGroup
                  row
                  aria-labelledby="panda-Colour"
                  name="panda-Colour-radio-buttons-group"
                  value={colour}
                  onChange={handleColourChange}
                >
                  {
                    redPandaColours.map(colour => (
                      <Radio
                        key={colour}
                        value={colour}
                        checkedIcon={<ColourPickerIconChecked />}
                        icon={<ColourPickerIcon />}
                        sx={{ backgroundColor: colour, margin: 1 }}
                      />
                    ))
                  }
                </RadioGroup>
              </FormControl>
            </Grid2>

            <Grid2 container spacing={2}>
              <Grid2 size={6}>
                <Button
                  color="primary"
                  variant="contained"
                  disabled={!PandaValidator.isDirty(RedPandaService.initFromPanda(props.panda), editedPanda) || !PandaValidator.isValid(editedPanda)}
                  onClick={handleSave}
                  fullWidth
                >
                  Submit
                </Button>
              </Grid2>

              <Grid2 size={6}>
                <Button
                  color="warning"
                  variant="outlined"
                  onClick={handleDiscard}
                  fullWidth
                >
                  Discard
                </Button>
              </Grid2>
            </Grid2>
          </Grid2>

          <ConfirmationDialog 
            open={showDiscardChangesDialog}
            message={"You have unsaved changes that you are about to lose. Are you sure you want to continue?"}
            onConfirm={onDiscardChangesConfirmed}
            onDiscard={() => setShowDiscardChangesDialog(false)}
            title={"Warning"}
          />
        </Grid2>
      </Grid2>

      <Grid2 size={{ sm : 12, md: 7 }}>
        <Box sx={{ display: "flex", alignItems: "center", justifyContent: "center", flexGrow: 1, height: "100%" }}>
          <Box sx={{ position: "relative" }}>
            <img src={species === RedPandaSpecies.Chinese ? ChinesePanda : HimalayanPanda} height={300} />
            {hasTracker && (
              <Box sx={{ position: "absolute", top: 120, left: 130 }}>
                <img src={Tracker} height={24} />
              </Box>
            )}
          </Box>
        </Box>
      </Grid2>
    </Grid2>
  );
}
