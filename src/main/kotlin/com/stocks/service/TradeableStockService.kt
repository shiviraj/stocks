package com.stocks.service

import com.stocks.domain.Stock
import com.stocks.domain.TradeType
import com.stocks.domain.TradeableStock
import com.stocks.repository.TradeableStockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TradeableStockService(
    val tradeableStockRepository: TradeableStockRepository
) {
    fun updateAsBuyable(stock: Stock?): Mono<TradeableStock> {
        return update(stock, TradeType.BUY)
    }

    fun updateAsSellable(stock: Stock?): Mono<TradeableStock> {
        return update(stock, TradeType.SELL)
    }

    private fun update(stock: Stock?, type: TradeType): Mono<TradeableStock> {
        if (stock != null) {
            return tradeableStockRepository.save(createTradeableStock(stock, type))
        }
        return Mono.empty()
    }

    private fun createTradeableStock(stock: Stock, type: TradeType): TradeableStock {
        return TradeableStock(
            symbol = stock.symbol,
            key = stock.key,
            cost = stock.close,
            stopLoss = stock.smma22,
            risk = stock.close - stock.smma22,
            date = stock.date,
            type = type
        )
    }
}
