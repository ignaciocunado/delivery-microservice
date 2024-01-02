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

    public ResponseEntity<OffsetDateTime> getPickUpEstimate(UUID deliveryID , String role ) {
        if (deliveryRepository.existsById(deliveryID.toString())) {
            Optional<Delivery> estimate = deliveryRepository.findById(deliveryID.toString());
            if (!estimate.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            OffsetDateTime r = estimate.get().getPickedUpTime();
            if (r == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(r, HttpStatus.OK);
//            return estimate.map(delivery -> new ResponseEntity<>(delivery.getPickupTimeEstimate(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            System.out.println("\033[31;40m getPickUpEstimate couldn't find UUID: \033[30;41m " + deliveryID + " \033[31;40m in database \033[0m");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
