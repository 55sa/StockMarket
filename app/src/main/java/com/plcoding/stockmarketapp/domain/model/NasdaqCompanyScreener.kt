package com.plcoding.stockmarketapp.domain.model

data class NasdaqCompanyScreener(
     val symbol: String,
     val name: String,
     val lastSale: Double,
     val netChange: Double,
     val percentChange: Double,
     val marketCap: Double,
     val country: String,
     val ipoYear: Int,
     val volume: Int,
     val sector: String,
     val industry: String
)

