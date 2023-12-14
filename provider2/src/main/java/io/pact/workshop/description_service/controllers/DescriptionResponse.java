package io.pact.workshop.description_service.controllers;

import io.pact.workshop.description_service.descriptions.Description;
import lombok.Data;

import java.util.List;

@Data
public class DescriptionResponse {
  private final List<Description> descriptions;
}
