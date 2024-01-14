package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


class VendorOrCourierControllerTest {

    private transient VendorOrCourierController vendorOrCourierController;
    private transient TestDeliveryRepository deliveryRepository;

    private transient UUID deliveryId;
    private transient OffsetDateTime sampleOffsetDateTime;

    @BeforeEach
    void setUp() {
        deliveryRepository = new TestDeliveryRepository();
        deliveryId = UUID.randomUUID();
        sampleOffsetDateTime = OffsetDateTime.of(
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
    void setDeliveryDelayOk() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, 2);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 2);
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 2);
    }

    @Test
    void setDeliveryDelayOk2() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, 0);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 0);
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 0);
    }

    @Test
    void setDeliveryDelayBadBody() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, -5);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNull(res.getBody());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
    }

    @Test
    void setDeliveryDelayNullBody() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(deliveryId, null);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNull(res.getBody());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
    }

    @Test
    void setDeliveryDelayNotFound() {
        ResponseEntity<Integer> res = vendorOrCourierController.setDeliveryDelay(UUID.randomUUID(), 2);
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void getDeliveryDelayOk() {
        ResponseEntity<Integer> res = vendorOrCourierController.getDeliveryDelay(deliveryId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 1);
    }

    @Test
    void getDeliveryDelayNotFound() {
        ResponseEntity<Integer> res = vendorOrCourierController.getDeliveryDelay(UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }


    @Test
    void assignOrderToCourierOK() {
        UUID courier = UUID.randomUUID();
        ResponseEntity<UUID> res = vendorOrCourierController.assignOrderToCourier(courier, deliveryId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), deliveryId);
        assertEquals(deliveryRepository.findById(deliveryId).get().getCourierID(), courier);
    }

    @Test
    void assignOrderToCourierNotFound() {
        UUID courier = UUID.randomUUID();
        ResponseEntity<UUID> res = vendorOrCourierController.assignOrderToCourier(courier, UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    public void setDeliveryExceptionReturnsOk() {
        String exception = "Test";

        ResponseEntity<String> response = vendorOrCourierController
                .setDeliveryException(deliveryId, exception);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200 OK", response.getBody());
        assertEquals(exception, deliveryRepository.findById(deliveryId).get().getUserException());

        response = vendorOrCourierController.setDeliveryException(deliveryId, exception);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200 OK", response.getBody());
        assertEquals(exception, deliveryRepository.findById(deliveryId).get().getUserException());
    }

    @Test
    public void setDeliveryExceptionReturnsNotFound() {
        String role = "courier";
        String exception = "123.321.666";
        UUID randomId = UUID.randomUUID();

        ResponseEntity<String> response = vendorOrCourierController.setDeliveryException(randomId, exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error 404: Delivery not found!", response.getBody());
    }

    @Test
    public void setDeliveryExceptionReturnsBadRequest() {
        ResponseEntity<String> response = vendorOrCourierController.setDeliveryException(deliveryId, "");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());

        response = vendorOrCourierController.setDeliveryException(deliveryId, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());

        response = vendorOrCourierController.setDeliveryException(deliveryId, "    ");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());
    }

    @Test
    void testSetPickUpEstimate() {
        ResponseEntity<String> res = vendorOrCourierController
                .setPickUpEstimate(deliveryId, sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getPickupTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetPickUpEstimate2() {
        ResponseEntity<String> res = vendorOrCourierController
                .setPickUpEstimate(deliveryId, sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getPickupTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetInvalidPickUpEstimate() {
        ResponseEntity<String> res = vendorOrCourierController.setPickUpEstimate(deliveryId, "hello");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void testSetPickUpNotFound() {
        ResponseEntity<String> res = vendorOrCourierController
                .setPickUpEstimate(UUID.randomUUID(), sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testGetDeliveryEstimate() {
        ResponseEntity<OffsetDateTime> res = vendorOrCourierController.getDeliveryEstimate(deliveryId);
        OffsetDateTime resBody = res.getBody();
        assertEquals(sampleOffsetDateTime, resBody);
    }

    @Test
    void testGetDeliveryEstimateNotFound() {
        ResponseEntity<OffsetDateTime> res = vendorOrCourierController.getDeliveryEstimate(UUID.randomUUID());
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testGetDeliveryEstimateDoesntExist() {
        TestRestaurantRepository restaurantRepository = new TestRestaurantRepository();
        TestDeliveryRepository deliveryRepository = new TestDeliveryRepository();
        UUID newRestaurantID = UUID.randomUUID();
        UUID newRandomDeliveryID = UUID.randomUUID();
        restaurantRepository.save(new Restaurant(newRestaurantID, UUID.randomUUID(), new ArrayList<>(), 1.0d));
        deliveryRepository.save(new Delivery(newRandomDeliveryID, UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "pending", null, null,
                1.d, null, "", "", 1));
        VendorOrCourierController vc = new VendorOrCourierController(deliveryRepository);
        ResponseEntity<OffsetDateTime> res = vc.getDeliveryEstimate(newRandomDeliveryID);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testSetDeliveryEstimate() {
        ResponseEntity<String> res = vendorOrCourierController.setDeliveryEstimate(deliveryId, sampleOffsetDateTime);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDeliveryTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetDeliveryEstimate2() {
        ResponseEntity<String> res = vendorOrCourierController.setDeliveryEstimate(deliveryId, sampleOffsetDateTime);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDeliveryTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetInvalidDeliveryEstimate() {
        ResponseEntity<String> res = vendorOrCourierController.setDeliveryEstimate(deliveryId,null);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void testSetDeliveryNotFound() {
        ResponseEntity<String> res = vendorOrCourierController.setDeliveryEstimate(UUID.randomUUID(), sampleOffsetDateTime);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testCreateDeliveryWrongRoles() {
        final List<String> rolesToTest = List.of("v", "ve", "vendo", "courie", "customer", "sudo", "admi");
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<Restaurant> response = vendorOrCourierController.checkAndHandle(roleToTest, operation);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Test
    void testCreateDeliveryCorrectRoles() {
        final List<String> rolesToTest = List.of("vendor", "admin", "courier");
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<Restaurant> response = vendorOrCourierController.checkAndHandle(roleToTest, operation);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}