package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;

/**
 * JPA는 기본 스펙으로 proceted 생성 까지 가능하다
 * 유지보수성을 위해서라도 다양하게 생성자를 만드는것을 막아 놓는다.
 * 항상 코드를 제약하는 스타일로!
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // 주문 가격
    private int orderPrice;

    // 주문 수량
    private int count;

    // 생성 메서드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);
        item.removeStock(count);
        return orderItem;
    }

    // 비즈니스 로직
    public void cancel() {
        getItem().addStock(count);
    }


    // 조회 로직
    /**
     * 주문 상품 전체 조회
     * */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
