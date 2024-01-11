package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.ExternalService;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CourierControllerTest {

    private transient CourierController courierController;
    private transient RestaurantRepository restaurantRepository;
    private transient TestDeliveryRepository deliveryRepository;
    private transient ExternalService externalService;

    UUID deliveryId;
    UUID restaurantId;

    @BeforeEach
    void setUp() {
        deliveryRepository = new TestDeliveryRepository();
        restaurantRepository = new TestRestaurantRepository();
        externalService = Mockito.mock(ExternalService.class);

        Restaurant restaurant = new Restaurant(
                UUID.randomUUID(),          // restaurantID
                UUID.randomUUID(),          // vendorID
                null,     // courierIDs
                500.0                        // maxDeliveryZone
        );
        restaurantRepository.save(restaurant);
        restaurantId = restaurantRepository.findAll().get(0).getRestaurantID();


        deliveryId = UUID.randomUUID();
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                restaurantId, "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "", "", 1);
        deliveryRepository.save(d);

        courierController = new CourierController(deliveryRepository, restaurantRepository, externalService);
    }

    @Test
    public void getPickUpLocationReturnsOk() {
        String role = "courier";
        String expectedLocation = "123.321.666";

        when(externalService.getRestaurantLocation(any())).thenReturn(expectedLocation);

        ResponseEntity<String> response = courierController.getPickUpLocation(deliveryId, role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("location: " + expectedLocation, response.getBody());
    }

    @Test
    public void getPickUpLocationReturnsNotFound() {
        // Arrange
        String role = "courier";
        UUID randomId = UUID.randomUUID(); // assuming this deliveryId does not exist in the repository


        // Act
        ResponseEntity<String> response = courierController.getPickUpLocation(randomId, role);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void getPickUpLocationReturnsUnauthorized() {
        UUID deliveryId = UUID.randomUUID();
        String role = "non-courier";

        ResponseEntity<String> response = courierController.getPickUpLocation(deliveryId, role);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authorization failed!", response.getBody());
    }

    @Test
    public void getLocationOfDeliveryReturnsOk() {
        String role = "courier";
        String expectedLocation = "123.321.666";

        when(externalService.getOrderDestination(any(), any())).thenReturn(expectedLocation);

        ResponseEntity<String> response = courierController.getLocationOfDelivery(deliveryId, role);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("location: " + expectedLocation, response.getBody());
    }

    @Test
    public void getLocationOfDeliveryReturnsNotFound() {
        String role = "courier";
        UUID randomId = UUID.randomUUID(); // assuming this deliveryId does not exist in the repository
        
        ResponseEntity<String> response = courierController.getLocationOfDelivery(randomId, role);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void getLocationOfDeliveryReturnsUnauthorized() {
        UUID deliveryId = UUID.randomUUID();
        String role = "non-courier";

        ResponseEntity<String> response = courierController.getLocationOfDelivery(deliveryId, role);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authorization failed!", response.getBody());
    }

    @Test
    public void deliveredDeliveryUnauthorized() {
        UUID deliveryId = UUID.randomUUID();
        String role = "non-courier";

        ResponseEntity<String> response = courierController.deliveredDelivery(deliveryId, role);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Authorization failed!", response.getBody());
    }

    @Test
    public void deliveredDeliveryNotFound() {
        UUID deliveryId = UUID.randomUUID();
        String role = "courier";

        ResponseEntity<String> response = courierController.deliveredDelivery(deliveryId, role);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void deliveredDeliveryOk() {
        ResponseEntity<String> response = courierController.deliveredDelivery(deliveryId, "courier");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery marked as delivered!", response.getBody());
        assertEquals("delivered", deliveryRepository.findById(deliveryId).get().getStatus());
    }
}