package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CourierControllerTest {

    private transient CourierController courierController;

    @BeforeEach
    void setUp() {
        courierController = new CourierController();
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
}