package nl.tudelft.sem.template.example.service.roles;


import lombok.Getter;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.DeliveryEstimateService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.DeliveryEventService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.OrderToCourierService;
import nl.tudelft.sem.template.example.service.vendorOrCourierFunctionalities.PickUpEstimateVendorCourierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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
