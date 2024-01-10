package nl.tudelft.sem.template.example.controllers;

import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class VendorOrCourierControllerTest {

    private transient VendorOrCourierController vendorOrCourierController;
    private transient TestDeliveryRepository deliveryRepository;
    private transient TestRestaurantRepository restaurantRepository;

    UUID deliveryId;

    @BeforeEach
    void setUp() {
        deliveryRepository = new TestDeliveryRepository();
        restaurantRepository = new TestRestaurantRepository();
        deliveryId = UUID.randomUUID();
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2024, 1, 4, 18, 23, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), "pending", sampleOffsetDateTime,
                sampleOffsetDateTime, 1.d, sampleOffsetDateTime, "69.655,69.425",
                "late", 1);
        deliveryRepository.save(d);

        vendorOrCourierController = new VendorOrCourierController(restaurantRepository, deliveryRepository);
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
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, "vendor", 2);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 2);
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 2);
    }

    @Test
    void setDeliveryDelayBadBody() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, "vendor", -5);
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
}