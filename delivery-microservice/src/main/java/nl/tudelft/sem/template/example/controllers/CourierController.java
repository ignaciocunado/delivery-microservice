package nl.tudelft.sem.template.example.controllers;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.MicroserviceClientService;
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
    RestaurantRepository restaurantRepository;
    MicroserviceClientService microserviceClientService;

    @Autowired
    public CourierController(DeliveryRepository deliveryRepository, RestaurantRepository restaurantRepository, MicroserviceClientService microserviceClientService) {
        this.deliveryRepository = deliveryRepository;
        this.restaurantRepository = restaurantRepository;
        this.microserviceClientService = microserviceClientService;
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
        if(!checkCourier(role)){
            return new ResponseEntity<>("Authorization failed!", HttpStatus.UNAUTHORIZED);
        }
        Optional<Delivery> delivery = deliveryRepository.findById(deliveryId);
        if(delivery.isEmpty()){
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }

        Restaurant rest = restaurantRepository.findById(delivery.get().getRestaurantID()).get();

        String location = microserviceClientService.getRestaurantLocation(rest.getVendorID());

        return new ResponseEntity<>("location: " + location, HttpStatus.OK);
    }

    /** Returns the destination of the order location.
     *
     * @param deliveryId id of the delivery
     * @param role the role of the user
     * @return the response Entity
     */
    public ResponseEntity<String> getLocationOfDelivery(UUID deliveryId, String role){
        if(!checkCourier(role)){
            return new ResponseEntity<>("Authorization failed!", HttpStatus.UNAUTHORIZED);
        }

        Optional<Delivery> delivery = deliveryRepository.findById(deliveryId);
        if(delivery.isEmpty()){
            return new ResponseEntity<>("Delivery not found!", HttpStatus.NOT_FOUND);
        }

        UUID customerId = delivery.get().getCustomerID();
        UUID orderId = delivery.get().getOrderID();

        String location = microserviceClientService.getOrderDestination(customerId, orderId);
        return new ResponseEntity<>("location: " + location, HttpStatus.OK);
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
