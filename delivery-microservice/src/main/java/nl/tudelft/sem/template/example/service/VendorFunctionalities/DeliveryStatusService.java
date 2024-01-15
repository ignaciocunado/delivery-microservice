package nl.tudelft.sem.template.example.service.VendorFunctionalities;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.interfaces.DSAKey;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryStatusService {

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    /**
     * Constructor for DeliveryStatusService.
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     * @param uuidGenerationService service to generate unique IDs
     */
    @Autowired
    public DeliveryStatusService(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
                            UUIDGenerationService uuidGenerationService) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
        this.uuidGenerationService = uuidGenerationService;
    }

    /** Sets the status to accepted for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as accepted. (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> acceptDelivery(UUID deliveryId) {
        Optional<Delivery> fetched = deliveryRepository.findById(deliveryId);
        if (!fetched.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery delivery = fetched.get();
        if(!delivery.getStatus().equalsIgnoreCase("pending")) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        delivery.setStatus("accepted");
        deliveryRepository.save(delivery);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /** Sets the status to rejected for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> rejectDelivery(UUID deliveryId) {
        if (deliveryRepository.findById(deliveryId).isPresent()) {
            Delivery delivery = deliveryRepository.findById(deliveryId).get();
            delivery.setStatus("rejected");
            deliveryRepository.save(delivery);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /** Gets the list of deliveries for a restaurant.
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @param status  The status of the delivery (required) must be 'preparing' or 'given to courier'
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> editStatusDelivery(UUID deliveryId, String status) {
        Optional<Delivery> d = deliveryRepository.findById(deliveryId);
        if (d.isPresent()) {
            status = status.replace("\"", "");
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

}
