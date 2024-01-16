package nl.tudelft.sem.template.example.service.roles;

import java.util.List;
import java.util.function.Supplier;

import lombok.Getter;

import nl.tudelft.sem.template.example.service.vendorFunctionalities.CourierToRestaurantService;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.DeliveryIdGetterService;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.DeliveryManipulationService;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.RestaurantGetterService;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.DeliveryStatusService;
import nl.tudelft.sem.template.example.service.vendorFunctionalities.PickUpEstimateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service that authorizes requests from vendors.
 */
@Getter
@Service
public class VendorService implements RoleService {

    private final transient CourierToRestaurantService courierToRestaurantService;

    private final transient DeliveryIdGetterService deliveryIdGetterService;

    private final transient DeliveryManipulationService deliveryManipulationService;

    private final transient DeliveryStatusService deliveryStatusService;

    private final transient PickUpEstimateService pickUpEstimateService;

    private final transient RestaurantGetterService restaurantGetterService;

    /**
     * Constructor for the VendorService.
     * @param courierToRestaurantService sub-service
     * @param deliveryIdGetterService sub-service
     * @param deliveryManipulationService sub-service
     * @param deliveryStatusService sub-service
     * @param pickUpEstimateService sub-service
     * @param restaurantGetterService sub-service
     */
    @Autowired
    public VendorService(CourierToRestaurantService courierToRestaurantService,
                         DeliveryIdGetterService deliveryIdGetterService,
                          DeliveryManipulationService deliveryManipulationService,
                         DeliveryStatusService deliveryStatusService,
                                      PickUpEstimateService pickUpEstimateService,
                         RestaurantGetterService restaurantGetterService) {
        this.courierToRestaurantService = courierToRestaurantService;
        this.deliveryIdGetterService = deliveryIdGetterService;
        this.deliveryManipulationService = deliveryManipulationService;
        this.deliveryStatusService = deliveryStatusService;
        this.pickUpEstimateService = pickUpEstimateService;
        this.restaurantGetterService = restaurantGetterService;
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
        final List<String> allowedRoles = List.of("admin", "vendor");
        if(allowedRoles.contains(role)) {
            return operation.get();
        }
        return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
    }

}
