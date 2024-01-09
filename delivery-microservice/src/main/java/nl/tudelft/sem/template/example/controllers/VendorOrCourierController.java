package nl.tudelft.sem.template.example.controllers;


import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sub-Controller of DeliveryController
 */
@Component
public class VendorOrCourierController {

    RestaurantRepository restaurantRepository;
    DeliveryRepository deliveryRepository;

    /**
     * Constructor
     * @param restaurantRepository restaurant DB
     * @param deliveryRepository delivery DB
     */
    @Autowired
    public VendorOrCourierController(RestaurantRepository restaurantRepository, DeliveryRepository deliveryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Checks whether the role provided is valid
     * @param role role
     * @return true iff the role is valid
     */
    public boolean checkVendorOrCourier(String role) {
        return "vendorcourier".contains(role);
    }


}
