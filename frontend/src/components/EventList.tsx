import React from "react";
import { Box, Paper, Typography, Button } from "@mui/material";
import { Link } from "react-router-dom";
import { EventData } from "../typings/event_data";
import styled from "@emotion/styled";
import DeleteModal from "./DeleteModal";

interface EventListProps {
  events: EventData[];
  onDelete: (id: number) => Promise<void>;
}

const List = styled(Box)`
  display: grid;
  gap: 16px;
  padding: 16px;
`;

const EventItem = styled(Paper)`
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
`;

function EventList({ events, onDelete }: EventListProps) {
  const [showDeleteModal, setShowDeleteModal] = React.useState(false);
  const [deleteId, setDeleteId] = React.useState<number | null>(null);

  return (
    <>
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
          <EventItem key={event.id}>
            <Typography variant="h6">{event.title}</Typography>
            <Typography variant="subtitle1">{event.eventDatetime}</Typography>
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
                  setDeleteId(event.id);
                  setShowDeleteModal(true);
                }}
                variant="outlined"
                color="error"
              >
                Delete
              </Button>
            </Box>
          </EventItem>
        ))}
      </List>
    </>
  );
}

export default EventList;
