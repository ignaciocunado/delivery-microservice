package nl.tudelft.sem.template.example.service;

import nl.tudelft.sem.template.example.authentication.ApiKeyAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.firewall.RequestRejectedException;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationService {

    public static boolean getAuthentication(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        String role = request.getParameter("role");

        if (userId == null || !userId.equals("exampl")) {
            return false;
        }

        if(role.equals("courier")){
            // Here we should have a request to the other microservice

        }

        return true;
    }
}
