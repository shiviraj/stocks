package com.stocks.service

import com.stocks.domain.Symbol
import com.stocks.repository.SymbolRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SymbolService(
    val symbolRepository: SymbolRepository
) {
    fun getAllSymbols(): Flux<Symbol> {
        return symbolRepository.findAll()
    }

    fun delete(symbol: Symbol): Mono<Void> {
        return symbolRepository.delete(symbol)

    }
}
