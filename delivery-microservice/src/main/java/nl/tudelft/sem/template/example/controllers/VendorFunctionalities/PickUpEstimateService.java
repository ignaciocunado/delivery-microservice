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
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

public class PickUpEstimateService {

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    @Autowired
    public PickUpEstimateService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
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
     * Gets the pick-up time for a delivery.
     * @param deliveryID UUID of the delivery object
     * @param role User role
     * @return OffsetDateTime of the picked-up time.
     */
    public ResponseEntity<OffsetDateTime> getPickedUpEstimate(UUID deliveryID, String role) {
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
}
