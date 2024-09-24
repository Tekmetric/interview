import { Autocomplete, Box, Button, Checkbox, FormControlLabel, Grid2, Modal, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import ConfirmationDialog from "../../ConfirmationDialog/ConfirmationDialog";
import { ISightingFormProps } from "./SightingForm.interface";
import RPMap from "../../RPMap/RPMap";
import { Location } from "../../../types/Location";
import { SightingService } from "../../../service/SightingsService";
import { SightingDTO } from "../../../types/Sighting";
import { SightingValidator } from "../validators/Sighting.validator";
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import dayjs, { Dayjs } from 'dayjs';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { pandaMock } from "../../../service/RedPandaService";
import { RedPanda } from "../../../types/RedPanda";
import { DEFAULT_CENTER_POINT } from "../../../constants/map.constants";
import { MapService } from "../../../service/MapService";
import AddIcon from "@mui/icons-material/Add";
import PandaForm from "../PandaForm/PandaForm";
import { StyledModalContainer } from "../../Modal/ModalContainer";

export default function SightingForm(props: ISightingFormProps) {
  const [dateTime, setDateTime] = useState<Dayjs | null>(dayjs(new Date().toISOString()));
  const [location, setLocation] = useState<Location>();
  const [panda, setPanda] = useState<RedPanda>();

  const [useCurrentLocation, setUseCurrentLocation] = useState(true);

  const [sighting, setSighting] = useState<SightingDTO>(SightingService.buildSighting(panda?.id, location, dateTime?.toISOString()));

  const [showDiscardChangesDialog, setShowDiscardChangesDialog] = useState(false);
  const [openAddPanda, setOpenAddPanda] = useState(false);

  useEffect(() => {
    handleUseCurrentLocation();
  }, []);

  useEffect(() => {
    resetState();
  }, []);

  useEffect(() => {
    setSighting(buildSighting());
  }, [panda, location, dateTime]);

  const resetState = () => {
    setLocation(undefined);
    setDateTime(dayjs(new Date().toISOString()));
    setPanda(undefined);
  }

  const buildSighting = () => SightingService.buildSighting(panda?.id, location, dateTime?.toISOString());

  const handleSave = () => {
    if (!SightingValidator.isValid(sighting)) {
      return;
    }
    
    console.log(sighting);
    props.onSave(sighting);
    setShowDiscardChangesDialog(false);
  }

  const onDiscardChangesConfirmed = () => {
    resetState();
    setShowDiscardChangesDialog(false);
    props.onDiscard();
  }

  const handleDiscard = () => {
    if (SightingValidator.isDirty(SightingService.buildSighting(panda?.id, undefined, dateTime?.toISOString()), sighting)) {
      setShowDiscardChangesDialog(true);
    } else {
      onDiscardChangesConfirmed();
    }
  }

  const handleUseCurrentLocation = () => {
    navigator.geolocation.getCurrentPosition((position) => {
      const crd = position.coords;
      setLocation({ lat: crd.latitude, lon: crd.longitude });
    });
  }

  const toggleUseCurrentLocation = (_: React.ChangeEvent<HTMLInputElement>, useCurrent: boolean) => {
    setUseCurrentLocation(useCurrent);

    if (useCurrent) {
      handleUseCurrentLocation();
    } else {
      setLocation(undefined);
    }
  }

  const handleSavePanda = (panda: RedPanda) => {
    props.onSavePanda(panda);
    setOpenAddPanda(false);
  } 

  return (
    <>
    <Grid2 container spacing={4}>
      <Grid2 container size={{ sm : 12, md: 5, lg: 4 }}>        
        <Grid2 container spacing={4}>
          <Grid2 size={12}>
            <Typography variant="h6">
              Add Sighting
            </Typography>
          </Grid2>

          <Grid2 container spacing={4}>
            <Grid2 size={12} flexGrow={1}>
              <LocalizationProvider dateAdapter={AdapterDayjs}>
                <DateTimePicker
                  label="Sighting time"
                  value={dateTime}
                  onChange={(newValue) => setDateTime(newValue)}
                />
              </LocalizationProvider>
            </Grid2>

            <Grid2 size={12} flexGrow={1}>
              <FormControlLabel
                control={
                  <Checkbox checked={useCurrentLocation} onChange={toggleUseCurrentLocation} />
                }
                label="Use current location"
              />

              {!useCurrentLocation && <>
                  <Typography>
                    {location ? "Your selected location is: " : "Please select location on the map"}
                  </Typography>
                  <Typography color="warning">
                    {location ? MapService.formatLocationForDisplay(location) : ""}
                  </Typography>
                </>
              }         
            </Grid2>

            <Grid2 size={12}>
              <Box sx={{ display: "flex" }}>
                <Autocomplete
                  value={panda}
                  onChange={(_, newValue) => {
                    if (typeof newValue === 'string') {
                      setPanda(pandaMock.find(panda => panda.name === newValue));
                      return;
                    }
                    newValue && setPanda(newValue as RedPanda);
                  }}
                  filterOptions={(options, params) => {
                    return options.filter((option) => option.name.includes(params.inputValue));
                  }}
                  selectOnFocus
                  clearOnBlur
                  handleHomeEndKeys
                  id="red-panda-select"
                  options={pandaMock}
                  getOptionLabel={(option) => {
                    // Value selected with enter, right from the input
                    if (typeof option === 'string') {
                      return option;
                    }
                    return option.name;
                  }}
                  renderOption={(props, option) => {
                    const { key, ...optionProps } = props;
                    return (
                      <li key={key} {...optionProps}>
                        {option.name}
                      </li>
                    );
                  }}
                  sx={{ width: 300 }}
                  renderInput={(params) => (
                    <TextField {...params} label="Red panda" />
                  )}
                />
                <Button color="primary" variant="contained" startIcon={<AddIcon />} onClick={() => setOpenAddPanda(true)}>panda</Button>
              </Box>
            </Grid2>


            <Grid2 container spacing={2}>
              <Grid2 size={6}>
                <Button
                  color="primary"
                  variant="contained"
                  disabled={!SightingValidator.isDirty(SightingService.buildSighting(panda?.id, undefined, dateTime?.toISOString()), sighting)
                    || !SightingValidator.isValid(sighting)}
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

      <Grid2 size={{ sm : 12, md: 7, lg: 8 }}>
        <Box sx={{ display: "flex", alignItems: "center", justifyContent: "center", flexGrow: 1, height: "100%" }}>
          <RPMap
            assets={location ? [location] : []}
            centerPoint={location || { lon: DEFAULT_CENTER_POINT[0], lat: DEFAULT_CENTER_POINT[1] }}
            onSelectLocation={setLocation}
            withSelectLocation
          />
        </Box>
      </Grid2>
    </Grid2>

    <Modal open={openAddPanda}>
      <StyledModalContainer>
        <PandaForm onSave={handleSavePanda} onDiscard={() => setOpenAddPanda(false)} />
      </StyledModalContainer>
    </Modal>
    </>
  );
}
