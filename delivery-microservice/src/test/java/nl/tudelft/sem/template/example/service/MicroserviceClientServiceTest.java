package nl.tudelft.sem.template.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MicroserviceClientServiceTest {

    private MicroserviceClientService microserviceClientService;

    @BeforeEach
    void setUp() {
        microserviceClientService = new MicroserviceClientService();
    }

    @Test
    public void getRestaurantLocation_returnsExpectedFormat() {
        UUID vendorID = UUID.randomUUID();
        String expectedLocation = "PickUp in format xxx.xxx";

        String location = microserviceClientService.getRestaurantLocation(vendorID);

        assertEquals(expectedLocation, location);
    }

    @Test
    public void getOrderDestination_returnsExpectedFormat() {
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        String expectedDestination = "Destination in format xxx.xxx";

        String destination = microserviceClientService.getOrderDestination(customerId, orderId);

        assertEquals(expectedDestination, destination);
    }
}