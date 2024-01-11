package nl.tudelft.sem.template.example.service;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

@Service
public class AssociationService {

    private final DeliveryRepository deliveryRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public AssociationService(DeliveryRepository deliveryRepository, RestaurantRepository restaurantRepository) {
        this.deliveryRepository = deliveryRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Authorizes the request.
     *
     * @param request the request to authorize
     * @return true if the request is authorized, false otherwise
     */
    public boolean authorize(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        userId = formatUuid(userId);
        String role = request.getParameter("role");

        // admins can do anything
        if (role.equals("admin")) {
            return true;
        }

        // only restrict patch requests
        if (!request.getMethod().equals("PATCH")) {
            return true;
        }

        // get the endpoint
        String endpoint = request.getRequestURI();


        if (endpoint.contains("/status/delivered")) {
            String deliveryId = endpoint.split("/")[2];
            return courierDeliveryAssociation(userId, deliveryId);
        } else if (endpoint.contains("/status/")) {
            String deliveryId = endpoint.split("/")[2];
            return vendorDeliveryAssociation(userId, deliveryId);
        }


        return true;
    }

    private boolean vendorDeliveryAssociation(String userId, String deliveryId) {
        // get the delivery object
        Optional<Delivery> delivery = deliveryRepository.findById(UUID.fromString(deliveryId));

        // if the delivery object is empty, let the request through (it will be caught by the controller)
        if (delivery.isEmpty()) {
            return true;
        }

        // get the restaurant object
        Optional<Restaurant> restaurant = restaurantRepository.findById(delivery.get().getRestaurantID());

        // if the restaurant object is empty, let the request through (it will be caught by the controller)
        if (restaurant.isEmpty()) {
            return true;
        }

        return restaurant.get().getVendorID().equals(UUID.fromString(userId));
    }

    /**
     * Checks if the courier is associated with the delivery.
     *
     * @param userId the id of the courier
     * @param deliveryId the id of the delivery
     * @return true if the courier is associated with the delivery, false otherwise
     */
    public boolean courierDeliveryAssociation(String userId, String deliveryId) {
        // get the delivery object
        Optional<Delivery> delivery = deliveryRepository.findById(UUID.fromString(deliveryId));

        // if the delivery object is empty, let the request through (it will be caught by the controller)
        if (delivery.isEmpty()) {
            return true;
        }

        return delivery.get().getCourierID().equals(UUID.fromString(userId));
    }

    private static String formatUuid(String uuidString) {
        // Add hyphens to the UUID string to match the standard format
        return String.format(
                "%s-%s-%s-%s-%s",
                uuidString.substring(0, 8),
                uuidString.substring(8, 12),
                uuidString.substring(12, 16),
                uuidString.substring(16, 20),
                uuidString.substring(20)
        );
    }
}
