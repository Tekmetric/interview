import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
// import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.interview.Application;
import com.interview.Entities.OlympicTeam;
import com.interview.Repositories.OlympicTeamsRepository;
import com.interview.Services.OlympicTeamsService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = Application.class)
class OlympicTeamsServiceTest {

    @Autowired
    private OlympicTeamsService olympicTeamsService;

    @MockBean
    private OlympicTeamsRepository repo;

    @Test
    void testGetOlympicTeamById_Found() {
        // Arrange
        Integer fakeID = 1;
        OlympicTeam fuzzedTeam = OlympicTeam.fuzzOlympicTeam();

        when(repo.findById(fakeID)).thenReturn(fuzzedTeam);

        // Act
        OlympicTeam actualTeam = olympicTeamsService.GetOlympicTeamById(fakeID);

        // Assert
        assertEquals(actualTeam, fuzzedTeam);
    }

    @Test
    void testGetOlympicTeamById_NotFound() {
        // Arrange
        Integer fakeID = 1;
        OlympicTeam fuzzedTeam = OlympicTeam.fuzzOlympicTeam();

        when(repo.findById(fakeID)).thenReturn(fuzzedTeam);

        // Act - given 987 id does not exist on mocked repo layer
        OlympicTeam actualTeam = olympicTeamsService.GetOlympicTeamById(987);

        // Assert
        assertNotEquals(actualTeam, fuzzedTeam);
        assertEquals(actualTeam, null);
    }

    // =============================================
    // Get all olympic teams
    // =============================================
    @Test
    void testGetOlympicTeams_NonEmpty() {
        // Arrange
        OlympicTeam fuzzedTeam = OlympicTeam.fuzzOlympicTeam();
        List<OlympicTeam> fuzzedTeams = List.of(fuzzedTeam);

        when(repo.findByDisabledOnIsNotNull()).thenReturn(fuzzedTeams);

        // Act - given 987 id does not exist on mocked repo layer
        List<OlympicTeam> actualTeams = olympicTeamsService.GetAllOlympicTeams();

        // Assert
        assertEquals(actualTeams, fuzzedTeams);
        assertEquals(actualTeams.size(), 1);
    }
}