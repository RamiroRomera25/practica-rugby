package ar.edu.utn.frc.tup.lciii.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MatchRestClient {
    @Autowired
    private RestTemplate restTemplate;

    private final String host = "https://my-json-server.typicode.com/LCIV-2023/fake-api-rwc2023/matches";

    public ResponseEntity<MatchDto[]> getAllMatches() {
        return restTemplate.getForEntity(host, MatchDto[].class);
    }
}
