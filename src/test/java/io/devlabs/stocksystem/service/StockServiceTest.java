package io.devlabs.stocksystem.service;

import io.devlabs.stocksystem.domain.Stock;
import io.devlabs.stocksystem.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void beforeEach() {
        stockRepository.saveAndFlush(new Stock(1L, 100));
    }

    @AfterEach
    void afterEach() {
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("100개의 재고 중 1개가 감소되었을 때 99개가 되는지 테스트")
    void decrease() {
        // Given

        // When
        stockService.decrease(1L, 1);

        // Then
        Stock stock = stockRepository.findById(1L).get();
        assertThat(stock.getQuantity()).isEqualTo(99);
    }

}