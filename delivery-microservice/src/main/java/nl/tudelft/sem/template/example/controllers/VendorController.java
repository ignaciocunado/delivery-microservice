package nl.tudelft.sem.template.example.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.model.RestaurantCourierIDsInner;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;


@Component
public class VendorController {

    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;

    @Autowired
    public VendorController(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public boolean checkVendor(String role) {
        return role.equals("vendor");
    }

    /** Adds a courier to a restaurant.
     *
     * @param courierId   ID of the courier to add to the restaurant. (required)
     * @param restaurantId ID of the restaurant to add the courier to. (required)
     * @param role       The role of the user (required)
     * @return Whether the request was successful or not
     */
    public ResponseEntity<Void> addCourierToRest(UUID courierId, UUID restaurantId, String role) {

        Restaurant r;

        if (!checkVendor(role)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (restaurantRepository.findById(restaurantId).isPresent()) {
            r = restaurantRepository.findById(restaurantId).get();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<RestaurantCourierIDsInner> courierList = r.getCourierIDs();
        RestaurantCourierIDsInner curr = new RestaurantCourierIDsInner();
        curr.setCourierID(courierId);

        courierList.add(curr);
        r.setCourierIDs(courierList);
        restaurantRepository.save(r);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public RestaurantRepository getRestaurantRepository() {
        return restaurantRepository;
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

    public ResponseEntity<Void> removeCourierRest(UUID courierId, UUID restaurantId, String role) {
        if(!checkVendor(role))
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);

        Optional<Restaurant> rest = restaurantRepository.findById(restaurantId);

        if(rest.isEmpty())
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

        List<RestaurantCourierIDsInner> couriers = rest.get().getCourierIDs();

        if(couriers.stream().filter(x -> x.getCourierID().equals(courierId)).collect(Collectors.toList()).isEmpty())
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

        RestaurantCourierIDsInner toRemove = couriers.stream().filter(x -> x.getCourierID().equals(courierId)).collect(Collectors.toList()).get(0);

        couriers.remove(toRemove);

        rest.get().setCourierIDs(couriers);

        restaurantRepository.save(rest.get());

        return new ResponseEntity<Void>(HttpStatus.OK);

    }
}
