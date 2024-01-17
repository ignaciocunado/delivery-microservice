package nl.tudelft.sem.template.example.service.filters;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseHandler implements Handler {
    private transient Handler next;

    /**
     * Sets the next handler in the chain of responsibility.
     * @param next the next handler
     */
    public void setNext(Handler next) {
        this.next = next;
    }

    /**
     * Handles the request.
     * @param request the request to handle
     * @return true if the request is authorized, false otherwise
     */
    public abstract boolean handle(String request);

    /**
     * Passes the request to the next handler in the chain.
     * @param request the request to handle
     * @return true if the request is authorized, false otherwise
     */
    boolean checkNext(HttpServletRequest request) {
        if (next == null) {
            return true;
        }
        return next.handle(request);
    }
}
