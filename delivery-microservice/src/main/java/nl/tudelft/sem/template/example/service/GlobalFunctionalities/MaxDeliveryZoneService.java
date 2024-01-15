package nl.tudelft.sem.template.example.service.GlobalFunctionalities;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MaxDeliveryZoneService {

    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;

    /**
     * Constructor for the global controller.
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public MaxDeliveryZoneService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Queries the Max Delivery Zone for a given restaurant and provides adequate error codes.
     * @param restaurantId id of the restaurant to be queried
     * @return the delivery zone, should it exist
     */
    public ResponseEntity<Double> getMaxDeliveryZone(UUID restaurantId) {
        Optional<Restaurant> r = restaurantRepository.findById(restaurantId);

        if(r.isEmpty()) {
            return new ResponseEntity<Double>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<Double>(r.get().getMaxDeliveryZone(), HttpStatus.OK);
    }

    /**
     * Sets the maxDeliveryZone of a given restaurant.
     * @param restaurantId the id of the restaurant
     * @param body the value to be set
     * @return Corresponding responseEntity
     */
    public ResponseEntity<Void> setMaxDeliveryZone(UUID restaurantId, Double body) {
        Optional<Restaurant> res = restaurantRepository.findById(restaurantId);
        if(res.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Restaurant r = res.get();
        r.setMaxDeliveryZone(body);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }




}
