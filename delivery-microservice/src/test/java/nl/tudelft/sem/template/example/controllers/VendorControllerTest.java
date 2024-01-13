package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.GetVendorRest200ResponseInner;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class VendorControllerTest {
    private transient TestRestaurantRepository restaurantRepo;
    private transient TestDeliveryRepository deliveryRepo;
    private transient UUIDGenerationService uuidGenerationService;
    private transient VendorController sut;

    private transient UUID restaurantId;
    private transient UUID restaurantId2;
    private transient UUID deliveryId;
    private transient UUID deliveryId2;

    private transient UUID vendorId;
    private transient UUID vendorId2;

    private transient OffsetDateTime sampleOffsetDateTime;

    private transient UUID courierId;

    @BeforeEach
    public void setup() {
        // create test repositories
        restaurantRepo = new TestRestaurantRepository();
        deliveryRepo = new TestDeliveryRepository();

        // initialize other services
        uuidGenerationService = new UUIDGenerationService();

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

        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), courierId,
                restaurantId, "pending", sampleOffsetDateTime, sampleOffsetDateTime,
                1.d, sampleOffsetDateTime, "", "", 1);

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
        sut = new VendorController(restaurantRepo, deliveryRepo, new UUIDGenerationService());
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
        //System.out.println("\033[96;40m testGetPickUpEstimate requested for UUID \033[30;106m " + deliveryId
        // + " \033[96;40m got response: \033[30;106m " + res + " \033[0m");
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
        VendorController vc = new VendorController(rp, dp, new UUIDGenerationService());
        ResponseEntity<OffsetDateTime> res = vc.getPickUpEstimate(did, "hi");
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

        assertEquals(res.getBody(), "NOT AUTHORIZED \n Requires vendor permissions!");
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
    void testSetPickUpEstimate2() {
        ResponseEntity<String> res = sut.setPickUpEstimate(deliveryId, "courier", sampleOffsetDateTime.toString());
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
    public void getCourierIdByDeliveryReturnsCourierId() {
        String role = "vendor";
        ResponseEntity<UUID> response = sut.getCourierIdByDelivery(deliveryId, role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courierId, response.getBody());
    }

    @Test
    public void getCourierIdByDeliveryReturnsNotFound() {
        String role = "vendor";
        ResponseEntity<UUID> response = sut.getCourierIdByDelivery(UUID.randomUUID(), role);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getCourierIdByDeliveryReturnsUnauthorized() {
        String role = "courier";
        ResponseEntity<UUID> response = sut.getCourierIdByDelivery(deliveryId, role);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testGetAllDeliveriesUnauthorized() {
        ResponseEntity<List<UUID>> res = sut.getAllDeliveriesVendor(UUID.randomUUID(), "a");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetAllDeliveriesNotFound() {
        UUID id = UUID.randomUUID();

        while (id.equals(vendorId) || id.equals(vendorId2)) {
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

    @Test
    void testGetDeliveryEstimate() {
        ResponseEntity<OffsetDateTime> res = sut.getDeliveryEstimate(deliveryId, "courier");
        OffsetDateTime resBody = res.getBody();
        assertEquals(sampleOffsetDateTime, resBody);
    }

    @Test
    void testGetDeliveryEstimateUnauthorized() {
        ResponseEntity<OffsetDateTime> res = sut.getDeliveryEstimate(deliveryId, "hi");
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    void testGetDeliveryEstimateNotFound() {
        ResponseEntity<OffsetDateTime> res = sut.getDeliveryEstimate(UUID.randomUUID(), "vendor");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testGetDeliveryEstimateDoesntExist() {
        TestRestaurantRepository restaurantRepository = new TestRestaurantRepository();
        TestDeliveryRepository deliveryRepository = new TestDeliveryRepository();
        UUID newRestaurantID = UUID.randomUUID();
        UUID newRandomDeliveryID = UUID.randomUUID();
        restaurantRepo.save(new Restaurant(newRestaurantID, UUID.randomUUID(), new ArrayList<>(), 1.0d));
        deliveryRepository.save(new Delivery(newRandomDeliveryID, UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "pending", null, null,
                1.d, null, "", "", 1));
        VendorController vc = new VendorController(restaurantRepository, deliveryRepository,
                new UUIDGenerationService());
        ResponseEntity<OffsetDateTime> res = vc.getDeliveryEstimate(newRandomDeliveryID, "vendor");
        System.out.println("\033[96;40m getDeliveryEstimateDoesntExist requested for UUID \033[30;106m "
                + newRandomDeliveryID + " \033[96;40m got response: \033[30;106m "
                + res.getBody() + " \033[0m");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testSetDeliveryEstimate() {
        ResponseEntity<String> res = sut.setDeliveryEstimate(deliveryId, "vendor", sampleOffsetDateTime);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getDeliveryTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetDeliveryEstimate2() {
        ResponseEntity<String> res = sut.setDeliveryEstimate(deliveryId, "courier", sampleOffsetDateTime);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getDeliveryTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetInvalidDeliveryEstimate() {
        ResponseEntity<String> res = sut.setDeliveryEstimate(deliveryId, "vendor", null);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void testSetDeliveryUnauthorized() {
        ResponseEntity<String> res = sut.setDeliveryEstimate(deliveryId, "noVendor", sampleOffsetDateTime);
        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    void testSetDeliveryNotFound() {
        ResponseEntity<String> res = sut.setDeliveryEstimate(UUID.randomUUID(), "vendor", sampleOffsetDateTime);
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
            Optional<UUID> newDeliveryId = uuidGenerationService.generateUniqueId(deliveryRepo);
            assertTrue(newDeliveryId.isPresent());

            Delivery newDelivery = new Delivery(
                    newDeliveryId.get(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
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
        Optional<UUID> newDeliveryId = uuidGenerationService.generateUniqueId(deliveryRepo);
        assertTrue(newDeliveryId.isPresent());

        Delivery firstNewDelivery = new Delivery(
                newDeliveryId.get(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "status", OffsetDateTime.now(), OffsetDateTime.now(), 1.0, OffsetDateTime.now(),
                "liveLocation", "userException", 0
        );

        // Create a different delivery, with that same ID
        Delivery secondNewDelivery = new Delivery(
                newDeliveryId.get(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
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

    /**
     * Tests the case where no more UUIDs are available.
     */
    @Test
    void testCreateDeliveryAllIdsUsed() {
        // We mock the repositories, so we can fake all IDs being taken.
        TestDeliveryRepository mockedDeliveryRepository = Mockito.mock(TestDeliveryRepository.class);
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);

        VendorController localVendorController = new VendorController(
                mockedRestaurantRepository, mockedDeliveryRepository, new UUIDGenerationService()
        );

        // Every single delivery ID is mapped to this one delivery
        Delivery foundDelivery = new Delivery();
        Mockito.when(mockedDeliveryRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(foundDelivery));

        // So, when a new delivery is created, it should get stuck in a loop and exit!
        final Delivery deliveryToCreate = new Delivery();
        ResponseEntity<Delivery> response = localVendorController.createDelivery("vendor", deliveryToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    /**
     * Saving to the database fails, and returns null. Error must be handled!
     */
    @Test
    void testCreateDeliverySavingFailed() {
        // We mock the repositories, so we can fake saving failing.
        TestDeliveryRepository mockedDeliveryRepository = Mockito.mock(TestDeliveryRepository.class);
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);

        VendorController localVendorController = new VendorController(
                mockedRestaurantRepository, mockedDeliveryRepository, new UUIDGenerationService()
        );

        // Saving always fails and returns null
        Mockito.when(mockedDeliveryRepository.save(Mockito.any()))
                .thenReturn(null);

        // Ensure a server error occurs
        final Delivery deliveryToCreate = new Delivery();
        ResponseEntity<Delivery> response = localVendorController.createDelivery("vendor", deliveryToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    /**
     * Retrieving the created delivery from the database fails! Ensure error occurs.
     */
    @Test
    void testCreateDeliveryRetrievalFailed() {
        // We mock the repositories, so we can fake retrieval failing.
        TestDeliveryRepository mockedDeliveryRepository = Mockito.mock(TestDeliveryRepository.class);
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);

        VendorController localVendorController = new VendorController(
                mockedRestaurantRepository, mockedDeliveryRepository, new UUIDGenerationService()
        );

        final Delivery deliveryToCreate = new Delivery();
        Mockito.when(mockedDeliveryRepository.save(Mockito.any()))
                .thenReturn(deliveryToCreate);

        // Retrieval always fails and returns empty
        Mockito.when(mockedDeliveryRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        // Ensure a server error occurs
        ResponseEntity<Delivery> response = localVendorController.createDelivery("vendor", deliveryToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    @Test
    void testGetVendorRestUnauthorized() {
        ResponseEntity<List<GetVendorRest200ResponseInner>> res = sut.getVendorRest(vendorId, "not");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testGetVendorRestNotFound() {
        UUID id = UUID.randomUUID();
        while (id.equals(vendorId) || id.equals(vendorId2)) {
            id = UUID.randomUUID();
        }
        ResponseEntity<List<GetVendorRest200ResponseInner>> res = sut.getVendorRest(id, "vendor");

        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetVendorRestOk() {
        ResponseEntity<List<GetVendorRest200ResponseInner>> res = sut.getVendorRest(vendorId, "vendor");

        GetVendorRest200ResponseInner elem = new GetVendorRest200ResponseInner();
        elem.setRestaurantID(restaurantId);
        assertEquals(res.getBody(), List.of(elem));
        assertEquals(res.getStatusCode(), HttpStatus.OK);
    }
}