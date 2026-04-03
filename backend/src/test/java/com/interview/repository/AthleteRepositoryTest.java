package com.interview.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.interview.model.Athlete;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AthleteRepositoryTest {

    @Autowired
    private AthleteRepository athleteRepository;

    @Test
    @DisplayName("Should save and retrieve an athlete")
    void testSaveAndFindAthlete() {
        // given
        Athlete athlete = Athlete.builder()
                .firstName("Usain")
                .lastName("Bolt")
                .birthTimestamp(524966400000L) // 1986-08-21
                .nationality("Jamaica")
                .discipline("100m")
                .personalBest("9.58s")
                .bio("Fastest man alive.")
                .build();

        // when
        Athlete saved = athleteRepository.save(athlete);
        List<Athlete> all = athleteRepository.findAll();

        // then
        assertThat(all).hasSize(1);
        assertThat(all.getFirst()).usingRecursiveComparison().isEqualTo(saved);
    }
}
