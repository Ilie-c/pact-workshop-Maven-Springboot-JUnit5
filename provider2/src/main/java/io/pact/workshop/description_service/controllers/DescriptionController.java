package io.pact.workshop.description_service.controllers;

import io.pact.workshop.description_service.descriptions.Description;
import io.pact.workshop.description_service.descriptions.DescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DescriptionController {
  @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "description not found")
  public static class DescriptionNotFoundException extends RuntimeException { }

  @Autowired
  private DescriptionRepository descriptionRepository;

  @GetMapping("/descriptions")
  public DescriptionResponse allDescriptions() {
    return new DescriptionResponse((List<Description>) descriptionRepository.findAll());
  }

  @GetMapping("/description/{id}")
  public Description descriptionOfProductById(@PathVariable("id") Long id) {
    return descriptionRepository.findById(id).orElseThrow(DescriptionNotFoundException::new);
  }
}
