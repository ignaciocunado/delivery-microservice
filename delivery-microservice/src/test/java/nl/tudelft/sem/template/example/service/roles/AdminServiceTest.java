package nl.tudelft.sem.template.example.service.roles;

import nl.tudelft.sem.model.Restaurant;
import nl.tudelft.sem.template.example.service.adminFunctionalities.DeliveryManagerAdminService;
import nl.tudelft.sem.template.example.service.adminFunctionalities.RestaurantManagerAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests functionality of the Admin Controller method implementations.
 */
public class AdminServiceTest {

    private transient AdminService sut;

    private final List<String> allowedRoles = List.of("admin");

    private final List<String> disallowedRoles = List.of("vendor", "courier", "customer", "sudo", "v", "c", "a", "");

    @BeforeEach
    void setUp() {
        RestaurantManagerAdminService restaurantManagerAdminService = Mockito.mock(RestaurantManagerAdminService.class);
        DeliveryManagerAdminService deliveryManagerAdminService = Mockito.mock(DeliveryManagerAdminService.class);
        sut = new AdminService(restaurantManagerAdminService, deliveryManagerAdminService);
    }

    @Test
    void testCheckAndHandleGoodWeather() {
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : allowedRoles) {
            ResponseEntity<Restaurant> response = sut.checkAndHandle(roleToTest, operation);

            assertEquals(
                    HttpStatus.OK,
                    response.getStatusCode()
            );
        }
    }

    @Test
    void testCheckAndHandleBadWeather() {
        Supplier<ResponseEntity<Restaurant>> operation = () -> new ResponseEntity<>(HttpStatus.OK);

        for (final String roleToTest : disallowedRoles) {
            ResponseEntity<Restaurant> response = sut.checkAndHandle(roleToTest, operation);

            assertEquals(
                    HttpStatus.UNAUTHORIZED,
                    response.getStatusCode()
            );
        }
    }
}
