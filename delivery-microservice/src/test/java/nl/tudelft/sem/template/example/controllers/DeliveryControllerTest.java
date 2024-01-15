package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.service.globalFunctionalities.AttributeGetterGlobalService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.DeliveryIdGetterGlobalService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.MaxDeliveryZoneService;
import nl.tudelft.sem.template.example.service.roles.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Tests that delivery controller calls on its subcontrollers correctly.
 * Note: Only tests that the methods are called, not the functionality of the methods.
 *       Those tests should be done in individual subcontroller test files.
 */
class DeliveryControllerTest {
    private transient CourierService courierService;
    private transient VendorService vendorService;
    private transient DeliveryController deliveryController;
    private transient GlobalService globalService;
    private transient VendorOrCourierService vendorOrCourierService;
    private transient CustomerService customerService;

    private transient AttributeGetterGlobalService attributeGetterGlobalService;

    private transient DeliveryIdGetterGlobalService deliveryIdGetterGlobalService;

    private transient MaxDeliveryZoneService maxDeliveryZoneService;

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
        courierService = Mockito.mock(CourierService.class);
        vendorService = Mockito.mock(VendorService.class);

        attributeGetterGlobalService = Mockito.mock(AttributeGetterGlobalService.class);
        deliveryIdGetterGlobalService = Mockito.mock(DeliveryIdGetterGlobalService.class);
        maxDeliveryZoneService = Mockito.mock(MaxDeliveryZoneService.class);

        globalService = Mockito.mock(GlobalService.class);

        Mockito.when(globalService.getAttributeGetterGlobalService()).thenReturn(attributeGetterGlobalService);
        Mockito.when(globalService.getMaxDeliveryZoneService()).thenReturn(maxDeliveryZoneService);
        Mockito.when(globalService.getDeliveryIdGetterGlobalService()).thenReturn(deliveryIdGetterGlobalService);

