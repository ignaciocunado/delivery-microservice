package nl.tudelft.sem.template.example.service.VendorFunctionalities;

import lombok.Getter;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.stream.Collectors;


@Service
public class CourierToRestaurantService {
    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    /**
     * Constructor for CourierToRestaurantService.
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     * @param uuidGenerationService service which generates unique IDs
     */
    @Autowired
    public CourierToRestaurantService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
                                      UUIDGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
        this.uuidGenerationService = uuidGenerationService;
    }


    /** Adds a courier to a restaurant.
     * @param courierId   ID of the courier to add to the restaurant. (required)
     * @param restaurantId ID of the restaurant to add the courier to. (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> addCourierToRest(UUID restaurantId, UUID courierId) {

        Optional<Restaurant> fetched = restaurantRepository.findById(restaurantId);
        if (!fetched.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return executeAddCourierToRest(courierId, fetched.get());
        }

    }

    /**
     * private method for executing logic of AddCourierToRest.
     * used to lower LOC
     * @param courierId id of courier to query
     * @return corresponding response entity
     */
    private ResponseEntity<Void> executeAddCourierToRest(UUID courierId, Restaurant r) {
        List<UUID> newCouriers = new ArrayList<>(r.getCourierIDs());
        if(!newCouriers.contains(courierId)) {
            newCouriers.add(courierId);
        }
        r.setCourierIDs(newCouriers);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Implementation for removing a courier from the database.
     * @param courierId ID of the courier to remove
     * @param restaurantId ID of the restaurant
     * @return void response entity with HTTP codes
     */
    public ResponseEntity<Void> removeCourierRest(UUID courierId, UUID restaurantId) {
        Optional<Restaurant> fetched = restaurantRepository.findById(restaurantId);
        if (fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Restaurant restaurant = fetched.get();
        List<UUID> couriers = restaurant.getCourierIDs();

        if(!couriers.contains(courierId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return executeRemoveCourierRest(restaurant, couriers, courierId);
    }

    private ResponseEntity<Void> executeRemoveCourierRest(Restaurant restaurant, List<UUID> couriers, UUID courierId) {


        couriers.remove(courierId);

        restaurantRepository.save(restaurant);

        return new ResponseEntity<>(HttpStatus.OK);

    }

}
