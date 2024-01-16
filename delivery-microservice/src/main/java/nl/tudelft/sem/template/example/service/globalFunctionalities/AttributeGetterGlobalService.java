package nl.tudelft.sem.template.example.service.globalFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttributeGetterGlobalService {

    private final transient RestaurantRepository restaurantRepository;
    private final transient DeliveryRepository deliveryRepository;

    /**
     * Constructor for the global controller.
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public AttributeGetterGlobalService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Implementation for get live location endpoint.
     *
     * @param deliveryID id of the delivery to query
     * @return string representing coordinates
     */
    public ResponseEntity<String> getLiveLocation(UUID deliveryID) {
        final Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if (!fetched.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fetched.get().getLiveLocation(), HttpStatus.OK);
    }

    /**
     * Implementation for get delivery exception endpoint.
     * @param deliveryID id of the delivery to query
     * @return string representing the exception if there is one
     */
    public ResponseEntity<String> getDeliveryException(UUID deliveryID) {
        final Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if (!fetched.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fetched.get().getUserException() == null ? "" : fetched.get().getUserException(),
                HttpStatus.OK);
    }

    /**
     * Implementation for the get pick up time endpoint.
     * Note that this returns the pickup time ESTIMATE, as specified in the OpenAPI spec.
     *
     * @param deliveryId ID of the delivery to query.
     * @return The estimated pickup time.
     */
    public ResponseEntity<OffsetDateTime> getPickUpTime(UUID deliveryId) {
        // Attempt to fetch the delivery from the DB
        final Optional<Delivery> deliveryFromDB = deliveryRepository.findById(deliveryId);
        if (deliveryFromDB.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch the pickup time estimate
        final Delivery delivery = deliveryFromDB.get();
        final OffsetDateTime pickUpTimeEstimate = delivery.getPickupTimeEstimate();

        return new ResponseEntity<>(pickUpTimeEstimate, HttpStatus.OK);
    }

}
