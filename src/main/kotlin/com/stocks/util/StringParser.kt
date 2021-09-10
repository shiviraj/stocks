package com.stocks.util

import com.stocks.domain.ResponseView
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class StringParser {

    companion object {
        fun parse(string: String): ResponseView {
            val stringWithoutHyphen = string.replace("--", "0", true)
            val map = linkedMapOf<String, String>()
            stringWithoutHyphen.removeSurrounding("{", "}")
                .split(",")
                .map {
                    it.split("=").apply {
                        map[this[0].trim()] = this[1].trim()
                    }
                }

            val format = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)

            return ResponseView(
                LastTrdTime = format.parse(map["LastTrdTime"]).time,
                ScripName = map["ScripName"]!!,
                Price = map["Price"]!!.toDouble(),
                Volume = map["Volume"]!!.toDouble(),
                Open = map["Open"]!!.toDouble(),
                High = map["High"]!!.toDouble(),
                Low = map["Low"]!!.toDouble(),
                PreCloseRate = map["PreCloseRate"]!!.toDouble(),
            )
        }
    }
}
