package nl.tudelft.sem.template.example.service.adminFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class DeliveryManagerAdminServiceTest {
    private transient TestRestaurantRepository restaurantRepository;

    private transient TestDeliveryRepository deliveryRepository;

    private transient DeliveryManagerAdminService sut;
    private transient UUID deliveryId;

    @BeforeEach
    void setup() {
        restaurantRepository = new TestRestaurantRepository();
        deliveryRepository = new TestDeliveryRepository();
        sut = new DeliveryManagerAdminService(restaurantRepository, deliveryRepository);

        deliveryId = UUID.randomUUID();
        OffsetDateTime sampleOffsetDateTime = OffsetDateTime.of(
                2023, 12, 31, 10, 30, 0, 0,
                ZoneOffset.ofHoursMinutes(5, 30)
        );

        Delivery d = new Delivery(deliveryId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "pending", sampleOffsetDateTime, sampleOffsetDateTime, 1.d,
                sampleOffsetDateTime, "", "", 1);
        deliveryRepository.save(d);
    }

    @Test
    void setIDOk() {
        UUID restID = UUID.randomUUID();
        Restaurant restaurant = new Restaurant(
                restID, UUID.randomUUID(), List.of(), 100.0
        );
        restaurantRepository.save(restaurant);
        ResponseEntity<UUID> res =  sut.setRestIdOfDelivery(deliveryId, restID);
        assertEquals(res.getStatusCode(), HttpStatus.OK);
        assertEquals(res.getBody(), restID);
        assertEquals(deliveryRepository.findById(deliveryId).get().getRestaurantID(), restID);
    }

    @Test
    void setIDDeliveryNotFound() {
        UUID restID = UUID.randomUUID();
        ResponseEntity<UUID> res =  sut.setRestIdOfDelivery(UUID.randomUUID(), restID);
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }

    @Test
    void setIDRestNotFound() {
        UUID restID = UUID.randomUUID();
        Restaurant restaurant = new Restaurant(
                restID, UUID.randomUUID(), List.of(), 100.0
        );
        restaurantRepository.save(restaurant);
        ResponseEntity<UUID> res =  sut.setRestIdOfDelivery(deliveryId, UUID.randomUUID());
        assertEquals(res.getStatusCode(), HttpStatus.NOT_FOUND);
        assertNull(res.getBody());
    }



}
