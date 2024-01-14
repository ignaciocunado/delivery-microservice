package nl.tudelft.sem.template.example.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import nl.tudelft.sem.template.example.controllers.interfaces.Controller;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Component
public class VendorController implements Controller{

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    @Autowired
    public VendorController(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository,
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
     * Implementation for the get customer ID endpoint.
     * @param deliveryID id of the delivery
     * @param role role of the caller
     * @return id of customer
     */
    public ResponseEntity<UUID> getCustomerByDeliveryId(UUID deliveryID, String role) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        final Optional<Delivery> fetched = deliveryRepository.findById(deliveryID);
        if (!fetched.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fetched.get().getCustomerID(), HttpStatus.OK);
    }

    /**
     * Get the courierId of the delivery.
     * @param deliveryId the id of delivery
     * @param role The role of the user (required)
     * @return the UUID in the response entity
     */
    public ResponseEntity<UUID> getCourierIdByDelivery(UUID deliveryId, String role) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Delivery> fetchedDelivery = deliveryRepository.findById(deliveryId);
        if (fetchedDelivery.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(fetchedDelivery.get().getCourierID(), HttpStatus.OK);
    }




    /**
     * Queries the database for a specific restaurant and throws respective errors.
     * @param restaurantId id of the queried restaurant
     * @param role the role of the user
     * @return the ResponseEntity containing the status of the request
     */
    public ResponseEntity<String> getRest(UUID restaurantId, String role) {
        if(!checkVendor(role)) {
            return new ResponseEntity<String>("NOT AUTHORIZED \n Requires vendor permissions!",
                    HttpStatus.UNAUTHORIZED);
        }
        Optional<Restaurant> r = restaurantRepository.findById(restaurantId);
        return r.map(restaurant -> new ResponseEntity<>(restaurant.toString(), HttpStatus.OK)).orElseGet(() ->
                new ResponseEntity<>("NOT FOUND \n No restaurant with the given id has been found",
                        HttpStatus.NOT_FOUND));
    }


    @Override
    public <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation) {
        if(!role.equals("vendor")) {
            return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
        }
        return operation.get();
    }
}
