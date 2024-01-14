package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;


class VendorOrCourierControllerTest {

    private transient VendorOrCourierController vendorOrCourierController;
    private transient TestDeliveryRepository deliveryRepository;

    private transient UUID deliveryId;

    @BeforeEach
    void setUp() {
        deliveryRepository = new TestDeliveryRepository();
        deliveryId = UUID.randomUUID();
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2024, 1, 4, 18, 23, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "pending", sampleOffsetDateTime, sampleOffsetDateTime,
                1.d, sampleOffsetDateTime, "69.655,69.425", "late", 1);
        deliveryRepository.save(d);

        vendorOrCourierController = new VendorOrCourierController(deliveryRepository);
    }

    @Test
    void checkVendorOrCourierAuthorised() {
        assertTrue(vendorOrCourierController.checkVendorOrCourier("vendor"));
        assertTrue(vendorOrCourierController.checkVendorOrCourier("courier"));
    }

    @Test
    void checkVendorOrCourierUnauthorised() {
        assertFalse(vendorOrCourierController.checkVendorOrCourier("customer"));
    }

    @Test
    void setDeliveryDelayOk() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, "vendor", 0);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 0);
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 0);
    }

    @Test
    void setDeliveryDelayBadBody() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, "vendor", -1);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNull(res.getBody());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
    }

    @Test
    void setDeliveryDelayNullBody() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, "vendor", null);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNull(res.getBody());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
    }

    @Test
    void setDeliveryDelayUnauthorised() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, "customer", 2);
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertNull(res.getBody());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
    }

    @Test
    void setDeliveryDelayNotFound() {
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(UUID.randomUUID(), "vendor", 2);
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void getDeliveryDelayOk() {
        ResponseEntity<Integer> res = vendorOrCourierController.getDeliveryDelay(deliveryId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 1);
    }

    @Test
    void getDeliveryDelayUnauthorised() {
        ResponseEntity<Integer> res = vendorOrCourierController.getDeliveryDelay(deliveryId, "restaurant");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertNull(res.getBody());
    }

    @Test
    void getDeliveryDelayNotFound() {
        ResponseEntity<Integer> res = vendorOrCourierController.getDeliveryDelay(UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }


    @Test
    void assignOrderToCourierOK() {
        UUID courier = UUID.randomUUID();
        ResponseEntity<UUID> res = vendorOrCourierController.assignOrderToCourier(courier, deliveryId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), deliveryId);
        assertEquals(deliveryRepository.findById(deliveryId).get().getCourierID(), courier);
    }

    @Test
    void assignOrderToCourierNotFound() {
        UUID courier = UUID.randomUUID();
        ResponseEntity<UUID> res = vendorOrCourierController.assignOrderToCourier(courier, UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void assignOrderToCourierUnauthorised() {
        UUID courier = UUID.randomUUID();
        ResponseEntity<UUID> res = vendorOrCourierController.assignOrderToCourier(courier, deliveryId, "restaurant");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertNull(res.getBody());
    }

    @Test
    public void setDeliveryExceptionReturnsOk() {
        String exception = "Test";

        ResponseEntity<String> response = vendorOrCourierController
                .setDeliveryException(deliveryId, "courier", exception);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200 OK", response.getBody());
        assertEquals(exception, deliveryRepository.findById(deliveryId).get().getUserException());

        response = vendorOrCourierController.setDeliveryException(deliveryId, "vendor", exception);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200 OK", response.getBody());
        assertEquals(exception, deliveryRepository.findById(deliveryId).get().getUserException());
    }

    @Test
    public void setDeliveryExceptionReturnsNotFound() {
        String role = "courier";
        String exception = "123.321.666";
        UUID randomId = UUID.randomUUID();

        ResponseEntity<String> response = vendorOrCourierController.setDeliveryException(randomId, role, exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error 404: Delivery not found!", response.getBody());
    }

    @Test
    public void setDeliveryExceptionReturnsUnauthorized() {
        String role = "admin";
        String exception = "123.321.666";

        ResponseEntity<String> response = vendorOrCourierController.setDeliveryException(deliveryId, role, exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("error 403: Authorization failed!", response.getBody());
    }

    @Test
    public void setDeliveryExceptionReturnsBadRequest() {
        String role = "courier";

        ResponseEntity<String> response = vendorOrCourierController.setDeliveryException(deliveryId, role, "");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());

        response = vendorOrCourierController.setDeliveryException(deliveryId, role, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());

        response = vendorOrCourierController.setDeliveryException(deliveryId, role, "    ");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());
    }
}