package com.interview.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.interview.exception.AthleteNotFoundException;
import com.interview.model.Athlete;
import com.interview.repository.AthleteRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AthleteServiceImplTest {

    @Autowired
    private AthleteRepository repository;

    private AthleteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AthleteServiceImpl(repository);
    }

    @Test
    @DisplayName("Should save an athlete")
    void testSaveAthlete() {
        Athlete athlete = validAthlete();
        Athlete saved = service.save(athlete);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Usain");
    }

    @Test
    @DisplayName("Should return all athletes")
    void testFindAll() {
        service.save(validAthlete());
        service.save(
                validAthlete().toBuilder().firstName("Mo").lastName("Farah").build());

        List<Athlete> athletes = service.findAll();

        assertThat(athletes).hasSize(2);
    }

    @Test
    @DisplayName("Should find athlete by ID")
    void testFindById() {
        Athlete saved = service.save(validAthlete());
        Athlete found = service.findById(saved.getId());

        assertThat(found).isNotNull();
        assertThat(found.getFirstName()).isEqualTo("Usain");
    }

    @Test
    @DisplayName("Should throw AthleteNotFoundException when athlete not found")
    void testFindByIdNotFound() {
        Long nonExistentId = 999L;
        assertThrows(AthleteNotFoundException.class, () -> service.findById(nonExistentId));
    }

    @Test
    @DisplayName("Should delete athlete by ID")
    void testDeleteById() {
        Athlete saved = service.save(validAthlete());
        service.deleteById(saved.getId());

        assertThrows(AthleteNotFoundException.class, () -> service.findById(saved.getId()));
    }

    private Athlete validAthlete() {
        return Athlete.builder()
                .firstName("Usain")
                .lastName("Bolt")
                .birthTimestamp(524966400000L)
                .nationality("Jamaica")
                .discipline("100m")
                .personalBest("9.58s")
                .bio("Fastest man alive.")
                .build();
    }
}
