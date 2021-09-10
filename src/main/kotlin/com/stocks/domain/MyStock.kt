package com.stocks.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime

const val MY_STOCK_COLLECTION = "myStocks"

@TypeAlias("MyStock")
@Document(MY_STOCK_COLLECTION)
data class MyStock(
    @Id
    var id: String? = null,
    val symbol: String,
    val cost: BigDecimal,
    var stopLoss: BigDecimal,
    val risk: BigDecimal,
    val qty: Int = 1,
    val date: LocalDateTime = LocalDateTime.now(),
    var sold: BigDecimal? = null,
    var soldDate: LocalDateTime? = null
)
