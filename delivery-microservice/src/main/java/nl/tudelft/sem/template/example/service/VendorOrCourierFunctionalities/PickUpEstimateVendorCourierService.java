package nl.tudelft.sem.template.example.service.VendorOrCourierFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

@Service
public class PickUpEstimateVendorCourierService {

    DeliveryRepository deliveryRepository;

    /**
     * Constructor.
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public PickUpEstimateVendorCourierService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }


    /**
     * Sets the estimated time of pick-up for a delivery.
     * @param deliveryID UUID of the delivery object
     * @param body String in OffsetDateTime format for the estimated time of pick-up
     * @return the set datetime if successful, otherwise error
     */
    public ResponseEntity<String> setPickUpEstimate(UUID deliveryID, String body) {
        Optional<Delivery> del = deliveryRepository.findById(deliveryID);

        if (del.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        body = body.replace("\"", "");
        Delivery delivery = del.get();
        OffsetDateTime time;
        try {
            time = OffsetDateTime.parse(body);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>("Invalid body. " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        delivery.setPickupTimeEstimate(time);
        deliveryRepository.save(delivery);

        return new ResponseEntity<>(delivery.getPickupTimeEstimate().toString(), HttpStatus.OK);

    }
}
