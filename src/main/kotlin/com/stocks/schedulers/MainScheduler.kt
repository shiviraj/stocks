package com.stocks.schedulers

import com.stocks.service.MyStockService
import com.stocks.service.StockFetcher
import com.stocks.service.StockService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class MainScheduler(
    val stockFetcher: StockFetcher,
    val stockService: StockService,
    val myStockService: MyStockService,
) {
    @Scheduled(cron = "0 * * * * *")
    fun start() {
        stockFetcher.fetch().blockLast()
        Thread.sleep(1000)

        stockService.updateSMMA().blockLast()
        Thread.sleep(1000)

        stockService.removeUnwantedStocks().block()
        Thread.sleep(1000)

        stockService.findTradeableStocks().blockLast()
        Thread.sleep(1000)

        myStockService.updateStopLoss().blockLast()
        Thread.sleep(1000)

        myStockService.findSellableStock().blockLast()
        exitProcess(0)
    }
}
