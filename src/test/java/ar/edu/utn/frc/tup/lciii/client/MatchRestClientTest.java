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
public class MatchRestClientTest {
    @MockBean
    private RestTemplate restTemplate;

    @SpyBean
    private MatchRestClient matchRestClient;

    @Test
    void getAllMatchesTest() {
        MatchDto match1 = new MatchDto();
        match1.setId(1L);
        MatchDto match2 = new MatchDto();
        match2.setId(2L);

        when(restTemplate.getForEntity(any(String.class), eq(MatchDto[].class)))
                .thenReturn(ResponseEntity.ok(new MatchDto[]{match1, match2}));

        ResponseEntity<MatchDto[]> result = matchRestClient.getAllMatches();

        assertNotNull(result.getBody());
        assertEquals(2, result.getBody().length);
        assertEquals(1L, result.getBody()[0].getId());
        assertEquals(2L, result.getBody()[1].getId());
    }
}
