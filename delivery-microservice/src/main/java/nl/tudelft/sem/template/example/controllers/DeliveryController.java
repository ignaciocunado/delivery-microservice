package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import java.util.UUID;

import lombok.Setter;
import nl.tudelft.sem.api.DeliveryApi;
import nl.tudelft.sem.model.Delivery;
import org.apache.catalina.util.CustomObjectInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;

import org.springframework.web.bind.annotation.RestController;

/**
 * Main Delivery Controller. Calls on other controllers to handle requests.
 * Note: Add methods here to integrate them into the API from other subcontrollers.
 */
@RestController
public class DeliveryController implements DeliveryApi {

    @Setter
    private transient CourierController courierController;
    private final transient VendorController vendorController;
    private final transient GlobalController globalController;
    private final transient VendorOrCourierController vendorOrCourierController;
    private final transient CustomerController customerController;

    /**
     * Autowired constructor for the controller.
     * @param courierController subcontroller for couriers
     * @param vendorController subcontroller for vendors
     * @param globalController subcontroller for global endpoints
     * @param customerController subcontroller for customers
     * @param vendorOrCourierController subcontroller for vendors and couriers
     */
    @Autowired
    public DeliveryController(CourierController courierController, VendorController vendorController,
                              GlobalController globalController, VendorOrCourierController vendorOrCourierController,
                              CustomerController customerController) {
        this.courierController = courierController;
        this.vendorController = vendorController;
        this.globalController = globalController;
        this.vendorOrCourierController = vendorOrCourierController;
        this.customerController = customerController;
    }

