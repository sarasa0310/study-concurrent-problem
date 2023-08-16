package io.devlabs.stocksystem.facade.redis;

import io.devlabs.stocksystem.repository.RedisLockRepository;
import io.devlabs.stocksystem.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LettuceLockStockFacade {

    private final StockService stockService;
    private final RedisLockRepository redisLockRepository;

    public void decrease(Long stockId, int quantity) throws InterruptedException {
        while (!redisLockRepository.lock(stockId)) {
            Thread.sleep(100);
        }

        try {
            stockService.decrease(stockId, quantity);
        } finally {
            redisLockRepository.unLock(stockId);
        }
    }

}
