import React from "react";
import { Box, Paper, Typography, Button } from "@mui/material";
import { Link } from "react-router-dom";
import { EventData } from "../typings/eventData";
import styled from "@emotion/styled";
import DeleteModal from "./DeleteModal";
import { formatDatetime } from "../utils/datetime";
import EventIcon from "@mui/icons-material/Event";
import posthog from "posthog-js";

interface EventListProps {
  events: EventData[];
  onDelete: (id: number) => Promise<void>;
}

const ListContainer = styled(Box)`
  width: 100%;
`;

const List = styled(Box)`
  display: grid;
  gap: 16px;
  padding: 16px;
`;

const EventItem = styled(Paper)`
  display: flex;
  flex-direction: row;
  border-radius: 16px;
  margin: 8px;
  border: 1px solid #454140;
`;

const EventDetails = styled(Box)`
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

const ImageContainer = styled(Box)`
  width: 200px;
  height: 100%;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  background-color: #f0f0f0;
  margin-left: auto;
  border-top-right-radius: inherit;
  border-bottom-right-radius: inherit;
  @media (max-width: 400px) {
    width: 100px;
  }
`;

function EventList({ events, onDelete }: EventListProps) {
  const [showDeleteModal, setShowDeleteModal] = React.useState(false);
  const [deleteId, setDeleteId] = React.useState<number | undefined>(undefined);

  return (
    <ListContainer>
      <DeleteModal
        open={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={() => {
          onDelete(deleteId!).then(() => {
            setShowDeleteModal(false);
          });
        }}
      />
      <List>
        {events.map((event) => (
          <EventItem key={event.id} elevation={3}>
            <EventDetails>
              <Typography variant="h6">{event.title}</Typography>
              <Typography variant="subtitle1">
                {formatDatetime(event.eventDatetime)}
              </Typography>
              <Typography variant="body2">{event.description}</Typography>
              <Box display="flex" gap="8px">
                <Button
                  component={Link}
                  to={`/details/${event.id}`}
                  variant="contained"
                >
                  Edit
                </Button>
                <Button
                  onClick={() => {
                    posthog.capture("DeleteButtonClicked");
                    setDeleteId(event.id);
                    setShowDeleteModal(true);
                  }}
                  variant="outlined"
                  color="error"
                >
                  Delete
                </Button>
              </Box>
            </EventDetails>
            <ImageContainer>
              {event.eventImageUrl ? (
                <Box
                  component="img"
                  src={event.eventImageUrl}
                  alt={event.title}
                  sx={{
                    height: "100%",
                    width: "100%",
                    objectFit: "cover",
                    borderTopRightRadius: "inherit",
                    borderBottomRightRadius: "inherit",
                  }}
                />
              ) : (
                <EventIcon sx={{ fontSize: 100, color: "#9e9e9e" }} />
              )}
            </ImageContainer>
          </EventItem>
        ))}
      </List>
    </ListContainer>
  );
}

export default EventList;
