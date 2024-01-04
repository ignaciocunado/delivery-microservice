package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.web.servlet.oauth2.resourceserver.OpaqueTokenDsl;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class GeneralController {

    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;

    @Autowired
    public GeneralController(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Cgecks whether the role provided is valid
     * @param role role
     * @return true iff the role is valid
     */
    public boolean checkGeneral(String role) {
        return "vendorcouriercustomeradmin".contains(role);
    }

    /**
     * Implementation for get live location endpoint
     * @param deliveryID id of the delivery to query
     * @param role role of the user
     * @return string representing coordinates
     */
    public ResponseEntity<String> getLiveLocation(UUID deliveryID, String role) {
        if(!checkGeneral(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        final Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if(!fetched.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fetched.get().getLiveLocation(), HttpStatus.OK);
    }
}