    /**
     * Integrates controller with API to get the PickUp location of delivery.
     * @param deliveryId id of the delivery
     * @param role The role of the user (required)
     * @return ResponseEntity
     */
    @Override
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId, String role) {
        return courierController.getPickUpLocation(deliveryId, role);
    }

    /**
     * Integrates controller with API to get the destination of delivery.
     * @param deliveryId id of the delivery
     * @param role The role of the user (required)
     * @return 200 + message, 403, or 404
     */
    @Override
    public ResponseEntity<String> getLocationOfDelivery(UUID deliveryId, String role) {
        return courierController.getLocationOfDelivery(deliveryId, role);
    }

    /**
     * Integrates controller with API to set the exception of delivery.
     * @param deliveryId ID of the delivery to query. (required)
     * @param role The role of the user (required)
     * @param body  (required)
     * @return 200 + message, 400, 403, or 404
     */
    @Override
    public ResponseEntity<String> setDeliveryException(UUID deliveryId, String role, String body) {
        return vendorOrCourierController.setDeliveryException(deliveryId, role, body);
    }

    /**
     * Integrates controller with API to get the courierId of delivery.
     * @param deliveryId the id of delivery
     * @param role The role of the user (required)
     * @return the UUID in the response entity
     */
    @Override
    public ResponseEntity<UUID> getCourierByDeliveryId(UUID deliveryId, String role) {
        return vendorController.getCourierIdByDelivery(deliveryId, role);
    }

    /**
     * Integrates controller with API to get the rating of courier deliveries.
     * @param courierID The ID of the courier to query (required)
     * @return the average rating
     */
    @Override
    public ResponseEntity<Double> getAvRateCourier(UUID courierID, String role) {
        return courierController.getAvrRating(courierID, role);
    }

    /**
     * Integrates controller with API to set the exception of delivery.
     * @param deliveryId the id of the delivery
     * @param role The role of the user (required)
     * @param body  (optional)
     * @return 200 + message, 403, or 404
     */
    @Override
    public ResponseEntity<String> setLiveLocation(UUID deliveryId, String role, String body) {
        return courierController.setLiveLocation(deliveryId, role, body);
    }

    /** Integrates controller with API for the get customer ID endpoint.
     *
     * @param deliveryID ID of the delivery to mark as accepted. (required)
     * @param role       The role of the user (required)
     * @return customer ID for a delivery
     */
    @Override
    public ResponseEntity<UUID> getCustomerByDeliveryId(UUID deliveryID, String role) {
        return vendorController.getCustomerByDeliveryId(deliveryID, role);
    }

    /** Integrates controller with API for accept delivery endpoint.
     *
     * @param deliveryId ID of the delivery to mark as accepted. (required)
     * @param role       The role of the user (required)
     * @return vendor controller's response entity
     */
    @Override
    public ResponseEntity<Void> acceptDelivery(UUID deliveryId, String role) {
        return vendorController.acceptDelivery(deliveryId, role);
    }

    /**
     * Integrates controller with API for getPickUpEstimate endpoint.
     * NOTE: this method is named incorrectly. It returns the PICKED-UP time of a delivery,
     * as is specified by the OpenAPI document.
     *
     * @param deliveryID ID of delivery to get the picked up timestamp of (required)
     * @param role The role of the user (required)
     * @return the picked time of the delivery object
     */
    @Override
    public ResponseEntity<OffsetDateTime> getPickUpEstimateDeliveryId(UUID deliveryID, String role) {
        return vendorController.getPickedUpEstimate(deliveryID, role);
    }

    /**
     * Integrates controller with API for getDropOffEstimate endpoint.
     * @param deliveryID ID of delivery to get the dropped off timestamp of (required)
     * @param role The role of the user (required)
     * @return 200 + message, 400, 403, or 404
     */
    @Override
    public ResponseEntity<String> setPickUpTime(UUID deliveryID, String role, String body) {
        return vendorController.setPickUpEstimate(deliveryID, role, body);
    }


    /** Integrates controller with API for reject delivery endpoint.
     *
     * @param deliveryId ID of the delivery to mark as rejected. (required)
     * @param role       The role of the user (required)
     * @return vendor controller's response entity
     */
    @Override
    public ResponseEntity<Void> rejectDelivery(UUID deliveryId, String role) {
        return vendorController.rejectDelivery(deliveryId, role);
    }

    /** Integrates controller with API for the get live location endpoint.
     *
     * @param deliveryID ID of the delivery to mark as rejected. (required)
     * @param role       The role of the user (required)
     * @return string representing the coordinates of the courier
     */
    @Override
    public ResponseEntity<String> getLiveLocation(UUID deliveryID, String role) {
        return globalController.getLiveLocation(deliveryID, role);
    }

    /** Integrates controller with API for get delivery endpoint.
     *
     * @param deliveryId ID of delivery to complete (required)
     * @param role       The role of the user (required)
     * @return courier controller's response entity
     */
    @Override
    public ResponseEntity<String> deliveryIdDone(UUID deliveryId, String role) {
        return courierController.deliveredDelivery(deliveryId, role);
    }

    /** Integrates controller with API for edit status delivery endpoint.
     *
     * @param deliveryId ID of the delivery to edit the status of. (required)
     * @param role       The role of the user (required)
     * @param status     The new status must be &#39;preparing&#39; or &#39;given to courier&#39;. (required)
     * @return vendor controller's response entity
     */
    @Override
    public ResponseEntity<Void> editStatusDelivery(UUID deliveryId, String role, String status) {
        return vendorController.editStatusDelivery(deliveryId, role, status);
    }

    /**
     * Integrates controller with API for get delivery exception endpoint.
     * @param deliveryID ID of delivery to query. (required)
     * @param role The role of the user (required)
     * @return the exception iff there is one
     */
    @Override
    public ResponseEntity<String> getDeliveryException(UUID deliveryID, String role) {
        return globalController.getDeliveryException(deliveryID, role);
    }

    /**
     * Integrates controller with API for set delivery delay endpoint.
     * @param deliveryID ID of delivery to update. (required)
     * @param role The role of the user (required)
     * @param body  (required)
     * @return the new delay
     */
    @Override
    public ResponseEntity<Integer> setDeliveryDelay(UUID deliveryID, String role, Integer body) {
        return vendorOrCourierController.setDeliveryDelay(deliveryID, role, body);
    }

    /**
     * Integrates controller with API for get delivery delay endpoint.
     * @param deliveryID ID of delivery to query. (required)
     * @param role The role of the user (required)
     * @return the delay of the delivery
     */
    @Override
    public ResponseEntity<Integer> getDeliveryDelay(UUID deliveryID, String role) {
        return vendorOrCourierController.getDeliveryDelay(deliveryID, role);
    }

    /**
     * Integrates controller with API for assign order to courier endpoint.
     * @param courierID The id of the courier to which the delivery will be assigned (required)
     * @param deliveryID The delivery to be assigned to the courier (required)
     * @param role The role of the user (required)
     * @return ID of the delivery
     */
    @Override
    public ResponseEntity<UUID> assignOrderToCourier(UUID courierID, UUID deliveryID, String role) {
        return vendorOrCourierController.assignOrderToCourier(courierID, deliveryID, role);
    }

    /**
     * Calls the implementation of the method in the vendorController.
     * @param vendorId The ID of the vendor to query (required)
     * @param role The role of the user (required)
     * @return the corresponding ResponseEntity
     */
    @Override
    public ResponseEntity<List<UUID>> getAllDeliveriesVendor(UUID vendorId, String role) {
        return vendorController.getAllDeliveriesVendor(vendorId, role);
    }

    /**
     * Integrates controller with API for the create delivery endpoint.
     * @param role The role of the user (required)
     * @param delivery Delivery data to create. ID is ignored entirely. (required)
     * @return The created Delivery object.
     */
    @Override
    public ResponseEntity<Delivery> createDelivery(String role, Delivery delivery) {
        return vendorController.createDelivery(role, delivery);
    }

    /**
     * Integrates controller with API for get delivery by ID endpoint.
     * Note that this method name is unfortunately misspelled - but the spec is locked in place.
     * @param deliveryId ID of the delivery to get (required)
     * @param role The role of the user (required)
     * @return The delivery object, if it was found.
     */
    @Override
    public ResponseEntity<Delivery> getDeliveyById(UUID deliveryId, String role) {
        return globalController.getDeliveryById(deliveryId, role);
    }

    /**
     * Integrates controller with API for get restaurant ID by delivery ID endpoint.
     * @param deliveryId ID of delivery to query. (required)
     * @param role The role of the user (required)
     * @return The delivery's restaurant ID.
     */
    @Override
    public ResponseEntity<UUID> getRestIdOfDel(UUID deliveryId, String role) {
        // Note: the implementation method uses a non-abbreviated name to be more consistent with our data model.
        return globalController.getRestaurantIdByDeliveryId(deliveryId, role);
    }

    /**
     * Integrates controller with API for get order by delivery ID endpoint.
     * @param deliveryId ID of the delivery to get. (required)
     * @param role The role of the user (required)
     * @return ID of the order.
     */
    @Override
    public ResponseEntity<UUID> getOrderByDeliveryId(UUID deliveryId, String role) {
        return globalController.getOrderByDeliveryId(deliveryId, role);
    }


    /**
     * Integrates controller with API for get delivery time estimate endpoint.
     * @param deliveryID The ID of the delivery for which the delivery time estimate will be queried. (required)
     * @param role The role of the user (required)
     * @return The delivery time estimate.
     */
    @Override
    public ResponseEntity<OffsetDateTime> getDeliveryEstimate(UUID deliveryID, String role) {
        return vendorController.getDeliveryEstimate(deliveryID, role);
    }

    /**
     * Integrates controller with API for set delivery time endpoint.
     * @param deliveryID The id of the delivery to which the delivery time will be assigned (required)
     * @param role The role of the user (required)
     * @param body  (required)
     * @return ID of the delivery
     */
    @Override
    public ResponseEntity<String> setDeliveryEstimate(UUID deliveryID, String role, OffsetDateTime body) {
        return vendorController.setDeliveryEstimate(deliveryID, role, body);
    }

    /**
     * Integrates controller with API for the get rating by delivery ID endpoint.
     * @param deliveryId ID of delivery to query. (required)
     * @param role The role of the user (required)
     * @return The user's delivery rating.
     */
    @Override
    public ResponseEntity<Double> getRateByDeliveryId(UUID deliveryId, String role) {
        // Note: the implementation function is named "get rating", to be more in line
        // with our model definitions. If necessary, this can be reverted to the original name.
        return sanityCheck(globalController.getRatingByDeliveryId(deliveryId, role), deliveryId);
    }

    /**
     * Integrates controller with API for the get pick up time endpoint.
     * Note: this returns the pickup time ESTIMATE, as specified in the OpenAPI spec.
     * NOT the actual picked-up time. The methods are just named a little confusingly.
     *
     * @param deliveryId ID of the delivery to query. (required)
     * @param role The role of the user (required)
     * @return The delivery's pick up time.
     */
    @Override
    public ResponseEntity<OffsetDateTime> getPickUpTime(UUID deliveryId, String role) {
        return globalController.getPickUpTime(deliveryId, role);
    }

    /**
     * Integrates controller with API for the get all deliveries for a courier endpoint.
     * @param courierID The ID of the courier to query (required)
     * @param role The role of the user (required)
     * @return a list of all delivery IDs for a courier
     */
    @Override
    public ResponseEntity<List<UUID>> getAllDeliveriesCourier(UUID courierID, String role) {
        return courierController.getAllDeliveriesCourier(courierID, role);
    }

    /**
     * Integrates controller with API for the get all deliveries for a customer endpoint.
     * @param customerID The ID of the customer to query (required)
     * @param role The role of the user (required)
     * @return a list of all delivery IDs for a customer
     */
    @Override
    public ResponseEntity<List<UUID>> getAllDeliveriesCustomer(UUID customerID, String role) {
        return customerController.getAllDeliveriesCustomer(customerID, role);
    }

    @lombok.Generated
    private static ResponseEntity<Double> sanityCheck(ResponseEntity<Double> r, UUID deliveryId) {
        if (r != null && r.getBody() != null) {
            if (r.getBody() < 0 || r.getBody() > 1) {
                System.out.println("\033[91;40m getRateByDeliveryId requested for UUID \033[30;101m " + deliveryId
                        + " \033[91;40m got response: \033[30;101m " + r.getBody() + " \033[0m");
            }
        } else {
            System.out.println("\033[91;40m ** getRateByDeliveryId requested for UUID \033[30;101m " + deliveryId
                    + " \033[91;40m was NULL ** \033[0m");
        }
        return r;
    }
}
