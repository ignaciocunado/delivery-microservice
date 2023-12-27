package nl.tudelft.sem.template.example.service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    /**
     * Authorization method for user types.
     *
     * @param request gotten request
     * @return if the user is authorized
     */
    public boolean authorize(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        String role = request.getParameter("role");
        if (role.equals("courier") || role.equals("vendor")) {
            // Here we should have a request to the other microservice
            if (userId != null) {
                return true;
            }
        }

        return false;
    }
}
