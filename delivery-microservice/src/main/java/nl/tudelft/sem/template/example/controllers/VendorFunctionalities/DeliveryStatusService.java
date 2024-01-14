package nl.tudelft.sem.template.example.controllers.VendorFunctionalities;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

public class DeliveryStatusService {

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    @Autowired
    public DeliveryStatusService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
                            UUIDGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    public boolean checkVendor(String role) {
        return role.equals("vendor");
    }

    public boolean checkCourier(String role) {
        return role.equals("courier");
    }

    /** Sets the status to accepted for a delivery.
     * @param deliveryId ID of the delivery to mark as accepted. (required)
     * @param role      The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> acceptDelivery(UUID deliveryId, String role) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (!deliveryRepository.findById(deliveryId).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return executeAcceptDelivery(deliveryId);
    }

    /**
     * Private methods which executes the logic of acceptDelivery.
     * Used to lower LOC for the method.
     * @param deliveryId the id to query
     * @return the corresponding response entity
     */
    private ResponseEntity<Void> executeAcceptDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).get();
        delivery.setStatus("accepted");
        deliveryRepository.save(delivery);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Sets the status to rejected for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @param role      The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> rejectDelivery(UUID deliveryId, String role) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (deliveryRepository.findById(deliveryId).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery delivery = deliveryRepository.findById(deliveryId).get();
        delivery.setStatus("rejected");
        deliveryRepository.save(delivery);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Gets the list of deliveries for a restaurant.
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @param role     The role of the user (required)
     * @param status  The status of the delivery (required) must be 'preparing' or 'given to courier'
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> editStatusDelivery(UUID deliveryId, String role, String status) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Optional<Delivery> d = deliveryRepository.findById(deliveryId);
        if (d.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return executeEditStatusDelivery(d, status);
    }

    private ResponseEntity<Void> executeEditStatusDelivery(Optional<Delivery> d, String status) {
        if (!status.equals("preparing") && !status.equals("given to courier")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Delivery delivery = d.get();
        delivery.setStatus(status);
        deliveryRepository.save(delivery);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
