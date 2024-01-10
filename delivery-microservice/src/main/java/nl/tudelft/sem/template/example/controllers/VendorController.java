package nl.tudelft.sem.template.example.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Component
public class VendorController {

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;

    @Autowired
    public VendorController(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public boolean checkVendor(String role) {
        return role.equals("vendor");
    }

    public boolean checkCourier(String role) {
        return role.equals("courier");
    }

    /** Adds a courier to a restaurant.
     *
     * @param courierId   ID of the courier to add to the restaurant. (required)
     * @param restaurantId ID of the restaurant to add the courier to. (required)
     * @param role       The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId, String role) {

        Restaurant r;

        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (restaurantRepository.findById(restaurantId).isPresent()) {
            r = restaurantRepository.findById(restaurantId).get();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        RestaurantCourierIDsInner curr = new RestaurantCourierIDsInner();
        curr.setCourierID(courierId);

        r.addCourierIDsItem(curr);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /** Sets the status to accepted for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as accepted. (required)
     * @param role      The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> acceptDelivery(UUID deliveryId, String role) {
        if (checkVendor(role)) {
            if (deliveryRepository.findById(deliveryId).isPresent()) {
                Delivery delivery = deliveryRepository.findById(deliveryId).get();
                delivery.setStatus("accepted");
                deliveryRepository.save(delivery);

                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Gets the estimated time of pick-up for a delivery.
     * @param deliveryID UUID of the delivery object
     * @param role User role
     * @return OffsetDateTime of the estimated time of pick-up
     */
    public ResponseEntity<OffsetDateTime> getPickUpEstimate(UUID deliveryID, String role) {
        Optional<Delivery> estimate = deliveryRepository.findById(deliveryID);
        if (estimate.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        OffsetDateTime r = estimate.get().getPickedUpTime();
        if (r == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    /**
     * Sets the estimated time of pick-up for a delivery.
     * @param deliveryID UUID of the delivery object
     * @param role User role
     * @param body String in OffsetDateTime format for the estimated time of pick-up
     * @return the set datetime if successful, otherwise error
     */
    public ResponseEntity<String> setPickUpEstimate(UUID deliveryID, String role, String body) {
        if (checkVendor(role) || checkCourier(role)) {
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

                return new ResponseEntity<>(time.toString(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    /** Sets the status to rejected for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @param role      The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> rejectDelivery(UUID deliveryId, String role) {
        if (checkVendor(role)) {
            if (deliveryRepository.findById(deliveryId).isPresent()) {
                Delivery delivery = deliveryRepository.findById(deliveryId).get();
                delivery.setStatus("rejected");
                deliveryRepository.save(delivery);

                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Implementation for removing a courier from the database.
     * @param courierId ID of the courier to remove
     * @param restaurantId ID of the restaurant
     * @param role role of the user
     * @return void response entity with HTTP codes
     */
    public ResponseEntity<Void> removeCourierRest(UUID courierId, UUID restaurantId, String role) {
        if (!checkVendor(role)) {
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
        }
        Optional<Restaurant> rest = restaurantRepository.findById(restaurantId);

        if (rest.isEmpty()) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        List<RestaurantCourierIDsInner> couriers = rest.get().getCourierIDs();

        if (couriers.stream().filter(x -> x.getCourierID().equals(courierId)).collect(Collectors.toList()).isEmpty()) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }

        RestaurantCourierIDsInner toRemove = couriers.stream()
                .filter(x -> x.getCourierID().equals(courierId))
                .collect(Collectors.toList()).get(0);

        couriers.remove(toRemove);

        rest.get().setCourierIDs(couriers);

        restaurantRepository.save(rest.get());

        return new ResponseEntity<Void>(HttpStatus.OK);

    }

    /**
     * Implementation for the get customer ID endpoint.
     * @param deliveryID id of the delivery
     * @param role role of the caller
     * @return id of customer
     */
    public ResponseEntity<UUID> getCustomerByDeliveryId(UUID deliveryID, String role) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        final Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if (!fetched.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fetched.get().getCustomerID(), HttpStatus.OK);
    }

    /** Gets the list of deliveries for a restaurant.
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @param role     The role of the user (required)
     * @param status  The status of the delivery (required) must be 'preparing' or 'given to courier'
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> editStatusDelivery(UUID deliveryId, String role, String status) {
        if (checkVendor(role)) {
            Optional<Delivery> d = deliveryRepository.findById(deliveryId);
            if (d.isPresent()) {
                if (!status.equals("preparing") && !status.equals("given to courier")) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
                Delivery delivery = d.get();
                delivery.setStatus(status);
                deliveryRepository.save(delivery);

                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Get the courierId of the delivery.
     * @param deliveryId the id of delivery
     * @param role The role of the user (required)
     * @return the UUID in the response entity
     */
    public ResponseEntity<UUID> getCourierIdByDelivery(UUID deliveryId, String role) {
        if(!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if(fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(fetchedDelivery.get().getCourierID(), HttpStatus.OK);
    }
}
