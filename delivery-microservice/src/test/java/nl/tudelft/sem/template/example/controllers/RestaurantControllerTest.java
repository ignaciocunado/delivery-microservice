package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.GlobalFunctionalities.AttributeGetterGlobalService;
import nl.tudelft.sem.template.example.service.GlobalFunctionalities.DeliveryIdGetterGlobalService;
import nl.tudelft.sem.template.example.service.GlobalFunctionalities.MaxDeliveryZoneService;
import nl.tudelft.sem.template.example.service.roles.AdminService;
import nl.tudelft.sem.template.example.service.roles.CourierService;
import nl.tudelft.sem.template.example.service.roles.GlobalService;
import nl.tudelft.sem.template.example.service.roles.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestaurantControllerTest {


    @Mock
    VendorService vc = Mockito.mock(VendorService.class);

    @Mock
    CourierService cc = Mockito.mock(CourierService.class);

    @Mock
    AdminService ac = Mockito.mock(AdminService.class);

    @Mock
    GlobalService gc = Mockito.mock(GlobalService.class);

    @Mock
    AttributeGetterGlobalService attributeGetterGlobalService= Mockito.mock(AttributeGetterGlobalService.class);

    @Mock
    DeliveryIdGetterGlobalService deliveryIdGetterGlobalService = Mockito.mock(DeliveryIdGetterGlobalService.class);

    @Mock
    MaxDeliveryZoneService maxDeliveryZoneService = Mockito.mock(MaxDeliveryZoneService.class);

    RestaurantController sut = new RestaurantController(cc, vc, ac, gc);

    @BeforeEach
    public void setup() {
        sut.setVendorService(vc);
        when(gc.getMaxDeliveryZoneService()).thenReturn(maxDeliveryZoneService);
        when(gc.getAttributeGetterGlobalService()).thenReturn(attributeGetterGlobalService);
        when(gc.getDeliveryIdGetterGlobalService()).thenReturn(deliveryIdGetterGlobalService);
    }

    @Test
    public void testCallAdd() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        sut.addCourierToRest(id1, id2, "a");
        verify(vc).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    public void testCallRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        sut.removeCourierRest(id1, id2, "a");
        verify(vc).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    public void testCreateRestaurant() {
        Restaurant restaurant = new Restaurant();
        sut.createRestaurant("admin", restaurant);

        verify(ac).checkAndHandle(Mockito.eq("admin"), Mockito.any());
    }

    @Test
    public void testCallMaxZone() {
        UUID id = UUID.randomUUID();
        sut.getMaxDeliveryZone(id, "a");
        verify(maxDeliveryZoneService).getMaxDeliveryZone(id);
    }

    @Test
    public void testCallGetRest() {
        UUID id = UUID.randomUUID();
        String s = "";

        sut.getRest(id, s);
        verify(vc).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    public void testAddCourierToRest(){
        UUID customerID = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(vc.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.addCourierToRest(restaurantId, customerID, "customer");
        Mockito.verify(vc).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    public void testRemoveCourierRest(){
        UUID customerID = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(vc.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.removeCourierRest(restaurantId, customerID, "customer");
        Mockito.verify(vc).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    public void testCreateRest(){
        String role = "admin";
        Restaurant restaurant = new Restaurant();
        Mockito.when(ac.checkAndHandle(Mockito.eq(role), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.createRestaurant(role, restaurant);
        Mockito.verify(ac).checkAndHandle(Mockito.eq(role), Mockito.any());
        assertNotNull(r);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    public void testGetMaxDeliveryZone(){
        String role = "admin";
        UUID deliveryId = UUID.randomUUID();
        Mockito.when(gc.getMaxDeliveryZoneService().getMaxDeliveryZone(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.getMaxDeliveryZone(deliveryId, role);
        Mockito.verify(maxDeliveryZoneService).getMaxDeliveryZone(deliveryId);
        assertNotNull(r);
    }

    @Test
    public void testSetMaxDeliveryZone(){
        String role = "admin";
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(gc.getMaxDeliveryZoneService().setMaxDeliveryZone(restaurantId, 1d))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.setMaxDeliveryZone(restaurantId, role, 1d);
        Mockito.verify(maxDeliveryZoneService).setMaxDeliveryZone(restaurantId, 1d);
        assertNotNull(r);
    }

    @Test
    public void testGetRest(){
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(vc.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.getRest(restaurantId, "customer");
        Mockito.verify(vc).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

    @Test
    public void testGetVendorRest(){
        UUID vendorId = UUID.randomUUID();
        Mockito.when(vc.checkAndHandle(Mockito.any(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.getVendorRest(vendorId, "customer");
        Mockito.verify(vc).checkAndHandle(Mockito.any(), Mockito.any());
        assertNotNull(r);
    }

}