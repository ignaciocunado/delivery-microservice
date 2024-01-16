package nl.tudelft.sem.template.example.service.roles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import java.util.function.Supplier;

import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryGettersCourierService;
import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryLocationCourierService;
import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryStatusCourierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CourierServiceTest {

    private transient CourierService sut;

    private final transient String role = "courier";

    @BeforeEach
    void setUp() {
        // Create authorization / impl. container services
        DeliveryGettersCourierService deliveryGettersCourierService = Mockito.mock(DeliveryGettersCourierService.class);
        DeliveryStatusCourierService deliveryStatusCourierService = Mockito.mock(DeliveryStatusCourierService.class);
        DeliveryLocationCourierService deliveryLocationCourierService = Mockito.mock(
                DeliveryLocationCourierService.class);

        sut = new CourierService(deliveryGettersCourierService, deliveryLocationCourierService,
                deliveryStatusCourierService);
    }

    @Test
    public void checkAndHandleReturnsOk() {
        Supplier<ResponseEntity<String>> operation = () -> new ResponseEntity<>("Success", HttpStatus.OK);

        ResponseEntity<String> result = sut.checkAndHandle(role, operation);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success", result.getBody());
    }

    @Test
    public void checkAndHandleReturnsOk2() {
        Supplier<ResponseEntity<String>> operation = () -> new ResponseEntity<>("Success", HttpStatus.OK);

        ResponseEntity<String> result = sut.checkAndHandle("admin", operation);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success", result.getBody());
    }

    @Test
    public void checkAndHandleReturnsUnauthorized() {
        String role = "non-courier";
        Supplier<ResponseEntity<String>> operation = () -> new ResponseEntity<>("Success", HttpStatus.OK);

        ResponseEntity<String> result = sut.checkAndHandle(role, operation);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }
}