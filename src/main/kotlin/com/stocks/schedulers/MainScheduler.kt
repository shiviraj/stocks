package com.stocks.schedulers

import com.stocks.service.MyStockService
import com.stocks.service.StockFetcher
import com.stocks.service.StockService
import com.stocks.webClient.WebClientWrapper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.system.exitProcess

@Component
class MainScheduler(
    val stockFetcher: StockFetcher,
    val stockService: StockService,
    val myStockService: MyStockService,
    val webClientWrapper: WebClientWrapper
) {
    @Scheduled(cron = "0 0/5 * * * *")
    fun start() {
        if (LocalDateTime.now().minute <= 10) {
            stockFetcher.fetch().blockLast()
            Thread.sleep(1000)

            stockService.updateSMMA().blockLast()
            println("Update SMMA")
            Thread.sleep(1000)

            stockService.removeUnwantedStocks().block()
            println("Removed Unwanted Stocks")
            Thread.sleep(1000)

            stockService.findTradeableStocks().blockLast()
            println("Tradeable Stock")
            Thread.sleep(1000)

            myStockService.updateStopLoss().blockLast()
            println("Update Stop loss")
            Thread.sleep(1000)

            myStockService.findSellableStock().blockLast()
            println("Update Sellable")

            webClientWrapper.get(
                baseUrl = System.getenv("NOTIFIER_URI"),
                path = "",
                returnType = String::class.java,
            ).block()
            exitProcess(0)
        }
    }
}
