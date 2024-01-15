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
 * WARNING: this class will be entirely removed in the next step of the refactor.
 */
@Service
public class GlobalService {

    @Getter
    private final AttributeGetterGlobalService attributeGetterGlobalService;

    @Getter
    private final DeliveryIdGetterGlobalService deliveryIdGetterGlobalService;

    @Getter
    private final MaxDeliveryZoneService maxDeliveryZoneService;

    public GlobalService(AttributeGetterGlobalService attributeGetterGlobalService,
                                      DeliveryIdGetterGlobalService deliveryIdGetterGlobalService,
                                      MaxDeliveryZoneService maxDeliveryZoneService) {
        this.attributeGetterGlobalService = attributeGetterGlobalService;
        this.deliveryIdGetterGlobalService = deliveryIdGetterGlobalService;
        this.maxDeliveryZoneService = maxDeliveryZoneService;
    }

    /**
     * Check the role and handle it further
     * @param role the role of the user
     * @param operation the method that should be called
     * @param <T> the passed param
     * @return the response type obj
     */
    public <T> ResponseEntity<T> checkAndHandle(String role, Supplier<ResponseEntity<T>> operation) {
        if(!role.equals("admin")) {
            return new ResponseEntity<T>(HttpStatus.UNAUTHORIZED);
        }
        return operation.get();
    }
}
