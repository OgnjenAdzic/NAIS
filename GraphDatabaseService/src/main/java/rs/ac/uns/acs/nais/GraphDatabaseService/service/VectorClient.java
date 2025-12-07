package rs.ac.uns.acs.nais.GraphDatabaseService.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.ac.uns.acs.nais.GraphDatabaseService.dto.VectorUserDTO;

@Service
public class VectorClient {
    private final RestTemplate restTemplate;
    private final String VECTOR_URL = "http://gateway-api:9000/vector-database-service/users";


    public VectorClient() {
        this.restTemplate = new RestTemplate();
    }

    public void syncUser(VectorUserDTO vectorUserDTO) {
        try {
            restTemplate.postForEntity(VECTOR_URL, vectorUserDTO, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to reach vector service: " + e.getMessage());
        }
    }
}

