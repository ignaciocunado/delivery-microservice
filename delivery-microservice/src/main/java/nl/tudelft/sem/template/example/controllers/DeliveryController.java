package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.UUID;
import nl.tudelft.sem.api.DeliveryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main Delivery Controller. Calls on other controllers to handle requests.
 * Note: Add methods here to integrate them into the API from other subcontrollers.
 */
@RestController
public class DeliveryController implements DeliveryApi {

    private transient CourierController courierController;
    private transient VendorController vendorController;
    private transient GlobalController globalController;

    @Autowired
    public DeliveryController(CourierController courierController, VendorController vendorController, GlobalController globalController) {
        this.courierController = courierController;
        this.vendorController = vendorController;
        this.globalController = globalController;
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

    public void setCourierController(CourierController courierController) {
        this.courierController = courierController;
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
}
