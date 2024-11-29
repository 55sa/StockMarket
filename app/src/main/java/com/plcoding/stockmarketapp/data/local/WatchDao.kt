package com.plcoding.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WatchDao {
    // Insert a single symbol into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertwatch(entity: WatchlistEntity)

    // Insert multiple symbols into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymbols(symbols: List<WatchlistEntity>)

    // Query all symbols in the watchlist
    @Query("SELECT * FROM watchlist")
    suspend fun getAllSymbols(): List<WatchlistEntity>

    // Delete a symbol by its Symbol
    @Query("DELETE FROM watchlist WHERE symbol = :symbol")
    suspend fun deleteBySymbol(symbol: String)

    // Clear the entire watchlist
    @Query("DELETE FROM watchlist")
    suspend fun clearWatchlist()



    @Query("""
    SELECT * FROM watchlist
    INNER JOIN companylistingentity
    ON watchlist.symbol = companylistingentity.symbol
""")
    suspend fun getWatchlistWithDetails(): List<CompanyListingEntity>


}