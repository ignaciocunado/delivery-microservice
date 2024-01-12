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
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalControllerTest {

    private transient GlobalController globalController;
    private transient TestDeliveryRepository deliveryRepository;
    private transient TestRestaurantRepository restaurantRepository;

    private transient UUID deliveryId;
    private transient UUID restaurantId;
    private transient UUID orderId;

    /**
     * The full delivery object that was created during test setup.
     */
    private transient Delivery delivery;

    @BeforeEach
    void setUp() {
        deliveryRepository = new TestDeliveryRepository();
        restaurantRepository = new TestRestaurantRepository();

        deliveryId = UUID.randomUUID();
        restaurantId = UUID.randomUUID();

        orderId = UUID.randomUUID();

        OffsetDateTime pickupTimeEstimate = OffsetDateTime.of(
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

        globalController = new GlobalController(restaurantRepository, deliveryRepository);
    }

    /**
     * Helper that generates a new UUID, that is NOT equal to the current deliveryId.
     * @return A delivery ID not equal to the test's 'deliveryId'. This ID is invalid for testing purposes,
     *         until used to save an object to the DB.
     */
    UUID generateNewDeliveryId() {

        UUID invalidDeliveryId;

        do {
            invalidDeliveryId = UUID.randomUUID();
        } while (invalidDeliveryId == deliveryId);

        return invalidDeliveryId;
    }

    @Test
    void getLiveLocation() {
        ResponseEntity<String> res = globalController.getLiveLocation(deliveryId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), "69.655,69.425");
    }

    @Test
    void getLiveLocationNotFound() {
        ResponseEntity<String> res = globalController.getLiveLocation(UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void getLiveLocationUnauthorized() {
        ResponseEntity<String> res = globalController.getLiveLocation(UUID.randomUUID(), "norole");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertNull(res.getBody());
    }

    @Test
    void getUserException() {
        ResponseEntity<String> res = globalController.getDeliveryException(deliveryId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), "late");
    }

    @Test
    void getUserExceptionNotFound() {
        ResponseEntity<String> res = globalController.getDeliveryException(UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void getUserExceptionUnauthorized() {
        ResponseEntity<String> res = globalController.getDeliveryException(deliveryId, "norole");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
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
        ResponseEntity<String> res = globalController.getDeliveryException(id, "vendor");
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
                "69.655,69.425", "", 1);
        deliveryRepository.save(save);
        ResponseEntity<String> res = globalController.getDeliveryException(id, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), "");
    }

    @Test
    void testDeliveryZoneUnauthorized() {
        ResponseEntity<Double> res = globalController.getMaxDeliveryZone(restaurantId, "nothing");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testDeliveryZoneNotFound() {
        ResponseEntity<Double> res = globalController.getMaxDeliveryZone(UUID.randomUUID(), "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeliveryZoneOk() {

        ResponseEntity<Double> res = globalController.getMaxDeliveryZone(restaurantId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), 10.2d);
    }


    /**
     * Normal situation, in which the queried delivery exists.
     */
    @Test
    void testGetDeliveryByIdGoodWeather() {
        ResponseEntity<Delivery> response = globalController.getDeliveryById(deliveryId, "courier");

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
        UUID invalidDeliveryId = generateNewDeliveryId();

        ResponseEntity<Delivery> response = globalController.getDeliveryById(invalidDeliveryId, "courier");
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );
    }

    /**
     * Ensures that every role can get a delivery.
     */
    @Test
    void testGetDeliveryByIdAllRoles() {
        List<String> rolesToTest = List.of("courier", "vendor", "admin", "customer");

        for (String roleToTest : rolesToTest) {
            ResponseEntity<Delivery> response = globalController.getDeliveryById(deliveryId, roleToTest);

            assertEquals(
                    HttpStatus.OK,
                    response.getStatusCode()
            );
            assertEquals(
                    delivery,
                    response.getBody()
            );
        }
    }

    /**
     * Ensures that a valid role is required to get a delivery.
     */
    @Test
    void testGetDeliveryByIdUnauthorized() {
        ResponseEntity<Delivery> response = globalController.getDeliveryById(deliveryId, "ve");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        response = globalController.getDeliveryById(deliveryId, "unauthorizedRole");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    /**
     * Ensures that a role is required at all to get a delivery.
     */
    @Test
    void testGetDeliveryByIdNoAuthorization() {
        ResponseEntity<Delivery> response = globalController.getDeliveryById(deliveryId, "");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    /**
     * The normal situation, in which a delivery and its restaurant ID both exist.
     */
    @Test
    void testGetRestaurantIdByDeliveryIdGoodWeather() {
        ResponseEntity<UUID> response = globalController.getRestaurantIdByDeliveryId(deliveryId, "courier");

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
        UUID invalidDeliveryId = generateNewDeliveryId();

        ResponseEntity<UUID> response = globalController.getRestaurantIdByDeliveryId(invalidDeliveryId, "courier");
        assertEquals(
                response.getStatusCode(),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Ensures that every role can get a delivery's restaurant.
     */
    @Test
    void testGetRestaurantIdByDeliveryIdAllRoles() {
        List<String> rolesToTest = List.of("courier", "vendor", "admin", "customer");

        for (String roleToTest : rolesToTest) {
            ResponseEntity<UUID> response = globalController.getRestaurantIdByDeliveryId(deliveryId, roleToTest);

            assertEquals(
                    HttpStatus.OK,
                    response.getStatusCode()
            );
            assertEquals(
                    delivery.getRestaurantID(),
                    response.getBody()
            );
        }
    }

    /**
     * Ensures that a valid role is required to access a delivery's restaurant.
     */
    @Test
    void testGetRestaurantIdByDeliveryIdUnauthorized() {
        ResponseEntity<UUID> response = globalController.getRestaurantIdByDeliveryId(deliveryId, "co");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        response = globalController.getRestaurantIdByDeliveryId(deliveryId, "unauthorizedRole");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    /**
     * Ensures that a role is required at all to access a delivery's restaurant.
     */
    @Test
    void testGetRestaurantIdByDeliveryIdNoAuthorization() {
        ResponseEntity<UUID> response = globalController.getRestaurantIdByDeliveryId(deliveryId, "");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    /**
     * The normal situation, in which a delivery and its order ID both exist.
     */
    @Test
    void testGetOrderByDeliveryIdGoodWeather() {
        ResponseEntity<UUID> response = globalController
                .getOrderByDeliveryId(deliveryId, "courier");

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
        UUID invalidDeliveryId = generateNewDeliveryId();

        ResponseEntity<UUID> response = globalController.getOrderByDeliveryId(invalidDeliveryId, "courier");
        assertEquals(
                response.getStatusCode(),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Ensures that every role can get a delivery's order.
     */
    @Test
    void testGetOrderByDeliveryIdAllRoles() {

        List<String> rolesToTest = List.of("courier", "vendor", "admin", "customer");

        for (String roleToTest : rolesToTest) {
            ResponseEntity<UUID> response = globalController.getOrderByDeliveryId(deliveryId, roleToTest);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(orderId, response.getBody());
        }
    }

    /**
     * Ensures that a valid role is required to access a delivery's order.
     */
    @Test
    void testGetOrderByDeliveryIdUnauthorized() {
        ResponseEntity<UUID> response = globalController.getOrderByDeliveryId(deliveryId, "co");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        response = globalController.getOrderByDeliveryId(deliveryId, "unauthorizedRole");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    /**
     * Ensures that a role is required at all to access a delivery's order.
     */
    @Test
    void testGetOrderByDeliveryIdNoAuthorization() {
        ResponseEntity<UUID> response = globalController.getOrderByDeliveryId(deliveryId, "");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    /**
     * The normal situation, in which a delivery and its rating both exist.
     */
    @Test
    void testGetRatingByDeliveryIdGoodWeather() {
        ResponseEntity<Double> response = globalController.getRatingByDeliveryId(deliveryId, "courier");

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
        UUID invalidDeliveryId = generateNewDeliveryId();

        ResponseEntity<Double> response = globalController.getRatingByDeliveryId(invalidDeliveryId, "courier");
        assertEquals(
                response.getStatusCode(),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Ensures that every role can get a delivery's rating.
     */
    @Test
    void testGetRatingByDeliveryIdAllRoles() {
        List<String> rolesToTest = List.of("courier", "vendor", "admin", "customer");

        for (String roleToTest : rolesToTest) {
            ResponseEntity<Double> response = globalController.getRatingByDeliveryId(deliveryId, roleToTest);
            assertEquals(
                    HttpStatus.OK,
                    response.getStatusCode()
            );
            assertEquals(
                    delivery.getCustomerRating(),
                    response.getBody()
            );
        }
    }

    /**
     * Ensures that a valid role is required to access a delivery's rating.
     */
    @Test
    void testGetRatingByDeliveryIdUnauthorized() {
        ResponseEntity<Double> response = globalController.getRatingByDeliveryId(deliveryId, "co");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        response = globalController.getRatingByDeliveryId(deliveryId, "unauthorizedRole");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );
    }

    /**
     * Ensures that a role is required at all to access a delivery's rating.
     */
    @Test
    void testGetRatingByDeliveryIdNoAuthorization() {
        ResponseEntity<Double> response = globalController.getRatingByDeliveryId(deliveryId, "");
        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        UUID id = UUID.randomUUID();
        Delivery save = new  Delivery(id, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "pending", delivery.getPickupTimeEstimate(), delivery.getDeliveryTimeEstimate(), 1.d,
                delivery.getPickedUpTime(), "69.655,69.425", null, 1);
        deliveryRepository.save(save);

        ResponseEntity<String> res = globalController.getDeliveryException(id, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), "");
    }

    /**
     * Ensure that each authorized role can access a delivery's estimated pick-up time.
     */
    @Test
    void testGetPickupTimeEstimateValidRoles() {
        final List<String> rolesToTest = List.of("customer", "courier", "vendor", "admin");

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<OffsetDateTime> response = globalController.getPickUpTime(deliveryId, roleToTest);

            assertEquals(
                    HttpStatus.OK,
                    response.getStatusCode()
            );
            assertEquals(
                    delivery.getPickupTimeEstimate(),
                    response.getBody()
            );
        }
    }

    /**
     * Ensure that an invalid delivery returns 404, when getting its pick-up time estimate.
     */
    @Test
    void testGetPickupTimeEstimateInvalidDelivery() {
        final List<String> rolesToTest = List.of("customer", "courier", "vendor", "admin");
        for (final String roleToTest : rolesToTest) {
            // Generate a delivery ID that is sure to be invalid
            Optional<UUID> invalidDeliveryId = Optional.of(UUID.randomUUID()); // TODO: merge w/ UUID generator
            assertTrue(invalidDeliveryId.isPresent());

            // Try to fetch that delivery
            ResponseEntity<OffsetDateTime> response = globalController.getPickUpTime(
                    invalidDeliveryId.get(), roleToTest);
            assertEquals(
                    HttpStatus.NOT_FOUND,
                    response.getStatusCode()
            );
        }
    }

    /**
     * Ensure that a valid role is required to get a delivery's pick-up time estimate.
     */
    @Test
    void testGetPickupTimeEstimateUnauthorized() {
        final List<String> rolesToTest = List.of("", "a", "aaa", "custom", "superuser", "sudo", "hi");

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<OffsetDateTime> response = globalController.getPickUpTime(deliveryId, roleToTest);

            assertEquals(
                    HttpStatus.UNAUTHORIZED,
                    response.getStatusCode()
            );
        }
    }
}
