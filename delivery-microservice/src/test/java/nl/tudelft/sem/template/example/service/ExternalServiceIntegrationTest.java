package nl.tudelft.sem.template.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("integration")
public class ExternalServiceIntegrationTest {

    private transient ExternalService externalService;
    private transient RestTemplate restTemplate;


    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        externalService = new ExternalServiceActual(restTemplate, "a", "b");
    }

    @Test
    void getRestaurantLocation() {
        Mockito.when(restTemplate.exchange(Mockito.anyString(), (HttpMethod) Mockito.any(), (HttpEntity<?>) Mockito.any(), (Class<Object>) Mockito.any()))
                .thenReturn(ResponseEntity.ok("{\"location\": {\n" +
                        "    \"latitude\": 34.092,\n" +
                        "    \"longitude\": 34.092\n" +
                        "  }}"));

        assert (externalService.getRestaurantLocation(UUID.randomUUID()).equals("34.092, 34.092"));
    }

    @Test
    void getOrderDestination() {
        Mockito.when(restTemplate.exchange(Mockito.anyString(), (HttpMethod) Mockito.any(), (HttpEntity<?>) Mockito.any(), (Class<Object>) Mockito.any()))
                .thenReturn(ResponseEntity.ok("{\"location\": {\n" +
                        "    \"latitude\": 34.092,\n" +
                        "    \"longitude\": 34.092\n" +
                        "  }}"));

        assert(externalService.getOrderDestination(UUID.randomUUID(), UUID.randomUUID())
                .equals("34.092, 34.092"));
    }

    @Test
    void verifyNoRole() {
        boolean result = externalService.verify("123", "nothing");
        assertFalse(result);
    }

    @Test
    void verifyVendorRole() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verify("123", "vendor");

        assertTrue(result);
    }

    @Test
    void verifyCourierRole() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verify("123", "courier");

        assertTrue(result);
    }

    @Test
    void verifyAdminRole() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verify("123", "admin");

        assertTrue(result);
    }

    @Test
    void verifyValidRoleNon200ErrorCode() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any()))
                .thenThrow(new RestClientException(""));
        boolean result = externalService.verify("123", "customer");

        assertFalse(result);
    }

    @Test
    void verifyClientExceptionThrown() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any()))
                .thenThrow(new RestClientException(":)"));
        boolean result = externalService.verify("123", "admin");

        assertFalse(result);
    }
}
