package io.devlabs.stocksystem.facade.redis;

import io.devlabs.stocksystem.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockStockFacade {

    private final StockService stockService;
    private final RedissonClient redissonClient;

    public void decrease(Long stockId, int quantity) {
        RLock lock = redissonClient.getLock(stockId.toString());

        try {
            boolean isAvailable = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if (!isAvailable) {
                log.error("Lock 획득 실패");
                return;
            }
            stockService.decrease(stockId, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException("RedissonLockStockFacade");
        } finally {
            lock.unlock();
        }
    }

}
