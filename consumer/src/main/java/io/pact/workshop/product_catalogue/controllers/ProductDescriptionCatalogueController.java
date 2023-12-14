package io.pact.workshop.product_catalogue.controllers;

import io.pact.workshop.product_catalogue.clients.DescriptionServiceClient;
import io.pact.workshop.product_catalogue.clients.ProductServiceClient;
import io.pact.workshop.product_catalogue.models.DescriptionCatalogue;
import io.pact.workshop.product_catalogue.models.ProductCatalogue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ProductDescriptionCatalogueController {
  @Autowired
  private DescriptionServiceClient descriptionServiceClient;

  @GetMapping("/description/catalogue")
  public String catalogue(Model model) {
    DescriptionCatalogue catalogue = new DescriptionCatalogue("description Catalogue", descriptionServiceClient.fetchDescriptions().getDescriptions());
    model.addAttribute("catalogue", catalogue);
    return "catalogue";
  }

  @GetMapping("/description/catalogue/{id}")
  public String catalogue(@PathVariable("id") Long id, Model model) {
    model.addAttribute("description", descriptionServiceClient.getDescriptionByProductId(id));
    return "details";
  }
}
