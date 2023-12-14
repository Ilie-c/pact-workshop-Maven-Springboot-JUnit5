package io.pact.workshop.product_catalogue.models;

import lombok.Data;

@Data
public class Description {
    private final Long id;
    private final String name;
    private final String type;
    private final Long price;
    private final String expirationDate;
}
