package nl.tudelft.sem.template.example.service.roles;


import lombok.Getter;
import nl.tudelft.sem.model.Delivery;
import nl.tudelft.sem.template.example.database.DeliveryRepository;
import nl.tudelft.sem.template.example.service.VendorOrCourierFunctionalities.DeliveryEstimateService;
import nl.tudelft.sem.template.example.service.VendorOrCourierFunctionalities.DeliveryEventService;
import nl.tudelft.sem.template.example.service.VendorOrCourierFunctionalities.OrderToCourierService;
import nl.tudelft.sem.template.example.service.VendorOrCourierFunctionalities.PickUpEstimateVendorCourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Service that authorizes requests from vendors, or couriers.
 */
@Service
public class VendorOrCourierService implements RoleService {

    @Getter
    private final DeliveryEstimateService deliveryEstimateService;

    @Getter
    private final DeliveryEventService deliveryEventService;

    @Getter
    private final PickUpEstimateVendorCourierService pickUpEstimateVendorCourierService;

    @Getter
    private final OrderToCourierService orderToCourierService;


    /**
     * Constructor for the VendorOrCourierService.
     * @param deliveryEstimateService sub-service
     * @param deliveryEventService sub-service
     * @param pickUpEstimateVendorCourierService sub-service
     * @param orderToCourierService sub-service
     */
    @Autowired
    public VendorOrCourierService(DeliveryEstimateService deliveryEstimateService,
                                               DeliveryEventService deliveryEventService,
                                               PickUpEstimateVendorCourierService pickUpEstimateVendorCourierService,
                                                OrderToCourierService orderToCourierService) {
        this.deliveryEstimateService = deliveryEstimateService;
        this.deliveryEventService = deliveryEventService;
        this.pickUpEstimateVendorCourierService = pickUpEstimateVendorCourierService;
        this.orderToCourierService = orderToCourierService;
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
        final List<String> allowedRoles = List.of("admin", "vendor", "courier");
        if(allowedRoles.contains(role)) {
            return operation.get();
        }
        return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
    }
}
