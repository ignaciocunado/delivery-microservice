package nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryEstimateService {


    DeliveryRepository deliveryRepository;


    @Autowired
    public DeliveryEstimateService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
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



}
