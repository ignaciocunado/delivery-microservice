package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.roles.CustomerService;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CustomerServiceTest {
    private transient CustomerService customerService;
    private transient TestDeliveryRepository deliveryRepository;

    private transient UUID deliveryId;
    private transient UUID customerId;

    /**
     * The full delivery object that was created during test setup.
     */
    private transient Delivery delivery;

    private transient OffsetDateTime sampleOffsetDateTime;

    @BeforeEach
    void setUp() {
        deliveryRepository = new TestDeliveryRepository();

        deliveryId = UUID.randomUUID();

        customerId = UUID.randomUUID();
        sampleOffsetDateTime = OffsetDateTime.of(
                2024, 1, 4, 18, 23, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );

        delivery = new Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "69.655,69.425", "late", 1);
        Delivery delivery2 = new Delivery(UUID.randomUUID(), null, null, null,
                null, null, null, null, 1.d,
                null, "69.655,69.425", "late", 1);
        deliveryRepository.save(delivery);

        deliveryRepository.save(delivery2);

        customerService = new CustomerService(deliveryRepository);
    }

    @Test
    void getAllDeliveriesCustomerOk() {
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2024, 1, 4, 18, 23, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );

        UUID del1ID = UUID.randomUUID();
        Delivery del1 = new Delivery(del1ID, UUID.randomUUID(), customerId, UUID.randomUUID(),
                UUID.randomUUID(), "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "69.655,69.425", "late", 1);

        UUID del2ID = UUID.randomUUID();
        Delivery del2 = new Delivery(del2ID, UUID.randomUUID(), customerId, UUID.randomUUID(),
                UUID.randomUUID(), "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "69.655,69.425", "late", 1);


        deliveryRepository.save(del1);
        deliveryRepository.save(del2);

        ResponseEntity<List<UUID>> res = customerService.getAllDeliveriesCustomer(customerId);

        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(del1.getDeliveryID(), del2.getDeliveryID()));
    }

    @Test
    void checkAndHandleWrongRoles() {
        final List<String> rolesToTest = List.of("vendor", "courier", "sudo", "admi", "v", "c", "a", "");
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : rolesToTest) {
            ResponseEntity<Restaurant> response = customerService.checkAndHandle(roleToTest, operation);
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Test
    void getAllDeliveriesEmpty() {
        ResponseEntity<List<UUID>> res = customerService.getAllDeliveriesCustomer(customerId);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), Collections.emptyList());
    }

    @Test
    void setRateOfDeliveryNotFound() {
        ResponseEntity<String> response = customerService.setRateOfDelivery(UUID.randomUUID(), 0.5d);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void setRateOfDeliveryBadRequest() {
        ResponseEntity<String> response = customerService.setRateOfDelivery(deliveryId, 1.5d);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void setRateOfDeliveryBadRequest2() {
        ResponseEntity<String> response = customerService.setRateOfDelivery(deliveryId, -15d);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void setRateOfDeliveryGoodRequest() {
        ResponseEntity<String> response = customerService.setRateOfDelivery(deliveryId, 0d);
        assertEquals(200, response.getStatusCodeValue());

        response = customerService.setRateOfDelivery(deliveryId, 1d);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("200 OK", response.getBody());

    }

    @Test
    void setRateOfDeliveryOk() {
        ResponseEntity<String> response = customerService.setRateOfDelivery(deliveryId, 0.5d);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0.5d, deliveryRepository.findById(deliveryId).get().getCustomerRating());
        assertEquals("200 OK", response.getBody());

    }

    @Test
    public void testCheckAndHandle_ReturnsNull() {
        String role = "customer";
        Supplier<ResponseEntity<String>> operation = () -> null;
        ResponseEntity<String> result = customerService.checkAndHandle(role, operation);
        assertNull(result);
    }

    @Test
    void checkCorrectRole() {
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);
        ResponseEntity<Restaurant> response = customerService.checkAndHandle("customer", operation);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
