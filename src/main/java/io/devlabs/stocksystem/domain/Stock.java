package io.devlabs.stocksystem.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Stock {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    private Long productId;

    private int quantity;

    @Version
    private Long version;

    public Stock(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(int quantity) {
        if (this.quantity < quantity) {
            throw new IllegalStateException("재고는 0개 미만이 될 수 없습니다.");
        }
        this.quantity -= quantity;
    }

}
