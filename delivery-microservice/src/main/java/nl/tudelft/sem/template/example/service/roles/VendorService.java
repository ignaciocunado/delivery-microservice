package nl.tudelft.sem.template.example.service.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.model.Restaurant;

import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.database.RestaurantRepository;
import nl.tudelft.sem.template.example.service.UUIDGenerationService;
import nl.tudelft.sem.template.example.service.VendorFunctionalities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * Service that authorizes requests from vendors.
 */
@Service
public class VendorService implements RoleService {

    @Getter
    private final CourierToRestaurantService courierToRestaurantService;

    @Getter
    private final DeliveryIdGetterService deliveryIdGetterService;

    @Getter
    private final DeliveryManipulationService deliveryManipulationService;

    @Getter
    private final DeliveryStatusService deliveryStatusService;

    @Getter
    private final PickUpEstimateService pickUpEstimateService;

    @Getter
    private final RestaurantGetterService restaurantGetterService;

    @Autowired
    public VendorService(CourierToRestaurantService courierToRestaurantService, DeliveryIdGetterService deliveryIdGetterService,
                                      DeliveryManipulationService deliveryManipulationService, DeliveryStatusService deliveryStatusService,
                                      PickUpEstimateService pickUpEstimateService, RestaurantGetterService restaurantGetterService) {
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
