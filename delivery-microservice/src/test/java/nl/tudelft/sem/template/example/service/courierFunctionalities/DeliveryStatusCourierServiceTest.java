package nl.tudelft.sem.template.example.service.courierFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.generation.UUIDGenerationService;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeliveryStatusCourierServiceTest {

    private transient DeliveryRepository deliveryRepository;

    private transient RestaurantRepository restaurantRepository;

    private transient UUIDGenerationService uuidGenerationService;

    private transient DeliveryStatusCourierService sut;

    private transient UUID deliveryId;

    @BeforeEach
    void setup() {
        // Setup repositories
        deliveryRepository = new TestDeliveryRepository();
        restaurantRepository = new TestRestaurantRepository();

        // Setup services
        uuidGenerationService = new UUIDGenerationService();

        // Create a sample restaurant
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID(),          // restaurantID
                UUID.randomUUID(),          // vendorID
                null,     // courierIDs
                500.0                        // maxDeliveryZone
        );
        restaurantRepository.save(restaurant);
        final UUID restaurantId = restaurantRepository.findAll().get(0).getRestaurantID();

        // Create a sample delivery
        deliveryId = UUID.randomUUID();
        Delivery delivery = new Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                restaurantId, "pending", null, null, 0.4d,
                null, "", "", 1);
        deliveryRepository.save(delivery);

        sut = new DeliveryStatusCourierService(deliveryRepository);
    }

    @Test
    public void deliveredDeliveryNotFound() {
        Optional<UUID> randomId = uuidGenerationService.generateUniqueId(deliveryRepository);
        assertTrue(randomId.isPresent());

        ResponseEntity<String> response = sut.deliveredDelivery(randomId.get());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Delivery not found!", response.getBody());
    }

    @Test
    public void deliveredDeliveryOk() {
        ResponseEntity<String> response = sut.deliveredDelivery(deliveryId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delivery marked as delivered!", response.getBody());
        assertEquals("delivered", deliveryRepository.findById(deliveryId).get().getStatus());
    }
}
