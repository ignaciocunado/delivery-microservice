package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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
        Mockito.when(courierController.getPickUpLocation(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getPickUpLocation(deliveryId, role);
        Mockito.verify(courierController).getPickUpLocation(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void acceptDelivery() {
        Mockito.when(vendorController.acceptDelivery(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.acceptDelivery(deliveryId, role);
        Mockito.verify(vendorController).acceptDelivery(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testGetPickUpEstimateDeliveryId() {
        Mockito.when(vendorController.getPickedUpEstimate(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getPickUpEstimateDeliveryId(deliveryId, role);
        Mockito.verify(vendorController).getPickedUpEstimate(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void rejectDelivery() {
        Mockito.when(vendorController.rejectDelivery(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.rejectDelivery(deliveryId, role);
        Mockito.verify(vendorController).rejectDelivery(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void getCustomerID() {
        Mockito.when(vendorController.getCustomerByDeliveryId(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getCustomerByDeliveryId(deliveryId, role);
        Mockito.verify(vendorController).getCustomerByDeliveryId(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void deliveryIdDone() {
        Mockito.when(courierController.deliveredDelivery(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.deliveryIdDone(deliveryId, role);
        Mockito.verify(courierController).deliveredDelivery(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void getLiveLocation() {
        Mockito.when(globalController.getLiveLocation(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getLiveLocation(deliveryId, role);
        Mockito.verify(globalController).getLiveLocation(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testEditStatusDelivery() {
        Mockito.when(vendorController.editStatusDelivery(deliveryId, role, "preparing")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.editStatusDelivery(deliveryId, role, "preparing");
        Mockito.verify(vendorController).editStatusDelivery(deliveryId, role, "preparing");
        assertNotNull(r);
    }

    @Test
    void testSetPickUpTime() {
        Mockito.when(vendorController.setPickUpEstimate(deliveryId, role, "preparing")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setPickUpTime(deliveryId, role, "preparing");
        Mockito.verify(vendorController).setPickUpEstimate(deliveryId, role, "preparing");
        assertNotNull(r);
    }

    @Test
    void testGetDeliveryException() {
        Mockito.when(globalController.getDeliveryException(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getDeliveryException(deliveryId, role);
        Mockito.verify(globalController).getDeliveryException(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testSetDeliveryDelay() {
        Mockito.when(vendorOrCourierController.setDeliveryDelay(deliveryId, role, 4)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setDeliveryDelay(deliveryId, role, 4);
        Mockito.verify(vendorOrCourierController).setDeliveryDelay(deliveryId, role, 4);
        assertNotNull(r);
    }

    @Test
    void testGetDeliveryDelay() {
        Mockito.when(vendorOrCourierController.getDeliveryDelay(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getDeliveryDelay(deliveryId, role);
        Mockito.verify(vendorOrCourierController).getDeliveryDelay(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void assignOrderToCourierTest() {
        UUID courier = UUID.randomUUID();
        Mockito.when(vendorOrCourierController.assignOrderToCourier(courier, deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.assignOrderToCourier(courier, deliveryId, role);
        Mockito.verify(vendorOrCourierController).assignOrderToCourier(courier, deliveryId, role);
        assertNotNull(r);
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
        Mockito.when(globalController.getDeliveryById(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getDeliveyById(deliveryId, role);
        Mockito.verify(globalController).getDeliveryById(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testGetRestaurantIdByDeliveryId() {
        Mockito.when(globalController.getRestaurantIdByDeliveryId(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getRestIdOfDel(deliveryId, role);
        Mockito.verify(globalController).getRestaurantIdByDeliveryId(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testGetOrderByDeliveryId() {
        Mockito.when(globalController.getOrderByDeliveryId(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getOrderByDeliveryId(deliveryId, role);
        Mockito.verify(globalController).getOrderByDeliveryId(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testSetLiveLocation() {
        Mockito.when(courierController.setLiveLocation(deliveryId, role, "Test")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setLiveLocation(deliveryId, role, "Test");
        Mockito.verify(courierController).setLiveLocation(deliveryId, role, "Test");
        assertNotNull(r);
    }

    @Test
    void testGetAvRateCourier() {
        UUID courierId = UUID.randomUUID();
        Mockito.when(courierController.getAvrRating(courierId, "courier")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getAvRateCourier(courierId, "courier");
        Mockito.verify(courierController).getAvrRating(courierId, "courier");
        assertNotNull(r);
    }

    @Test
    void testGetCourierByDeliveryId() {
        Mockito.when(vendorController.getCourierIdByDelivery(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getCourierByDeliveryId(deliveryId, role);
        Mockito.verify(vendorController).getCourierIdByDelivery(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testSetDeliveryException() {
        Mockito.when(vendorOrCourierController.setDeliveryException(deliveryId, role, "Fall")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setDeliveryException(deliveryId, role, "Fall");
        Mockito.verify(vendorOrCourierController).setDeliveryException(deliveryId, role, "Fall");
        assertNotNull(r);
    }

    @Test
    void testGetLocationOfDelivery() {
        Mockito.when(courierController.getLocationOfDelivery(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getLocationOfDelivery(deliveryId, role);
        Mockito.verify(courierController).getLocationOfDelivery(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testGetDeliveryEstimate() {
        Mockito.when(vendorController.getDeliveryEstimate(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getDeliveryEstimate(deliveryId, role);
        Mockito.verify(vendorController).getDeliveryEstimate(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testSetDeliveryEstimate() {
        Mockito.when(vendorController.setDeliveryEstimate(deliveryId, role,
                OffsetDateTime.of(2024, 1, 1, 1,
                        1, 1, 1, ZoneOffset.ofHours(0))))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setDeliveryEstimate(deliveryId, role,
                OffsetDateTime.of(2024, 1, 1, 1,
                        1, 1, 1, ZoneOffset.ofHours(0)));

        Mockito.verify(vendorController).setDeliveryEstimate(deliveryId, role,
                OffsetDateTime.of(2024, 1, 1, 1,
                        1, 1, 1, ZoneOffset.ofHours(0)));
        assertNotNull(r);
    }

    @Test
    void testGetRatingByDeliveryId() {
        Mockito.when(globalController.getRatingByDeliveryId(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getRateByDeliveryId(deliveryId, role);
        Mockito.verify(globalController).getRatingByDeliveryId(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testGetPickUpTime() {
        Mockito.when(globalController.getPickUpTime(deliveryId, role)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getPickUpTime(deliveryId, role);
        Mockito.verify(globalController).getPickUpTime(deliveryId, role);
        assertNotNull(r);
    }

    @Test
    void testCallGetAllDeliveries() {
        UUID vendorId = UUID.randomUUID();
        Mockito.when(vendorController.getAllDeliveriesVendor(vendorId, "vendor")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getAllDeliveriesVendor(vendorId, "vendor");
        Mockito.verify(vendorController).getAllDeliveriesVendor(vendorId, "vendor");
        assertNotNull(r);
    }

    @Test
    void testGetAllDeliveriesCourier() {
        UUID courierID = UUID.randomUUID();
        Mockito.when(courierController.getAllDeliveriesCourier(courierID, "courier")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getAllDeliveriesCourier(courierID, "courier");
        Mockito.verify(courierController).getAllDeliveriesCourier(courierID, "courier");
        assertNotNull(r);
    }

    @Test
    void testGetAllDeliveriesCustomer() {
        UUID customerID = UUID.randomUUID();
        Mockito.when(customerController.getAllDeliveriesCustomer(customerID, "customer")).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getAllDeliveriesCustomer(customerID, "customer");
        Mockito.verify(customerController).getAllDeliveriesCustomer(customerID, "customer");
        assertNotNull(r);
    }

    @Test
    void testSetRateOfDelivery() {
        Mockito.when(vendorController.setRateOfDelivery(deliveryId, role, 1d)).thenReturn(new ResponseEntity<>("", null, HttpStatus.OK));
        ResponseEntity<String> r = deliveryController.setRateOfDelivery(deliveryId, role, 1d);
        Mockito.verify(vendorController).setRateOfDelivery(deliveryId, role, 1d);
        assertNotNull(r);
    }
}