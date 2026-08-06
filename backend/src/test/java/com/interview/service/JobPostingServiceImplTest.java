package com.interview.service;

import com.interview.dto.JobPostingFilter;
import com.interview.dto.JobPostingRequest;
import com.interview.dto.JobPostingResponse;
import com.interview.exception.IllegalStateTransitionException;
import com.interview.exception.ResourceNotFoundException;
import com.interview.mapper.JobPostingMapper;
import com.interview.model.JobPosting;
import com.interview.model.enums.ExperienceLevel;
import com.interview.model.enums.JobStatus;
import com.interview.model.enums.JobType;
import com.interview.repository.JobPostingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Pure unit test for {@link JobPostingServiceImpl}.
 * <p>
 * No Spring context is loaded — everything runs with bare Mockito.
 * Repository and Mapper are mocked; only the service's own logic is exercised.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JobPostingServiceImpl — unit tests")
class JobPostingServiceImplTest {

    @Mock
    JobPostingRepository repository;
    @Mock
    JobPostingMapper mapper;

    @InjectMocks
    JobPostingServiceImpl service;

    // ── shared fixtures ───────────────────────────────────────

    private JobPosting draftEntity;
    private JobPosting activeEntity;
    private JobPosting closedEntity;
    private JobPostingRequest request;
    private JobPostingResponse response;

    @BeforeEach
    void setUp() {
        draftEntity = JobPosting.builder()
                .id(1L)
                .title("Senior Backend Engineer")
                .company("Tekmetric")
                .status(JobStatus.DRAFT)
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .remote(false)
                .currency("USD")
                .description("Build the platform.")
                .build();

        activeEntity = draftEntity.toBuilder().status(JobStatus.ACTIVE).build();
        closedEntity = draftEntity.toBuilder().status(JobStatus.CLOSED).build();

        request = JobPostingRequest.builder()
                .title("Senior Backend Engineer")
                .company("Tekmetric")
                .jobType(JobType.FULL_TIME)
                .experienceLevel(ExperienceLevel.SENIOR)
                .description("Build the platform.")
                .build();

        response = JobPostingResponse.builder()
                .id(1L)
                .title("Senior Backend Engineer")
                .company("Tekmetric")
                .status(JobStatus.DRAFT)
                .build();
    }

    // ══════════════════════════════════════════════════════════
    // CREATE
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("maps request → entity → saves → maps to response → returns it")
        void create_happyPath_savesAndReturnsResponse() {
            given(mapper.toEntity(request)).willReturn(draftEntity);
            given(repository.save(draftEntity)).willReturn(draftEntity);
            given(mapper.toResponse(draftEntity)).willReturn(response);

            JobPostingResponse result = service.create(request);

            assertThat(result).isEqualTo(response);
            verify(mapper).toEntity(request);
            verify(repository).save(draftEntity);
            verify(mapper).toResponse(draftEntity);
        }

