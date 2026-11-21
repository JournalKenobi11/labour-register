package com.labourregister.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        LabourerEntity::class,
        ShopEntity::class,
        ProductQuoteEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class LabourDatabase : RoomDatabase() {

    abstract fun labourerDao(): LabourerDao
    abstract fun shopInquiryDao(): ShopInquiryDao

    companion object {
        @Volatile
        private var INSTANCE: LabourDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE labourers ADD COLUMN skill TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `shops` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `shopName` TEXT NOT NULL,
                        `contactName` TEXT NOT NULL,
                        `mobileNumber` TEXT NOT NULL,
                        `location` TEXT NOT NULL,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `product_quotes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `shopId` INTEGER NOT NULL,
                        `productName` TEXT NOT NULL,
                        `brandOrModel` TEXT NOT NULL,
                        `pricePaise` INTEGER,
                        `gstIncluded` INTEGER NOT NULL,
                        `notes` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        FOREIGN KEY(`shopId`) REFERENCES `shops`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_product_quotes_shopId` ON `product_quotes` (`shopId`)"
                )
            }
        }

        fun getDatabase(context: Context): LabourDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LabourDatabase::class.java,
                    "labour_register.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
