package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;


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
    private transient VendorOrCourierController vendorOrCourierController;
    private transient CustomerController customerController;

    private transient UUID deliveryId;
    private transient String role;

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
        vendorOrCourierController = Mockito.mock(VendorOrCourierController.class);
        customerController = Mockito.mock(CustomerController.class);
        deliveryController = new DeliveryController(courierController, vendorController,
                globalController, vendorOrCourierController, customerController);
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
    void testGetPickUpEstimateDeliveryId() {
        deliveryController.getPickUpEstimateDeliveryId(deliveryId, role);
        Mockito.verify(vendorController).getPickUpEstimate(deliveryId, role);
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

        Mockito.verify(globalController).getLiveLocation(deliveryId, role);
    }

    @Test
    void testEditStatusDelivery() {
        deliveryController.editStatusDelivery(deliveryId, role, "preparing");

        Mockito.verify(vendorController).editStatusDelivery(deliveryId, role, "preparing");
    }

    @Test
    void testSetPickUpTime() {
        deliveryController.setPickUpTime(deliveryId, role, "preparing");

        Mockito.verify(vendorController).setPickUpEstimate(deliveryId, role, "preparing");
    }

    @Test
    void testGetDeliveryException() {
        deliveryController.getDeliveryException(deliveryId, role);

        Mockito.verify(globalController).getDeliveryException(deliveryId, role);
    }

    @Test
    void testSetDeliveryDelay() {
        deliveryController.setDeliveryDelay(deliveryId, role, 4);

        Mockito.verify(vendorOrCourierController).setDeliveryDelay(deliveryId, role, 4);
    }

    @Test
    void testGetDeliveryDelay() {
        deliveryController.getDeliveryDelay(deliveryId, role);

        Mockito.verify(vendorOrCourierController).getDeliveryDelay(deliveryId, role);
    }

    @Test
    void assignOrderToCourierTest() {
        UUID courier = UUID.randomUUID();
        deliveryController.assignOrderToCourier(courier, deliveryId, role);

        Mockito.verify(vendorOrCourierController).assignOrderToCourier(courier, deliveryId, role);
    }

    @Test
    void testCreateDelivery() {
        // Since only chained method calls are being tested, we don't need to pass data to the new Delivery.
        final Delivery newDelivery = new Delivery();
        deliveryController.createDelivery(role, newDelivery);

        Mockito.verify(vendorController).createDelivery(role, newDelivery);
    }

    @Test
    void testGetDeliveryById() {
        deliveryController.getDeliveyById(deliveryId, role);

        Mockito.verify(globalController).getDeliveryById(deliveryId, role);
    }

    @Test
    void testGetRestaurantIdByDeliveryId() {
        deliveryController.getRestIdOfDel(deliveryId, role);

        Mockito.verify(globalController).getRestaurantIdByDeliveryId(deliveryId, role);
    }

    @Test
    void testGetOrderByDeliveryId() {
        deliveryController.getOrderByDeliveryId(deliveryId, role);

        Mockito.verify(globalController).getOrderByDeliveryId(deliveryId, role);
    }

    @Test
    void testSetLiveLocation() {
        deliveryController.setLiveLocation(deliveryId, role, "Test");

        Mockito.verify(courierController).setLiveLocation(deliveryId, role, "Test");
    }

    @Test
    void testGetAvRateCourier() {
        UUID courierId = UUID.randomUUID();
        deliveryController.getAvRateCourier(courierId);

        Mockito.verify(courierController).getAvrRating(courierId);
    }

    @Test
    void testGetCourierByDeliveryId() {
        deliveryController.getCourierByDeliveryId(deliveryId, role);

        Mockito.verify(vendorController).getCourierIdByDelivery(deliveryId, role);
    }

    @Test
    void testSetDeliveryException() {
        deliveryController.setDeliveryException(deliveryId, role, "Fall");

        Mockito.verify(vendorOrCourierController).setDeliveryException(deliveryId, role, "Fall");
    }

    @Test
    void testGetLocationOfDelivery() {
        deliveryController.getLocationOfDelivery(deliveryId, role);

        Mockito.verify(courierController).getLocationOfDelivery(deliveryId, role);

    }

    @Test
    void testGetDeliveryEstimate() {
        deliveryController.getDeliveryEstimate(deliveryId, role);
        Mockito.verify(vendorController).getDeliveryEstimate(deliveryId, role);
    }

    @Test
    void testSetDeliveryEstimate() {
        deliveryController.setDeliveryEstimate(deliveryId, role,
                OffsetDateTime.of(2024, 1, 1, 1,
                        1, 1, 1, ZoneOffset.ofHours(0)));

        Mockito.verify(vendorController).setDeliveryEstimate(deliveryId, role,
                OffsetDateTime.of(2024, 1, 1, 1,
                        1, 1, 1, ZoneOffset.ofHours(0)));
    }

    @Test
    void testGetRatingByDeliveryId() {
        deliveryController.getRateByDeliveryId(deliveryId, role);
        Mockito.verify(globalController).getRatingByDeliveryId(deliveryId, role);
    }

    @Test
    void testCallGetAllDeliveries() {
        UUID vendorId = UUID.randomUUID();
        deliveryController.getAllDeliveriesVendor(vendorId, "vendor");
        Mockito.verify(vendorController).getAllDeliveriesVendor(vendorId, "vendor");
    }

    @Test
    void testGetAllDeliveriesCourier() {
        UUID courierID = UUID.randomUUID();
        deliveryController.getAllDeliveriesCourier(courierID, "courier");
        Mockito.verify(courierController).getAllDeliveriesCourier(courierID, "courier");
    }
}