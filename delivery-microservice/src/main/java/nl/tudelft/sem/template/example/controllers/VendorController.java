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

    /** Sets the status to rejected for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @param role      The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> rejectDelivery(UUID deliveryId, String role) {
        if (checkVendor(role)) {
            if (deliveryRepository.findById(deliveryId).isPresent()) {
                Delivery delivery = deliveryRepository.findById(deliveryId).get();
                delivery.setStatus("rejected");
                deliveryRepository.save(delivery);

                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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


    /**
     * Create a new Delivery object in the database. The Delivery is given a new, fully unique ID.
     * @param role Requesting user's role.
     * @param delivery Data of delivery to create. ID is ignored.
     * @return The newly created Delivery object.
     */
    public ResponseEntity<Delivery> createDelivery(String role, Delivery delivery) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (delivery == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return createDeliveryNoIdCheck(delivery);
    }

    /**
     * Checks whether a new Id is generated. Improves LOC and CC for createDelivery.
     * @param delivery the delivery for which an Id is generated
     * @return the resulting ResponseEntity
     */
    private ResponseEntity<Delivery> createDeliveryNoIdCheck(Delivery delivery) {
        final Optional<UUID> newId = uuidGenerationService.generateUniqueId(deliveryRepository);
        if (newId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return createDeliveryDeliveryNotValidCheck(delivery, newId);
    }

    /**
     * Checks if the delivery saved is correctly created. Improves LOC and CC for createDelivery.
     * @param delivery the delivery to be checked
     * @param newId the id of the delivery
     * @return the corresponding ResponseEntity
     */
    private ResponseEntity<Delivery> createDeliveryDeliveryNotValidCheck(Delivery delivery, Optional<UUID> newId) {
        delivery.setDeliveryID(newId.get());
        Delivery savedDelivery = deliveryRepository.save(delivery);

        if (savedDelivery == null || savedDelivery.getDeliveryID() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return createDeliveryCorrectlySaved(savedDelivery);
    }

    /**
     * Checks if the delivery was correctly added to the database. Improves LOC and CC for createDelivery.
     * @param savedDelivery the new delivery
     * @return the corresponding ResponseEntity
     */
    private ResponseEntity<Delivery> createDeliveryCorrectlySaved(Delivery savedDelivery) {
        final Optional<Delivery> databaseDelivery = deliveryRepository.findById(savedDelivery.getDeliveryID());
        if (databaseDelivery.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(databaseDelivery.get(), HttpStatus.OK);
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

    /**
     * Return all deliveries for a given vendor.
     * @param vendorId the id of the vendor to be queried
     * @param role the role of the user calling the endpoint
     * @return all deliveries for the vendor
     */
    public ResponseEntity<List<UUID>> getAllDeliveriesVendor(UUID vendorId, String role) {
        if(!checkVendor(role)) {
            return new ResponseEntity<List<UUID>>(HttpStatus.UNAUTHORIZED);
        }

        List<Restaurant> restaurants = restaurantRepository.findAll();

        List<UUID> filteredRestaurants = restaurants.stream().filter(x -> x.getVendorID().equals(vendorId))
                .map(x -> x.getRestaurantID()).collect(Collectors.toList());
        if(filteredRestaurants.isEmpty()) {
            return new ResponseEntity<List<UUID>>(HttpStatus.NOT_FOUND);
        }
        List<Delivery> deliveries = deliveryRepository.findAll();

        List<UUID> filteredDeliveries = deliveries.stream().filter(x -> filteredRestaurants
                .contains(x.getRestaurantID())).map(x -> x.getDeliveryID()).collect(Collectors.toList());

        if(filteredDeliveries.isEmpty()) {
            return new ResponseEntity<List<UUID>>(new ArrayList<UUID>(), HttpStatus.OK);
        }

        return new ResponseEntity<List<UUID>>(filteredDeliveries, HttpStatus.OK);
    }

    @Override
    public <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation) {
        if(!role.equals("vendor")) {
            return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
        }
        return operation.get();
    }
}
