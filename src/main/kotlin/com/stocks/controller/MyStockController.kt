package com.stocks.controller

import com.stocks.domain.MyStock
import com.stocks.service.MyStockService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.math.BigDecimal

@RestController
@RequestMapping("/my-stocks")
class MyStockController(
    val myStockService: MyStockService
) {
    @PostMapping
    fun addMyStock(@RequestBody myStock: MyStock): Mono<MyStock> {
        return myStockService.save(myStock)
    }

    @PutMapping("/{id}/sold/{price}")
    fun soldMyStock(@PathVariable id: String, @PathVariable price: BigDecimal): Mono<MyStock> {
        return myStockService.soldById(id, price)
    }

    @GetMapping
    fun getMyStocks(): Mono<List<MyStock>> {
        return myStockService.getAllStocks()
    }

    @GetMapping("/{symbol}")
    fun getMyStocksBySymbol(@PathVariable symbol: String): Mono<List<MyStock>> {
        return myStockService.getStocksBySymbol(symbol)
    }
}
