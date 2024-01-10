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
    UUID restaurantId2;
    UUID deliveryId;
    UUID deliveryId2;

    UUID vendorId;
    UUID vendorId2;

    OffsetDateTime sampleOffsetDateTime;

    UUID courierId;

    @BeforeEach
    public void setup() {
        // create test repositories
        restaurantRepo = new TestRestaurantRepository();
        deliveryRepo = new TestDeliveryRepository();

        // generate random UUID
        restaurantId = UUID.randomUUID();
        restaurantId2 = UUID.randomUUID();
        deliveryId = UUID.randomUUID();
        courierId = UUID.randomUUID();
        vendorId = UUID.randomUUID();
        vendorId2 = UUID.randomUUID();
        deliveryId2 = UUID.randomUUID();

        RestaurantCourierIDsInner elem1 = new RestaurantCourierIDsInner();
        elem1.setCourierID(courierId);

        List<RestaurantCourierIDsInner> param = new ArrayList<>();
        param.add(elem1);

        // setup test repository with some sample objects
        Restaurant r = new Restaurant(restaurantId, vendorId, param, 1.0d);
        Restaurant r2 = new Restaurant(restaurantId2, vendorId2, param, 1.0d);
        restaurantRepo.save(r);
        restaurantRepo.save(r2);

        sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );

        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), restaurantId, "pending", sampleOffsetDateTime,
                sampleOffsetDateTime, 1.d, sampleOffsetDateTime,
                "", "", 1);
        Delivery d2 = new  Delivery(deliveryId2, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), restaurantId, "pending", sampleOffsetDateTime,
                sampleOffsetDateTime, 1.d, sampleOffsetDateTime, "",
                "", 1);
        Delivery d3 = new  Delivery(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), "pending", sampleOffsetDateTime,
                sampleOffsetDateTime, 1.d, sampleOffsetDateTime, "",
                "", 1);
        deliveryRepo.save(d);
        deliveryRepo.save(d2);
        deliveryRepo.save(d3);
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
    void testGetPickUpEstimate() {
        ResponseEntity<OffsetDateTime> res = sut.getPickUpEstimate(deliveryId, "idk");
        OffsetDateTime resBody = res.getBody();
        System.out.println("\033[96;40m testGetPickUpEstimate requested for UUID \033[30;106m "
                + deliveryId + " \033[96;40m got response: \033[30;106m " + res + " \033[0m");
        assertEquals(sampleOffsetDateTime, resBody);
    }

    @Test
    void pickUpEstimate404() {
        ResponseEntity<OffsetDateTime> res = sut.getPickUpEstimate(UUID.randomUUID(), "idk");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void pickUpEstimateDoesntExist() {
        TestRestaurantRepository rp = new TestRestaurantRepository();
        TestDeliveryRepository dp = new TestDeliveryRepository();
        UUID rid = UUID.randomUUID();
        UUID did = UUID.randomUUID();
        restaurantRepo.save(new Restaurant(rid, UUID.randomUUID(), new ArrayList<>(), 1.0d));
        dp.save(new Delivery(did, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "pending", null, null, 1.d, null,
                "", "", 1));
        VendorController vc = new VendorController(rp, dp);
        ResponseEntity<OffsetDateTime> res = vc.getPickUpEstimate(did, "hi");
        System.out.println("\033[96;40m pickUpEstimateDoesntExist requested for UUID \033[30;106m " + did
                + " \033[96;40m got response: \033[30;106m " + res.getBody() + " \033[0m");
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
    void testGetRestaurantUnauthorized() {
        ResponseEntity<String> res = sut.getRest(UUID.randomUUID(), "noVendor");

        assertEquals(res.getBody(),"NOT AUTHORIZED \n Requires vendor permissions!" );
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetRestaurantNotFound() {
        ResponseEntity<String> res = sut.getRest(UUID.randomUUID(), "vendor");

        assertEquals(res.getBody(), "NOT FOUND \n No restaurant with the given id has been found");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetRestaurantOk() {
        ResponseEntity<String> res = sut.getRest(restaurantId, "vendor");

        assertEquals(res.getBody(), restaurantRepo.findById(restaurantId).get().toString());
        assertEquals(res.getStatusCode(), HttpStatus.OK);
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

    @Test
    void testGetAllDeliveriesUnauthorized() {
        ResponseEntity<List<UUID>> res = sut.getAllDeliveriesVendor(UUID.randomUUID(), "a");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetAllDeliveriesNotFound() {
        UUID id = UUID.randomUUID();

        while(id.equals(vendorId) || id.equals(vendorId2)) {
            id = UUID.randomUUID();
        }

        ResponseEntity<List<UUID>> res = sut.getAllDeliveriesVendor(id, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    void testGetAllDeliveriesOkEmptyArray() {
        ResponseEntity<List<UUID>> res = sut.getAllDeliveriesVendor(vendorId2, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), new ArrayList<>());
    }

    @Test
    void testGetAllDeliveriesOkNonEmpty() {
        ResponseEntity<List<UUID>> res = sut.getAllDeliveriesVendor(vendorId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(deliveryId, deliveryId2));
    }
}