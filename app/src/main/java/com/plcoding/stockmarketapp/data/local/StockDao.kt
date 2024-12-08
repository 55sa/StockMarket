package com.plcoding.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StockDao {
    @Query("SELECT COUNT(*) FROM companylistingentity")
    fun getCompanyListingCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertCompanyListings(
        companyListingEntities: List<CompanyListingEntity>
    ): List<Long> // 修改返回类型为 List<Long>

    @Query("DELETE FROM companylistingentity")
     fun clearCompanyListings(): Int // 修改返回类型为 Int 或 void

    @Query(
        """
            SELECT * 
            FROM companylistingentity
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR
                UPPER(:query) == symbol
        """
    )
     fun searchCompanyListing(query: String): List<CompanyListingEntity>

    @Query("SELECT * FROM companylistingentity WHERE symbol = :symbol")
     fun findBySymbol(symbol: String): CompanyListingEntity
}