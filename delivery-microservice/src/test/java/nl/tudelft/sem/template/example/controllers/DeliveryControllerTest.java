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

        Mockito.verify(courierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void acceptDelivery() {
        deliveryController.acceptDelivery(deliveryId, role);

        Mockito.verify(vendorController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetPickUpEstimateDeliveryId() {
        deliveryController.getPickUpEstimateDeliveryId(deliveryId, role);
        Mockito.verify(vendorController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void rejectDelivery() {
        deliveryController.rejectDelivery(deliveryId, role);

        Mockito.verify(vendorController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void getCustomerID() {
        deliveryController.getCustomerByDeliveryId(deliveryId, role);

        Mockito.verify(vendorController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void deliveryIdDone() {
        deliveryController.deliveryIdDone(deliveryId, role);

        Mockito.verify(courierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void getLiveLocation() {
        deliveryController.getLiveLocation(deliveryId, role);

        Mockito.verify(globalController).getLiveLocation(deliveryId);
    }

    @Test
    void testEditStatusDelivery() {
        deliveryController.editStatusDelivery(deliveryId, role, "preparing");

        Mockito.verify(vendorController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testSetPickUpTime() {
        deliveryController.setPickUpTime(deliveryId, role, "preparing");

        Mockito.verify(vendorOrCourierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetDeliveryException() {
        deliveryController.getDeliveryException(deliveryId, role);

        Mockito.verify(globalController).getDeliveryException(deliveryId);
    }

    @Test
    void testSetDeliveryDelay() {
        deliveryController.setDeliveryDelay(deliveryId, role, 4);

        Mockito.verify(vendorOrCourierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetDeliveryDelay() {
        deliveryController.getDeliveryDelay(deliveryId, role);

        Mockito.verify(vendorOrCourierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void assignOrderToCourierTest() {
        UUID courier = UUID.randomUUID();
        deliveryController.assignOrderToCourier(courier, deliveryId, role);

        Mockito.verify(vendorOrCourierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testCreateDelivery() {
        // Since only chained method calls are being tested, we don't need to pass data to the new Delivery.
        final Delivery newDelivery = new Delivery();
        deliveryController.createDelivery(role, newDelivery);

        Mockito.verify(vendorController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetDeliveryById() {
        deliveryController.getDeliveyById(deliveryId, role);

        Mockito.verify(globalController).getDeliveryById(deliveryId);
    }

    @Test
    void testGetRestaurantIdByDeliveryId() {
        deliveryController.getRestIdOfDel(deliveryId, role);

        Mockito.verify(globalController).getRestaurantIdByDeliveryId(deliveryId);
    }

    @Test
    void testGetOrderByDeliveryId() {
        deliveryController.getOrderByDeliveryId(deliveryId, role);

        Mockito.verify(globalController).getOrderByDeliveryId(deliveryId);
    }

    @Test
    void testSetLiveLocation() {
        deliveryController.setLiveLocation(deliveryId, role, "Test");

        Mockito.verify(courierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetAvRateCourier() {
        UUID courierId = UUID.randomUUID();
        deliveryController.getAvRateCourier(courierId);

        Mockito.verify(courierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetCourierByDeliveryId() {
        deliveryController.getCourierByDeliveryId(deliveryId, role);

        Mockito.verify(vendorController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testSetDeliveryException() {
        deliveryController.setDeliveryException(deliveryId, role, "Fall");

        Mockito.verify(vendorOrCourierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetLocationOfDelivery() {
        deliveryController.getLocationOfDelivery(deliveryId, role);

        Mockito.verify(courierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetDeliveryEstimate() {
        deliveryController.getDeliveryEstimate(deliveryId, role);
        Mockito.verify(vendorOrCourierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testSetDeliveryEstimate() {
        deliveryController.setDeliveryEstimate(deliveryId, role,
                OffsetDateTime.of(2024, 1, 1, 1,
                        1, 1, 1, ZoneOffset.ofHours(0)));

        Mockito.verify(vendorOrCourierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetRatingByDeliveryId() {
        deliveryController.getRateByDeliveryId(deliveryId, role);
        Mockito.verify(globalController).getRatingByDeliveryId(deliveryId);
    }

    @Test
    void testCallGetAllDeliveries() {
        UUID vendorId = UUID.randomUUID();
        deliveryController.getAllDeliveriesVendor(vendorId, "vendor");
        Mockito.verify(vendorController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetAllDeliveriesCourier() {
        UUID courierID = UUID.randomUUID();
        deliveryController.getAllDeliveriesCourier(courierID, "courier");
        Mockito.verify(courierController).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    void testGetAllDeliveriesCustomer() {
        UUID customerID = UUID.randomUUID();
        deliveryController.getAllDeliveriesCustomer(customerID, "customer");
        Mockito.verify(customerController).checkAndHandle(Mockito.any(), Mockito.any());
    }
}