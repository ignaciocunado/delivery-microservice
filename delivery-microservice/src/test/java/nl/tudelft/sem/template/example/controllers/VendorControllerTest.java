package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
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

    @BeforeEach
    public void setup() {
        // create test repositories
        restaurantRepo = new TestRestaurantRepository();
        deliveryRepo = new TestDeliveryRepository();

        // generate random UUID
        restaurantId = UUID.randomUUID();
        deliveryId = UUID.randomUUID();

        // setup test repository with some sample objects
        Restaurant r = new Restaurant(restaurantId, UUID.randomUUID(), new ArrayList<>(), 1.0d);
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
    Tests for the addCourierToRest endpoint
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

        Restaurant newRes = sut.getRestaurantRepository().findById(restaurantId.toString()).get();
        assertFalse(newRes.getCourierIDs().stream().filter(x -> x.getCourierID().equals(courierId)).collect(Collectors.toList()).isEmpty());

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
        assertEquals(deliveryRepo.findById(deliveryId.toString()).get().getStatus(), "accepted");
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

}