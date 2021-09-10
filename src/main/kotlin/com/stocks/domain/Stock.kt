package com.stocks.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime

const val STOCK_COLLECTION = "stocks"

@TypeAlias("Stock")
@Document(STOCK_COLLECTION)
data class Stock(
    @Id
    var id: String? = null,
    var key: String,
    val symbol: String,
    val high: BigDecimal,
    val low: BigDecimal,
    val open: BigDecimal,
    val close: BigDecimal,
    val volume: BigDecimal,
    val date: LocalDateTime,
    val price: BigDecimal = BigDecimal(0),
    var smma200: BigDecimal = BigDecimal(0),
    var smma125: BigDecimal = BigDecimal(0),
    var smma60: BigDecimal = BigDecimal(0),
    var smma22: BigDecimal = BigDecimal(0),
    var smma5: BigDecimal = BigDecimal(0),
)
