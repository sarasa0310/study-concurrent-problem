package io.devlabs.stocksystem.repository;

import io.devlabs.stocksystem.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
