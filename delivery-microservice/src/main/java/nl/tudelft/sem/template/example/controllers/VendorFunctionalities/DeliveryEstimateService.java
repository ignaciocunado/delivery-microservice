package nl.tudelft.sem.template.example.controllers.VendorFunctionalities;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class DeliveryEstimateService {

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    @Autowired
    public DeliveryEstimateService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
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

    /**
     * Gets the estimated time of delivery for a delivery.
     * @param deliveryID UUID of the delivery object
     * @param role User role
     * @return OffsetDateTime of the estimated time of delivery
     */
    public ResponseEntity<OffsetDateTime> getDeliveryEstimate(UUID deliveryID, String role) {
        if (!checkVendor(role) && !checkCourier(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
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


    /** Sets the estimated time of delivery for a delivery.
     *
     * @param deliveryID ID of the delivery to mark as rejected. (required)
     * @param role      The role of the user (required)
     * @param body      The estimated time of delivery (required)
     * @return Whether the request was successful or not, the set time if successful
     */
    public ResponseEntity<String> setDeliveryEstimate(UUID deliveryID, String role, OffsetDateTime body) {
        if (checkVendor(role) || checkCourier(role)) {
            if (deliveryRepository.findById(deliveryID).isPresent()) {
                if (body == null) {
                    return new ResponseEntity<>("Invalid body.", HttpStatus.BAD_REQUEST);
                }
                Delivery delivery = deliveryRepository.findById(deliveryID).get();
                delivery.setDeliveryTimeEstimate(body);
                deliveryRepository.save(delivery);
                return new ResponseEntity<>(body.toString(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }



}
