package nl.tudelft.sem.template.example.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements ChainHandler {

    ExternalService externalService;

    /**
     * Constructor for the external microservice.
     * @param externalService external microservice
     */
    @Autowired
    public AuthorizationService(ExternalService externalService) {
        this.externalService = externalService;
    }

    /**
     * Authorization method for user types.
     *
     * @param request gotten request
     * @return if the user is authorized
     */
    public boolean authorize(HttpServletRequest request) {
        //System.out.println("\033[96;40m new http request authorize(): \033[30;106m " + request + " \033[0m");
        if (request == null) {
            return false;
        }

        String userId = request.getHeader("X-User-Id");
        String role = request.getParameter("role");
        if (role == null) {
            //System.out.println("\033[91;40m role was null \033[0m");
            return false;
        }

        return switch (role) {
            case "courier" -> externalService.isCourier(userId);
            case "vendor" -> externalService.isVendor(userId);
            case "admin" -> externalService.isAdmin(userId);
            case "customer" -> externalService.isCustomer(userId);
            default -> false;
        };
    }
}
