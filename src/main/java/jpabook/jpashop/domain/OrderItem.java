package jpabook.jpashop.domain;

import javax.persistence.*;

@Entity
public class OrderItem {
    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

//    @Column(name = "order_id")
//    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

//    @Column(name = "item_id")
//    private Long itemId;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    public Long getOrderId() {
//        return orderId;
//    }

//    public void setOrderId(Long orderId) {
//        this.orderId = orderId;
//    }

//    public Long getItemId() {
//        return itemId;
//    }

//    public void setItemId(Long itemId) {
//        this.itemId = itemId;
//    }


    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
