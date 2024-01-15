package nl.tudelft.sem.template.example.service.roles;

import java.util.List;
import java.util.function.Supplier;

import lombok.Getter;
import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryGettersCourierService;
import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryLocationCourierService;
import nl.tudelft.sem.template.example.service.courierFunctionalities.DeliveryStatusCourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service that authorizes requests from couriers.
 */
@Service
public class CourierService implements RoleService {

    @Getter
    private final transient DeliveryGettersCourierService deliveryGettersCourierService;

    @Getter
    private final transient DeliveryLocationCourierService deliveryLocationCourierService;

    @Getter
    private final transient DeliveryStatusCourierService deliveryStatusCourierService;

    /**
     * Construct a new CourierService.
     * @param deliveryGettersCourierService Delivery getters.
     * @param deliveryLocationCourierService Delivery location methods.
     * @param deliveryStatusCourierService Delivery status methods.
     */
    @Autowired
    public CourierService(DeliveryGettersCourierService deliveryGettersCourierService,
                          DeliveryLocationCourierService deliveryLocationCourierService,
                          DeliveryStatusCourierService deliveryStatusCourierService) {
        this.deliveryGettersCourierService = deliveryGettersCourierService;
        this.deliveryLocationCourierService = deliveryLocationCourierService;
        this.deliveryStatusCourierService = deliveryStatusCourierService;
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
        final List<String> allowedRoles = List.of("admin", "courier");
        if(allowedRoles.contains(role)) {
            return operation.get();
        }
        return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
    }
}
