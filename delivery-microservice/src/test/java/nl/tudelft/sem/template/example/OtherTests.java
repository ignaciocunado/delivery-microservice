package nl.tudelft.sem.template.example;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class OtherTests {
    @Test
    public void mainTest() {
        String[] args = {};
        try {
            Application.main(args);
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }
}
