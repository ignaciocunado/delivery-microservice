package nl.tudelft.sem.template.example.service.filters;

import javax.servlet.http.HttpServletRequest;

public interface Handler {

    /**
     * Defines a handler in the chain of responsibilities design pattern.
     * @param request the request to authorize
     * @return true if the request is authorized, false otherwise
     */
    boolean handle(HttpServletRequest request);

    void setNext(Handler next);
}
