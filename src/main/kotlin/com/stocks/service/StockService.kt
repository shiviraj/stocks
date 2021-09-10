package com.stocks.service

import com.stocks.domain.Stock
import com.stocks.repository.StockRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime

@Service
class StockService(
    val stockRepository: StockRepository,
    val symbolService: SymbolService
) {
    fun saveAll(stocks: List<Stock>): Flux<Stock> {
        return stockRepository.saveAll(stocks)
    }

    fun findAllBySymbol(symbol: String): Mono<List<Stock>> {
        return stockRepository.findAllBySymbolOrderByDateDesc(symbol).collectList()
    }

    fun updateSMMA(): Flux<Stock> {
        return getLastFiveStocks()
            .flatMap {
                if (it.size == 2) {
                    updateSMMA(it)
                    stockRepository.save(it.first())
                } else {
                    Mono.empty()
                }
            }
    }

    fun findTradeableStocks(): Flux<Stock> {
        return getLastFiveStocks()
            .map {
                calculateBuyable(it)
                it.first()
            }

    }

    fun calculateBuyable(list: List<Stock>): Stock? {
        val last5th = list[4]
        val last3rd = list[2]
        val last2nd = list[1]
        val stockToday = list.first()
        if (last2nd.smma200 > last2nd.smma5 && stockToday.smma5 >= stockToday.smma200) {
            return stockToday
        } else if (last2nd.smma200 > last2nd.smma22 && stockToday.smma22 >= stockToday.smma200) {
            return stockToday
        } else if (last2nd.smma22 > last2nd.smma5 && stockToday.smma5 >= stockToday.smma22) {
            return stockToday
        } else if (last5th.smma22 < last5th.smma5 && last5th.close < last5th.smma22 && last3rd.close > last3rd.smma5 &&
            last2nd.close > last2nd.smma5 && stockToday.close > stockToday.smma5
        ) {
            return stockToday
        }
        return null
    }

    private fun getLastFiveStocks(): Flux<List<Stock>> {
        return symbolService.getAllSymbols()
            .delayElements(Duration.ofMillis(10))
            .flatMapSequential { symbol ->
                getLastFiveStocks(symbol.name)
            }
    }

    fun getLastFiveStocks(symbol: String): Mono<List<Stock>> {
        return stockRepository.findAllBySymbolOrderByDateDesc(symbol, PageRequest.of(0, 5))
            .collectList()
    }


    fun updateSMMA(list: List<Stock>) {
        val stockToday = list.first()
        val stockYes = list[1]
        val closePrice = stockToday.close
        val n5 = BigDecimal(5)
        val n22 = BigDecimal(22)
        val n60 = BigDecimal(60)
        val n125 = BigDecimal(125)
        val n200 = BigDecimal(200)
        stockToday.smma5 = (stockYes.smma5 * (n5 - BigDecimal(1)) + closePrice) / n5
        stockToday.smma22 = (stockYes.smma22 * (n22 - BigDecimal(1)) + closePrice) / n22
        stockToday.smma60 = (stockYes.smma60 * (n60 - BigDecimal(1)) + closePrice) / n60
        stockToday.smma125 = (stockYes.smma125 * (n125 - BigDecimal(1)) + closePrice) / n125
        stockToday.smma200 = (stockYes.smma200 * (n200 - BigDecimal(1)) + closePrice) / n200
    }

    fun removeUnwantedStocks(): Mono<Void> {
        return findAllOlderThan(LocalDateTime.now().minusYears(3))
            .collectList()
            .flatMap {
                stockRepository.deleteAll(it)
            }
    }

    private fun findAllOlderThan(date: LocalDateTime): Flux<Stock> {
        return stockRepository.findAllByDateBefore(date)
    }

    fun save(stock: Stock): Mono<Stock> {
        return stockRepository.save(stock)
    }
}
