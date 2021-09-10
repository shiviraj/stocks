package com.stocks.repository

import com.stocks.domain.TradeableStock
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TradeableStockRepository : ReactiveCrudRepository<TradeableStock, String>
