package com.stocks.repository

import com.stocks.domain.Stock
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
interface StockRepository : ReactiveCrudRepository<Stock, String> {
    fun findAllBySymbolOrderByDateDesc(symbol: String, pageable: Pageable): Flux<Stock>
    fun findAllBySymbolOrderByDateDesc(symbol: String): Flux<Stock>
    fun findAllByDateBefore(date: LocalDateTime): Flux<Stock>
}
