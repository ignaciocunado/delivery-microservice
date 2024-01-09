package nl.tudelft.sem.template.example.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that delivery controller calls on its subcontrollers correctly.
 * Note: Only tests that the methods are called, not the functionality of the methods.
 *       Those tests should be done in individual subcontroller test files.
 */
class DeliveryControllerTest {
    private transient CourierController courierController;
    private transient VendorController vendorController;
    private transient DeliveryController deliveryController;
    private transient GlobalController globalController;

    private UUID deliveryId;
    private String role;

    /**
     * Mocks courier controller to setup delivery controller.
     */
    @BeforeEach
    void setUp() {
        // set up parameters
        deliveryId = UUID.randomUUID();
        role = "courier";

        // mock courier controller to verify its methods are called
        courierController = Mockito.mock(CourierController.class);
        vendorController = Mockito.mock(VendorController.class);
        globalController = Mockito.mock(GlobalController.class);
        deliveryController = new DeliveryController(courierController, vendorController, globalController);
    }

    @Test
    void getPickUpLocation() {

        deliveryController.getPickUpLocation(deliveryId, role);

        Mockito.verify(courierController).getPickUpLocation(deliveryId, role);
    }

    @Test
    void acceptDelivery() {
        deliveryController.acceptDelivery(deliveryId, role);

        Mockito.verify(vendorController).acceptDelivery(deliveryId, role);
    }

    @Test
<<<<<<< HEAD
    void testGetPickUpEstimateDeliveryId() {
        deliveryController.getPickUpEstimateDeliveryId(deliveryId, role);
        Mockito.verify(vendorController).getPickUpEstimate(deliveryId, role);
    }

=======
    void rejectDelivery() {
        deliveryController.rejectDelivery(deliveryId, role);

        Mockito.verify(vendorController).rejectDelivery(deliveryId, role);
    }

    @Test
    void getCustomerID() {
        deliveryController.getCustomerByDeliveryId(deliveryId, role);

        Mockito.verify(vendorController).getCustomerByDeliveryId(deliveryId, role);
    }

    @Test
    void deliveryIdDone() {
        deliveryController.deliveryIdDone(deliveryId, role);

        Mockito.verify(courierController).deliveredDelivery(deliveryId, role);
    }

    @Test
    void getLiveLocation() {
        deliveryController.getLiveLocation(deliveryId, role);

        Mockito.verify(globalController).getLiveLocation(deliveryId, role);
    }

    @Test
    void testEditStatusDelivery() {
        deliveryController.editStatusDelivery(deliveryId, role, "preparing");

        Mockito.verify(vendorController).editStatusDelivery(deliveryId, role, "preparing");
    }
>>>>>>> 35d324b90da3df3f988f17ac3f39825a8443cf5e
}