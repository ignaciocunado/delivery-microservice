package nl.tudelft.sem.template.example.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
    @GetMapping("/home")
    public String homeEndpoint() {
        System.out.println("Got to endpoint");
        return "Baeldung!";
    }
}
