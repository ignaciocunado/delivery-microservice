package nl.tudelft.sem.template.example.service.adminFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Manages the editing of delivery properties that are only accessible by admins.
 */
@Service
public class DeliveryManagerAdminService {

    private final transient RestaurantRepository restaurantRepository;
    private final transient DeliveryRepository deliveryRepository;

    /**
     * Constructor for DeliveryManagerAdminService.
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public DeliveryManagerAdminService(RestaurantRepository restaurantRepository,
                                       DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Implementation for the set restaurant ID of a delivery endpoint.
     * @param deliveryID id of the delivery
     * @param body new restaurant id
     * @return restaurant id
     */
    public ResponseEntity<UUID> setRestIdOfDelivery(UUID deliveryID, UUID body) {
        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryID);
        Optional<Restaurant> fetchedRestaurant = restaurantRepository.findById(body);
        if(fetchedRestaurant.isEmpty() || fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery del = fetchedDelivery.get();
        del.setRestaurantID(body);
        deliveryRepository.save(del);
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
