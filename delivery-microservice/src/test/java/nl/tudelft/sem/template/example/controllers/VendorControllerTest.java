package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class VendorControllerTest {
    TestRestaurantRepository restaurantRepo;
    VendorController sut;

    UUID restaurantId;
    @BeforeEach
    public void setup() {
        restaurantRepo = new TestRestaurantRepository();
        restaurantId = UUID.randomUUID();
        Restaurant r = new Restaurant(restaurantId, UUID.randomUUID(), new ArrayList<>(), 1.0d);
        restaurantRepo.save(r);
        sut = new VendorController(restaurantRepo);
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
        ResponseEntity<Void> res = sut.addCourierToRest(UUID.randomUUID(), UUID.randomUUID(), "Vendor");
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testOkNoDuplicate() {
        UUID courierId = UUID.randomUUID();
        ResponseEntity<Void> res = sut.addCourierToRest(courierId, restaurantId, "Vendor");
        assertEquals(res.getStatusCode(), HttpStatus.OK);

        Restaurant newRes = sut.getRestaurantRepository().findById(restaurantId.toString()).get();
        assertFalse(newRes.getCourierIDs().stream().filter(x -> x.getCourierID().equals(courierId)).collect(Collectors.toList()).isEmpty());

    }

}