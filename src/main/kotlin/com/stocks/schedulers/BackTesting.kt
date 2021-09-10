package com.stocks.schedulers

import com.opencsv.CSVWriter
import com.stocks.domain.MyStock
import com.stocks.service.StockService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.File
import java.io.StringWriter
import kotlin.system.exitProcess

@Component
class BackTesting(
    val stockService: StockService,
) {
    @Scheduled(cron = "0/5 * * * * *")
    fun start() {
        val stocks = mutableListOf<MyStock>()

        stockService.findAllBySymbol("DLF")
            .map { list1 ->
                val list = list1.reversed()
                var last5th = list.first()
                var last4th = list[1]
                var last3rd = list[2]
                var last2nd = list[3]
                list.subList(1, list.size - 1).forEach { stock ->
                    val buyable = stockService.calculateBuyable(listOf(stock, last2nd, last3rd, last4th, last5th))
                    val nextStock = list[list.indexOf(stock) + 1]
                    if (buyable != null) {
                        val myStock = MyStock(
                            symbol = nextStock.symbol,
                            cost = nextStock.open,
                            stopLoss = nextStock.smma22,
                            risk = nextStock.open - nextStock.smma22,
                            qty = 1,
                            date = nextStock.date,
                        )
                        if (myStock.risk < buyable.smma22)
                            stocks.add(myStock)
                    }

                    val sellableStocks = stocks.filter { it.sold == null }
                    sellableStocks
                        .forEach {
                            if (stock.smma22 > it.stopLoss) {
                                it.stopLoss = stock.smma22
                            }
                            if (stock.close < it.stopLoss) {
                                it.sold = nextStock.open
                                it.soldDate = nextStock.date
                                println("sold $it ${stock.smma22}")
                            }
                        }
                    last5th = last4th
                    last4th = last3rd
                    last3rd = last2nd
                    last2nd = stock
                }
            }.block()
        val csv = createCSV(stocks)
        val fileName = "stocks.csv"
        val file = File(fileName)
        file.writeText(csv)

        exitProcess(0)
    }

    private fun createCSV(stocks: List<MyStock>): String {
        val data: MutableList<Array<String>> = ArrayList()
        data.add(
            arrayOf(
                "Buy Date",
                "Qty",
                "Cost",
                "StopLoss",
                "Sold Date",
                "Sold",
            )
        )
        stocks.forEach {
            data.add(
                arrayOf(
                    getOrDefault(it.date),
                    getOrDefault(it.qty),
                    getOrDefault(it.cost),
                    getOrDefault(it.stopLoss),
                    getOrDefault(it.soldDate),
                    getOrDefault(it.sold),
                )
            )
        }

        return createReport(data)
    }

    private fun createReport(data: List<Array<String>>): String {
        val stringWriter = StringWriter()
        val writer = CSVWriter(stringWriter)
        writer.writeAll(data)
        writer.close()
        return stringWriter.toString()
    }

    private fun getOrDefault(value: Any?): String {
        return when (value) {
            null -> ""
            else -> value.toString()
        }
    }
}
