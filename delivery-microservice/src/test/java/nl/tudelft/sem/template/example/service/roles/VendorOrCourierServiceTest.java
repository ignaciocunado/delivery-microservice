package nl.tudelft.sem.template.example.service.roles;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.DeliveryEstimateService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.DeliveryEventService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.OrderToCourierService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.PickUpEstimateVendorCourierService;
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


class VendorOrCourierServiceTest {

    private transient TestRestaurantRepository restaurantRepository;

    private transient UUIDGenerationService uuidGenerationService;

    private transient VendorOrCourierService vendorOrCourierService;
    private transient TestDeliveryRepository deliveryRepository;

    private transient UUID deliveryId;
    private transient OffsetDateTime sampleOffsetDateTime;

    private transient DeliveryEstimateService deliveryEstimateService;

    private transient DeliveryEventService deliveryEventService;

    private transient PickUpEstimateVendorCourierService pickUpEstimateVendorCourierService;

    private transient OrderToCourierService orderToCourierService;

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

        deliveryEstimateService = new DeliveryEstimateService(deliveryRepository);

        deliveryEventService = new DeliveryEventService(deliveryRepository);

        pickUpEstimateVendorCourierService = new PickUpEstimateVendorCourierService(deliveryRepository);

        orderToCourierService = new OrderToCourierService(deliveryRepository);

        vendorOrCourierService = new VendorOrCourierService(deliveryEstimateService, deliveryEventService,
                pickUpEstimateVendorCourierService, orderToCourierService);
    }

    @Test
    void setDeliveryDelayOk() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = deliveryEventService.setDeliveryDelay(deliveryId, 2);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 2);
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 2);
    }

    @Test
    void setDeliveryDelayOk2() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = deliveryEventService.setDeliveryDelay(deliveryId, 0);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 0);
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 0);
    }

    @Test
    void setDeliveryDelayBadBody() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = deliveryEventService.setDeliveryDelay(deliveryId, -5);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNull(res.getBody());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
    }

    @Test
    void setDeliveryDelayNullBody() {
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
        ResponseEntity<Integer> res = deliveryEventService.setDeliveryDelay(deliveryId, null);
        assertEquals(res.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertNull(res.getBody());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDelay(), 1);
    }

    @Test
    void setDeliveryDelayNotFound() {
        ResponseEntity<Integer> res = deliveryEventService.setDeliveryDelay(UUID.randomUUID(), 2);
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void getDeliveryDelayOk() {
        ResponseEntity<Integer> res = deliveryEventService.getDeliveryDelay(deliveryId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 1);
    }

    @Test
    void getDeliveryDelayNotFound() {
        ResponseEntity<Integer> res = deliveryEventService.getDeliveryDelay(UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }


    @Test
    void assignOrderToCourierOK() {
        UUID courier = UUID.randomUUID();
        ResponseEntity<UUID> res = orderToCourierService.assignOrderToCourier(courier, deliveryId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), deliveryId);
        assertEquals(deliveryRepository.findById(deliveryId).get().getCourierID(), courier);
    }

    @Test
    void assignOrderToCourierNotFound() {
        UUID courier = UUID.randomUUID();
        ResponseEntity<UUID> res = orderToCourierService.assignOrderToCourier(courier, UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    public void setDeliveryExceptionReturnsOk() {
        String exception = "Test";

        ResponseEntity<String> response = deliveryEventService
                .setDeliveryException(deliveryId, exception);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200 OK", response.getBody());
        assertEquals(exception, deliveryRepository.findById(deliveryId).get().getUserException());

        response = deliveryEventService.setDeliveryException(deliveryId, exception);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200 OK", response.getBody());
        assertEquals(exception, deliveryRepository.findById(deliveryId).get().getUserException());
    }

    @Test
    public void setDeliveryExceptionReturnsNotFound() {
        String role = "courier";
        String exception = "123.321.666";
        UUID randomId = UUID.randomUUID();

        ResponseEntity<String> response = deliveryEventService.setDeliveryException(randomId, exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error 404: Delivery not found!", response.getBody());
    }

    @Test
    public void setDeliveryExceptionReturnsBadRequest() {
        ResponseEntity<String> response = deliveryEventService.setDeliveryException(deliveryId, "");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());

        response = deliveryEventService.setDeliveryException(deliveryId, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());

        response = deliveryEventService.setDeliveryException(deliveryId, "    ");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());
    }

    @Test
    void testSetPickUpEstimate() {
        ResponseEntity<String> res = pickUpEstimateVendorCourierService
                .setPickUpEstimate(deliveryId, sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getPickupTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetPickUpEstimateWithExtraQuotes() {
        ResponseEntity<String> res = pickUpEstimateVendorCourierService
                .setPickUpEstimate(deliveryId, "\"" + sampleOffsetDateTime.toString() + "\"");
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getPickupTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetPickUpEstimate2() {
        ResponseEntity<String> res = pickUpEstimateVendorCourierService
                .setPickUpEstimate(deliveryId, sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getPickupTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetInvalidPickUpEstimate() {
        ResponseEntity<String> res = pickUpEstimateVendorCourierService.setPickUpEstimate(deliveryId, "hello");
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void testSetPickUpNotFound() {
        ResponseEntity<String> res = pickUpEstimateVendorCourierService
                .setPickUpEstimate(UUID.randomUUID(), sampleOffsetDateTime.toString());
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testGetDeliveryEstimate() {
        ResponseEntity<OffsetDateTime> res = deliveryEstimateService.getDeliveryEstimate(deliveryId);
        OffsetDateTime resBody = res.getBody();
        assertEquals(sampleOffsetDateTime, resBody);
    }

    @Test
    void testGetDeliveryEstimateNotFound() {
        ResponseEntity<OffsetDateTime> res = deliveryEstimateService.getDeliveryEstimate(UUID.randomUUID());
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
        DeliveryEstimateService vc = new DeliveryEstimateService(deliveryRepository);
        ResponseEntity<OffsetDateTime> res = vc.getDeliveryEstimate(newRandomDeliveryID);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testSetDeliveryEstimate() {
        ResponseEntity<String> res = deliveryEstimateService.setDeliveryEstimate(deliveryId, sampleOffsetDateTime);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDeliveryTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetDeliveryEstimate2() {
        ResponseEntity<String> res = deliveryEstimateService.setDeliveryEstimate(deliveryId, sampleOffsetDateTime);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals(deliveryRepository.findById(deliveryId).get().getDeliveryTimeEstimate(), sampleOffsetDateTime);
    }

    @Test
    void testSetInvalidDeliveryEstimate() {
        ResponseEntity<String> res = deliveryEstimateService.setDeliveryEstimate(deliveryId,null);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void testSetDeliveryNotFound() {
        ResponseEntity<String> res = deliveryEstimateService.setDeliveryEstimate(UUID.randomUUID(), sampleOffsetDateTime);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void testCreateDeliveryWrongRoles() {
        final List<String> rolesToTest = List.of("v", "ve", "vendo", "courie", "customer", "sudo", "admi");
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<Restaurant> response = vendorOrCourierService.checkAndHandle(roleToTest, operation);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Test
    void testCreateDeliveryCorrectRoles() {
        final List<String> rolesToTest = List.of("vendor", "admin", "courier");
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<Restaurant> response = vendorOrCourierService.checkAndHandle(roleToTest, operation);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}