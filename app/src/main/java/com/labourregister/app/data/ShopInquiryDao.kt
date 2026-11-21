package com.labourregister.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopInquiryDao {

    @Transaction
    @Query("SELECT * FROM shops ORDER BY updatedAt DESC")
    fun getAllShopsWithQuotes(): Flow<List<ShopWithQuotes>>

    @Transaction
    @Query("SELECT * FROM shops ORDER BY updatedAt DESC")
    suspend fun getAllShopsWithQuotesOnce(): List<ShopWithQuotes>

    @Query("SELECT * FROM shops WHERE id = :id LIMIT 1")
    suspend fun getShopById(id: Long): ShopEntity?

    @Query("SELECT * FROM shops WHERE LOWER(TRIM(shopName)) = LOWER(TRIM(:name)) LIMIT 1")
    suspend fun getShopByName(name: String): ShopEntity?

    @Query("SELECT DISTINCT shopName FROM shops WHERE shopName != '' ORDER BY shopName ASC")
    fun getDistinctShopNames(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShop(shop: ShopEntity): Long

    @Update
    suspend fun updateShop(shop: ShopEntity)

    @Delete
    suspend fun deleteShop(shop: ShopEntity)

    @Query("DELETE FROM shops WHERE id = :id")
    suspend fun deleteShopById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: ProductQuoteEntity): Long

    @Update
    suspend fun updateQuote(quote: ProductQuoteEntity)

    @Delete
    suspend fun deleteQuote(quote: ProductQuoteEntity)

    @Query("DELETE FROM product_quotes WHERE id = :id")
    suspend fun deleteQuoteById(id: Long)

    @Query("SELECT DISTINCT productName FROM product_quotes WHERE productName != '' ORDER BY productName ASC")
    fun getDistinctProductNames(): Flow<List<String>>

    @Query("UPDATE product_quotes SET productName = :name, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateQuoteProductName(id: Long, name: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE product_quotes SET brandOrModel = :brand, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateQuoteBrand(id: Long, brand: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE product_quotes SET pricePaise = :paise, gstIncluded = :gst, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateQuotePriceAndGst(id: Long, paise: Long?, gst: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE product_quotes SET notes = :notes, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateQuoteNotes(id: Long, notes: String, updatedAt: Long = System.currentTimeMillis())
}
