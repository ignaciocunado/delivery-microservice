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

    OffsetDateTime sampleOffsetDateTime;

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

        sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );

        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d, sampleOffsetDateTime, "", "", 1);
        deliveryRepo.save(d);
        sut = new VendorController(restaurantRepo, deliveryRepo);
    }

    /**
     * Helper that generates a new UUID, that is NOT equal to the current deliveryId.
     *
     * NOTE: as this method is re-used in several tests, this code should be extracted
     * to e.g. its own service, so it can be used in multiple places at once.
     *
     * @return A delivery ID not equal to the test's 'deliveryId'. This ID is invalid for testing purposes,
     *         until used to save an object to the DB.
     */
    UUID generateNewDeliveryId() {
        UUID newDeliveryId;

        do { newDeliveryId = UUID.randomUUID(); }
        while (newDeliveryId == deliveryId);

        return newDeliveryId;
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
    void testGetPickUpEstimate() {
        ResponseEntity<OffsetDateTime> res = sut.getPickUpEstimate(deliveryId  , "idk" );
        OffsetDateTime resBody = res.getBody();
        System.out.println("\033[96;40m testGetPickUpEstimate requested for UUID \033[30;106m " + deliveryId + " \033[96;40m got response: \033[30;106m " + res + " \033[0m");
        assertEquals(sampleOffsetDateTime, resBody);
    }

    @Test
    void pickUpEstimate404() {
        ResponseEntity<OffsetDateTime> res = sut.getPickUpEstimate(UUID.randomUUID()  , "idk" );
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void pickUpEstimateDoesntExist() {
        TestRestaurantRepository rp = new TestRestaurantRepository();
        TestDeliveryRepository dp = new TestDeliveryRepository();
        UUID rid = UUID.randomUUID();
        UUID did = UUID.randomUUID();
        restaurantRepo.save(new Restaurant(rid, UUID.randomUUID(), new ArrayList<>(), 1.0d));
        dp.save(new Delivery(did, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "pending", null, null, 1.d, null, "", "", 1));
        VendorController vc = new VendorController(rp, dp);
        ResponseEntity<OffsetDateTime> res = vc.getPickUpEstimate(did, "hi");
        System.out.println("\033[96;40m pickUpEstimateDoesntExist requested for UUID \033[30;106m " + did + " \033[96;40m got response: \033[30;106m " + res.getBody() + " \033[0m");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
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

    @Test
    void testSetPickUpEstimate() {
        ResponseEntity<String> res = sut.setPickUpEstimate(deliveryId, "vendor", sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getPickupTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetInvalidPickUpEstimate() {
        ResponseEntity<String> res = sut.setPickUpEstimate(deliveryId, "vendor", "hello");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void testSetPickUpUnauthorized() {
        ResponseEntity<String> res = sut.setPickUpEstimate(deliveryId, "noVendor", sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    void testSetPickUpNotFound() {
        ResponseEntity<String> res = sut.setPickUpEstimate(UUID.randomUUID(), "vendor", sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    /**
     * Good weather case: adding a new delivery, with a completely unique ID, to the database.
     * Both admins and vendors should be able to do this.
     */
    @Test
    void testCreateDeliveryGoodWeather() {
        final List<String> rolesToTest = List.of("vendor", "admin");

        for (final String roleToTest : rolesToTest) {
            // Create a new uniquely IDd delivery
            UUID newDeliveryId = generateNewDeliveryId();
            Delivery newDelivery = new Delivery(
                    newDeliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                    "status", OffsetDateTime.now(), OffsetDateTime.now(), 1.0, OffsetDateTime.now(),
                    "liveLocation", "userException", 0
            );

            // Check response status
            ResponseEntity<Delivery> response = sut.createDelivery("vendor", newDelivery);
            assertEquals(
                    HttpStatus.OK,
                    response.getStatusCode()
            );

            // Check saved delivery content
            newDelivery.setDeliveryID(response.getBody().getDeliveryID());
            assertEquals(
                    newDelivery,
                    response.getBody()
            );

            // Ensure we can fetch the new delivery from the database
            Delivery deliveryFromRepo = deliveryRepo.findById(response.getBody().getDeliveryID()).get();
            assertEquals(
                    newDelivery,
                    deliveryFromRepo
            );
        }
    }

    /**
     * Save two deliveries with the same ID to the database. This should still give them both unique IDs.
     */
    @Test
    void testCreateDeliveryDoubleId() {
        // Create a new uniquely IDd delivery
        UUID newDeliveryId = generateNewDeliveryId();
        Delivery firstNewDelivery = new Delivery(
                newDeliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "status", OffsetDateTime.now(), OffsetDateTime.now(), 1.0, OffsetDateTime.now(),
                "liveLocation", "userException", 0
        );

        // Create a different delivery, with that same ID
        Delivery secondNewDelivery = new Delivery(
                newDeliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "secondStatus", OffsetDateTime.now(), OffsetDateTime.now(), 0.5, OffsetDateTime.now(),
                "secondLiveLocation", "secondUserException", 1
        );

        // Check both response statuses
        ResponseEntity<Delivery> firstResponse = sut.createDelivery("vendor", firstNewDelivery);
        ResponseEntity<Delivery> secondResponse = sut.createDelivery("vendor", secondNewDelivery);

        assertEquals(
                HttpStatus.OK,
                firstResponse.getStatusCode()
        );
        assertEquals(
                HttpStatus.OK,
                secondResponse.getStatusCode()
        );

        // The new deliveries should not have the same ID!
        assertNotEquals(
                firstResponse.getBody().getDeliveryID(),
                secondResponse.getBody().getDeliveryID()
        );

        // Check saved content of both deliveries
        firstNewDelivery.setDeliveryID(firstResponse.getBody().getDeliveryID());
        secondNewDelivery.setDeliveryID(secondResponse.getBody().getDeliveryID());

        assertEquals(
                firstNewDelivery,
                firstResponse.getBody()
        );
        assertEquals(
                secondNewDelivery,
                secondResponse.getBody()
        );
    }

    /**
     * Passing a null delivery should result in a bad request.
     */
    @Test
    void testCreateDeliveryNull() {
        ResponseEntity<Delivery> response = sut.createDelivery("vendor", null);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    /**
     * Only vendors & admins should be able to create deliveries.
     */
    @Test
    void testCreateDeliveryWrongRoles() {
        final List<String> rolesToTest = List.of("v", "ve", "vendo", "courier", "customer", "sudo", "admi");

        for (final String roleToTest : rolesToTest) {
            Delivery delivery = new Delivery();
            ResponseEntity<Delivery> response = sut.createDelivery(roleToTest, delivery);

            assertEquals(
                    HttpStatus.UNAUTHORIZED,
                    response.getStatusCode()
            );
        }
    }

    /**
     * An empty role should not allow for delivery creation.
     */
    @Test
    void testCreateDeliveryNoRole() {
        Delivery delivery = new Delivery();
        ResponseEntity<Delivery> response = sut.createDelivery("", delivery);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }
}