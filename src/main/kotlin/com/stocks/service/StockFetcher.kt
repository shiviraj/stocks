package com.stocks.service

import com.stocks.domain.ResponseView
import com.stocks.domain.Stock
import com.stocks.util.StringParser
import com.stocks.webClient.WebClientWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.LocalDateTime

@Service
class StockFetcher(
    @Autowired private val webClientWrapper: WebClientWrapper,
    @Autowired private val symbolService: SymbolService,
    @Autowired private val stockService: StockService,
) {
    fun fetch(): Flux<Stock> {
        val symbols = symbolService.getAllSymbols()
            .map { it.name }
            .collectList()
            .block() ?: emptyList()
        return Flux.range(0, 200)
            .delayElements(Duration.ofSeconds(1))
            .flatMap {
                fetchStocks(symbols, it)
            }
    }

    private fun fetchStocks(symbols: List<String>, pageNo: Int): Flux<Stock> {
        return fetchStock(pageNo)
            .map {
                filterNewStocks(it, symbols)
            }
            .flatMapMany { stocks ->
                println("saved stock ${LocalDateTime.now()}, $pageNo")
                stockService.saveAll(stocks)
            }
            .onErrorResume {
                if (it is DuplicateKeyException) Mono.empty() else throw it
            }

    }

    private fun filterNewStocks(responses: List<ResponseView>, symbols: List<String>): List<Stock> {
        return responses
            .filter { responseView ->
                symbols.contains(responseView.ScripName)
            }
            .map { responseView ->
                responseView.toStock()
            }
    }

    private fun fetchStock(pageNo: Int): Mono<List<ResponseView>> {
        val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
        linkedMultiValueMap.add("flag", "Equity")
        linkedMultiValueMap.add("ddlVal1", "All");
        linkedMultiValueMap.add("ddlVal2", "All")
        linkedMultiValueMap.add("m", "0")
        linkedMultiValueMap.add("pgN", "$pageNo")

        return webClientWrapper.get(
            baseUrl = "https://api.bseindia.com",
            path = "/BseIndiaAPI/api/GetStkCurrMain/w",
            returnType = List::class.java,
            queryParams = linkedMultiValueMap,
            headers = mapOf(
                "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:85.0) Gecko/20100101 Firefox/85.0",
            ),
        ).map {
            it.map { str ->
                StringParser.parse(str.toString())
            }
        }
    }
}
