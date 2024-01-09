package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.hibernate.type.OffsetDateTimeType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VendorControllerTest {
    TestRestaurantRepository restaurantRepo;
    TestDeliveryRepository deliveryRepo;
    VendorController sut;

    UUID restaurantId;
    UUID deliveryId;
    UUID courierId;
    @BeforeEach
    public void setup() {
        // create test repositories
        restaurantRepo = new TestRestaurantRepository();
        deliveryRepo = new TestDeliveryRepository();

        // generate random UUID
        restaurantId = UUID.randomUUID();
        deliveryId = UUID.randomUUID();
        courierId = UUID.randomUUID();

        RestaurantCourierIDsInner elem1 = new RestaurantCourierIDsInner();
        elem1.setCourierID(courierId);

        List<RestaurantCourierIDsInner> param = new ArrayList<>();
        param.add(elem1);

        // setup test repository with some sample objects
        Restaurant r = new Restaurant(restaurantId, UUID.randomUUID(), param, 1.0d);
        restaurantRepo.save(r);
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d, sampleOffsetDateTime, "", "", 1);
        deliveryRepo.save(d);
        sut = new VendorController(restaurantRepo, deliveryRepo);
    }

    /**
    Tests for the addCourierToRest endpoint.
     **/
    @Test
    public void testUnauthorized() {
        ResponseEntity<Void> res = sut.addCourierToRest(UUID.randomUUID(), restaurantId, "noVendor");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);

    }

    @Test
    public void testNotFound() {
        ResponseEntity<Void> res = sut.addCourierToRest(UUID.randomUUID(), UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testOkNoDuplicate() {
        UUID courierId = UUID.randomUUID();
        ResponseEntity<Void> res = sut.addCourierToRest(courierId, restaurantId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);

        Restaurant newRes = sut.getRestaurantRepository().findById(restaurantId).get();
        assertFalse(
                newRes.getCourierIDs().stream()
                        .filter(x -> x.getCourierID().equals(courierId))
                        .collect(Collectors.toList()).isEmpty()
        );

    }

    @Test
    void testAcceptUnauthorized() {
        ResponseEntity<Void> res = sut.acceptDelivery(deliveryId, "noVendor");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testAcceptNotFound() {
        ResponseEntity<Void> res = sut.acceptDelivery(UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testAcceptOk() {
        ResponseEntity<Void> res = sut.acceptDelivery(deliveryId, "vendor");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "accepted");
    }

    @Test
    void testRejectUnauthorized() {
        ResponseEntity<Void> res = sut.rejectDelivery(deliveryId, "noVendor");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testRejectNotFound() {
        ResponseEntity<Void> res = sut.rejectDelivery(UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testRejectOk() {
        ResponseEntity<Void> res = sut.rejectDelivery(deliveryId, "vendor");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "rejected");
    }

    @Test
    void testRemoveUnauthorized() {
        ResponseEntity<Void> res = sut.removeCourierRest(UUID.randomUUID(), UUID.randomUUID(), "courier");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testRemoveRestaurantNotFound() {
        ResponseEntity<Void> res = sut.removeCourierRest(UUID.randomUUID(), UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testRemoveCourierNotFound() {
        ResponseEntity<Void> res = sut.removeCourierRest(UUID.randomUUID(), restaurantId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testRemoveCourierOk() {

        ResponseEntity<Void> res = sut.removeCourierRest(courierId, restaurantId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);

        TestRestaurantRepository repo = (TestRestaurantRepository) sut.getRestaurantRepository();

        assertTrue(repo.findById(restaurantId).get().getCourierIDs().isEmpty());


    }

    @Test
    void testGetCustomerIDOk() {
        ResponseEntity<UUID> response = sut.getCustomerByDeliveryId(deliveryId, "vendor");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), deliveryRepo.findById(deliveryId).get().getCustomerID());
    }

    @Test
    void testGetCustomerIDUnauthored() {
        ResponseEntity<UUID> response = sut.getCustomerByDeliveryId(deliveryId, "courier");
        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertNull(response.getBody());
    }

    @Test
    void testGetCustomerIDNotFound() {
        ResponseEntity<UUID> response = sut.getCustomerByDeliveryId(UUID.randomUUID(), "vendor");
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(response.getBody());
    }

    @Test
    void testEditStatusDeliveryUnauthorized() {
        ResponseEntity<Void> res = sut.editStatusDelivery(deliveryId, "noVendor", "preparing");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testEditStatusDeliveryNotFound() {
        ResponseEntity<Void> res = sut.editStatusDelivery(UUID.randomUUID(), "vendor", "preparing");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testEditStatusDeliveryOk() {
        ResponseEntity<Void> res = sut.editStatusDelivery(deliveryId, "vendor", "preparing");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "preparing");
    }

    @Test
    void testEditStatusDeliveryInvalidStatus() {
        ResponseEntity<Void> res = sut.editStatusDelivery(deliveryId, "vendor", "invalid");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void testEditStatusDeliveryValidStatus2() {
        ResponseEntity<Void> res = sut.editStatusDelivery(deliveryId, "vendor", "given to courier");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "given to courier");
    }
}