package com.interview.util;

import com.interview.dto.JobPostingRequest;
import com.interview.model.enums.ExperienceLevel;
import com.interview.model.enums.JobStatus;
import com.interview.model.enums.JobType;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Produces realistic {@link JobPostingRequest} objects for tests.
 *
 * <p>Usage:
 * <pre>
 *   // Completely random
 *   JobPostingRequest req = JobPostingFactory.random();
 *
 *   // Seed for reproducible tests
 *   JobPostingRequest req = JobPostingFactory.withSeed(42).build();
 *
 *   // Override specific fields via builder
 *   JobPostingRequest req = JobPostingFactory.random().toBuilder()
 *       .status(JobStatus.ACTIVE)
 *       .remote(true)
 *       .build();
 * </pre>
 */
public class JobPostingFactory {

    private static final Faker FAKER = new Faker();

    private static final JobType[]        JOB_TYPES   = JobType.values();
    private static final ExperienceLevel[] EXP_LEVELS  = ExperienceLevel.values();
    private static final String[]         DEPARTMENTS = {
        "Engineering", "Product", "Design", "Data", "QA", "DevOps",
        "Marketing", "Finance", "HR", "Customer Success"
    };

    /** Build a fully randomised but valid request. */
    public static JobPostingRequest random() {
        return build(FAKER);
    }

    /** Build with a fixed seed — same seed → same data every run. */
    public static JobPostingRequest withSeed(long seed) {
        return build(new Faker(new java.util.Random(seed)));
    }

    /** Build a request that will always result in DRAFT status. */
    public static JobPostingRequest draft() {
        return random().toBuilder()
                .status(JobStatus.DRAFT)
                .expiresAt(null)
                .build();
    }

    /** Build a request ready to be published (status = ACTIVE). */
    public static JobPostingRequest active() {
        return random().toBuilder()
                .status(JobStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusDays(60))
                .build();
    }

    private static JobPostingRequest build(Faker faker) {
        BigDecimal salaryMin = BigDecimal.valueOf(faker.number().numberBetween(40_000, 150_000));
        BigDecimal salaryMax = salaryMin.add(BigDecimal.valueOf(faker.number().numberBetween(10_000, 50_000)));

        return JobPostingRequest.builder()
                .title(faker.job().title())
                .company(faker.company().name())
                .department(DEPARTMENTS[faker.random().nextInt(DEPARTMENTS.length)])
                .location(faker.address().city() + ", " + faker.address().stateAbbr())
                .remote(faker.bool().bool())
                .jobType(JOB_TYPES[faker.random().nextInt(JOB_TYPES.length)])
                .experienceLevel(EXP_LEVELS[faker.random().nextInt(EXP_LEVELS.length)])
                .status(JobStatus.DRAFT)
                .salaryMin(salaryMin)
                .salaryMax(salaryMax)
                .currency("USD")
                .description(faker.lorem().paragraph(3))
                .requirements(faker.lorem().paragraph(2))
                .benefits(faker.lorem().sentence(10))
                .expiresAt(LocalDateTime.now().plusDays(faker.number().numberBetween(30, 180)))
                .build();
    }
}
