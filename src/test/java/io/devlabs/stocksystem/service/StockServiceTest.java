package io.devlabs.stocksystem.service;

import io.devlabs.stocksystem.domain.Stock;
import io.devlabs.stocksystem.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    private Stock testStock;

    @BeforeEach
    void beforeEach() {
        testStock = new Stock(1L, 100);
        stockRepository.saveAndFlush(testStock);
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
        stockService.decrease(testStock.getId(), 1);

        // Then
        Stock result = stockRepository.findById(testStock.getId()).get();
        assertThat(result.getQuantity()).isEqualTo(99);
    }

    @Test
    @DisplayName("비관적 락을 건 환경에서 " +
            "동시에 100개의 재고 감소 요청이 발생했을 경우" +
            "재고가 0이 되는지 확인하는 테스트")
    void decreaseConcurrently() throws InterruptedException {
        // Given
        int numOfThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThread);
        CountDownLatch latch = new CountDownLatch(numOfThread);

        // When
        for (int i = 0; i < numOfThread; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(testStock.getId(), 1);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Then
        Stock result = stockRepository.findById(testStock.getId()).get();
        assertThat(result.getQuantity()).isEqualTo(0);
    }

}