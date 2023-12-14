package io.pact.workshop.product_catalogue.models;

import lombok.Data;

import java.util.List;

@Data
public class DescriptionCatalogue {
  private final String name;
  private final List<Description> descriptions;
}
