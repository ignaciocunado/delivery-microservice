package nl.tudelft.sem.template.example.service.GlobalFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryIdGetterGlobalService {

    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;

    /**
     * Constructor for the global controller.
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public DeliveryIdGetterGlobalService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
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





}
