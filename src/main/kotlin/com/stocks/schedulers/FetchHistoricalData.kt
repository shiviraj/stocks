package com.stocks.schedulers

import com.stocks.domain.Stock
import com.stocks.service.StockService
import com.stocks.service.SymbolService
import com.stocks.webClient.WebClientWrapper
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import kotlin.system.exitProcess

@Component
class FetchHistoricalData(
    val stockService: StockService,
    val symbolService: SymbolService,
    val webClientWrapper: WebClientWrapper

) {
    //    @Scheduled(cron = "0/5 * * * * *")
    fun start() {
        fetchHistoricalData().blockLast()
        updateSMMA().blockLast()
        exitProcess(0)
    }

    private fun fetchHistoricalData(): Flux<List<Stock>> {
        return symbolService.getAllSymbols()
            .delayElements(Duration.ofSeconds(1))
            .flatMap { symbol ->
                webClientWrapper.post(
                    baseUrl = "https://apiconnect.angelbroking.com",
                    path = "/rest/secure/angelbroking/historical/v1/getCandleData",
                    body = mapOf(
                        "exchange" to "NSE",
                        "symboltoken" to symbol.token,
                        "interval" to "ONE_DAY",
                        "fromdate" to "2016-01-01 09:00",
                        "todate" to "2021-09-08 09:00"
                    ),
                    returnType = Response::class.java,
                    uriVariables = mapOf(),
                    headers = mapOf(
                        "Authorization" to "Bearer eyJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6IlM1NzA3OTUiLCJyb2xlcyI6MCwidXNlcnR5cGUiOiJVU0VSIiwiaWF0IjoxNjMxMjAwMjUzLCJleHAiOjE3MTc2MDAyNTN9.L0PwlrEjaAHqS7BqX4U5IURo5kutyEYNV5kYb_u8eJOrfylsWd8Abur4lRU5jd2zGA9m1d7NTroP4ix67ElpNg",
                        "Content-Type" to "application/json",
                        "Accept" to "application/json",
                        "X-UserType" to "USER",
                        "X-SourceID" to "WEB",
                        "X-ClientLocalIP" to "CLIENT_LOCAL_IP",
                        "X-ClientPublicIP" to "CLIENT_PUBLIC_IP",
                        "X-MACAddress" to "MAC_ADDRESS",
                        "X-PrivateKey" to "ZZUKFY6g",
                    ),
                    requestTimeout = null,
                    skipLoggingRequestBody = false,
                    skipLoggingResponseBody = false
                )
                    .map { response ->
                        if (response.status && !response.data.isNullOrEmpty()) {
                            response.data.map {
                                val time = LocalDateTime.parse((it[0] as String).substring(0, 19))
                                Stock(
                                    key = "${symbol.name} ${time.toString().substring(0, 10)}",
                                    symbol = symbol.name,
                                    open = BigDecimal(it[1] as Double),
                                    high = BigDecimal(it[2] as Double),
                                    low = BigDecimal(it[3] as Double),
                                    close = BigDecimal(it[4] as Double),
                                    volume = BigDecimal(it[5] as Int),
                                    date = time,
                                    price = BigDecimal(it[4] as Double),
                                    smma200 = BigDecimal(0),
                                    smma125 = BigDecimal(0),
                                    smma60 = BigDecimal(0),
                                    smma22 = BigDecimal(0),
                                    smma5 = BigDecimal(0)
                                )
                            }
                        } else {
                            emptyList()
                        }
                    }
            }
            .map { list ->
                if (list.isNotEmpty())
                    println("saved stock ${list.first().symbol}")
                list.forEach { stock ->
                    stockService.save(stock)
                        .onErrorResume {
                            Mono.empty()
                        }
                        .subscribe()
                }
                list
            }
    }

    fun updateSMMA(): Flux<Stock> {
        return symbolService.getAllSymbols()
            .delayElements(Duration.ofSeconds(1))
            .flatMap { symbol ->
                stockService.findAllBySymbol(symbol.name)
                    .map { list1 ->
                        val list = list1.reversed()
                        var last = list.first()
                        last.smma200 = last.close
                        last.smma125 = last.close
                        last.smma60 = last.close
                        last.smma22 = last.close
                        last.smma5 = last.close
                        list.map {
                            stockService.updateSMMA(listOf(it, last))
                            last = it
                            it
                        }
                    }
            }
            .flatMap { list ->
                if (list.isNotEmpty())
                    println("updated smma ${list.first().symbol}")
                stockService.saveAll(list)
                    .onErrorResume {
                        if (it is DuplicateKeyException) Mono.empty() else throw it
                    }
            }
    }
}

data class Response(
    val status: Boolean,
    val message: String,
    val errorcode: String,
    val data: List<List<Any>>?
)
