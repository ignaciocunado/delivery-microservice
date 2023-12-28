package nl.tudelft.sem.template.example.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class DeliveryControllerTest {

    @Mock
    CourierController cc = Mockito.mock(CourierController.class);

    DeliveryController sut = new DeliveryController();

    @BeforeEach
    public void setup() {
        sut.setCourierController(cc);
    }

    @Test
    public void testCall() {
        UUID id1 = UUID.randomUUID();
        sut.getPickUpLocation(id1, "a");
        verify(cc).getPickUpLocation(id1, "a");
    }
}