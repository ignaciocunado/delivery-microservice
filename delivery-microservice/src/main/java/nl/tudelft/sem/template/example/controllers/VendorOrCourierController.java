package nl.tudelft.sem.template.example.controllers;


import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.controllers.interfaces.Controller;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Sub-Controller of DeliveryController.
 */
@Component
public class VendorOrCourierController implements Controller {

    DeliveryRepository deliveryRepository;

    /**
     * Constructor.
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public VendorOrCourierController(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Sets the delivery exception.
     * @param deliveryId ID of the delivery to query. (required)
     * @param body (required)
     * @return 200 + message, 400, 403, or 404
     */
    public ResponseEntity<String> setDeliveryException(UUID deliveryId, String body) {
        if (body == null || body.isBlank()) {
            return new ResponseEntity<>("error 400", HttpStatus.BAD_REQUEST);
        }

        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if(fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>("error 404: Delivery not found!", HttpStatus.NOT_FOUND);
        }

        Delivery delivery = fetchedDelivery.get();
        delivery.setUserException(body);
        deliveryRepository.save(delivery);
        return new ResponseEntity<>("200 OK", HttpStatus.OK);
    }

    /**
     * Implementation of set delivery delay endpoint.
     * @param deliveryID id of the delivery to query
     * @param body new delay to update
     * @return the new delay
     */
    public ResponseEntity<Integer> setDeliveryDelay(UUID deliveryID, Integer body) {
        if (body == null || body < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if(fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery del = fetched.get();
        del.setDelay(body);
        deliveryRepository.save(del);
        return new ResponseEntity<>(del.getDelay(), HttpStatus.OK);
    }

    /**
     * Implementation of get delivery delay endpoint.
     * @param deliveryID id of the delivery
     * @return delay of the delivery
     */
    public ResponseEntity<Integer> getDeliveryDelay(UUID deliveryID) {
        Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if(fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(fetched.get().getDelay(), HttpStatus.OK);
    }

    /**
     * Implementation for assign order to courier, modifies delivery object's courier field in the database.
     * @param courierID ID of the courier
     * @param deliveryID ID of the delivery
     * @return ID of the delivery
     */
    public ResponseEntity<UUID> assignOrderToCourier(UUID courierID, UUID deliveryID) {
        Optional<Delivery> fetchedFromDB = deliveryRepository.findById(deliveryID);
        if (fetchedFromDB.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery delivery = fetchedFromDB.get();
        delivery.setCourierID(courierID);
        deliveryRepository.save(delivery);
        return new ResponseEntity<>(delivery.getDeliveryID(), HttpStatus.OK);
    }

    /** Sets the estimated time of delivery for a delivery.
     *
     * @param deliveryID ID of the delivery to mark as rejected. (required)
     * @param body      The estimated time of delivery (required)
     * @return Whether the request was successful or not, the set time if successful
     */
    public ResponseEntity<String> setDeliveryEstimate(UUID deliveryID, OffsetDateTime body) {
        if (deliveryRepository.findById(deliveryID).isPresent()) {
            if (body == null) {
                return new ResponseEntity<>("Invalid body.", HttpStatus.BAD_REQUEST);
            }
            Delivery delivery = deliveryRepository.findById(deliveryID).get();
            delivery.setDeliveryTimeEstimate(body);
            deliveryRepository.save(delivery);
            return new ResponseEntity<>(delivery.getDeliveryTimeEstimate().toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Gets the estimated time of delivery for a delivery.
     * @param deliveryID UUID of the delivery object
     * @return OffsetDateTime of the estimated time of delivery
     */
    public ResponseEntity<OffsetDateTime> getDeliveryEstimate(UUID deliveryID) {
        Optional<Delivery> estimate = deliveryRepository.findById(deliveryID);
        if (estimate.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OffsetDateTime r = estimate.get().getDeliveryTimeEstimate();
        if (r == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    /**
     * Sets the estimated time of pick-up for a delivery.
     * @param deliveryID UUID of the delivery object
     * @param body String in OffsetDateTime format for the estimated time of pick-up
     * @return the set datetime if successful, otherwise error
     */
    public ResponseEntity<String> setPickUpEstimate(UUID deliveryID, String body) {
        if (deliveryRepository.findById(deliveryID).isPresent()) {
            Delivery delivery = deliveryRepository.findById(deliveryID).get();
            OffsetDateTime time;
            try {
                time = OffsetDateTime.parse(body);
            } catch (DateTimeParseException e) {
                return new ResponseEntity<>("Invalid body. " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            delivery.setPickupTimeEstimate(time);
            deliveryRepository.save(delivery);

            return new ResponseEntity<>(delivery.getPickupTimeEstimate().toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Check the role and handle it further.
     * @param role the role of the user
     * @param operation the method that should be called
     * @param <T> the passed param
     * @return the response type obj
     */
    @Override
    public <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation) {
        final List<String> allowedRoles = List.of("admin", "vendor", "courier");
        if(allowedRoles.contains(role)) {
            return operation.get();
        }
        return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
    }
}
