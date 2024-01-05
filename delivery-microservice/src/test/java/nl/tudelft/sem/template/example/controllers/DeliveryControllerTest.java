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
    private transient GeneralController generalController;

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
        generalController = Mockito.mock(GeneralController.class);
        deliveryController = new DeliveryController(courierController, vendorController, generalController);
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

        Mockito.verify(generalController).getLiveLocation(deliveryId, role);
    }

    @Test
    void testEditStatusDelivery() {
        deliveryController.editStatusDelivery(deliveryId, role, "preparing");

        Mockito.verify(vendorController).editStatusDelivery(deliveryId, role, "preparing");
    }
}