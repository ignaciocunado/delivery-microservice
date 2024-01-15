package nl.tudelft.sem.template.example.service.roles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.*;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;


class VendorServiceTest {
    private transient TestRestaurantRepository restaurantRepo;
    private transient TestDeliveryRepository deliveryRepo;
    private transient UUIDGenerationService uuidGenerationService;
    private transient VendorService sut;

    private transient UUID restaurantId;
    private transient UUID deliveryId;
    private transient UUID deliveryId2;

    private transient UUID vendorId;
    private transient UUID vendorId2;

    private transient OffsetDateTime sampleOffsetDateTime;

    private transient UUID courierId;

    private transient List<UUID> couriersList;

    private transient CourierToRestaurantService courierToRestaurantService;

    private transient DeliveryIdGetterService deliveryIdGetterService;

    private transient DeliveryManipulationService deliveryManipulationService;

    private transient DeliveryStatusService deliveryStatusService;

    private transient PickUpEstimateService pickUpEstimateService;

    private transient RestaurantGetterService restaurantGetterService;


    @BeforeEach
    public void setup() {
        // create test repositories
        restaurantRepo = new TestRestaurantRepository();
        deliveryRepo = new TestDeliveryRepository();

        // initialize other services
        uuidGenerationService = new UUIDGenerationService();

        // generate random UUID
        restaurantId = UUID.randomUUID();
        UUID restaurantId2 = UUID.randomUUID();
        deliveryId = UUID.randomUUID();
        courierId = UUID.randomUUID();
        vendorId = UUID.randomUUID();
        vendorId2 = UUID.randomUUID();
        deliveryId2 = UUID.randomUUID();

        // setup test repository with some sample objects
        if ("true".equals(System.getProperty("isRunningPiTest"))) {
            // Logic specific to PiTest
            couriersList = new ArrayList<UUID>();
        } else {
            couriersList = Mockito.spy(new ArrayList<UUID>());
        }
        couriersList.add(courierId);
        Restaurant r1 = new Restaurant(restaurantId, vendorId, couriersList, 1.0d);
        Restaurant r2 = new Restaurant(restaurantId2, vendorId2, List.of(courierId), 1.0d);
        restaurantRepo.save(r1);
        restaurantRepo.save(r2);

        sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );

