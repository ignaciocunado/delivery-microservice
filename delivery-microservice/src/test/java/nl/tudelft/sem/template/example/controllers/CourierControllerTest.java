package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CourierControllerTest {

    private transient CourierController courierController;
    private transient TestDeliveryRepository deliveryRepository;

    UUID deliveryId;

    @BeforeEach
    void setUp() {
        deliveryRepository = new TestDeliveryRepository();
        deliveryId = UUID.randomUUID();
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "", "", 1);
        deliveryRepository.save(d);

        courierController = new CourierController(deliveryRepository);
    }

    @Test
    public void courierRole_returnsOk() {
        UUID deliveryId = UUID.randomUUID();
        String role = "courier";

        ResponseEntity<String> response = courierController.getPickUpLocation(deliveryId, role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("PickUp location is 123.321.666", response.getBody());
    }

    @Test
    public void nonCourierRole_ReturnsUnauthorized() {
        UUID deliveryId = UUID.randomUUID();
        String role = "non-courier";

        ResponseEntity<String> response = courierController.getPickUpLocation(deliveryId, role);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authorization failed!", response.getBody());
    }

    @Test
    public void deliveredDeliveryUnauthorized() {
        UUID deliveryId = UUID.randomUUID();
        String role = "non-courier";

        ResponseEntity<String> response = courierController.deliveredDelivery(deliveryId, role);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authorization failed!", response.getBody());
    }

    @Test
    public void deliveredDeliveryNotFound() {
        UUID deliveryId = UUID.randomUUID();
        String role = "courier";

        ResponseEntity<String> response = courierController.deliveredDelivery(deliveryId, role);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void deliveredDeliveryOk() {
        ResponseEntity<String> response = courierController.deliveredDelivery(deliveryId, "courier");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery marked as delivered!", response.getBody());
        assertEquals("delivered", deliveryRepository.findById(deliveryId).get().getStatus());
    }
}