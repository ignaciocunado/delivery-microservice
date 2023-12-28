package nl.tudelft.sem.template.example.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class RestaurantControllerTest {


    @Mock
    VendorController vc = Mockito.mock(VendorController.class);

    RestaurantController sut = new RestaurantController();
    @BeforeEach
    public void setup() {
        sut.setVendorController(vc);
    }

    @Test
    public void testCall() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        sut.addCourierToRest(id1, id2, "a");
        verify(vc).addCourierToRest(id1, id2, "a");
    }



}