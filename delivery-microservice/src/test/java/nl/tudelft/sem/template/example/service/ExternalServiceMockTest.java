package nl.tudelft.sem.template.example.service;

import nl.tudelft.sem.template.example.service.externalCommunication.ExternalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class ExternalServiceMockTest {


    @Autowired
    private ExternalService externalService;


    @Test
    public void getRestaurantLocation_returnsExpectedFormat() {
        UUID vendorID = UUID.randomUUID();
        String expectedLocation = "PickUp in format xxx.xxx";

        String location = externalService.getRestaurantLocation(vendorID);

        assertEquals(expectedLocation, location);
    }

    @Test
    public void getOrderDestination_returnsExpectedFormat() {
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        String expectedDestination = "Destination in format xxx.xxx";

        String destination = externalService.getOrderDestination(customerId, orderId);

        assertEquals(expectedDestination, destination);
    }

    @Test
    public void verify_returnsTrue() {
        String userId = "123";
        String role = "courier";

        boolean result = externalService.verify(userId, role);

        assertTrue(result);
    }

}