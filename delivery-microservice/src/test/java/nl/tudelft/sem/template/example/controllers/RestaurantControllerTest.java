package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.adminFunctionalities.RestaurantManagerAdminService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.AttributeGetterGlobalService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.DeliveryIdGetterGlobalService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.MaxDeliveryZoneService;
import nl.tudelft.sem.template.example.service.roles.AdminService;
import nl.tudelft.sem.template.example.service.roles.GlobalService;
import nl.tudelft.sem.template.example.service.roles.VendorService;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.CourierToRestaurantService;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.RestaurantGetterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerTest {

    @Mock
    VendorService vendorService = Mockito.mock(VendorService.class);

    @Mock
    AdminService adminService = Mockito.mock(AdminService.class);

    @Mock
    GlobalService globalService = Mockito.mock(GlobalService.class);

    @Mock
    RestaurantManagerAdminService restaurantManagerAdminService = Mockito.mock(RestaurantManagerAdminService.class);

    @Mock
    AttributeGetterGlobalService attributeGetterGlobalService = Mockito.mock(AttributeGetterGlobalService.class);

    @Mock
    DeliveryIdGetterGlobalService deliveryIdGetterGlobalService = Mockito.mock(DeliveryIdGetterGlobalService.class);

    @Mock
    MaxDeliveryZoneService maxDeliveryZoneService = Mockito.mock(MaxDeliveryZoneService.class);


    @Captor
    private transient ArgumentCaptor<Supplier<ResponseEntity<Void>>> functionArgumentCaptor;

    RestaurantController sut;

    @BeforeEach
    public void setup() {
        sut = new RestaurantController(vendorService, adminService, globalService);


    }

    @Test
    public void testCallAdd() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        sut.addCourierToRest(id1, id2, "a");
        verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    public void testCallRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        sut.removeCourierRest(id1, id2, "a");
        verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    public void testCreateRestaurant() {
        Restaurant restaurant = new Restaurant();
        sut.createRestaurant("admin", restaurant);

        verify(adminService).checkAndHandle(Mockito.eq("admin"), Mockito.any());
    }

    @Test
    public void testCallMaxZone() {
        UUID id = UUID.randomUUID();
        when(globalService.getMaxDeliveryZoneService()).thenReturn(maxDeliveryZoneService);
        sut.getMaxDeliveryZone(id, "a");

        verify(maxDeliveryZoneService).getMaxDeliveryZone(id);
    }

    @Test
    public void testCallGetRest() {
        UUID id = UUID.randomUUID();
        String s = "";

        sut.getRest(id, s);
        verify(vendorService).checkAndHandle(Mockito.any(), Mockito.any());
    }

    @Test
    public void testAddCourierToRest() {
        UUID customerID = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), functionArgumentCaptor.capture()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Mockito.when(vendorService.getCourierToRestaurantService())
                .thenReturn(Mockito.mock(CourierToRestaurantService.class));
        ResponseEntity<?> r = sut.addCourierToRest(restaurantId, customerID, "customer");
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), functionArgumentCaptor.capture());

        functionArgumentCaptor.getValue().get();
        Mockito.verify(vendorService).getCourierToRestaurantService();

        assertNotNull(r);
    }

    @Test
    public void testRemoveCourierRest() {
        UUID customerID = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), functionArgumentCaptor.capture()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Mockito.when(vendorService.getCourierToRestaurantService())
                .thenReturn(Mockito.mock(CourierToRestaurantService.class));
        ResponseEntity<?> r = sut.removeCourierRest(restaurantId, customerID, "customer");
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), functionArgumentCaptor.capture());

        functionArgumentCaptor.getValue().get();
        Mockito.verify(vendorService).getCourierToRestaurantService();

        assertNotNull(r);
    }

    @Test
    public void testCreateRest() {
        String role = "admin";
        Restaurant restaurant = new Restaurant();
        Mockito.when(adminService.checkAndHandle(Mockito.eq(role), functionArgumentCaptor.capture()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Mockito.when(adminService.getRestaurantManagerAdminService())
                .thenReturn(Mockito.mock(RestaurantManagerAdminService.class));
        ResponseEntity<?> r = sut.createRestaurant(role, restaurant);
        Mockito.verify(adminService).checkAndHandle(Mockito.eq(role), functionArgumentCaptor.capture());

        functionArgumentCaptor.getValue().get();
        Mockito.verify(adminService).getRestaurantManagerAdminService();

        assertNotNull(r);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    public void testGetMaxDeliveryZone() {
        String role = "admin";
        UUID deliveryId = UUID.randomUUID();

        when(globalService.getMaxDeliveryZoneService()).thenReturn(maxDeliveryZoneService);

        Mockito.when(globalService.getMaxDeliveryZoneService().getMaxDeliveryZone(deliveryId))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.getMaxDeliveryZone(deliveryId, role);
        Mockito.verify(maxDeliveryZoneService).getMaxDeliveryZone(deliveryId);

        assertNotNull(r);
    }

    @Test
    public void testSetMaxDeliveryZone() {
        String role = "admin";
        UUID restaurantId = UUID.randomUUID();

        when(globalService.getMaxDeliveryZoneService()).thenReturn(maxDeliveryZoneService);

        Mockito.when(globalService.getMaxDeliveryZoneService().setMaxDeliveryZone(restaurantId, 1d))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        ResponseEntity<?> r = sut.setMaxDeliveryZone(restaurantId, role, 1d);
        Mockito.verify(maxDeliveryZoneService).setMaxDeliveryZone(restaurantId, 1d);

        assertNotNull(r);
    }

    @Test
    public void testGetRest() {
        UUID restaurantId = UUID.randomUUID();
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), functionArgumentCaptor.capture()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Mockito.when(vendorService.getRestaurantGetterService())
                .thenReturn(Mockito.mock(RestaurantGetterService.class));
        ResponseEntity<?> r = sut.getRest(restaurantId, "customer");
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), functionArgumentCaptor.capture());

        functionArgumentCaptor.getValue().get();
        Mockito.verify(vendorService).getRestaurantGetterService();

        assertNotNull(r);
    }

    @Test
    public void testGetVendorRest() {
        UUID vendorId = UUID.randomUUID();
        Mockito.when(vendorService.checkAndHandle(Mockito.any(), functionArgumentCaptor.capture()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        Mockito.when(vendorService.getRestaurantGetterService())
                .thenReturn(Mockito.mock(RestaurantGetterService.class));
        ResponseEntity<?> r = sut.getVendorRest(vendorId, "customer");
        Mockito.verify(vendorService).checkAndHandle(Mockito.any(), functionArgumentCaptor.capture());

        functionArgumentCaptor.getValue().get();
        Mockito.verify(vendorService).getRestaurantGetterService();

        assertNotNull(r);
    }

}