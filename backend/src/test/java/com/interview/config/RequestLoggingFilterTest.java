package com.interview.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.interview.model.Athlete;
import com.interview.service.AthleteService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(OutputCaptureExtension.class)
@WebMvcTest
class RequestLoggingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AthleteService athleteService() {
            var mock = Mockito.mock(AthleteService.class);
            Mockito.when(mock.findAll()).thenReturn(List.of(new Athlete()));

            Page<Athlete> mockPage = new PageImpl<>(List.of(new Athlete()));
            Mockito.when(mock.findAll(any(), any(Pageable.class))).thenReturn(mockPage);

            return mock;
        }

        @Bean
        public RequestLoggingFilter requestLoggingFilter() {
            return new RequestLoggingFilter();
        }

        @Bean
        public RateLimitProperties rateLimitProperties() {
            return new RateLimitProperties();
        }

        @Bean
        public Bucket rateLimitBucket() {
            return Bucket.builder()
                    .addLimit(Bandwidth.builder()
                            .capacity(1000)
                            .refillIntervally(1000, Duration.ofMinutes(1))
                            .build())
                    .build();
        }
    }

    @Test
    void shouldLogRequest(CapturedOutput output) throws Exception {
        mockMvc.perform(get("/api/v1/athletes")).andExpect(status().isOk());

        assertThat(output).contains("Incoming request: method=GET, uri=/api/v1/athletes");
    }
}
