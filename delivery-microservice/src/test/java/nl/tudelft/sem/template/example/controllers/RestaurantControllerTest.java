package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Restaurant;
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

class RestaurantControllerTest {


    @Mock
    VendorController vc = Mockito.mock(VendorController.class);

    @Mock
    CourierController cc = Mockito.mock(CourierController.class);

    @Mock
    AdminController ac = Mockito.mock(AdminController.class);

    @Mock
    GlobalController gc = Mockito.mock(GlobalController.class);

    RestaurantController sut = new RestaurantController(cc, vc, ac, gc);

    @BeforeEach
    public void setup() {
        sut.setVendorController(vc);
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
        verify(gc).getMaxDeliveryZone(id);
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
        Mockito.when(gc.getMaxDeliveryZone(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.getMaxDeliveryZone(deliveryId, role);
        Mockito.verify(gc).getMaxDeliveryZone(deliveryId);
        assertNotNull(r);
    }

    @Test
    public void testSetMaxDeliveryZone(){
        String role = "admin";
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(gc.setMaxDeliveryZone(restaurantId, 1d))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.setMaxDeliveryZone(restaurantId, role, 1d);
        Mockito.verify(gc).setMaxDeliveryZone(restaurantId, 1d);
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