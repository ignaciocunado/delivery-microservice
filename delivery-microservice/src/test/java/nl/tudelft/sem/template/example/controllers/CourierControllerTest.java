package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

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

    private transient UUID deliveryId;
    private transient UUID restaurantId;
    private transient UUID courierId;
    private transient String role = "courier";

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
        courierId = UUID.randomUUID();
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        Delivery d = new  Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), courierId,
                restaurantId, "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "", "", 1);
        deliveryRepository.save(d);

        courierController = new CourierController(deliveryRepository, restaurantRepository, externalService);
    }

    @Test
    public void getPickUpLocationReturnsOk() {
        String expectedLocation = "123.321.667";

        when(externalService.getRestaurantLocation(any())).thenReturn(expectedLocation);

        ResponseEntity<String> response = courierController.getPickUpLocation(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("location: " + expectedLocation, response.getBody());
    }

    @Test
    public void getPickUpLocationReturnsNotFound() {
        UUID randomId = UUID.randomUUID();

        ResponseEntity<String> response = courierController.getPickUpLocation(randomId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void getLocationOfDeliveryReturnsOk() {
        String expectedLocation = "123.321.656";

        when(externalService.getOrderDestination(any(), any())).thenReturn(expectedLocation);

        ResponseEntity<String> response = courierController.getLocationOfDelivery(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("location: " + expectedLocation, response.getBody());
    }

    @Test
    public void getLocationOfDeliveryReturnsNotFound() {
        UUID randomId = UUID.randomUUID();
        
        ResponseEntity<String> response = courierController.getLocationOfDelivery(randomId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void checkAndHandleReturnsOk() {
        Supplier<ResponseEntity<String>> operation = () -> new ResponseEntity<>("Success", HttpStatus.OK);

        ResponseEntity<String> result = courierController.checkAndHandle(role, operation);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success", result.getBody());
    }

    @Test
    public void checkAndHandleReturnsOk2() {
        Supplier<ResponseEntity<String>> operation = () -> new ResponseEntity<>("Success", HttpStatus.OK);

        ResponseEntity<String> result = courierController.checkAndHandle("admin", operation);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success", result.getBody());
    }

    @Test
    public void checkAndHandleReturnsUnauthorized() {
        String role = "non-courier";
        Supplier<ResponseEntity<String>> operation = () -> new ResponseEntity<>("Success", HttpStatus.OK);

        ResponseEntity<String> result = courierController.checkAndHandle(role, operation);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    public void deliveredDeliveryNotFound() {
        UUID deliveryId = UUID.randomUUID();

        ResponseEntity<String> response = courierController.deliveredDelivery(deliveryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void deliveredDeliveryOk() {
        ResponseEntity<String> response = courierController.deliveredDelivery(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery marked as delivered!", response.getBody());
        assertEquals("delivered", deliveryRepository.findById(deliveryId).get().getStatus());
    }

    @Test
    public void setLiveLocationReturnsOk() {
        String location = "123.331.666";

        ResponseEntity<String> response = courierController.setLiveLocation(deliveryId, location);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200 OK", response.getBody());
        assertEquals(location, deliveryRepository.findById(deliveryId).get().getLiveLocation());
    }

    @Test
    public void setLiveLocationReturnsNotFound() {
        String location = "123.322.666";
        UUID randomId = UUID.randomUUID();

        ResponseEntity<String> response = courierController.setLiveLocation(randomId, location);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("error 404: Delivery not found!", response.getBody());
    }

    @Test
    public void setLiveLocationReturnsBadRequest() {
        ResponseEntity<String> response = courierController.setLiveLocation(deliveryId, "");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());

        response = courierController.setLiveLocation(deliveryId, null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());

        response = courierController.setLiveLocation(deliveryId, "    ");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error 400", response.getBody());
    }

    @Test
    public void getAvrRatingReturnsAverageRating() {
        ResponseEntity<Double> response = courierController.getAvrRating(courierId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1.0, response.getBody());
    }

    @Test
    public void getAvrRatingReturns0AverageRating() {
        deliveryRepository.deleteAll();
        ResponseEntity<Double> response = courierController.getAvrRating(courierId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody());
    }

    @Test
    public void getAvrRatingReturnsZeroWhenNoRatings() {
        UUID courierId = UUID.randomUUID();

        ResponseEntity<Double> response = courierController.getAvrRating(courierId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody());
    }

    @Test
    public void testGetAllDeliveriesCourierOk() {
        UUID courierId = UUID.randomUUID();
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );
        UUID del1ID = UUID.randomUUID();
        Delivery d1 = new  Delivery(del1ID, UUID.randomUUID(), UUID.randomUUID(), courierId,
                restaurantId, "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "", "", 1);
        UUID del2ID = UUID.randomUUID();
        Delivery d2 = new  Delivery(del2ID, UUID.randomUUID(), UUID.randomUUID(), courierId,
                restaurantId, "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "", "", 1);
        deliveryRepository.save(d1);
        deliveryRepository.save(d2);

        ResponseEntity<List<UUID>> response = courierController.getAllDeliveriesCourier(courierId);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        List<UUID> compare = List.of(del1ID, del2ID);
        List<UUID> responseList = response.getBody();
        assertEquals(compare, responseList);
    }

    @Test
    public void testGetAllDeliveriesCourierEmpty() {
        ResponseEntity<List<UUID>> response = courierController.getAllDeliveriesCourier(UUID.randomUUID());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(0, response.getBody().size());
    }
}