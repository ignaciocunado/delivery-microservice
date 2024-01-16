package nl.tudelft.sem.template.example.service.roles;

import lombok.Getter;
import nl.tudelft.sem.template.example.service.adminFunctionalities.DeliveryManagerAdminService;
import nl.tudelft.sem.template.example.service.adminFunctionalities.RestaurantManagerAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

/**
 * Service that authorizes requests from admins.
 */
@Getter
@Service
public class AdminService implements RoleService {

    private final transient RestaurantManagerAdminService restaurantManagerAdminService;
    private final transient DeliveryManagerAdminService deliveryManagerAdminService;

    /**
     * Construct a new Admin Service.
     * @param restaurantManagerAdminService Restaurant manager.
     */
    @Autowired
    public AdminService(RestaurantManagerAdminService restaurantManagerAdminService,
                        DeliveryManagerAdminService deliveryManagerAdminService) {
        this.restaurantManagerAdminService = restaurantManagerAdminService;
        this.deliveryManagerAdminService = deliveryManagerAdminService;
    }

    /**
     * Check the role and handle it further.
     * @param role the role of the user
     * @param operation the method that should be called
     * @param <T> the passed param
     * @return the response type obj
     */
    @Override
    public <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation) {
        if(!role.equals("admin")) {
            return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
        }
        return operation.get();
    }
}
