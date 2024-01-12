package nl.tudelft.sem.template.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class ExternalServiceTest {


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
    public void isCustomerReturnsTrue() {
        assertTrue(externalService.isCustomer("123"));
    }

    @Test
    public void isAdminReturnsTrue() {
        assertTrue(externalService.isAdmin("123"));
    }

    @Test
    public void isCourierReturnsTrue() {
        assertTrue(externalService.isCourier("123"));
    }

    @Test
    public void isVendorReturnsTrue() {
        assertTrue(externalService.isVendor("123"));
    }
}