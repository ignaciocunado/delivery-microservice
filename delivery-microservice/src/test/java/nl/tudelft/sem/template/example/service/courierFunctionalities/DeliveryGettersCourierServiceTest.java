package nl.tudelft.sem.template.example.service.courierFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeliveryGettersCourierServiceTest {

    private transient RestaurantRepository restaurantRepository;

    private transient DeliveryRepository deliveryRepository;

    private transient DeliveryGettersCourierService sut;

    private transient UUID restaurantId;

    private transient UUID deliveryId;

    private transient UUID courierId;

    @BeforeEach
    void setup() {
        // Setup repositories
        restaurantRepository = new TestRestaurantRepository();
        deliveryRepository = new TestDeliveryRepository();

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

        sut = new DeliveryGettersCourierService(deliveryRepository);
    }

    @Test
    public void getAvrRatingReturnsAverageRating() {
        ResponseEntity<Double> response = sut.getAvrRating(courierId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.7d, response.getBody());
    }

    @Test
    public void getAvrRatingReturns0AverageRating() {
        deliveryRepository.deleteAll();
        ResponseEntity<Double> response = sut.getAvrRating(courierId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0.0, response.getBody());
    }

    @Test
    public void testGetAvRatingNoCourierId() {
        ResponseEntity<Double> response = sut.getAvrRating(UUID.randomUUID());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(0, response.getBody());
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

        ResponseEntity<List<UUID>> response = sut.getAllDeliveriesCourier(courierId);
        assertEquals(response.getStatusCode(), HttpStatus.OK);

        List<UUID> compare = List.of(del1ID, del2ID);
        List<UUID> responseList = response.getBody();
        assertEquals(compare, responseList);
    }

    @Test
    public void testGetAllDeliveriesCourierEmpty() {
        ResponseEntity<List<UUID>> response = sut.getAllDeliveriesCourier(UUID.randomUUID());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(0, response.getBody().size());
    }
}
