package nl.tudelft.sem.template.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalServiceActual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ExternalServiceActualTest {

    private transient ExternalServiceActual externalService;
    private transient RestTemplate restTemplate;

    private HttpEntity<String> requestEntity;

    @Captor
    private transient ArgumentCaptor<HttpEntity> entityArgumentCaptor;

    @Captor
    private transient ArgumentCaptor<String> stringArgumentCaptor;

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
    void testGetRestaurantLocation() {
        UUID vendorID = UUID.randomUUID();
        Mockito.when(restTemplate.exchange(stringArgumentCaptor.capture(), Mockito.eq(HttpMethod.GET),
                        entityArgumentCaptor.capture(), Mockito.eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"location\": {\n"
                        + "    \"latitude\": 34.092,\n"
                        + "    \"longitude\": 34.092\n"
                        + "  }}"));

        assertEquals("34.092, 34.092", externalService.getRestaurantLocation(vendorID));
        assertTrue(stringArgumentCaptor.getValue().contains(vendorID.toString()));
        assertEquals(vendorID.toString(), entityArgumentCaptor.getValue().getHeaders().get("X-User-ID").get(0));
    }

    @Test
    void testGetLocationJsonFails() {
        Mockito.when(restTemplate.exchange(stringArgumentCaptor.capture(), Mockito.eq(HttpMethod.GET),
                        entityArgumentCaptor.capture(), Mockito.eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"location\": {\n"
                        + "    \"latitude\": 34.092,\n"
                        + "    \"longitude\": 34.092\n"
                ));

        assertNull(externalService.getOrderDestination(UUID.randomUUID(), UUID.randomUUID()));
        assertNull(externalService.getRestaurantLocation(UUID.randomUUID()));
    }

    @Test
    void testGetOrderDestination() {
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Mockito.when(restTemplate.exchange(stringArgumentCaptor.capture(), Mockito.eq(HttpMethod.GET),
                        entityArgumentCaptor.capture(), Mockito.eq(String.class)))
                .thenReturn(ResponseEntity.ok("{\"location\": {\n"
                        + "    \"latitude\": 34.092,\n"
                        + "    \"longitude\": 34.092\n"
                        + "  }}"));

        assertEquals("34.092, 34.092", externalService.getOrderDestination(customerId, orderId));
        assertTrue(stringArgumentCaptor.getValue().contains(customerId.toString()));
        assertTrue(stringArgumentCaptor.getValue().contains(orderId.toString()));
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

    @Test
    void testVerifyWithProof() {
        Mockito.when(restTemplate.exchange("b/vendors/123/proof", HttpMethod.POST, requestEntity, String.class))
                .thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verifyWithProof("123", "vendor");

        assertTrue(result);
    }

    @Test
    void testVerifyWithProofException() {
        Mockito.when(restTemplate.exchange("b/vendors/123/proof", HttpMethod.POST, requestEntity, String.class))
                .thenThrow(new RestClientException(""));
        boolean result = externalService.verifyWithProof("123", "vendor");

        assertFalse(result);
    }

    @Test
    void testThatPerformRequestReturnsTheCorrectErrorCode401() {
        Mockito.when(restTemplate.exchange("b/vendors/123/proof", HttpMethod.POST, requestEntity, String.class))
                .thenThrow(new RestClientException(""));
        int r = externalService.performRequest("b/vendors/123/proof", "123", HttpMethod.POST);
        assertEquals(401, r);
    }

    @Test
    void testVerifyWithGetter() {
        Mockito.when(restTemplate.exchange("b/admins/123", HttpMethod.GET, requestEntity, String.class))
                .thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verifyWithGetter("123", "admin");

        assertTrue(result);
    }

    @Test
    void testGetLocationFromJson() {
        String json = "{\"location\": {\n"
                + "    \"latitude\": 34.092,\n"
                + "    \"longitude\": 34.092\n"
                + "  }}";
        try {
            String result = externalService.getLocationFromJson(json);
            assertEquals("34.092, 34.092", result);
        } catch (JsonProcessingException e) {
            assert(false);
        }
    }

    @Test
    void testGetLocationFromJsonException() {
        String json = "{\"location\": {\n"
                + "  ";
        try {
            externalService.getLocationFromJson(json);
            assert(false);
        } catch (JsonProcessingException e) {
            assert(true);
        }
    }
}
