package nl.tudelft.sem.template.example.integration;

import nl.tudelft.sem.template.example.service.AuthorizationService;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthorizationIntegrationTest {

    @LocalServerPort
    private transient int port;

    private WireMockServer wireMockServer;

    private UUID userId;

    @Autowired
    private AuthorizationService authorizationService;

    @BeforeEach
    public void setUp() {
        int port = 8081;
        wireMockServer = new WireMockServer(port);
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());

        userId = UUID.randomUUID();
    }

    @AfterEach
    public void tearDown() {
        wireMockServer.stop();
    }


    @Test
    public void testCourierRequest() {
        stubFor(post(urlEqualTo("/couriers/123/proof"))
                .willReturn(aResponse()
                        .withStatus(200)));


        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Id", userId.toString());
        request.addParameter("role", "courier");
        request.setRequestURI("/anywhere");
        request.setMethod("GET");


        boolean result = authorizationService.authorize(request);

        assertTrue(result);
        verify(postRequestedFor(urlEqualTo("/couriers/123/proof")));
    }

}
