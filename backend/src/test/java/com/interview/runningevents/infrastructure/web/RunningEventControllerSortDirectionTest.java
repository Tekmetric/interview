package com.interview.runningevents.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.model.SortDirection;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.DeleteRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.application.port.in.ListRunningEventsUseCase;
import com.interview.runningevents.application.port.in.UpdateRunningEventUseCase;
import com.interview.runningevents.domain.model.RunningEvent;
import com.interview.runningevents.infrastructure.web.dto.RunningEventDTOMapper;

@WebMvcTest(RunningEventController.class)
@Import({RunningEventDTOMapper.class})
class RunningEventControllerSortDirectionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateRunningEventUseCase createRunningEventUseCase;

    @MockBean
    private GetRunningEventUseCase getRunningEventUseCase;

    @MockBean
    private ListRunningEventsUseCase listRunningEventsUseCase;

    @MockBean
    private UpdateRunningEventUseCase updateRunningEventUseCase;

    @MockBean
    private DeleteRunningEventUseCase deleteRunningEventUseCase;

    @Test
    void shouldUseAscendingSortByDefault() throws Exception {
        // Given
        PaginatedResult<RunningEvent> emptyResult =
                new PaginatedResult<>(Collections.emptyList(), 0, 0, 20, 0, false, false);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);

        // When
        mockMvc.perform(get("/api/events")).andExpect(status().isOk());

        // Then
        verify(listRunningEventsUseCase, times(1)).listRunningEvents(queryCaptor.capture());

        RunningEventQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getSortDirection()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void shouldUseAscendingSortWhenExplicitlyRequested() throws Exception {
        // Given
        PaginatedResult<RunningEvent> emptyResult =
                new PaginatedResult<>(Collections.emptyList(), 0, 0, 20, 0, false, false);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);

        // When
        mockMvc.perform(get("/api/events").param("sortDir", "ASC")).andExpect(status().isOk());

        // Then
        verify(listRunningEventsUseCase, times(1)).listRunningEvents(queryCaptor.capture());

        RunningEventQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getSortDirection()).isEqualTo(SortDirection.ASC);
    }

    @Test
    void shouldUseDescendingSortWhenRequested() throws Exception {
        // Given
        PaginatedResult<RunningEvent> emptyResult =
                new PaginatedResult<>(Collections.emptyList(), 0, 0, 20, 0, false, false);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);

        // When
        mockMvc.perform(get("/api/events").param("sortDir", "DESC")).andExpect(status().isOk());

        // Then
        verify(listRunningEventsUseCase, times(1)).listRunningEvents(queryCaptor.capture());

        RunningEventQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getSortDirection()).isEqualTo(SortDirection.DESC);
    }

    @ParameterizedTest
    @ValueSource(strings = {"asc", "Asc", "ASC"})
    void shouldAcceptAscDirectionRegardlessOfCase(String direction) throws Exception {
        // Given
        PaginatedResult<RunningEvent> emptyResult =
                new PaginatedResult<>(Collections.emptyList(), 0, 0, 20, 0, false, false);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        // When/Then
        mockMvc.perform(get("/api/events").param("sortDir", direction)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"desc", "Desc", "DESC"})
    void shouldAcceptDescDirectionRegardlessOfCase(String direction) throws Exception {
        // Given
        PaginatedResult<RunningEvent> emptyResult =
                new PaginatedResult<>(Collections.emptyList(), 0, 0, 20, 0, false, false);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        // When/Then
        mockMvc.perform(get("/api/events").param("sortDir", direction)).andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "ASCENDING", "DESCENDING", "up", "down"})
    void shouldRejectInvalidSortDirections(String direction) throws Exception {
        // When/Then
        mockMvc.perform(get("/api/events").param("sortDir", direction)).andExpect(status().isBadRequest());
    }
}
