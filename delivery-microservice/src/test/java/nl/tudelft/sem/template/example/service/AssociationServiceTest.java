package nl.tudelft.sem.template.example.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

class AssociationServiceTest {

    private transient HttpServletRequest request;
    private transient AssociationService associationService;
    private RestaurantRepository restaurantRepository;
    private DeliveryRepository deliveryRepository;

    @BeforeEach
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
        restaurantRepository = Mockito.mock(RestaurantRepository.class);
        deliveryRepository = Mockito.mock(DeliveryRepository.class);

        associationService = new AssociationService(deliveryRepository, restaurantRepository);
    }

    @Test
    void testAdmin() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440001");
        when(request.getParameter("role")).thenReturn("admin");

        assertTrue(associationService.authorize(request));
    }

    @Test
    void testNotPatchRequest() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440001");
        when(request.getParameter("role")).thenReturn("vendor");
        when(request.getMethod()).thenReturn("GET");

        assertTrue(associationService.authorize(request));
    }

    @Test
    void notSensitiveEndpoint() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440001");
        when(request.getParameter("role")).thenReturn("vendor");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("example/endpoint/without/sensitive/data");

        assertTrue(associationService.authorize(request));
    }

    @Test
    void testVendorDeliveryAssociationNotFound() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440001");
        when(request.getParameter("role")).thenReturn("vendor");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("127.0.0.1:8082/delivery/550e8400-e29b-41d4-a716-446655440000/status/accept/?role=vendor");
        when(deliveryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertTrue(associationService.authorize(request));
    }

    @Test
    void testVendorDeliveryAssociationNotFoundInRestaurant() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440001");
        when(request.getParameter("role")).thenReturn("vendor");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("127.0.0.1:8082/delivery/550e8400-e29b-41d4-a716-446655440000/status/accept/?role=vendor");
        Delivery d = Mockito.mock(Delivery.class);
        when(deliveryRepository.findById(Mockito.any())).thenReturn(Optional.of(d));
        when(restaurantRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertTrue(associationService.authorize(request));
    }

    @Test
    void testVendorDeliveryAssociationFoundNotEqual() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440001");
        when(request.getParameter("role")).thenReturn("vendor");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("127.0.0.1:8082/delivery/550e8400-e29b-41d4-a716-446655440000/status/accept/?role=vendor");
        Delivery d = Mockito.mock(Delivery.class);
        Restaurant r = Mockito.mock(Restaurant.class);
        when(deliveryRepository.findById(Mockito.any())).thenReturn(Optional.of(d));
        UUID id = UUID.randomUUID();
        when(d.getRestaurantID()).thenReturn(id);
        when(restaurantRepository.findById(Mockito.any())).thenReturn(Optional.of(r));
        when(r.getVendorID()).thenReturn(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));


        assertFalse(associationService.authorize(request));
    }

    @Test
    void testVendorDeliveryAssociationFoundEqual() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440001");
        when(request.getParameter("role")).thenReturn("vendor");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("127.0.0.1:8082/delivery/550e8400-e29b-41d4-a716-446655440000/status/accept/?role=vendor");
        Delivery d = Mockito.mock(Delivery.class);
        Restaurant r = Mockito.mock(Restaurant.class);
        when(deliveryRepository.findById(Mockito.any())).thenReturn(Optional.of(d));
        UUID id = UUID.randomUUID();
        when(d.getRestaurantID()).thenReturn(id);
        when(restaurantRepository.findById(Mockito.any())).thenReturn(Optional.of(r));
        when(r.getVendorID()).thenReturn(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"));


        assertTrue(associationService.authorize(request));
    }

    @Test
    void testCourierDeliveryAssociationNotFound() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440001");
        when(request.getParameter("role")).thenReturn("courier");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("127.0.0.1:8082/delivery/550e8400-e29b-41d4-a716-446655440000/status/delivered/?role=courier");
        when(deliveryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertTrue(associationService.authorize(request));
    }

    @Test
    void testCourierDeliveryAssociationFoundNotEqual() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440002");
        when(request.getParameter("role")).thenReturn("courier");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("127.0.0.1:8082/delivery/550e8400-e29b-41d4-a716-446655440000/status/delivered/?role=courier");
        Delivery d = Mockito.mock(Delivery.class);
        when(deliveryRepository.findById(Mockito.any())).thenReturn(Optional.of(d));
        when(d.getCourierID()).thenReturn(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        assertFalse(associationService.authorize(request));
    }

    @Test
    void testCourierDeliveryAssociationFoundEqual() {
        when(request.getHeader("X-User-Id")).thenReturn("550e8400e29b41d4a716446655440000");
        when(request.getParameter("role")).thenReturn("courier");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("127.0.0.1:8082/delivery/550e8400-e29b-41d4-a716-446655440000/status/delivered/?role=courier");
        Delivery d = Mockito.mock(Delivery.class);
        when(deliveryRepository.findById(Mockito.any())).thenReturn(Optional.of(d));
        when(d.getCourierID()).thenReturn(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));

        assertTrue(associationService.authorize(request));
    }
}