package com.labourregister.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [LabourerEntity::class],
    version = 2,
    exportSchema = false
)
abstract class LabourDatabase : RoomDatabase() {

    abstract fun labourerDao(): LabourerDao

    companion object {
        @Volatile
        private var INSTANCE: LabourDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE labourers ADD COLUMN skill TEXT")
            }
        }

        fun getDatabase(context: Context): LabourDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LabourDatabase::class.java,
                    "labour_register.db"
                ).addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
