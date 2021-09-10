package com.stocks.schedulers

import com.stocks.service.MyStockService
import com.stocks.service.StockFetcher
import com.stocks.service.StockService
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class MainScheduler(
    val stockFetcher: StockFetcher,
    val stockService: StockService,
    val myStockService: MyStockService
) {
    //    @Scheduled(cron = "0/5 * * * * *")
    fun start(  ) {
        stockFetcher.fetch().blockLast()
        stockService.updateSMMA().blockLast()
        stockService.removeUnwantedStocks().block()
        stockService.findTradeableStocks().blockLast()
        myStockService.updateStopLoss().blockLast()
        myStockService.findSellableStock().blockLast()
        exitProcess(0)
    }
}
