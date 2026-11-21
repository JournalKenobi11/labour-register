package com.labourregister.app

import androidx.sqlite.db.SupportSQLiteDatabase
import com.labourregister.app.data.LabourDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Proxy

class DatabaseMigrationTest {

    @Test
    fun testMigration_2_3_executesSqlStatements() {
        val executedSql = mutableListOf<String>()

        // Dynamic proxy for SupportSQLiteDatabase to record execSQL calls
        val dbProxy = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedSql.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        LabourDatabase.MIGRATION_2_3.migrate(dbProxy)

        assertEquals(3, executedSql.size)
        // Verify shops table creation
        assertTrue(executedSql[0].contains("CREATE TABLE IF NOT EXISTS `shops`"))
        assertTrue(executedSql[0].contains("`shopName` TEXT NOT NULL"))

        // Verify product_quotes table creation & foreign key
        assertTrue(executedSql[1].contains("CREATE TABLE IF NOT EXISTS `product_quotes`"))
        assertTrue(executedSql[1].contains("`pricePaise` INTEGER"))
        assertTrue(executedSql[1].contains("FOREIGN KEY(`shopId`) REFERENCES `shops`(`id`)"))

        // Verify index creation
        assertTrue(executedSql[2].contains("CREATE INDEX IF NOT EXISTS `index_product_quotes_shopId`"))
    }

    @Test
    fun testMigration_3_4_addsQuantityColumn() {
        val executedSql = mutableListOf<String>()

        val dbProxy = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedSql.add(args[0] as String)
            }
            null
        } as SupportSQLiteDatabase

        LabourDatabase.MIGRATION_3_4.migrate(dbProxy)

        assertEquals(1, executedSql.size)
        assertTrue(executedSql[0].contains("ALTER TABLE `product_quotes` ADD COLUMN `quantity` INTEGER NOT NULL DEFAULT 1"))
    }
}
