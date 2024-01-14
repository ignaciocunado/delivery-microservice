package nl.tudelft.sem.template.example.service.roles;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service that authorizes requests from any user.
 * WARNING: this class will be entirely removed in the next step of the refactor.
 */
@Service
public class GlobalService {

    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;

    /**
     * Constructor for the global controller.
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public GlobalService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
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
     * Implementation for the get delivery by ID endpoint. This fetches the full Delivery object from the database,
     * returning every piece of data relating to it.
     * @param deliveryId ID of the delivery to get.
     * @return The delivery object, if found.
     */
    public ResponseEntity<Delivery> getDeliveryById(UUID deliveryId) {
        // Attempt to fetch the delivery from the DB
        final Optional<Delivery> deliveryFromDB = deliveryRepository.findById(deliveryId);
        if (deliveryFromDB.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        final Delivery delivery = deliveryFromDB.get();
        return new ResponseEntity<>(delivery, HttpStatus.OK);
    }

    /**
     * Implementation for the get restaurant ID by delivery ID endpoint. This points to a 'Restaurant' entity in the DB.
     * @param deliveryId ID of the delivery to query.
     * @return The delivery's restaurant ID.
     */
    public ResponseEntity<UUID> getRestaurantIdByDeliveryId(UUID deliveryId) {
        // Attempt to fetch the delivery from the DB
        final Optional<Delivery> deliveryFromDB = deliveryRepository.findById(deliveryId);
        if (deliveryFromDB.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch & validate the restaurant ID
        final Delivery delivery = deliveryFromDB.get();
        final UUID restaurantId = delivery.getRestaurantID();

        return new ResponseEntity<>(restaurantId, HttpStatus.OK);
    }

    /**
     * Implementation for the get order by delivery ID endpoint. This fetches the 'order' object's ID attribute from
     * the database. Note that this order object DNE in this microservice - instead, the ID points to an object from
     * a different database.
     * @param deliveryId ID of the delivery to query.
     * @return The delivery's order ID.
     */
    public ResponseEntity<UUID> getOrderByDeliveryId(UUID deliveryId) {
        // Attempt to fetch the delivery from the DB
        final Optional<Delivery> deliveryFromDB = deliveryRepository.findById(deliveryId);
        if (deliveryFromDB.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch & validate the order ID
        final Delivery delivery = deliveryFromDB.get();
        final UUID orderId = delivery.getOrderID();

        return new ResponseEntity<>(orderId, HttpStatus.OK);
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

    /**
     * Fetches the 'rating' property of a delivery. This property reflects a customer-specified rating.
     * @param deliveryId Delivery to query.
     * @return The delivery's customer rating.
     */
    public ResponseEntity<Double> getRatingByDeliveryId(UUID deliveryId) {
        // Attempt to fetch the delivery from the DB
        final Optional<Delivery> deliveryFromDB = deliveryRepository.findById(deliveryId);
        if (deliveryFromDB.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Fetch the rating
        final Delivery delivery = deliveryFromDB.get();
        final Double rating = delivery.getCustomerRating();

        return new ResponseEntity<>(rating, HttpStatus.OK);
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