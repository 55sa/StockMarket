package com.plcoding.stockmarketapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [CompanyListingEntity::class, WatchlistEntity::class],
    version = 2,
   exportSchema = false
)
abstract class StockDatabase: RoomDatabase() {
    abstract val stockdao: StockDao
   abstract val watchdao: WatchDao

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS watchlist (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                symbol TEXT NOT NULL
            )
        """.trimIndent())
            }
    }
}}

