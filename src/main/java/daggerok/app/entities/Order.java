package daggerok.app.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@Table(name = "orders")
@Accessors(chain = true)
public class Order implements Serializable {
  private static final long serialVersionUID = -7479450314788871578L;

  enum Status {
    ACTIVE,
    INACTIVE,
    DELIVERED
  }

  @Id
  @Column(name = "order_id")
  @GeneratedValue(strategy = IDENTITY)
  Long id;

  @Enumerated(STRING)
  @Column(length = 16, nullable = false)
  Status status = Status.INACTIVE;

  @ManyToMany(cascade = ALL, fetch = LAZY)
  @JoinTable(
      name = "orders_items",
      foreignKey = @ForeignKey(name = "orders_items_to_order_items"),
      inverseForeignKey = @ForeignKey(name = "orders_items_to_orders"),
      joinColumns = {
          @JoinColumn(name = "order_id", referencedColumnName = "order_id"),
      },
      inverseJoinColumns = {
          @JoinColumn(name = "order_item_id", referencedColumnName = "order_item_id"),
      }
  )
  Set<OrderItem> orderItems = new LinkedHashSet<>();
}
