package com.stocks

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*

@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@EnableTransactionManagement
@ConfigurationPropertiesScan
class StocksApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val properties = Properties()
            properties["spring.data.mongodb.uri"] = System.getenv("MONGODB_URL")
            SpringApplicationBuilder(StocksApplication::class.java).properties(properties).run(*args)
        }
    }
}
