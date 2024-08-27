import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { EventData } from "../../typings/eventData";
import { createEvent, getEvent, updateEvent } from "../api/eventData";
import { useNavigate } from "react-router-dom";
import { request } from "../request";

export function useGetEvent(id: number) {
  return useQuery<EventData>({
    queryKey: ["event", id],
    queryFn: () => getEvent(id),
  });
}

export function useCreateEventMutation() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createEvent,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["events"] });
      navigate("/");
    },
  });
}

export function useUpdateEventMutation() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: updateEvent,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["events"] });
      navigate("/");
    },
  });
}

export function useDeleteEventMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => {
      return request("DELETE", `/api/events/${id}/`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["events"] });
    },
  });
}
