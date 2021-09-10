package com.stocks.service

import com.stocks.domain.MyStock
import com.stocks.domain.Stock
import com.stocks.repository.MyStockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class MyStockService(
    val myStockRepository: MyStockRepository,
    val stockService: StockService,
) {
    fun save(myStock: MyStock): Mono<MyStock> {
        return myStockRepository.save(myStock)
    }

    fun findSellableStock(): Flux<Stock> {
        return myStockRepository.findAll()
            .flatMap { myStock ->
                stockService.getLastFiveStocks(myStock.symbol)
                    .map {
                        if (it.first().close < myStock.stopLoss) {
                            println("Ready to sell")
                        }
                        it.first()
                    }
            }
    }

    fun updateStopLoss(): Flux<MyStock> {
        return myStockRepository.findAll()
            .flatMap { myStock ->
                stockService.getLastFiveStocks(myStock.symbol)
                    .flatMap {
                        if (it.first().smma22 > myStock.stopLoss) {
                            myStock.stopLoss = it.first().smma22
                            save(myStock)
                        } else {
                            Mono.empty()
                        }
                    }
            }
    }

    fun soldById(id: String, price: BigDecimal): Mono<MyStock> {
        return myStockRepository.findById(id)
            .flatMap {
                it.sold = price
                it.soldDate = LocalDateTime.now()
                save(it)
            }
    }

    fun getAllStocks(): Mono<List<MyStock>> {
        return myStockRepository.findAll().collectList()
    }

    fun getStocksBySymbol(symbol: String): Mono<List<MyStock>> {
        return myStockRepository.findAllBySymbol(symbol)
    }
}
