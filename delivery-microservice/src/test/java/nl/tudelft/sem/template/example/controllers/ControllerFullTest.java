package nl.tudelft.sem.template.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.authorization.AuthorizationFilter;
import nl.tudelft.sem.template.example.config.AppConfig;
import nl.tudelft.sem.template.example.config.AuthorizationFilterConfiguration;
import nl.tudelft.sem.template.example.config.SecurityConfig;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.adminFunctionalities.DeliveryManagerAdminService;
import nl.tudelft.sem.template.example.service.adminFunctionalities.RestaurantManagerAdminService;
import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryGettersCourierService;
import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryLocationCourierService;
import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryStatusCourierService;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalService;
import nl.tudelft.sem.template.example.service.externalCommunication.ExternalServiceMock;
import nl.tudelft.sem.template.example.service.handlers.AssociationHandler;
import nl.tudelft.sem.template.example.service.handlers.RoleHandler;
import nl.tudelft.sem.template.example.service.generation.UuidGenerationService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.AttributeGetterGlobalService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.DeliveryIdGetterGlobalService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.MaxDeliveryZoneService;
import nl.tudelft.sem.template.example.service.roles.*;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.*;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.DeliveryEstimateService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.DeliveryEventService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.OrderToCourierService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.PickUpEstimateVendorCourierService;
import nl.tudelft.sem.template.example.testRepositories.TestDeliveryRepository;
import nl.tudelft.sem.template.example.testRepositories.TestRestaurantRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ControllerFullTest {
    private transient AuthorizationFilter authorizationFilter;
    private transient AppConfig appConfig;
    private transient SecurityConfig securityConfig;
    private transient DeliveryController dc;
    private transient RestaurantController rc;
    private transient DeliveryRepository deliveryRepository;
    private transient RestaurantRepository restaurantRepository;
    private transient DeliveryManagerAdminService deliveryManagerAdminService;
    private transient RestaurantManagerAdminService restaurantManagerAdminService;
    private transient DeliveryGettersCourierService deliveryGettersCourierService;
    private transient DeliveryLocationCourierService deliveryLocationCourierService;
    private transient DeliveryStatusCourierService deliveryStatusCourierService;
    private transient ExternalService externalService;
    private transient AssociationHandler associationHandler;
    private transient RoleHandler roleHandler;
    private transient UuidGenerationService uuidGenerationService;
    private transient AttributeGetterGlobalService attributeGetterGlobalService;
    private transient DeliveryIdGetterGlobalService deliveryIdGetterGlobalService;
    private transient MaxDeliveryZoneService maxDeliveryZoneService;
    private transient AdminService adminService;
    private transient CourierService courierService;
    private transient CustomerService customerService;
    private transient GlobalService globalService;
    private transient VendorOrCourierService vendorOrCourierService;
    private transient VendorService vendorService;
    private transient CourierToRestaurantService courierToRestaurantService;
    private transient DeliveryIdGetterService deliveryIdGetterService;
    private transient DeliveryManipulationService deliveryManipulationService;
    private transient DeliveryStatusService deliveryStatusService;
    private transient PickUpEstimateService pickUpEstimateService;
    private transient RestaurantGetterService restaurantGetterService;
    private transient DeliveryEstimateService deliveryEstimateService;
    private transient DeliveryEventService deliveryEventService;
    private transient OrderToCourierService orderToCourierService;
    private transient PickUpEstimateVendorCourierService pickUpEstimateVendorCourierService;

    @Mock
    private transient RestTemplate restTemplate;


    @BeforeEach
    void setUp() {
        // 1. no args
        deliveryRepository = new TestDeliveryRepository();
        restaurantRepository = new TestRestaurantRepository();
        uuidGenerationService = new UuidGenerationService();
        externalService = mock(ExternalServiceMock.class);

        // 2. only repositories, external and uuid
        attributeGetterGlobalService = new AttributeGetterGlobalService(restaurantRepository, deliveryRepository);
        deliveryIdGetterGlobalService = new DeliveryIdGetterGlobalService(restaurantRepository, deliveryRepository);
        maxDeliveryZoneService = new MaxDeliveryZoneService(restaurantRepository, deliveryRepository);
        deliveryManagerAdminService = new DeliveryManagerAdminService(restaurantRepository, deliveryRepository);
        restaurantManagerAdminService = new RestaurantManagerAdminService(restaurantRepository, uuidGenerationService);
        deliveryGettersCourierService = new DeliveryGettersCourierService(deliveryRepository);
        deliveryStatusCourierService = new DeliveryStatusCourierService(deliveryRepository);
        associationHandler = new AssociationHandler(deliveryRepository, restaurantRepository);
        roleHandler = new RoleHandler(externalService);
        customerService = new CustomerService(deliveryRepository);
        deliveryEstimateService = new DeliveryEstimateService(deliveryRepository);
        deliveryEventService = new DeliveryEventService(deliveryRepository);
        orderToCourierService = new OrderToCourierService(deliveryRepository);
        courierToRestaurantService = new CourierToRestaurantService(restaurantRepository, deliveryRepository,
                uuidGenerationService);
        deliveryLocationCourierService = new DeliveryLocationCourierService(deliveryRepository, restaurantRepository,
                externalService);
        deliveryIdGetterService = new DeliveryIdGetterService(restaurantRepository, deliveryRepository,
                uuidGenerationService);
        deliveryManipulationService = new DeliveryManipulationService(restaurantRepository, deliveryRepository,
                uuidGenerationService);
        deliveryStatusService = new DeliveryStatusService(restaurantRepository, deliveryRepository,
                uuidGenerationService);
        pickUpEstimateService = new PickUpEstimateService(restaurantRepository, deliveryRepository,
                uuidGenerationService);
        restaurantGetterService = new RestaurantGetterService(restaurantRepository, deliveryRepository,
                uuidGenerationService);
        pickUpEstimateVendorCourierService = new PickUpEstimateVendorCourierService(deliveryRepository);

        // 3. other
        courierService = new CourierService(deliveryGettersCourierService, deliveryLocationCourierService,
                deliveryStatusCourierService);
        globalService = new GlobalService(attributeGetterGlobalService, deliveryIdGetterGlobalService,
                maxDeliveryZoneService);
        vendorService = new VendorService(courierToRestaurantService, deliveryIdGetterService,
                deliveryManipulationService, deliveryStatusService, pickUpEstimateService, restaurantGetterService);
        vendorOrCourierService = new VendorOrCourierService(deliveryEstimateService, deliveryEventService,
                pickUpEstimateVendorCourierService, orderToCourierService);

        authorizationFilter = new AuthorizationFilter(roleHandler, associationHandler);
        appConfig = new AppConfig();
        securityConfig = new SecurityConfig(new AuthorizationFilterConfiguration(roleHandler, associationHandler));
        adminService = new AdminService(restaurantManagerAdminService, deliveryManagerAdminService);

        dc = new DeliveryController(courierService, vendorService, globalService, vendorOrCourierService,
                customerService, adminService);
        rc = new RestaurantController(vendorService, adminService, globalService);
    }

    @Test
    public void ScenarioOne() {
        // constants
        final UUID vendor1 = UUID.randomUUID();
        final UUID courier1 = UUID.randomUUID();
        final UUID courier2 = UUID.randomUUID();
        final UUID restaurant1 = UUID.randomUUID();
        final UUID delivery1 = UUID.randomUUID();
        final UUID delivery1Order = UUID.randomUUID();
        final UUID customer1 = UUID.randomUUID();
        final Double maxDeliveryZone = 10.0;
        final OffsetDateTime time1 = OffsetDateTime.now();
        final OffsetDateTime time2 = time1.plusSeconds(420);

        Restaurant restaurant = new Restaurant(restaurant1, vendor1, List.of(courier1), maxDeliveryZone);
        ResponseEntity<Restaurant> r = rc.createRestaurant("admin", restaurant);
        assertEquals(restaurant, r.getBody());
        ObjectMapper o = new ObjectMapper();
        String js = "";
        try {
            js = o.writeValueAsString(restaurant);
        } catch (Exception e) {
            assert (false);
        }
        assertEquals(js, rc.getRest(restaurant1, "admin").getBody());

        // test adding and removing couriers
        ResponseEntity<Void> rr = rc.addCourierToRest(restaurant1, courier2, "vendor");
        assertNotNull(rr);
        assertTrue(restaurantRepository.findById(restaurant1).get().getCourierIDs().contains(courier1));
        assertTrue(restaurantRepository.findById(restaurant1).get().getCourierIDs().contains(courier2));

        rr = rc.removeCourierRest(restaurant1, courier1, "vendor");
        assertFalse(restaurantRepository.findById(restaurant1).get().getCourierIDs().contains(courier1));
        assertNotNull(rr);

        // now a new delivery appears!
        Delivery delivery = new Delivery().deliveryID(delivery1).orderID(delivery1Order).customerID(customer1)
                .restaurantID(restaurant1).status("pending");
        dc.createDelivery("admin", delivery);
        assertTrue(deliveryRepository.findById(delivery1).isPresent());
        System.out.println(deliveryRepository.findAll());

        // then it gets assigned to a courier
        ResponseEntity<UUID> a = dc.assignOrderToCourier(courier2, delivery1, "vendor");
        assertEquals(delivery1, a.getBody());
        assertEquals(courier2, deliveryRepository.findById(delivery1).get().getCourierID());
        ResponseEntity<?> aa = dc.acceptDelivery(delivery1, "admin");
        System.out.println(aa);
        assertEquals(HttpStatus.OK, aa.getStatusCode());
        assertEquals("accepted", deliveryRepository.findById(delivery1).get().getStatus());

        // guess how long till pick-up
        ResponseEntity<String> b = dc.setPickUpTime(delivery1, "courier", time1.toString());
        assertEquals(time1.toString(), b.getBody());

        // then the courier picks it up
        ResponseEntity<String> es = dc.setDeliveryEstimate(delivery1, "courier", time1);
        assertEquals(HttpStatus.OK, es.getStatusCode());
        assertEquals(time1.toString(), es.getBody());
        deliveryRepository.findById(delivery1);
        ResponseEntity<?> s = dc.editStatusDelivery(delivery1, "vendor", "given to courier");
        assertEquals(time1, deliveryRepository.findById(delivery1).get().getDeliveryTimeEstimate());
        assertEquals(HttpStatus.OK, s.getStatusCode());

        // courier slips on the ice and fucking dies
        ResponseEntity<String> er = dc.setDeliveryException(delivery1, "courier", "courier fell and died");
        assertEquals("200 OK", er.getBody());
        ResponseEntity<Integer> ir = dc.setDeliveryDelay(delivery1, "vendor", 420);
        assertEquals(time2, dc.getDeliveryEstimate(delivery1, "vendor").getBody());
        assertEquals(420, ir.getBody());
        assertEquals(420, dc.getDeliveryDelay(delivery1, "admin").getBody());

        assertEquals(List.of(restaurant1), rc.getVendorRest(vendor1, "admin").getBody());

        ResponseEntity<String> rrr = dc.setRateOfDelivery(delivery1, "customer", 0.42);
        assertEquals("200 OK", rrr.getBody());
        assertEquals(0.42, dc.getAvRateCourier(dc.getCourierByDeliveryId(delivery1, "admin").getBody(),
                "admin").getBody());

        ResponseEntity<?> f = dc.deliveryIdDone(delivery1, "courier");
        assertEquals("Delivery marked as delivered!", f.getBody());
        assertEquals("delivered", dc.getDeliveyById(delivery1, "admin").getBody().getStatus());
    }


    @Test
    public void ScenarioTwo() {
        // here i kinda gave up on pretending there's a story
        final UUID vendor1 = UUID.randomUUID();
        final UUID vendor2 = UUID.randomUUID();
        final UUID courier1 = UUID.randomUUID();
        final UUID courier2 = UUID.randomUUID();
        final UUID restaurant1 = UUID.randomUUID();
        final UUID restaurant2 = UUID.randomUUID();
        final UUID delivery1 = UUID.randomUUID();
        final UUID delivery1Order = UUID.randomUUID();
        final UUID delivery2 = UUID.randomUUID();
        final UUID delivery3 = UUID.randomUUID();
        final UUID customer1 = UUID.randomUUID();
        final UUID customer2 = UUID.randomUUID();
        final Double maxDeliveryZone = 10.0;
        final OffsetDateTime time1 = OffsetDateTime.now();
        final OffsetDateTime time2 = time1.plusSeconds(42);
        final String location1 = "123.456";
        final String location2 = "987.426";

        Restaurant restaurant = new Restaurant(restaurant1, vendor1, List.of(courier1), maxDeliveryZone);
        rc.createRestaurant("admin", restaurant);
        Restaurant otherestaurant = new Restaurant(restaurant2, vendor2, List.of(courier2), maxDeliveryZone);
        rc.createRestaurant("admin", otherestaurant);

        Delivery delivery = new Delivery().deliveryID(delivery1).orderID(delivery1Order).customerID(customer1)
                .restaurantID(restaurant1).status("pending");
        dc.createDelivery("vendor", delivery);
        Delivery otherdelivery = new Delivery().deliveryID(delivery2).orderID(UUID.randomUUID()).customerID(customer2)
                .restaurantID(restaurant2).status("pending");
        dc.createDelivery("vendor", otherdelivery);
        Delivery thirdelivery = new Delivery().deliveryID(delivery3).orderID(UUID.randomUUID()).customerID(customer2)
                .restaurantID(restaurant1).status("pending").pickupTimeEstimate(time1).pickedUpTime(time1);
        ResponseEntity<Delivery> dd = dc.createDelivery("vendor", thirdelivery);
        assertEquals(thirdelivery, dd.getBody());

        when(externalService.getOrderDestination(customer1, delivery.getOrderID())).thenReturn(location1);

        ResponseEntity<String> d = dc.getLocationOfDelivery(delivery1, "admin");
        assertEquals("location: " + location1, d.getBody());

        when(externalService.getRestaurantLocation(vendor1)).thenReturn(location1);
        d = dc.getPickUpLocation(delivery1, "admin");
        assertEquals("location: " + location1, d.getBody());

        ResponseEntity<?> l = dc.setLiveLocation(delivery1, "courier", location2);
        assertEquals("200 OK", l.getBody());
        assertEquals(location2, deliveryRepository.findById(delivery1).get().getLiveLocation());

        ResponseEntity<?> g = dc.getCustomerByDeliveryId(delivery1, "admin");
        assertEquals(customer1, g.getBody());

        ResponseEntity<?> rj = dc.rejectDelivery(delivery2, "vendor");
        assertEquals(HttpStatus.OK, rj.getStatusCode());
        assertEquals("rejected", deliveryRepository.findById(delivery2).get().getStatus());

        dc.assignOrderToCourier(courier1, delivery1, "vendor");
        dc.assignOrderToCourier(courier1, delivery2, "vendor");
        ResponseEntity<List<UUID>> ll = dc.getAllDeliveriesCourier(courier1, "admin");
        assertTrue(ll.getBody().contains(delivery1));
        assertTrue(ll.getBody().contains(delivery2));
        assertFalse(ll.getBody().contains(delivery3));

        ResponseEntity<List<UUID>> lll = dc.getAllDeliveriesVendor(vendor1, "admin");
        assertTrue(lll.getBody().contains(delivery1));
        assertFalse(lll.getBody().contains(delivery2));
        assertTrue(lll.getBody().contains(delivery3));
        ResponseEntity<UUID> cri = dc.setRestIdOfDelivery(delivery2, "admin", restaurant1);
        assertEquals(HttpStatus.OK, cri.getStatusCode());
        assertEquals(restaurant1, deliveryRepository.findById(delivery2).get().getRestaurantID());

        dc.editStatusDelivery(delivery1, "vendor", "given to courier");
        OffsetDateTime actuallyPickedUpFromRestaurantByCourier = dc.getDeliveyById(delivery1, "admin")
                .getBody().getPickedUpTime();
        assertEquals(actuallyPickedUpFromRestaurantByCourier,
                dc.getPickUpEstimateDeliveryId(delivery1, "admin").getBody());

        assertEquals(maxDeliveryZone, rc.getMaxDeliveryZone(restaurant1, "admin").getBody());

        ResponseEntity<String> lvl = dc.setLiveLocation(delivery1, "courier", location2);
        assertEquals("200 OK", lvl.getBody());
        ResponseEntity<String> gll = dc.getLiveLocation(delivery1, "admin");
        assertEquals(location2, gll.getBody());
    }

    @Test
    void WhenStatusIsSetToPickedUpByCourierTheTimestampForPickedUpChangesProperly() {
        UUID res = UUID.randomUUID();
        UUID ven = UUID.randomUUID();
        UUID cou = UUID.randomUUID();
        UUID del = UUID.randomUUID();

        Restaurant restaurant = new Restaurant(res, ven, List.of(cou), 1.0);
        rc.createRestaurant("admin", restaurant);
        Delivery delivery = new Delivery().deliveryID(del).orderID(UUID.randomUUID()).customerID(UUID.randomUUID())
                .restaurantID(res).status("pending");
        dc.createDelivery("vendor", delivery);

        assertNull(dc.getDeliveyById(del, "admin").getBody().getPickedUpTime());
        dc.editStatusDelivery(del, "vendor", "accepted");
        assertNull(dc.getDeliveyById(del, "admin").getBody().getPickedUpTime());

        dc.editStatusDelivery(del, "vendor", "given to courier");
        assertNotNull(dc.getDeliveyById(del, "admin").getBody().getPickedUpTime());
        OffsetDateTime actualPickUpTime = dc.getDeliveyById(del, "admin").getBody().getPickedUpTime();
        assertEquals(actualPickUpTime, dc.getPickUpEstimateDeliveryId(del, "admin").getBody());
    }

    @AfterAll
    static void gracefulExit() {
        System.out.println("\033[97;45m Thank you for your patience, \033[38;5;197m\033[48;5;16m <3 \033[0m");
    }

}