        Delivery d = new Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), courierId,
                restaurantId, "pending", sampleOffsetDateTime, sampleOffsetDateTime,
                1.d, sampleOffsetDateTime, "", "", 1);

        Delivery d2 = new Delivery(deliveryId2, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), restaurantId, "pending", sampleOffsetDateTime,
                sampleOffsetDateTime, 1.d, sampleOffsetDateTime, "",
                "", 1);
        Delivery d3 = new Delivery(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), "pending", sampleOffsetDateTime,
                sampleOffsetDateTime, 1.d, sampleOffsetDateTime, "",
                "", 1);
        deliveryRepo.save(d);
        deliveryRepo.save(d2);
        deliveryRepo.save(d3);

        courierToRestaurantService = new CourierToRestaurantService(restaurantRepo, deliveryRepo, new UUIDGenerationService());

        deliveryIdGetterService = new DeliveryIdGetterService(restaurantRepo, deliveryRepo, new UUIDGenerationService());

        deliveryManipulationService = new DeliveryManipulationService(restaurantRepo, deliveryRepo, new UUIDGenerationService());

        deliveryStatusService = new DeliveryStatusService(restaurantRepo, deliveryRepo, new UUIDGenerationService());

        pickUpEstimateService = new PickUpEstimateService(restaurantRepo, deliveryRepo, new UUIDGenerationService());

        restaurantGetterService = new RestaurantGetterService(restaurantRepo, deliveryRepo, new UUIDGenerationService());

        sut = new VendorService(courierToRestaurantService, deliveryIdGetterService, deliveryManipulationService, deliveryStatusService, pickUpEstimateService, restaurantGetterService);
    }

    @Test
    public void testAddCourierToRestNotFound() {
        ResponseEntity<Void> res = courierToRestaurantService.addCourierToRest(UUID.randomUUID(), UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testAddCourierToRestOkNoDuplicate() {
        UUID courierId = UUID.randomUUID();
        ResponseEntity<Void> res = courierToRestaurantService.addCourierToRest(restaurantId, courierId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);

        Restaurant newRes = courierToRestaurantService.getRestaurantRepository().findById(restaurantId).get();
        assertFalse(
                newRes.getCourierIDs().stream()
                        .filter(x -> x.equals(courierId))
                        .collect(Collectors.toList()).isEmpty()
        );
    }

    @Test
    public void testAddCourierToRestDuplicate() {
        UUID courierId = UUID.randomUUID();
        ResponseEntity<Void> res = courierToRestaurantService.addCourierToRest(restaurantId, courierId);

        assertEquals(res.getStatusCode(), HttpStatus.OK);
        Restaurant newRes = courierToRestaurantService.getRestaurantRepository().findById(restaurantId).get();
        assertTrue(newRes.getCourierIDs().contains(courierId));

        ResponseEntity<Void> res2 = courierToRestaurantService.addCourierToRest(restaurantId, courierId);
        assertEquals(res2.getStatusCode(), HttpStatus.OK);
        assertNull(res2.getBody());
        Restaurant newRes2 = courierToRestaurantService.getRestaurantRepository().findById(restaurantId).get();
        assertEquals(newRes.getCourierIDs().stream().filter(c -> c.equals(courierId)).collect(Collectors.toList()).size(), 1);

    }

    @Test
    void testAcceptNotFound() {
        ResponseEntity<Void> res = deliveryStatusService.acceptDelivery(UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testAcceptOk() {
        ResponseEntity<Void> res = deliveryStatusService.acceptDelivery(deliveryId);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "accepted");
    }

    @Test
    void testRejectNotFound() {
        ResponseEntity<Void> res = deliveryStatusService.rejectDelivery(UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testRejectOk() {
        ResponseEntity<Void> res = deliveryStatusService.rejectDelivery(deliveryId);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "rejected");
    }

    @Test
    void testRemoveRestaurantNotFound() {
        ResponseEntity<Void> res = courierToRestaurantService.removeCourierRest(UUID.randomUUID(), UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }


    @Test
    void testRemoveCourierNotFound() {
        UUID randomCourierId = UUID.randomUUID();
        ResponseEntity<Void> res = courierToRestaurantService.removeCourierRest(randomCourierId, restaurantId);
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        // make sure that the restaurant is found AND that there is a check whether the courier is in the restaurant
        if (!"true".equals(System.getProperty("isRunningPiTest"))) {
            verify(couriersList, atLeastOnce()).contains(randomCourierId);
        }
    }

    @Test
    void testRemoveCourierOk() {
        ResponseEntity<Void> res = courierToRestaurantService.removeCourierRest(courierId, restaurantId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);

        TestRestaurantRepository repo = (TestRestaurantRepository) courierToRestaurantService.getRestaurantRepository();

        assertTrue(repo.findById(restaurantId).get().getCourierIDs().isEmpty());
    }

    @Test
    void testGetPickUpEstimate() {
        ResponseEntity<OffsetDateTime> res = pickUpEstimateService.getPickUpEstimate(deliveryId);
        OffsetDateTime resBody = res.getBody();
        assertEquals(sampleOffsetDateTime, resBody);
    }

    @Test
    void pickUpEstimate404() {
        ResponseEntity<OffsetDateTime> res = pickUpEstimateService.getPickUpEstimate(UUID.randomUUID());
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
        PickUpEstimateService vc = new PickUpEstimateService(rp, dp, new UUIDGenerationService());
        ResponseEntity<OffsetDateTime> res = vc.getPickUpEstimate(did);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testGetCustomerIDOk() {
        ResponseEntity<UUID> response = deliveryIdGetterService.getCustomerByDeliveryId(deliveryId);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), deliveryRepo.findById(deliveryId).get().getCustomerID());
    }

    @Test
    void testGetCustomerIDNotFound() {
        ResponseEntity<UUID> response = deliveryIdGetterService.getCustomerByDeliveryId(UUID.randomUUID());
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(response.getBody());
    }

    @Test
    void testEditStatusDeliveryNotFound() {
        ResponseEntity<Void> res = deliveryStatusService.editStatusDelivery(UUID.randomUUID(), "preparing");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testEditStatusDeliveryOk() {
        ResponseEntity<Void> res = deliveryStatusService.editStatusDelivery(deliveryId, "preparing");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "preparing");
    }

    @Test
    void testEditStatusDeliveryOkQuotes() {
        ResponseEntity<Void> res = deliveryStatusService.editStatusDelivery(deliveryId, "\"preparing\"");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "preparing");
    }

    @Test
    void testEditStatusDeliveryInvalidStatus() {
        ResponseEntity<Void> res = deliveryStatusService.editStatusDelivery(deliveryId, "invalid");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void testEditStatusDeliveryValidStatus2() {
        ResponseEntity<Void> res = deliveryStatusService.editStatusDelivery(deliveryId, "given to courier");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepo.findById(deliveryId).get().getStatus(), "given to courier");
    }

    @Test
    void testGetRestaurantNotFound() {
        ResponseEntity<String> res = restaurantGetterService.getRest(UUID.randomUUID());

        assertEquals(res.getBody(), "NOT FOUND \n No restaurant with the given id has been found");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetRestaurantOk() throws JsonProcessingException {
        ResponseEntity<String> res = restaurantGetterService.getRest(restaurantId);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(restaurantRepo.findById(restaurantId).get());
        assertEquals(res.getBody(), jsonString);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getCourierIdByDeliveryReturnsCourierId() {
        ResponseEntity<UUID> response = deliveryIdGetterService.getCourierIdByDelivery(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courierId, response.getBody());
    }

    @Test
    public void getCourierIdByDeliveryReturnsNotFound() {
        ResponseEntity<UUID> response = deliveryIdGetterService.getCourierIdByDelivery(UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetAllDeliveriesNotFound() {
        UUID id = UUID.randomUUID();

        while (id.equals(vendorId) || id.equals(vendorId2)) {
            id = UUID.randomUUID();
        }

        ResponseEntity<List<UUID>> res = deliveryManipulationService.getAllDeliveriesVendor(id);
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetAllDeliveriesOkEmptyArray() {
        ResponseEntity<List<UUID>> res = deliveryManipulationService.getAllDeliveriesVendor(vendorId2);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), new ArrayList<>());
    }

    @Test
    void testGetAllDeliveriesOkNonEmpty() {
        ResponseEntity<List<UUID>> res = deliveryManipulationService.getAllDeliveriesVendor(vendorId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(deliveryId, deliveryId2));
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
                    newDeliveryId.get(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), restaurantId,
                    "status", OffsetDateTime.now(), OffsetDateTime.now(), 1.0, OffsetDateTime.now(),
                    "liveLocation", "userException", 0
            );

            // Check response status
            ResponseEntity<Delivery> response = deliveryManipulationService.createDelivery(newDelivery);
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
                newDeliveryId.get(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), restaurantId,
                "status", OffsetDateTime.now(), OffsetDateTime.now(), 1.0, OffsetDateTime.now(),
                "liveLocation", "userException", 0
        );

        // Create a different delivery, with that same ID
        Delivery secondNewDelivery = new Delivery(
                newDeliveryId.get(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), restaurantId,
                "secondStatus", OffsetDateTime.now(), OffsetDateTime.now(), 0.5, OffsetDateTime.now(),
                "secondLiveLocation", "secondUserException", 1
        );

        // Check both response statuses
        ResponseEntity<Delivery> firstResponse = deliveryManipulationService.createDelivery(firstNewDelivery);
        ResponseEntity<Delivery> secondResponse = deliveryManipulationService.createDelivery(secondNewDelivery);

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
        ResponseEntity<Delivery> response = deliveryManipulationService.createDelivery(null);

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
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<Restaurant> response = sut.checkAndHandle(roleToTest, operation);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Test
    void testCreateDeliveryCorrectRoles() {
        final List<String> rolesToTest = List.of("vendor", "admin");
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<Restaurant> response = sut.checkAndHandle(roleToTest, operation);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }


    /**
     * Tests the case where no more UUIDs are available.
     */
    @Test
    void testCreateDeliveryAllIdsUsed() {
        // We mock the repositories, so we can fake all IDs being taken.
        TestDeliveryRepository mockedDeliveryRepository = Mockito.mock(TestDeliveryRepository.class);
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);

        DeliveryManipulationService localVendorService = new DeliveryManipulationService(
                mockedRestaurantRepository, mockedDeliveryRepository, new UUIDGenerationService()
        );

        // Every single delivery ID is mapped to this one delivery
        Delivery foundDelivery = new Delivery();
        foundDelivery.setRestaurantID(restaurantId);

        Mockito.when(mockedDeliveryRepository.findById(Mockito.any()))
                .thenReturn(Optional.of(foundDelivery));

        // So, when a new delivery is created, it should get stuck in a loop and exit!
        final Delivery deliveryToCreate = new Delivery();
        deliveryToCreate.setRestaurantID(restaurantId);

        ResponseEntity<Delivery> response = localVendorService.createDelivery(deliveryToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }


    // Reason for removing this test:
    // it is actually testing the JPA repository .save() method, not the controller.
    // furthermore, the mocked output is something the actual repository can legally never return.
//    /**
//     * Saving to the database fails, and returns null. Error must be handled!
//     */
//    @Test
//    void testCreateDeliverySavingFailed() {
//        // We mock the repositories, so we can fake saving failing.
//        TestDeliveryRepository mockedDeliveryRepository = Mockito.mock(TestDeliveryRepository.class);
//        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);
//
//        VendorService localVendorController = new VendorService(
//                mockedRestaurantRepository, mockedDeliveryRepository, new UUIDGenerationService()
//        );
//
//        // Saving always fails and returns null
//        Mockito.when(mockedDeliveryRepository.save(Mockito.any()))
//                .thenReturn(null);
//
//        // Restaurants always exist
//        Mockito.when(mockedRestaurantRepository.existsById(Mockito.any()))
//                .thenReturn(true);
//
//        // Ensure a server error occurs
//        final Delivery deliveryToCreate = new Delivery();
//        deliveryToCreate.setRestaurantID(restaurantId);
//        ResponseEntity<Delivery> response = localVendorController.createDelivery("vendor", deliveryToCreate);
//
//        assertEquals(
//                HttpStatus.BAD_REQUEST,
//                response.getStatusCode()
//        );
//    }

    /**
     * Retrieving the created delivery from the database fails! Ensure error occurs.
     */
    @Test
    void testCreateDeliveryRetrievalFailed() {
        // We mock the repositories, so we can fake retrieval failing.
        TestDeliveryRepository mockedDeliveryRepository = Mockito.mock(TestDeliveryRepository.class);
        TestRestaurantRepository mockedRestaurantRepository = Mockito.mock(TestRestaurantRepository.class);

        DeliveryManipulationService localVendorService = new DeliveryManipulationService(
                mockedRestaurantRepository, mockedDeliveryRepository, new UUIDGenerationService()
        );

        final Delivery deliveryToCreate = new Delivery();
        deliveryToCreate.setRestaurantID(restaurantId);

        Mockito.when(mockedDeliveryRepository.save(Mockito.any()))
                .thenReturn(deliveryToCreate);

        // Retrieval always fails and returns empty
        Mockito.when(mockedDeliveryRepository.findById(Mockito.any()))
                .thenReturn(Optional.empty());

        // Restaurants always exist
        Mockito.when(mockedRestaurantRepository.existsById(Mockito.any()))
                .thenReturn(true);

        // Ensure a server error occurs
        ResponseEntity<Delivery> response = localVendorService.createDelivery(deliveryToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    @Test
    void testGetVendorRestNotFound() {
        UUID id = UUID.randomUUID();
        while (id.equals(vendorId) || id.equals(vendorId2)) {
            id = UUID.randomUUID();
        }
        ResponseEntity<List<UUID>> res = restaurantGetterService.getVendorRest(id);

        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testGetVendorRestOk() {
        ResponseEntity<List<UUID>> res = restaurantGetterService.getVendorRest(vendorId);

        assertEquals(res.getBody(), List.of(restaurantId));
        assertEquals(res.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void testCreateDeliveryButDeliveryIsNull() {
        ResponseEntity<Delivery> response = deliveryManipulationService.createDelivery(null);
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }
    @Test
    void testCreateDeliveryRestaurantNull() {
        final Delivery deliveryToCreate = new Delivery();
        deliveryToCreate.setRestaurantID(null);

        ResponseEntity<Delivery> response = deliveryManipulationService.createDelivery(deliveryToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }

    @Test
    void testCreateDeliveryRestaurantDoesNotExist() {
        // Generate a new, non-existing restaurant ID
        final Optional<UUID> invalidRestaurantId = uuidGenerationService.generateUniqueId(restaurantRepo);
        assertTrue(invalidRestaurantId.isPresent());

        final Delivery deliveryToCreate = new Delivery();
        deliveryToCreate.setRestaurantID(invalidRestaurantId.get());

        ResponseEntity<Delivery> response = deliveryManipulationService.createDelivery(deliveryToCreate);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );
    }
}
