package nl.tudelft.sem.template.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

@SpringBootTest
public class ExternalServiceActualTest {

    private transient ExternalServiceActual externalService;
    private transient RestTemplate restTemplate;

    private HttpEntity<String> requestEntity;


    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        externalService = new ExternalServiceActual(restTemplate, "a", "b");

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-ID", "123");
        headers.setContentType(MediaType.APPLICATION_JSON);

        requestEntity = new HttpEntity<>("123", headers);
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
        Mockito.when(restTemplate.exchange("b/vendors/123/proof", HttpMethod.POST, requestEntity, String.class))
                .thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verify("123", "vendor");

        assertTrue(result);
    }

    @Test
    void verifyCourierRole() {
        Mockito.when(restTemplate.exchange("b/couriers/123/proof", HttpMethod.POST, requestEntity, String.class))
                .thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verify("123", "courier");

        assertTrue(result);
    }

    @Test
    void verifyAdminRole() {
        Mockito.when(restTemplate.exchange("b/admins/123", HttpMethod.GET, requestEntity, String.class))
                .thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verify("123", "admin");

        assertTrue(result);
    }

    @Test
    void verifyProofExceptionThrown() {
        Mockito.when(restTemplate.exchange("b/customers/123", HttpMethod.GET, requestEntity, String.class))
                .thenThrow(new RestClientException(""));
        boolean result = externalService.verify("123", "customer");

        assertFalse(result);
    }

    @Test
    void verifyGetExceptionThrown() {
        Mockito.when(restTemplate.exchange("b/admins/123", HttpMethod.GET, requestEntity, String.class))
                .thenThrow(new RestClientException(":)"));
        boolean result = externalService.verify("123", "admin");

        assertFalse(result);
    }
}