        vendorOrCourierService = Mockito.mock(VendorOrCourierService.class);
        customerService = Mockito.mock(CustomerService.class);
        deliveryController = new DeliveryController(courierService, vendorService,
                globalService, vendorOrCourierService, customerService);
    }

    @Test
    void getPickUpLocation() {
        Mockito.when(courierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getPickUpLocation(deliveryId, role);
        Mockito.verify(courierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void acceptDelivery() {
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.acceptDelivery(deliveryId, role);
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetPickUpEstimateDeliveryId() {
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getPickUpEstimateDeliveryId(deliveryId, role);
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void rejectDelivery() {
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.rejectDelivery(deliveryId, role);
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void getCustomerID() {
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getCustomerByDeliveryId(deliveryId, role);
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void deliveryIdDone() {
        Mockito.when(courierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.deliveryIdDone(deliveryId, role);
        Mockito.verify(courierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void getLiveLocation() {
        Mockito.when(globalService.getAttributeGetterGlobalService().getLiveLocation(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getLiveLocation(deliveryId, role);


        Mockito.verify(attributeGetterGlobalService).getLiveLocation(deliveryId);
        assertNotNull(r);
    }

    @Test
    void testEditStatusDelivery() {
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.editStatusDelivery(deliveryId, role, "preparing");
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testSetPickUpTime() {
        Mockito.when(vendorOrCourierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setPickUpTime(deliveryId, role, "preparing");
        Mockito.verify(vendorOrCourierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetDeliveryException() {
        Mockito.when(attributeGetterGlobalService.getDeliveryException(deliveryId)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getDeliveryException(deliveryId, role);
        Mockito.verify(attributeGetterGlobalService).getDeliveryException(deliveryId);
        assertNotNull(r);
    }

    @Test
    void testSetDeliveryDelay() {
        Mockito.when(vendorOrCourierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setDeliveryDelay(deliveryId, role, 4);
        Mockito.verify(vendorOrCourierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetDeliveryDelay() {
        Mockito.when(vendorOrCourierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getDeliveryDelay(deliveryId, role);
        Mockito.verify(vendorOrCourierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void assignOrderToCourierTest() {
        UUID courier = UUID.randomUUID();
        Mockito.when(vendorOrCourierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.assignOrderToCourier(courier, deliveryId, role);
        Mockito.verify(vendorOrCourierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testCreateDelivery() {
        // Since only chained method calls are being tested, we don't need to pass data to the new Delivery.
        final Delivery newDelivery = new Delivery();
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.createDelivery(role, newDelivery);
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }


    @Test
    void testGetDeliveryById() {
        Mockito.when(globalService.getDeliveryIdGetterGlobalService().getDeliveryById(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getDeliveyById(deliveryId, role);
        Mockito.verify(deliveryIdGetterGlobalService).getDeliveryById(deliveryId);
        assertNotNull(r);
    }

    @Test
    void testGetRestaurantIdByDeliveryId() {
        Mockito.when(globalService.getDeliveryIdGetterGlobalService().getRestaurantIdByDeliveryId(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getRestIdOfDel(deliveryId, role);
        Mockito.verify(deliveryIdGetterGlobalService).getRestaurantIdByDeliveryId(deliveryId);
        assertNotNull(r);
    }

    @Test
    void testGetOrderByDeliveryId() {
        Mockito.when(globalService.getDeliveryIdGetterGlobalService().getOrderByDeliveryId(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getOrderByDeliveryId(deliveryId, role);
        Mockito.verify(deliveryIdGetterGlobalService).getOrderByDeliveryId(deliveryId);
        assertNotNull(r);
    }

    @Test
    void testSetLiveLocation() {
        Mockito.when(courierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setLiveLocation(deliveryId, role, "Test");
        Mockito.verify(courierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetAvRateCourier() {
        UUID courierId = UUID.randomUUID();
        Mockito.when(courierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getAvRateCourier(courierId, "courier");
        Mockito.verify(courierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetCourierByDeliveryId() {
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getCourierByDeliveryId(deliveryId, role);
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testSetDeliveryException() {
        Mockito.when(vendorOrCourierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setDeliveryException(deliveryId, role, "Fall");
        Mockito.verify(vendorOrCourierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetLocationOfDelivery() {
        Mockito.when(courierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getLocationOfDelivery(deliveryId, role);
        Mockito.verify(courierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetDeliveryEstimate() {
        Mockito.when(vendorOrCourierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getDeliveryEstimate(deliveryId, role);
        Mockito.verify(vendorOrCourierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testSetDeliveryEstimate() {
        Mockito.when(vendorOrCourierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.setDeliveryEstimate(deliveryId, role,
                OffsetDateTime.of(2024, 1, 1, 1,
                        1, 1, 1, ZoneOffset.ofHours(0)));

        Mockito.verify(vendorOrCourierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetRatingByDeliveryId() {
        Mockito.when(globalService.getDeliveryIdGetterGlobalService().getRatingByDeliveryId(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getRateByDeliveryId(deliveryId, role);
        Mockito.verify(deliveryIdGetterGlobalService).getRatingByDeliveryId(deliveryId);
        assertNotNull(r);
    }

    @Test
    void testGetPickUpTime() {
        Mockito.when(globalService.getAttributeGetterGlobalService().getPickUpTime(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getPickUpTime(deliveryId, role);
        Mockito.verify(attributeGetterGlobalService).getPickUpTime(deliveryId);
        assertNotNull(r);
    }

    @Test
    void testCallGetAllDeliveries() {
        UUID vendorId = UUID.randomUUID();
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getAllDeliveriesVendor(vendorId, "vendor");
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetAllDeliveriesCourier() {
        UUID courierID = UUID.randomUUID();
        Mockito.when(courierService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getAllDeliveriesCourier(courierID, "courier");
        Mockito.verify(courierService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testGetAllDeliveriesCustomer() {
        UUID customerID = UUID.randomUUID();
        Mockito.when(customerService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = deliveryController.getAllDeliveriesCustomer(customerID, "customer");
        Mockito.verify(customerService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void testSetRateOfDelivery() {
        Mockito.when(customerService.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>("", null, HttpStatus.OK));
        ResponseEntity<String> r = deliveryController.setRateOfDelivery(deliveryId, role, 1d);
        Mockito.verify(customerService).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    void getPickUpLocation2() {
        UUID deliveryId = UUID.randomUUID();
        String role = "courier";

        // Mock the behavior of courierService
        Mockito.when(courierService.checkAndHandle(Mockito.eq(role), Mockito.any()))
                .thenAnswer(invocation -> {
                    return ResponseEntity.ok("MockedResponse");
                });

        ResponseEntity<String> response = deliveryController.getPickUpLocation(deliveryId, role);

        assertEquals("MockedResponse", response.getBody());
    }
}