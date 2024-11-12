package com.plcoding.stockmarketapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    val symbol: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)
