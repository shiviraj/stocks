package com.stocks.domain

import org.springframework.data.annotation.Id
import java.math.BigDecimal
import java.time.LocalDateTime

data class TradeableStock(
    @Id
    var id: String? = null,
    val symbol: String,
    val cost: BigDecimal,
    var stopLoss: BigDecimal,
    val risk: BigDecimal,
    val date: LocalDateTime = LocalDateTime.now(),
    val type: TradeType = TradeType.BUY
)

enum class TradeType {
    BUY,
    SELL
}
