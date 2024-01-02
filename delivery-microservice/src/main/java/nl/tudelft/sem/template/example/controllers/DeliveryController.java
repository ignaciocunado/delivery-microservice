package nl.tudelft.sem.template.example.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

// delivery api from generated yaml
import nl.tudelft.sem.api.DeliveryApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Main Delivery Controller. Calls on other controllers to handle requests.
 * Note: Add methods here to integrate them into the API from other subcontrollers.
 */
@RestController
public class DeliveryController implements DeliveryApi {

    private transient CourierController courierController;
    private final transient VendorController vendorController;

    @Autowired
    public DeliveryController(CourierController courierController, VendorController vendorController) {
        this.courierController = courierController;
        this.vendorController = vendorController;
    }

    @Override
    public ResponseEntity<String> getPickUpLocation(UUID deliveryId, String role) {
        return courierController.getPickUpLocation(deliveryId, role);
    }

    public void setCourierController(CourierController courierController) {
        this.courierController = courierController;
    }

    /** Integrates controller with API for accept delivery endpoint
     * @param deliveryId ID of the delivery to mark as accepted. (required)
     * @param role       The role of the user (required)
     * @return vendor controller's response entity
     */
    @Override
    public ResponseEntity<Void> acceptDelivery(UUID deliveryId, String role) {
        return vendorController.acceptDelivery(deliveryId, role);
    }

    @Override
    public ResponseEntity<OffsetDateTime> getPickUpEstimateDeliveryId(
            @Parameter(name = "deliveryID", description = "ID of delivery to get the picked up timestamp of", required = true, in = ParameterIn.PATH) @PathVariable("deliveryID") UUID deliveryID
            ,@NotNull @Parameter(name = "role", description = "The role of the user", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "role", required = true) String role
    ) {
        return vendorController.getPickUpEstimate(deliveryID , role );
    }
}
