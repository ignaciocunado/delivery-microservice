package nl.tudelft.sem.template.example.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private transient MockMvc mockMvc;

    @Test
    public void testSecurityConfig_UnauthorizedRequest_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/endpoint")
                        .header("X-User-Id", "123")
                        .param("role", "non-courier"))
                .andExpect(status().isUnauthorized());
    }
}