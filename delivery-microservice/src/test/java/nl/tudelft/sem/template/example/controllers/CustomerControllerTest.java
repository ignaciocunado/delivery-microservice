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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerControllerTest {
    private transient CustomerController customerController;
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
        deliveryRepository.save(delivery);

        deliveryRepository.save(delivery);

        customerController = new CustomerController(deliveryRepository);
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

        ResponseEntity<List<UUID>> res = customerController.getAllDeliveriesCustomer(customerId, "customer");

        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), List.of(del1.getDeliveryID(), del2.getDeliveryID()));
    }

    @Test
    void getAllDeliveriesCustomerUnauthorised() {
        ResponseEntity<List<UUID>> res = customerController.getAllDeliveriesCustomer(customerId, "vendor");
        assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertNull(res.getBody());
    }

    @Test
    void getAllDeliveriesEmpty() {
        ResponseEntity<List<UUID>> res = customerController.getAllDeliveriesCustomer(customerId, "customer");
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), Collections.emptyList());
    }

    @Test
    void checkRoleOk() {
        assertTrue(customerController.checkCustomer("customer"));
    }

    @Test
    void checkRoleFalse() {
        assertFalse(customerController.checkCustomer("vendor"));
    }

}
