package com.stocks.repository

import com.stocks.domain.Symbol
import org.bson.types.ObjectId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface SymbolRepository : ReactiveCrudRepository<Symbol, String> {
    fun findByName(name: String): Mono<Symbol>
    fun deleteById(id: ObjectId): Mono<Symbol>
    fun findAllBySymbol(symbol: String): Flux<Symbol>
    fun findAllByIsFetched(b: Boolean): Flux<Symbol>
}
