package ar.edu.utn.frc.tup.lciii.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest

public class TeamRestClientTest {
    @MockBean
    private RestTemplate restTemplate;

    @SpyBean
    private TeamRestClient teamRestClient;

    @Test
    void getAllMatchesTest() {
        TeamDto team1 = new TeamDto();
        team1.setId(1L);
        TeamDto team2 = new TeamDto();
        team2.setId(2L);

        when(restTemplate.getForEntity(any(String.class), eq(TeamDto[].class)))
                .thenReturn(ResponseEntity.ok(new TeamDto[]{team1, team2}));

        ResponseEntity<TeamDto[]> result = teamRestClient.getTeams();

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().length);
        assertEquals(1L, result.getBody()[0].getId());
        assertEquals(2L, result.getBody()[1].getId());
    }
}
