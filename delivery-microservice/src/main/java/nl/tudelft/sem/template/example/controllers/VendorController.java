package nl.tudelft.sem.template.example.controllers;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class VendorController {

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

    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId, String role) {

        Restaurant r;

        if(!checkVendor(role))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if(restaurantRepository.findById(restaurantId.toString()).isPresent()) {
            r = restaurantRepository.findById(restaurantId.toString()).get();}
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<RestaurantCourierIDsInner> courierList = r.getCourierIDs();
        RestaurantCourierIDsInner curr = new RestaurantCourierIDsInner();
        curr.setCourierID(courierId);

        courierList.add(curr);
        r.setCourierIDs(courierList);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public RestaurantRepository getRestaurantRepository() {
        return restaurantRepository;
    }


    /** Sets the status to accepted for a delivery.
     * @param deliveryId ID of the delivery to mark as accepted. (required)
     * @param role      The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> acceptDelivery(UUID deliveryId, String role) {
        if (checkVendor(role)) {
            if (deliveryRepository.findById(deliveryId.toString()).isPresent()) {
                Delivery delivery = deliveryRepository.findById(deliveryId.toString()).get();
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
    public ResponseEntity<OffsetDateTime> getPickUpEstimate(UUID deliveryID , String role ) {
        Optional<Delivery> estimate = deliveryRepository.findById(deliveryID.toString());
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
            if (deliveryRepository.findById(deliveryID.toString()).isPresent()) {
                Delivery delivery = deliveryRepository.findById(deliveryID.toString()).get();
                OffsetDateTime time;
                try {
                    time = OffsetDateTime.parse(body);
                } catch (DateTimeParseException e){
                    return new ResponseEntity<>("Invalid body. "+e.getMessage(), HttpStatus.BAD_REQUEST);
                }
                delivery.setPickupTimeEstimate(time);
                deliveryRepository.save(delivery);

                return new ResponseEntity<>(time.toString(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
