package nl.tudelft.sem.template.example.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Component
public class VendorController {

    @Getter
    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;
    UUIDGenerationService uuidGenerationService;

    /**
     * Constructor for the Vendor Controller.
     * @param restaurantRepository the restaurant repository
     * @param deliveryRepository the delivery repository
     * @param uuidGenerationService the service for generating UUIDs
     */
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
     * Authorisation method.
     * @param role the role of the user making the request
     * @return true if the user is a customer, false otherwise
     */
    private boolean checkCustomer(String role) {
        return role.equals("customer");
    }

    /**
     * Authorisation method.
     * @param role  the role of the user making the request
     * @return true if the user is an admin, false otherwise
     */
    private boolean checkAdmin(String role) {
        return role.equals("admin");
    }
    /** Adds a courier to a restaurant.
     *
     * @param courierId   ID of the courier to add to the restaurant. (required)
     * @param restaurantId ID of the restaurant to add the courier to. (required)
     * @param role       The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> addCourierToRest(UUID restaurantId, UUID courierId, String role) {

        Restaurant r;

        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (restaurantRepository.findById(restaurantId).isPresent()) {
            r = restaurantRepository.findById(restaurantId).get();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<UUID> newCouriers = new ArrayList<>(r.getCourierIDs());
        newCouriers.add(courierId);
        r.setCourierIDs(newCouriers);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /** Sets the status to accepted for a delivery.
     *
     * @param deliveryId ID of the delivery to mark as accepted. (required)
     * @param role      The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> acceptDelivery(UUID deliveryId, String role) {
        if (checkVendor(role)) {
            if (deliveryRepository.findById(deliveryId).isPresent()) {
                Delivery delivery = deliveryRepository.findById(deliveryId).get();
                delivery.setStatus("accepted");
                deliveryRepository.save(delivery);

                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
     * Implementation for removing a courier from the database.
     * @param restaurantId ID of the restaurant
     * @param courierId ID of the courier to remove
     * @param role role of the user
     * @return void response entity with HTTP codes
     */
    public ResponseEntity<Void> removeCourierRest(UUID restaurantId, UUID courierId, String role) {
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Restaurant> fetched = restaurantRepository.findById(restaurantId);
        if (fetched.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Restaurant restaurant = fetched.get();
        List<UUID> couriers = restaurant.getCourierIDs();

        if(!couriers.contains(courierId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        couriers.remove(courierId);

//        restaurant.setCourierIDs(couriers);

        restaurantRepository.save(restaurant);

        return new ResponseEntity<>(HttpStatus.OK);

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
        if (checkVendor(role)) {
            Optional<Delivery> d = deliveryRepository.findById(deliveryId);
            if (d.isPresent()) {
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
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
        // Authorize the user
        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Ensure delivery validity
        if (delivery == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Generate a new ID for the delivery
        final Optional<UUID> newDeliveryId = uuidGenerationService.generateUniqueId(deliveryRepository);
        if (newDeliveryId.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // If the given restaurant does not exist, fail.
        if (delivery.getRestaurantID() == null || !restaurantRepository.existsById(delivery.getRestaurantID())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Once we have the new ID - save delivery to the DB.
        delivery.setDeliveryID(newDeliveryId.get());
        Delivery savedDelivery = deliveryRepository.save(delivery);

        final Optional<Delivery> databaseDelivery = deliveryRepository.findById(savedDelivery.getDeliveryID());
        return databaseDelivery.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));

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

    /**
     * returns a list of restaurants for a vendor
     * @param vendorId the vendor to query
     * @param role the role of the user
     * @return the list of restaurant Ids
     */
    public ResponseEntity<List<UUID>> getVendorRest(UUID vendorId, String role) {

        if(!checkVendor(role)) {
            return new ResponseEntity<List<UUID>>(HttpStatus.UNAUTHORIZED);
        }

        List<Restaurant> allRestaurants= restaurantRepository.findAll();
        List<Restaurant> filteredRestaurants = allRestaurants.stream().filter(x -> x.getVendorID().equals(vendorId)).collect(Collectors.toList());

        if(filteredRestaurants.isEmpty()) {
            return new ResponseEntity<List<UUID>>(HttpStatus.NOT_FOUND);
        }
        List<UUID> res = new ArrayList<>();

        for (Restaurant r : filteredRestaurants) {
            res.add(r.getRestaurantID());
        }

        return new ResponseEntity<List<UUID>>(res, HttpStatus.OK);

    }


    /**
     * Rate a delivery.
     * @param deliveryID the delivery to rate
     * @param role the role of the user making the request
     * @param body the rating to give the delivery
     * @return a response entity with the given rating
     */
    public ResponseEntity<String> setRateOfDelivery(UUID deliveryID, String role, Double body) {
        if (!checkCustomer(role) && !checkAdmin(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        if (deliveryRepository.existsById(deliveryID)) {
            if (body < 0 || body > 1) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(deliveryRepository.save(deliveryRepository.findById(deliveryID).get()
                    .customerRating(body)).toString(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
