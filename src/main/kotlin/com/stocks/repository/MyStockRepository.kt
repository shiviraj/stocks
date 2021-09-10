package com.stocks.repository

import com.stocks.domain.MyStock
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface MyStockRepository : ReactiveCrudRepository<MyStock, String> {
    fun findAllBySymbol(symbol: String): Mono<List<MyStock>>
}
