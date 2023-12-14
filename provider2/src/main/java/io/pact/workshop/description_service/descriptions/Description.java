package io.pact.workshop.description_service.descriptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="descriptions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Description {
  @Id
  private Long id;
  private String name;
  private String type;
  private Long price;
  private String expirationDate;
}
