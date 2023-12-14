package io.pact.workshop.description_service;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import io.pact.workshop.description_service.descriptions.Description;
import io.pact.workshop.description_service.descriptions.DescriptionRepository;
import org.apache.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Provider("DescriptionService")
//@PactBroker
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("DescriptionService")
//@PactFolder("pacts")//url
@PactBroker(host="penta.pactflow.io",
        scheme = "https",
        authentication= @PactBrokerAuth(token="xpfbgBdzNOlxLn4hAZtr9w"))
public class PactVerificationTest {
  @LocalServerPort
  private int port;

  @Autowired
  private DescriptionRepository descriptionRepository;

  @BeforeEach
  void setup(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("localhost", port));
  }

  @TestTemplate
  @ExtendWith(PactVerificationSpringProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context, HttpRequest request) {
    // WARNING: Do not modify anything else on the request, because you could invalidate the contract
    if (request.containsHeader("Authorization")) {
      request.setHeader("Authorization", "Bearer " + generateToken());
    }
    context.verifyInteraction();
  }

  private static String generateToken() {
    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.putLong(System.currentTimeMillis());
    return Base64.getEncoder().encodeToString(buffer.array());
  }

  @State(value = "descriptions exists", action = StateChangeAction.SETUP)
  void productsExists() {
    descriptionRepository.deleteAll();
    descriptionRepository.saveAll(Arrays.asList(
      new Description(100L, "Test Product 1", "CREDIT_CARD", 20L, "07/09/2021"),
      new Description(200L, "Test Product 2", "CREDIT_CARD", 40L, "08/09/2021"),
      new Description(300L, "Test Product 3", "PERSONAL_LOAN", 1L, "09/09/2021"),
      new Description(400L, "Test Product 4", "SAVINGS", 33L, "10/09/2021")
    ));
  }

  @State(value = "no descriptions exists", action = StateChangeAction.SETUP)
  void noProductsExist() {
    descriptionRepository.deleteAll();
  }

  @State(value = "description of product with ID 10 exists", action = StateChangeAction.SETUP)
  void productExists(Map<String, Object> params) {
    long productId = ((Number) params.get("id")).longValue();
    Optional<Description> product = descriptionRepository.findById(productId);
    if (!product.isPresent()) {
      descriptionRepository.save(new Description(productId, "Product", "TYPE", 100L, "10/09/2021"));
    }
  }

  @State(value = "description for product with ID 10 does not exist", action = StateChangeAction.SETUP)
  void productNotExist(Map<String, Object> params) {
    long productId = ((Number) params.get("id")).longValue();
    Optional<Description> product = descriptionRepository.findById(productId);
    if (product.isPresent()) {
      descriptionRepository.deleteById(productId);
    }
  }
}
