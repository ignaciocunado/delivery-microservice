package nl.tudelft.sem.template.example.controllers;

import java.util.UUID;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Sub controller of DeliveryController. Handles requests from couriers.
 * Note: Remember to define methods here and add them in DeliveryController.
 */
@Component
public class CourierController  {

    DeliveryRepository deliveryRepository;

    @Autowired
    public CourierController(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    private boolean checkCourier(String role) {
        return role.equals("courier");
    }

    /** returns the pickup location.
     *
     * @param deliveryId id of the delivery
     * @param role role of the user
     * @return the pickup location
     */
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId, String role) {
        if (checkCourier(role)) {
            return new ResponseEntity<>("PickUp location is 123.321.666", HttpStatus.OK);
        }
        return new ResponseEntity<>("Authorization failed!", HttpStatus.UNAUTHORIZED);
    }


    /** Integrates controller with API for delivered delivery endpoint.
     *
     * @param deliveryId ID of the delivery to mark as delivered. (required)
     * @param role      The role of the user (required)
     * @return courier controller's response entity
     */
    public ResponseEntity<String> deliveredDelivery(UUID deliveryId, String role) {
        if (checkCourier(role)) {
            if (deliveryRepository.findById(deliveryId).isPresent()) {
                Delivery d = deliveryRepository.findById(deliveryId).get();
                d.setStatus("delivered");
                deliveryRepository.save(d);

                return new ResponseEntity<>("Delivery marked as delivered!", HttpStatus.OK);
            }
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Authorization failed!", HttpStatus.UNAUTHORIZED);
    }
}
