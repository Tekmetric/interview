package com.interview.runningevents.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.interview.runningevents.application.model.PaginatedResult;
import com.interview.runningevents.application.model.RunningEventQuery;
import com.interview.runningevents.application.port.in.CreateRunningEventUseCase;
import com.interview.runningevents.application.port.in.DeleteRunningEventUseCase;
import com.interview.runningevents.application.port.in.GetRunningEventUseCase;
import com.interview.runningevents.application.port.in.ListRunningEventsUseCase;
import com.interview.runningevents.application.port.in.UpdateRunningEventUseCase;
import com.interview.runningevents.infrastructure.web.dto.RunningEventDTOMapper;

@WebMvcTest(RunningEventController.class)
@Import({RunningEventDTOMapper.class})
class RunningEventControllerSortFieldTest {

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
    void shouldUseSortByParameterFromRequest() throws Exception {
        // Given
        PaginatedResult<com.interview.runningevents.domain.model.RunningEvent> emptyResult =
                new PaginatedResult<>(Collections.emptyList(), 0, 0, 20, 0, false, false);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);

        // When - specify sortBy=name in the request
        mockMvc.perform(get("/api/events").param("sortBy", "name").param("sortDir", "DESC"))
                .andExpect(status().isOk());

        // Then - verify the correct sortBy is passed to the use case
        verify(listRunningEventsUseCase).listRunningEvents(queryCaptor.capture());

        RunningEventQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getSortBy()).isEqualTo("name");
    }

    @Test
    void shouldUseSortByLocationParameterFromRequest() throws Exception {
        // Given
        PaginatedResult<com.interview.runningevents.domain.model.RunningEvent> emptyResult =
                new PaginatedResult<>(Collections.emptyList(), 0, 0, 20, 0, false, false);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);

        // When - specify sortBy=location in the request
        mockMvc.perform(get("/api/events").param("sortBy", "location")).andExpect(status().isBadRequest());
    }

    @Test
    void shouldDefaultToDateTimeSortWhenSortByNotSpecified() throws Exception {
        // Given
        PaginatedResult<com.interview.runningevents.domain.model.RunningEvent> emptyResult =
                new PaginatedResult<>(Collections.emptyList(), 0, 0, 20, 0, false, false);

        when(listRunningEventsUseCase.listRunningEvents(any(RunningEventQuery.class)))
                .thenReturn(emptyResult);

        ArgumentCaptor<RunningEventQuery> queryCaptor = ArgumentCaptor.forClass(RunningEventQuery.class);

        // When - don't specify sortBy in the request
        mockMvc.perform(get("/api/events")).andExpect(status().isOk());

        // Then - verify default sortBy (dateTime) is used
        verify(listRunningEventsUseCase).listRunningEvents(queryCaptor.capture());

        RunningEventQuery capturedQuery = queryCaptor.getValue();
        assertThat(capturedQuery.getSortBy()).isEqualTo("dateTime");
    }
}
