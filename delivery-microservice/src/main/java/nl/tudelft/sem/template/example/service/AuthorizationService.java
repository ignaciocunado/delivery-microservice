package nl.tudelft.sem.template.example.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    private transient ExternalService externalService;

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
        System.out.println("\033[96;40m new http request authorize(): \033[30;106m " + request + " \033[0m");
        if (request == null) {
            return false;
        }

        String userId = request.getHeader("X-User-Id");
        String role = request.getParameter("role");
        if (role == null) {
            System.out.println("\033[91;40m role was null \033[0m");
            return false;
        }

        if (userId == null) {
            System.out.println("\033[91;40m API Key userId was null \033[0m");
            return false;
        }

        if (role.equals("courier") || role.equals("vendor")
                || role.equals("admin")) {
            System.out.println("\033[92;40m role was courier or vendor \033[0m");

            boolean verification = externalService.verify(userId, role);
            System.out.println("\033[92;40m verification: \033[30;102m " + verification + " \033[0m");
            return verification;
        }

        return false;
    }
}
