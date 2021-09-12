package com.stocks.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime

const val TRADEABLE_STOCK_COLLECTION = "tradeablestocks"

@TypeAlias("TradeableStock")
@Document(TRADEABLE_STOCK_COLLECTION)
data class TradeableStock(
    @Id
    var id: String? = null,
    val symbol: String,
    val cost: BigDecimal,
    var stopLoss: BigDecimal,
    val risk: BigDecimal,
    val date: LocalDateTime = LocalDateTime.now(),
    val type: TradeType = TradeType.BUY,
    val isAlertSent: Boolean = false
)

enum class TradeType {
    BUY,
    SELL
}
