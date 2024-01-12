package nl.tudelft.sem.template.example.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExternalService {

    /**
     * Gets the restaurant location from external microservice.
     * @param vendorID the id of the vendor
     * @return location
     */
    public String getRestaurantLocation(UUID vendorID) {
        return "PickUp in format xxx.xxx";
    }

    /**
     * Gets the order destination from external microservice.
     * @param customerId the customer id
     * @param orderID the id of the order
     * @return the location
     */
    public String getOrderDestination(UUID customerId, UUID orderID) {
        return "Destination in format xxx.xxx";
    }

    /**
     * Checks if the user is courier with the help of external microservice.
     * @param userId id of the user
     * @return the belonging
     */
    public boolean isCourier(String userId) {
        return true;
    }

    /**
     * Checks if the user is vendor with the help of external microservice.
     * @param userId id of the user
     * @return the belonging
     */
    public boolean isVendor(String userId) {
        return true;
    }

    /**
     * Checks if the user is admin with the help of external microservice.
     * @param userId id of the user
     * @return the belonging
     */
    public boolean isAdmin(String userId) {
        return true;
    }

    /**
     * Checks if the user is customer with the help of external microservice.
     * @param userId id of the user
     * @return the belonging
     */
    public boolean isCustomer(String userId) {
        return true;
    }
}
