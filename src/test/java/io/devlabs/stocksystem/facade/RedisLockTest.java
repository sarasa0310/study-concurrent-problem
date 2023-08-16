package io.devlabs.stocksystem.facade;

import io.devlabs.stocksystem.domain.Stock;
import io.devlabs.stocksystem.facade.redis.LettuceLockStockFacade;
import io.devlabs.stocksystem.facade.redis.RedissonLockStockFacade;
import io.devlabs.stocksystem.repository.StockRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisLockTest {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private LettuceLockStockFacade lettuceLockStockFacade;

    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;

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
    @Disabled
    @DisplayName("Lettuce Redis를 사용한 환경에서 " +
            "동시에 100개의 재고 감소 요청이 발생했을 경우" +
            "재고가 0이 되는지 확인하는 테스트")
    void decreaseConcurrentlyWithLettuceRedis() throws InterruptedException {
        // Given
        int numOfThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThread);
        CountDownLatch latch = new CountDownLatch(numOfThread);

        // When
        for (int i = 0; i < numOfThread; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockStockFacade.decrease(testStock.getId(), 1);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Lettuce Lock Failed");
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

    @Test
    @DisplayName("Redisson Redis를 사용한 환경에서 " +
            "동시에 100개의 재고 감소 요청이 발생했을 경우" +
            "재고가 0이 되는지 확인하는 테스트")
    void decreaseConcurrentlyWithRedissonRedis() throws InterruptedException {
        // Given
        int numOfThread = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThread);
        CountDownLatch latch = new CountDownLatch(numOfThread);

        // When
        for (int i = 0; i < numOfThread; i++) {
            executorService.submit(() -> {
                try {
                    redissonLockStockFacade.decrease(testStock.getId(), 1);
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