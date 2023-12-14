package io.pact.workshop.product_catalogue.clients.description;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.pact.workshop.product_catalogue.clients.DescriptionServiceClient;
import io.pact.workshop.product_catalogue.clients.DescriptionServiceResponse;
import io.pact.workshop.product_catalogue.models.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.lanwen.wiremock.ext.WiremockResolver;
import ru.lanwen.wiremock.ext.WiremockResolver.Wiremock;
import ru.lanwen.wiremock.ext.WiremockUriResolver;
import ru.lanwen.wiremock.ext.WiremockUriResolver.WiremockUri;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ExtendWith({WiremockResolver.class, WiremockUriResolver.class})
class DescriptionServiceClientTest {
    @Autowired
    private DescriptionServiceClient descriptionServiceClient;

    @Test
    void fetchProducts(@Wiremock WireMockServer server, @WiremockUri String uri) {
        descriptionServiceClient.setBaseUrl(uri);
        server.stubFor(
                get(urlPathEqualTo("/descriptions"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\n" +
                                        "\"descriptions\": [\n" +
                                        "            {\n" +
                                        "                \"id\": 9,\n" +
                                        "                \"type\": \"CREDIT_CARD\",\n" +
                                        "                \"name\": \"GEM Visa\",\n" +
                                        "                \"price\": 20,\n" +
                                        "                \"expirationDate\": \"01/01/2023\"\n" +
                                        "            },\n" +
                                        "            {\n" +
                                        "                \"id\": 10,\n" +
                                        "                \"type\": \"CREDIT_CARD\",\n" +
                                        "                \"name\": \"28 Degrees\",\n" +
                                        "                \"price\": 10,\n" +
                                        "                \"expirationDate\": \"01/01/2023\"\n" +
                                        "            }\n" +
                                        "        ]\n" +
                                        "\n}")
                                .withHeader("Content-Type", "application/json"))
        );

        DescriptionServiceResponse response = descriptionServiceClient.fetchDescriptions();
        assertThat(response.getDescriptions(), hasSize(2));
        assertThat(response.getDescriptions().stream().map(Description::getId).collect(Collectors.toSet()),
                is(equalTo(new HashSet<>(Arrays.asList(9L, 10L)))));
    }

    @Test
    void getProductById(@Wiremock WireMockServer server, @WiremockUri String uri) {
        descriptionServiceClient.setBaseUrl(uri);
        server.stubFor(
                get(urlPathEqualTo("/description/10"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withBody("{\n" +
                                        "            \"id\": 10,\n" +
                                        "            \"type\": \"CREDIT_CARD\",\n" +
                                        "            \"name\": \"28 Degrees\",\n" +
                                        "            \"price\": 20,\n" +
                                        "            \"expirationDate\": \"01/01/2023\"\n" +
                                        "        }\n")
                                .withHeader("Content-Type", "application/json"))
        );

        Description description = descriptionServiceClient.getDescriptionByProductId(10);
        assertThat(description, is(equalTo(new Description(10L, "28 Degrees", "CREDIT_CARD", 20L, "01/01/2023"))));
    }
}
