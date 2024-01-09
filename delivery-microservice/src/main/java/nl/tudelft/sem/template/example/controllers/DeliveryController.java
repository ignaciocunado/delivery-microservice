package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.UUID;

import lombok.Setter;
import nl.tudelft.sem.api.DeliveryApi;
import nl.tudelft.sem.model.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.print.DocFlavor;
import javax.print.attribute.IntegerSyntax;

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

    /**
     * Autowired constructor for the controller.
     * @param courierController subcontroller for couriers
     * @param vendorController subcontroller for vendors
     * @param globalController subcontroller for global endpoints
     */
    @Autowired
    public DeliveryController(CourierController courierController, VendorController vendorController,
                              GlobalController globalController, VendorOrCourierController vendorOrCourierController) {
        this.courierController = courierController;
        this.vendorController = vendorController;
        this.globalController = globalController;
        this.vendorOrCourierController = vendorOrCourierController;
    }

    @Override
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId, String role) {
        return courierController.getPickUpLocation(deliveryId, role);
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
     * @param deliveryID ID of delivery to get the picked up timestamp of (required)
     * @param role The role of the user (required)
     * @return the estimated pickup time of the delivery object
     */
    @Override
    public ResponseEntity<OffsetDateTime> getPickUpEstimateDeliveryId(
            @Parameter(name = "deliveryID", description = "ID of delivery to get the picked up timestamp of", required = true, in = ParameterIn.PATH) @PathVariable("deliveryID") UUID deliveryID,
            @NotNull @Parameter(name = "role", description = "The role of the user", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "role", required = true) String role
    ) {
        return vendorController.getPickUpEstimate(deliveryID, role);
    }

    /**
     * Integrates controller with API for getDropOffEstimate endpoint.
     * @param deliveryID ID of delivery to get the dropped off timestamp of (required)
     * @param role The role of the user (required)
     * @return 200 + message, 400, 403, or 404
     */
    @Override
    public ResponseEntity<String> setPickUpTime(
            @Parameter(name = "deliveryID", description = "ID of the delivery to query.", required = true, in = ParameterIn.PATH) @PathVariable("deliveryID") UUID deliveryID,
            @NotNull @Parameter(name = "role", description = "The role of the user", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "role", required = true) String role,
            @Parameter(name = "body", description = "", required = true) @Valid @RequestBody String body
    ) {
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
     * Integrates controller with API for get delivery by ID endpoint.
     * Note that this method name is unfortunately misspelled - but the spec is locked in place.
     * @param deliveryId ID of the delivery to get (required)
     * @param role The role of the user (required)
     * @return The delivery object, if it was found.
     */
    @Override
    public ResponseEntity<Delivery> getDeliveyById(UUID deliveryId, String role) {
        return globalController.getDeliveryById(deliveryId, role);

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
}
