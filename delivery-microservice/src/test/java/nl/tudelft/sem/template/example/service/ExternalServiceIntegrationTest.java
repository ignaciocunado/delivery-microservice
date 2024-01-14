package nl.tudelft.sem.template.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;

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
        UUID vendorID = UUID.randomUUID();
        Mockito.when(restTemplate.getForObject("a/vendor/" + vendorID + "/location", String.class)).thenReturn("PickUp in format xxx.xxx");

        assertEquals("PickUp in format xxx.xxx", externalService.getRestaurantLocation(vendorID));
    }

    @Test
    void getOrderDestination() {
        UUID customerId = UUID.randomUUID();
        UUID orderID = UUID.randomUUID();
        Mockito.when(restTemplate.getForObject("a/delivery/" + customerId + "/order/" + orderID + "/destination", String.class)).thenReturn("Destination in format xxx.xxx");

        System.out.println("\033[95:40m rest response: \033[30:105m " + restTemplate.getForObject("a/delivery/" + customerId + "/order/" + orderID + "/destination", String.class) + " \033[0m");
        System.out.println("\033[95:40m externalService response: \033[30:105m " + externalService.getOrderDestination(customerId, orderID) + " \033[0m");
        assertEquals("Destination in format xxx.xxx", externalService.getOrderDestination(customerId, orderID));
    }

    @Test
    void verifyNoRole() {
        boolean result = externalService.verify("123", "nothing");
        assertFalse(result);
    }

    @Test
    void verifyVendorRole() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
        boolean result = externalService.verify("123", "vendor");

        assertTrue(result);
    }

    @Test
    void verifyCourierRole() {
        Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.any())).thenReturn(new ResponseEntity<>(null, null, HttpStatus.OK));
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
