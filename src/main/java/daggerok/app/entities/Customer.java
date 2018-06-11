package daggerok.app.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

/*

  Customer |1|----->|many| Order |many|----->|many| OrderItem

*/

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "companies")
public class Customer implements Serializable {
  private static final long serialVersionUID = 3210157165535517115L;

  @Id
  @Column(name = "company_id")
  @GeneratedValue(strategy = IDENTITY)
  Long id;

  @Column(name = "company_name", nullable = false)
  String name;

  // this fk configuration is important! otherwise spring will create in addition `companies_orders` table...
  @JoinColumn(name = "company_id",
      foreignKey = @ForeignKey(name = "orders_to_companies"))
  @OneToMany(cascade = ALL, fetch = LAZY, orphanRemoval = true)
  Set<Order> orders = new LinkedHashSet<>();
}
