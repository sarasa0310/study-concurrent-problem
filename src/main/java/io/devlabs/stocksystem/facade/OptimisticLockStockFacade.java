package io.devlabs.stocksystem.facade;

import io.devlabs.stocksystem.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final StockService stockService;

    public void decrease(Long stockId, int quantity) throws InterruptedException {
        while (true) {
            try {
                stockService.decrease(stockId, quantity);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

}
