package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.model.Restaurant;
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

    @Mock
    CourierController cc = Mockito.mock(CourierController.class);

    @Mock
    AdminController ac = Mockito.mock(AdminController.class);

    RestaurantController sut = new RestaurantController(cc, vc, ac);
    @BeforeEach
    public void setup() {
        sut.setVendorController(vc);
    }

    @Test
    public void testCallAdd() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        sut.addCourierToRest(id1, id2, "a");
        verify(vc).addCourierToRest(id1, id2, "a");
    }

    @Test
    public void testCallRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        sut.removeCourierRest(id1, id2, "a");
        verify(vc).removeCourierRest(id1, id2, "a");
    }

    @Test
    public void testCreateRestaurant() {
        Restaurant restaurant = new Restaurant();
        sut.createRestaurant("admin", restaurant);

        verify(ac).createRestaurant("admin", restaurant);
    }
}