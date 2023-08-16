package io.devlabs.stocksystem.service;

import io.devlabs.stocksystem.domain.Stock;
import io.devlabs.stocksystem.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public void decrease(Long stockId, int quantity) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new EntityNotFoundException("재고 조회에 실패했습니다."));

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }

}
