package com.plcoding.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchDao {
    // Insert a single symbol into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertwatch(entity: WatchlistEntity)

    // Insert multiple symbols into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertSymbols(symbols: List<WatchlistEntity>)

    // Query all symbols in the watchlist
    @Query("SELECT symbol FROM watchlist")
     fun getAllSymbols(): List<String>

    // Delete a symbol by its symbol
    @Query("DELETE FROM watchlist WHERE symbol = :symbol")
     fun deleteBySymbol(symbol: String): Int // Return number of rows affected

    // Clear the entire watchlist
    @Query("DELETE FROM watchlist")
     fun clearWatchlist(): Int // Return number of rows affected

    // Get watchlist details by joining with company listing entity
    @Query("""
        SELECT * 
        FROM watchlist
        INNER JOIN companylistingentity
        ON watchlist.symbol = companylistingentity.symbol
    """)
     fun getWatchlistWithDetails(): List<CompanyListingEntity>

    // Check if a symbol exists in the watchlist
    @Query("SELECT COUNT(*) > 0 FROM watchlist WHERE symbol = :symbol")
    fun isSymbolInWatchlist(symbol: String): Boolean
}
