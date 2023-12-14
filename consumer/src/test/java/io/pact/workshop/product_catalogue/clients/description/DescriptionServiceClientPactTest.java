package io.pact.workshop.product_catalogue.clients.description;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.pact.workshop.product_catalogue.clients.DescriptionServiceClient;
import io.pact.workshop.product_catalogue.clients.DescriptionServiceResponse;
import io.pact.workshop.product_catalogue.models.Description;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "DescriptionService")
class DescriptionServiceClientPactTest {
    @Autowired
    private DescriptionServiceClient descriptionServiceClient;

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact allDescriptions(PactDslWithProvider builder) {
        return builder
                .given("descriptions exists")
                .uponReceiving("get all descriptions")
                .path("/descriptions")
                .matchHeader("Authorization", "Bearer [a-zA-Z0-9=\\+/]+", "Bearer AAABd9yHUjI=")
                .willRespondWith()
                .status(200)
                .body(
                        new PactDslJsonBody()
                                .minArrayLike("descriptions", 1, 2)
                                .integerType("id", 10L)
                                .stringType("name", "Gem Visa")
                                .stringType("type", "CREDIT_CARD")
                                .integerType("price", 20L)
                                .stringType("expirationDate", "01/01/2023")
                                .closeObject()
                                .closeArray()
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "allDescriptions")
    void testAllDescriptions(MockServer mockServer) {
        descriptionServiceClient.setBaseUrl(mockServer.getUrl());
        List<Description> descriptions = descriptionServiceClient.fetchDescriptions().getDescriptions();
        assertThat(descriptions, hasSize(2));
        assertThat(descriptions.get(0), is(equalTo(new Description(10L, "Gem Visa", "CREDIT_CARD", 20L, "01/01/2023"))));
    }

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact singleDescription(PactDslWithProvider builder) {
        return builder
                .given("description of product with ID 10 exists", "id", 10)
                .uponReceiving("get description of product with ID 10")
                .path("/description/10")
                .matchHeader("Authorization", "Bearer [a-zA-Z0-9=\\+/]+", "Bearer AAABd9yHUjI=")
                .willRespondWith()
                .status(200)
                .body(
                        new PactDslJsonBody()
                                .integerType("id", 10L)
                                .stringType("name", "28 Degrees")
                                .stringType("type", "CREDIT_CARD")
                                .integerType("price", 20L)
                                .stringType("expirationDate", "01/01/2023")
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "singleDescription")
    void testSingleProduct(MockServer mockServer) {
        descriptionServiceClient.setBaseUrl(mockServer.getUrl());
        Description description = descriptionServiceClient.getDescriptionByProductId(10L);
        assertThat(description, is(equalTo(new Description(10L, "28 Degrees", "CREDIT_CARD", 20L, "01/01/2023"))));
    }

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact noDescriptions(PactDslWithProvider builder) {
        return builder
                .given("no descriptions exists")
                .uponReceiving("get all descriptions")
                .path("/descriptions")
                .matchHeader("Authorization", "Bearer [a-zA-Z0-9=\\+/]+", "Bearer AAABd9yHUjI=")
                .willRespondWith()
                .status(200)
                .body(
                        new PactDslJsonBody().array("descriptions")
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "noDescriptions")
    void testNoProducts(MockServer mockServer) {
        descriptionServiceClient.setBaseUrl(mockServer.getUrl());
        DescriptionServiceResponse descriptions = descriptionServiceClient.fetchDescriptions();
        assertThat(descriptions.getDescriptions(), hasSize(0));
    }

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact singleDescriptionNotExists(PactDslWithProvider builder) {
        return builder
                .given("description for product with ID 10 does not exist", "id", 10)
                .uponReceiving("get description of product with ID 10")
                .path("/description/10")
                .matchHeader("Authorization", "Bearer [a-zA-Z0-9=\\+/]+", "Bearer AAABd9yHUjI=")
                .willRespondWith()
                .status(404)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "singleDescriptionNotExists")
    void testSingleProductNotExists(MockServer mockServer) {
        descriptionServiceClient.setBaseUrl(mockServer.getUrl());
        try {
            descriptionServiceClient.getDescriptionByProductId(10L);
            fail("Expected service call to throw an exception");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("404 Not Found"));
        }
    }

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact noDescriptionAuthToken(PactDslWithProvider builder) {
        return builder
                .uponReceiving("get all descriptions with no auth token")
                .path("/descriptions")
                .willRespondWith()
                .status(401)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "noDescriptionAuthToken")
    void testNoAuthToken(MockServer mockServer) {
        descriptionServiceClient.setBaseUrl(mockServer.getUrl());
        try {
            descriptionServiceClient.fetchDescriptions();
            fail("Expected service call to throw an exception");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("401 Unauthorized"));
        }
    }

    @Pact(consumer = "ProductCatalogue")
    public RequestResponsePact noDescriptionAuthToken2(PactDslWithProvider builder) {
        return builder
                .uponReceiving("get description for product by ID with no auth token")
                .path("/description/10")
                .willRespondWith()
                .status(401)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "noDescriptionAuthToken2")
    void testNoAuthToken2(MockServer mockServer) {
        descriptionServiceClient.setBaseUrl(mockServer.getUrl());
        try {
            descriptionServiceClient.getDescriptionByProductId(10L);
            fail("Expected service call to throw an exception");
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getMessage(), containsString("401 Unauthorized"));
        }
    }
}
