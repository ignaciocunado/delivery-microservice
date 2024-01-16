package nl.tudelft.sem.template.example.service.courierFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.ExternalService;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DeliveryLocationCourierServiceTest {

    private transient DeliveryRepository deliveryRepository;

    private transient RestaurantRepository restaurantRepository;

    private transient ExternalService externalService;

    private transient UUIDGenerationService uuidGenerationService;

    private transient DeliveryLocationCourierService sut;

    private transient UUID restaurantId;

    private transient UUID deliveryId;

    private transient UUID courierId;

    @BeforeEach
    void setup() {
        // Setup repositories
        deliveryRepository = new TestDeliveryRepository();
        restaurantRepository = new TestRestaurantRepository();

        // Setup other services
        externalService = Mockito.mock(ExternalService.class);
        uuidGenerationService = new UUIDGenerationService();

        // Create a sample restaurant
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID(),          // restaurantID
                UUID.randomUUID(),          // vendorID
                null,     // courierIDs
                500.0                        // maxDeliveryZone
        );
        restaurantRepository.save(restaurant);
        restaurantId = restaurantRepository.findAll().get(0).getRestaurantID();

        // Create sample deliveries
        deliveryId = UUID.randomUUID();
        courierId = UUID.randomUUID();
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );

        Delivery d = new Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), courierId,
                restaurantId, "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "", "", 1);
        Delivery d2 = new Delivery(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), courierId,
                restaurantId, "pending", sampleOffsetDateTime, sampleOffsetDateTime, 0.4d,
                sampleOffsetDateTime, "", "", 1);
        deliveryRepository.save(d);
        deliveryRepository.save(d2);

        sut = new DeliveryLocationCourierService(deliveryRepository, restaurantRepository, externalService);
    }

    @Test
    public void getPickUpLocationReturnsOk() {
        String expectedLocation = "123.321.667";

        when(externalService.getRestaurantLocation(any())).thenReturn(expectedLocation);

        ResponseEntity<String> response = sut.getPickUpLocation(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("location: " + expectedLocation, response.getBody());
    }

    @Test
    public void getPickUpLocationReturnsNotFound() {
        Optional<UUID> randomId = uuidGenerationService.generateUniqueId(deliveryRepository);
        assertTrue(randomId.isPresent());

        ResponseEntity<String> response = sut.getPickUpLocation(randomId.get());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void getPickUpLocationReturnsNotFound2() {
        when(externalService.getRestaurantLocation(any())).thenReturn(null);

        ResponseEntity<String> response = sut.getPickUpLocation(deliveryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Location not found!", response.getBody());
    }

    @Test
    public void getLocationOfDeliveryReturnsOk() {
        String expectedLocation = "123.321.656";

        when(externalService.getOrderDestination(any(), any())).thenReturn(expectedLocation);

        ResponseEntity<String> response = sut.getLocationOfDelivery(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("location: " + expectedLocation, response.getBody());
    }

    @Test
    public void getLocationOfDeliveryReturnsNotFound() {
        Optional<UUID> randomId = uuidGenerationService.generateUniqueId(deliveryRepository);
        assertTrue(randomId.isPresent());

        ResponseEntity<String> response = sut.getLocationOfDelivery(randomId.get());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void getLocationOfDeliveryReturnsNotFound2() {
        when(externalService.getOrderDestination(any(), any())).thenReturn(null);

        ResponseEntity<String> response = sut.getLocationOfDelivery(deliveryId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Location not found!", response.getBody());
    }

    @Test
    public void setLiveLocationReturnsOk() {
        String location = "123.331.666";

        ResponseEntity<String> response = sut.setLiveLocation(deliveryId, location);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200 OK", response.getBody());
        assertEquals(location, deliveryRepository.findById(deliveryId).get().getLiveLocation());
    }

    @Test
    public void setLiveLocationReturnsNotFound() {
        String location = "123.322.666";
        Optional<UUID> randomId = uuidGenerationService.generateUniqueId(deliveryRepository);
        assertTrue(randomId.isPresent());

        ResponseEntity<String> response = sut.setLiveLocation(randomId.get(), location);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404 not found", response.getBody());
    }

    @Test
    public void setLiveLocationReturnsBadRequest() {
        ResponseEntity<String> response = sut.setLiveLocation(deliveryId, "");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404 not found", response.getBody());

        response = sut.setLiveLocation(deliveryId, null);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404 not found", response.getBody());

        response = sut.setLiveLocation(deliveryId, "    ");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404 not found", response.getBody());
    }


}