        @Test
        @DisplayName("saved entity is returned from repository and mapped — not the pre-save object")
        void create_usesRepositoryReturnValue() {
            JobPosting savedWithId = draftEntity.toBuilder().id(42L).build();
            JobPostingResponse savedResponse = response.toBuilder().id(42L).build();

            given(mapper.toEntity(request)).willReturn(draftEntity);
            given(repository.save(draftEntity)).willReturn(savedWithId);
            given(mapper.toResponse(savedWithId)).willReturn(savedResponse);

            JobPostingResponse result = service.create(request);

            assertThat(result.id()).isEqualTo(42L);
        }
    }

    // ══════════════════════════════════════════════════════════
    // FIND BY ID
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("existing id → maps entity to response")
        void findById_existing_returnsResponse() {
            given(repository.findById(1L)).willReturn(Optional.of(draftEntity));
            given(mapper.toResponse(draftEntity)).willReturn(response);

            JobPostingResponse result = service.findById(1L);

            assertThat(result).isEqualTo(response);
        }

        @Test
        @DisplayName("missing id → throws ResourceNotFoundException with id in message")
        void findById_missing_throwsResourceNotFoundException() {
            given(repository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ══════════════════════════════════════════════════════════
    // FIND ALL
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("delegates to repository with spec and pageable, maps each element")
        void findAll_delegatesAndMaps() {
            Pageable pageable = PageRequest.of(0, 10);
            JobPostingFilter filter = JobPostingFilter.builder().build();
            Page<JobPosting> repoPage = new PageImpl<>(List.of(draftEntity), pageable, 1);

            given(repository.findAll(any(Specification.class), eq(pageable))).willReturn(repoPage);
            given(mapper.toResponse(draftEntity)).willReturn(response);

            Page<JobPostingResponse> result = service.findAll(filter, pageable);

            assertThat(result.getContent()).containsExactly(response);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }

        @Test
        @DisplayName("empty result → returns empty page, no mapper calls")
        void findAll_empty_returnsEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            given(repository.findAll(any(Specification.class), eq(pageable)))
                    .willReturn(Page.empty());

            Page<JobPostingResponse> result = service.findAll(JobPostingFilter.builder().build(), pageable);

            assertThat(result.getContent()).isEmpty();
            verifyNoInteractions(mapper);
        }
    }

    // ══════════════════════════════════════════════════════════
    // UPDATE
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("DRAFT posting → calls mapper.updateEntity, saves, returns response")
        void update_draftPosting_updatesAndReturns() {
            given(repository.findById(1L)).willReturn(Optional.of(draftEntity));
            given(repository.save(draftEntity)).willReturn(draftEntity);
            given(mapper.toResponse(draftEntity)).willReturn(response);

            JobPostingResponse result = service.update(1L, request);

            assertThat(result).isEqualTo(response);
            verify(mapper).updateEntity(request, draftEntity);
            verify(repository).save(draftEntity);
        }

        @Test
        @DisplayName("ACTIVE posting → allows update")
        void update_activePosting_allowsUpdate() {
            given(repository.findById(1L)).willReturn(Optional.of(activeEntity));
            given(repository.save(activeEntity)).willReturn(activeEntity);
            given(mapper.toResponse(activeEntity)).willReturn(response);

            assertThatNoException().isThrownBy(() -> service.update(1L, request));
        }

        @Test
        @DisplayName("CLOSED posting → throws IllegalStateTransitionException")
        void update_closedPosting_throwsIllegalStateTransition() {
            given(repository.findById(1L)).willReturn(Optional.of(closedEntity));

            assertThatThrownBy(() -> service.update(1L, request))
                    .isInstanceOf(IllegalStateTransitionException.class)
                    .hasMessageContaining("CLOSED");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("ARCHIVED posting → throws IllegalStateTransitionException")
        void update_archivedPosting_throwsIllegalStateTransition() {
            JobPosting archived = draftEntity.toBuilder().status(JobStatus.ARCHIVED).build();
            given(repository.findById(1L)).willReturn(Optional.of(archived));

            assertThatThrownBy(() -> service.update(1L, request))
                    .isInstanceOf(IllegalStateTransitionException.class)
                    .hasMessageContaining("ARCHIVED");
        }

        @Test
        @DisplayName("missing id → throws ResourceNotFoundException before any save")
        void update_missing_throwsResourceNotFoundException() {
            given(repository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(repository, never()).save(any());
        }
    }


    // ══════════════════════════════════════════════════════════
    // DELETE
    // ══════════════════════════════════════════════════════════

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("existing id → fetches entity and deletes it")
        void delete_existing_deletesEntity() {
            given(repository.findById(1L)).willReturn(Optional.of(draftEntity));

            service.delete(1L);

            verify(repository).delete(draftEntity);
        }

        @Test
        @DisplayName("missing id → throws ResourceNotFoundException, delete never called")
        void delete_missing_throwsResourceNotFoundException() {
            given(repository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");

            verify(repository, never()).delete(any(JobPosting.class));
        }
    }
}