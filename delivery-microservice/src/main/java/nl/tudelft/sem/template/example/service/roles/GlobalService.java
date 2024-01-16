package nl.tudelft.sem.template.example.service.roles;

import lombok.Getter;
import nl.tudelft.sem.template.example.service.globalFunctionalities.AttributeGetterGlobalService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.DeliveryIdGetterGlobalService;
import nl.tudelft.sem.template.example.service.globalFunctionalities.MaxDeliveryZoneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.function.Supplier;

/**
 * Service that authorizes requests from any user.
 */
@Service
public class GlobalService {

    @Getter
    private final AttributeGetterGlobalService attributeGetterGlobalService;

    @Getter
    private final DeliveryIdGetterGlobalService deliveryIdGetterGlobalService;

    @Getter
    private final MaxDeliveryZoneService maxDeliveryZoneService;

    /**
     * Constructor for GlobalService.
     * @param attributeGetterGlobalService sub-service
     * @param deliveryIdGetterGlobalService sub-service
     * @param maxDeliveryZoneService sub-service
     */
    public GlobalService(AttributeGetterGlobalService attributeGetterGlobalService,
                                      DeliveryIdGetterGlobalService deliveryIdGetterGlobalService,
                                      MaxDeliveryZoneService maxDeliveryZoneService) {
        this.attributeGetterGlobalService = attributeGetterGlobalService;
        this.deliveryIdGetterGlobalService = deliveryIdGetterGlobalService;
        this.maxDeliveryZoneService = maxDeliveryZoneService;
    }

    /**
     * Check the role and handle it further.
     * @param role the role of the user
     * @param operation the method that should be called
     * @param <T> the passed param
     * @return the response type obj
     */
    public <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation) {
        // here we do not have a check for the role,
        // as the correctness of role has already been checked in
        // the AssociationService class
        return operation.get();
    }
}
