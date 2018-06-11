package daggerok.app.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "order_items")
public class OrderItem implements Serializable {
  private static final long serialVersionUID = -8982723241890360168L;

  @Id
  @Column(name = "order_item_id")
  @GeneratedValue(strategy = IDENTITY)
  Long id;

  @Column(name = "order_item_name", nullable = false)
  String name;

  @Column(name = "qty", nullable = false)
  Long amount = 0L;
}
