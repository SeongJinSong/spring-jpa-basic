package jpql;

import javax.persistence.*;

@Entity
@Table(name = "orders") //order가 예약어이기 때문에 관례상 orders로 자주 쓴다.
public class JOrder {
    @Id @GeneratedValue
    private Long id;
    private int orderAmount;

    @Embedded
    private Address address;

    @ManyToOne
    @JoinColumn(name="product_id")
    private JProduct product;
}
