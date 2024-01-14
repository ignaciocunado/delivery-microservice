package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import nl.tudelft.sem.template.example.service.roles.GlobalService;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

public class GlobalServiceTest {

    private transient GlobalService globalService;
    private transient TestDeliveryRepository deliveryRepository;
    private transient TestRestaurantRepository restaurantRepository;

    private transient UUID deliveryId;
    private transient UUID restaurantId;
    private transient UUID orderId;

    /**
     * The full delivery object that was created during test setup.
     */
    private transient Delivery delivery;

    /**
     * Used to generate UUIDs for test objects.
     */
    private transient UUIDGenerationService uuidGenerationService;

    private transient OffsetDateTime pickupTimeEstimate;

    @BeforeEach
    void setUp() {
        deliveryRepository = new TestDeliveryRepository();
        restaurantRepository = new TestRestaurantRepository();
        uuidGenerationService = new UUIDGenerationService();

        deliveryId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();

        orderId = UUID.randomUUID();

        pickupTimeEstimate = OffsetDateTime.of(
                2024, 1, 4, 18, 23, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        OffsetDateTime deliveryTimeEstimate = OffsetDateTime.of(
                2024, 1, 4, 22, 10, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        OffsetDateTime pickedUpTime = OffsetDateTime.of(
                2024, 1, 4, 18, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );

        delivery = new Delivery(deliveryId, orderId, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "pending", pickupTimeEstimate, deliveryTimeEstimate, 1.d,
                pickedUpTime, "69.655,69.425", "late", 1);
        deliveryRepository.save(delivery);

        Restaurant r = new Restaurant(restaurantId, UUID.randomUUID(), new ArrayList<>(), 10.2d);

        deliveryRepository.save(delivery);

        restaurantRepository.save(r);

        globalService = new GlobalService(restaurantRepository, deliveryRepository);
    }

    @Test
    void getLiveLocation() {
        ResponseEntity<String> res = globalService.getLiveLocation(deliveryId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), "69.655,69.425");
    }

    @Test
    void getLiveLocationNotFound() {
        ResponseEntity<String> res = globalService.getLiveLocation(UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void getUserException() {
        ResponseEntity<String> res = globalService.getDeliveryException(deliveryId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), "late");
    }

    @Test
    void getUserExceptionNotFound() {
        ResponseEntity<String> res = globalService.getDeliveryException(UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void getUserExceptionEmpty() {
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2024, 1, 4, 18, 23, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        UUID id = UUID.randomUUID();
        Delivery save = new  Delivery(id, UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), "pending", sampleOffsetDateTime,
                sampleOffsetDateTime, 1.d, sampleOffsetDateTime,
                "69.655,69.425", "", 1);

        deliveryRepository.save(save);
        ResponseEntity<String> res = globalService.getDeliveryException(id);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), "");
    }


    @Test
    void getUserExceptionNull() {
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2024, 1, 4, 18, 23, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        UUID id = UUID.randomUUID();
        Delivery save = new  Delivery(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d, sampleOffsetDateTime,
                "69.655,69.425", null, 1);
        deliveryRepository.save(save);
        ResponseEntity<String> res = globalService.getDeliveryException(id);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), "");
    }

    @Test
    void testDeliveryZoneNotFound() {
        ResponseEntity<Double> res = globalService.getMaxDeliveryZone(UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeliveryZoneOk() {

        ResponseEntity<Double> res = globalService.getMaxDeliveryZone(restaurantId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 10.2d);
    }


    /**
     * Normal situation, in which the queried delivery exists.
     */
    @Test
    void testGetDeliveryByIdGoodWeather() {
        ResponseEntity<Delivery> response = globalService.getDeliveryById(deliveryId);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );
        assertEquals(
                delivery,
                response.getBody()
        );
    }

    /**
     * The specified delivery does not exist.
     */
    @Test
    void testGetDeliveryByIdNotFound() {
        Optional<UUID> invalidDeliveryId = uuidGenerationService.generateUniqueId(List.of(deliveryId));
        assertTrue(invalidDeliveryId.isPresent());

        ResponseEntity<Delivery> response = globalService.getDeliveryById(invalidDeliveryId.get());
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
    }

    /**
     * The normal situation, in which a delivery and its restaurant ID both exist.
     */
    @Test
    void testGetRestaurantIdByDeliveryIdGoodWeather() {
        ResponseEntity<UUID> response = globalService.getRestaurantIdByDeliveryId(deliveryId);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );
        assertEquals(
                delivery.getRestaurantID(),
                response.getBody()
        );
    }

    /**
     * The specified delivery does not exist.
     */
    @Test
    void testGetRestaurantIdByDeliveryIdNotFound() {
        Optional<UUID> invalidDeliveryId = uuidGenerationService.generateUniqueId(List.of(deliveryId));
        assertTrue(invalidDeliveryId.isPresent());

        ResponseEntity<UUID> response = globalService.getRestaurantIdByDeliveryId(invalidDeliveryId.get());
        assertEquals(
                response.getStatusCode(),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * The normal situation, in which a delivery and its order ID both exist.
     */
    @Test
    void testGetOrderByDeliveryIdGoodWeather() {
        ResponseEntity<UUID> response = globalService
                .getOrderByDeliveryId(deliveryId);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );
        assertEquals(
                orderId,
                response.getBody()
        );
    }

    /**
     * The specified delivery does not exist.
     */
    @Test
    void testGetOrderByDeliveryIdNotFound() {
        Optional<UUID> invalidDeliveryId = uuidGenerationService.generateUniqueId(List.of(deliveryId));
        assertTrue(invalidDeliveryId.isPresent());

        ResponseEntity<UUID> response = globalService.getOrderByDeliveryId(invalidDeliveryId.get());
        assertEquals(
                response.getStatusCode(),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * The normal situation, in which a delivery and its rating both exist.
     */
    @Test
    void testGetRatingByDeliveryIdGoodWeather() {
        ResponseEntity<Double> response = globalService.getRatingByDeliveryId(deliveryId);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );
        assertEquals(
                delivery.getCustomerRating(),
                response.getBody()
        );
    }

    /**
     * The specified delivery does not exist.
     */
    @Test
    void testGetRatingByDeliveryIdNotFound() {
        Optional<UUID> invalidDeliveryId = uuidGenerationService.generateUniqueId(List.of(deliveryId));
        assertTrue(invalidDeliveryId.isPresent());

        ResponseEntity<Double> response = globalService.getRatingByDeliveryId(invalidDeliveryId.get());
        assertEquals(
                response.getStatusCode(),
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    void testSetMaxZoneNotFound() {
        UUID id = UUID.randomUUID();
        while (id.equals(restaurantId)) {
            id = UUID.randomUUID();
        }

        ResponseEntity<Void> res = globalService.setMaxDeliveryZone(id, 25.0);

        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testSetMaxZoneOk() {
        ResponseEntity<Void> res = globalService.setMaxDeliveryZone(restaurantId, 25.1d);

        assertEquals(res.getStatusCode(), HttpStatus.OK);

        Restaurant r = restaurantRepository.findById(restaurantId).get();

        assertEquals(r.getMaxDeliveryZone(), 25.1d);
    }

    /**
     * Ensure that an invalid delivery returns 404, when getting its pick-up time estimate.
     */
    @Test
    void testGetPickupTimeEstimateInvalidDelivery() {
        final List<String> rolesToTest = List.of("customer");
        for (final String roleToTest : rolesToTest) {
            // Generate a delivery ID that is sure to be invalid (it doesn't point to a DB object)
            Optional<UUID> invalidDeliveryId = uuidGenerationService.generateUniqueId(deliveryRepository);
            assertTrue(invalidDeliveryId.isPresent());

            // Try to fetch that delivery
            ResponseEntity<OffsetDateTime> response = globalService.getPickUpTime(
                    invalidDeliveryId.get());
            assertEquals(
                    HttpStatus.NOT_FOUND,
                    response.getStatusCode()
            );
        }
    }

    @Test
    public void testGetPickUpTime(){
        ResponseEntity<OffsetDateTime> response = globalService.getPickUpTime(deliveryId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(pickupTimeEstimate, response.getBody());
    }
}
