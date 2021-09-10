package com.stocks.domain

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ResponseView(
    val LastTrdTime: Long,
    val ScripName: String,
    val Price: Double,
    val Volume: Double,
    val Open: Double,
    val High: Double,
    val Low: Double,
    val PreCloseRate: Double,
) {
    fun toStock(): Stock {
        val time = LocalDateTime.ofEpochSecond(LastTrdTime / 1000, 0, ZoneOffset.of("+05:30"))
        return Stock(
            key = "$ScripName ${time.toString().split("T")[0]}",
            symbol = ScripName,
            date = time,
            price = BigDecimal(Price),
            volume = BigDecimal(Volume),
            open = BigDecimal(Open),
            high = BigDecimal(High),
            low = BigDecimal(Low),
            close = BigDecimal(PreCloseRate),
        )
    }
}
