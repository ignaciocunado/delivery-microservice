package nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DeliveryEventService {
    private final transient DeliveryRepository deliveryRepository;

    /**
     * Constructor.
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public DeliveryEventService(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Implementation of set delivery delay endpoint.
     * @param deliveryID id of the delivery to query
     * @param body new delay to update
     * @return the new delay
     */
    public ResponseEntity<Integer> setDeliveryDelay(UUID deliveryID, Integer body) {
        if (body == null || body < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if(fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Delivery del = fetched.get();
        del.setDelay(body);
        deliveryRepository.save(del);
        return new ResponseEntity<>(del.getDelay(), HttpStatus.OK);
    }

    /**
     * Implementation of get delivery delay endpoint.
     * @param deliveryID id of the delivery
     * @return delay of the delivery
     */
    public ResponseEntity<Integer> getDeliveryDelay(UUID deliveryID) {
        Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if(fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(fetched.get().getDelay(), HttpStatus.OK);
    }

    /**
     * Sets the delivery exception.
     * @param deliveryId ID of the delivery to query. (required)
     * @param body (required)
     * @return 200 + message, 400, 403, or 404
     */
    public ResponseEntity<String> setDeliveryException(UUID deliveryId, String body) {
        if (body == null || body.isBlank()) {
            return new ResponseEntity<>("error 400", HttpStatus.BAD_REQUEST);
        }

        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if(fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>("error 404: Delivery not found!", HttpStatus.NOT_FOUND);
        }

        Delivery delivery = fetchedDelivery.get();
        delivery.setUserException(body);
        deliveryRepository.save(delivery);
        return new ResponseEntity<>("200 OK", HttpStatus.OK);
    }





}
