package nl.tudelft.sem.template.example.service.handlers;

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
public class AssociationHandler extends BaseHandler {
    @Autowired
    private final transient DeliveryRepository deliveryRepository;
    private final transient RestaurantRepository restaurantRepository;


    public AssociationHandler(DeliveryRepository deliveryRepository, RestaurantRepository restaurantRepository) {
        this.deliveryRepository = deliveryRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Authorizes the request.
     *
     * @param request the request to authorize
     * @return true if the request is authorized, false otherwise
     */
    public boolean handle(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");

        try {
            UUID userUuid = UUID.fromString(userId);
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
                UUID deliveryId = UUID.fromString(endpoint.split("/")[2]);
                return courierDeliveryAssociation(userUuid, deliveryId);
            } else if (endpoint.contains("/status/")) {
                UUID deliveryId = UUID.fromString(endpoint.split("/")[2]);
                return vendorDeliveryAssociation(userUuid, deliveryId);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean vendorDeliveryAssociation(UUID userId, UUID deliveryId) {
        // get the delivery object
        Optional<Delivery> delivery = deliveryRepository.findById(deliveryId);

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

        return restaurant.get().getVendorID().equals(userId);
    }

    /**
     * Checks if the courier is associated with the delivery.
     *
     * @param userId the id of the courier
     * @param deliveryId the id of the delivery
     * @return true if the courier is associated with the delivery, false otherwise
     */
    public boolean courierDeliveryAssociation(UUID userId, UUID deliveryId) {
        // get the delivery object
        Optional<Delivery> delivery = deliveryRepository.findById(deliveryId);

        // if the delivery object is empty, let the request through (it will be caught by the controller)
        if (delivery.isEmpty()) {
            return true;
        }

        return delivery.get().getCourierID().equals(userId);
    }
}
