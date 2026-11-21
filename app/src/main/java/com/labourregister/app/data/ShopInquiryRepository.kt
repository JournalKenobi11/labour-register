package com.labourregister.app.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ShopInquiryRepository(
    private val shopInquiryDao: ShopInquiryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getAllShopsWithQuotesFlow(): Flow<List<ShopWithQuotes>> =
        shopInquiryDao.getAllShopsWithQuotes().flowOn(ioDispatcher)

    suspend fun getAllShopsWithQuotesOnce(): List<ShopWithQuotes> =
        withContext(ioDispatcher) {
            shopInquiryDao.getAllShopsWithQuotesOnce()
        }

    fun getDistinctProductNames(): Flow<List<String>> =
        shopInquiryDao.getDistinctProductNames().flowOn(ioDispatcher)

    fun getDistinctShopNames(): Flow<List<String>> =
        shopInquiryDao.getDistinctShopNames().flowOn(ioDispatcher)

    suspend fun getShopById(id: Long): ShopEntity? =
        withContext(ioDispatcher) {
            shopInquiryDao.getShopById(id)
        }

    suspend fun getShopByName(name: String): ShopEntity? =
        withContext(ioDispatcher) {
            shopInquiryDao.getShopByName(name)
        }

    suspend fun insertShop(shop: ShopEntity): Long =
        withContext(ioDispatcher) {
            shopInquiryDao.insertShop(shop)
        }

    suspend fun updateShop(shop: ShopEntity) =
        withContext(ioDispatcher) {
            shopInquiryDao.updateShop(shop)
        }

    suspend fun deleteShop(shop: ShopEntity) =
        withContext(ioDispatcher) {
            shopInquiryDao.deleteShop(shop)
        }

    suspend fun deleteShopById(id: Long) =
        withContext(ioDispatcher) {
            shopInquiryDao.deleteShopById(id)
        }

    suspend fun insertQuote(quote: ProductQuoteEntity): Long =
        withContext(ioDispatcher) {
            shopInquiryDao.insertQuote(quote)
        }

    suspend fun updateQuote(quote: ProductQuoteEntity) =
        withContext(ioDispatcher) {
            shopInquiryDao.updateQuote(quote)
        }

    suspend fun deleteQuote(quote: ProductQuoteEntity) =
        withContext(ioDispatcher) {
            shopInquiryDao.deleteQuote(quote)
        }

    suspend fun deleteQuoteById(id: Long) =
        withContext(ioDispatcher) {
            shopInquiryDao.deleteQuoteById(id)
        }

    suspend fun updateQuoteProductName(id: Long, name: String) =
        withContext(ioDispatcher) {
            shopInquiryDao.updateQuoteProductName(id, name)
        }

    suspend fun updateQuoteBrand(id: Long, brand: String) =
        withContext(ioDispatcher) {
            shopInquiryDao.updateQuoteBrand(id, brand)
        }

    suspend fun updateQuotePriceAndGst(id: Long, paise: Long?, gst: Boolean) =
        withContext(ioDispatcher) {
            shopInquiryDao.updateQuotePriceAndGst(id, paise, gst)
        }

    suspend fun updateQuoteNotes(id: Long, notes: String) =
        withContext(ioDispatcher) {
            shopInquiryDao.updateQuoteNotes(id, notes)
        }
}
