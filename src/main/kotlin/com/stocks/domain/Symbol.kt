package com.stocks.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val SYMBOL_COLLECTION = "symbols"

@TypeAlias("Symbol")
@Document(SYMBOL_COLLECTION)
data class Symbol(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val name: String,
    val symbol: String,
    val token: String,
    var isFetched: Boolean = false
)
