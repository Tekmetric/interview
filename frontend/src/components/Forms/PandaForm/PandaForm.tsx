import React from "react";
import { Button, Checkbox, FormControlLabel, Grid2, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { RedPanda, RedPandaSpecies } from "../../../types/RedPanda";
import { IPandaFormProps } from "./PandaForm.interface";
import { redPandaColours } from "../../../constants/panda.constants";
import { PandaValidator } from "../validators/Panda.validator";
import ConfirmationDialog from "../../ConfirmationDialog/ConfirmationDialog";
import { RedPandaService } from "../../../service/RedPandaService";
import NameInput from "./NameInput";
import AgeInput from "./AgeInput";
import SpeciesInput from "./SpeciesInput";
import ColourInput from "./ColourInput";
import PandaAvatar from "./PandaAvatar";

export default function PandaForm(props: IPandaFormProps) {
  const [name, setName] = useState<string | undefined>(props.panda?.name || "");
  const [age, setAge] = useState<number | undefined>(props.panda?.age);
  const [color, setColour] = useState<string>(props.panda?.color || redPandaColours[0]);
  const [hasTracker, setHasTracker] = useState<boolean>(props.panda?.hasTracker || false);
  const [species, setSpecies] = useState<RedPandaSpecies>(props.panda?.species || RedPandaSpecies.Himalayan);

  const [editedPanda, setEditedPanda] = useState<RedPanda>(RedPandaService.initFromPanda(props.panda));

  const [showDiscardChangesDialog, setShowDiscardChangesDialog] = useState(false);

  useEffect(() => {
    resetState(props.panda);
  }, [props.panda]);

  useEffect(() => {
    setEditedPanda(buildPanda());
  }, [name, age, color, species, hasTracker]);

  const handleTrackerChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setHasTracker(event.target.checked);
  };

  const resetState = (panda: RedPanda | undefined) => {
    setName(panda?.name || "");
    setAge(panda?.age);
    setColour(panda?.color || redPandaColours[0]);
    setHasTracker(panda?.hasTracker || false);
    setSpecies(panda?.species || RedPandaSpecies.Himalayan);
    setEditedPanda(RedPandaService.initFromPanda(props.panda));
  }

  const buildPanda = () => RedPandaService.buildPanda(props.panda?.id, name, age, species, hasTracker, color);

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
              <NameInput value={name} onChange={setName} />
            </Grid2>
            <Grid2 size={12} flexGrow={1}>
              <AgeInput value={age} onChange={setAge}/>
            </Grid2>
            <Grid2 size={12}>
              <SpeciesInput value={species} onChange={setSpecies} />
            </Grid2>

            <Grid2 size={12}>
              <FormControlLabel
                control={<Checkbox checked={hasTracker} onChange={handleTrackerChange} />}
                label="Has tracker"
              />
            </Grid2>

            <Grid2 size={12}>
              <ColourInput value={color} onChange={setColour} />
            </Grid2>

            <Grid2 container spacing={2}>
              <Grid2 size={6}>
                <Button
                  color="primary"
                  variant="contained"
                  disabled={!PandaValidator.isDirty(RedPandaService.initFromPanda(props.panda), editedPanda) 
                    || !PandaValidator.isValid(editedPanda)}
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
        <PandaAvatar hasTracker={hasTracker} species={species} />
      </Grid2>
    </Grid2>
  );
}
