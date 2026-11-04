package com.labourregister.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LabourerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class LabourDatabase : RoomDatabase() {

    abstract fun labourerDao(): LabourerDao

    companion object {
        @Volatile
        private var INSTANCE: LabourDatabase? = null

        fun getDatabase(context: Context): LabourDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LabourDatabase::class.java,
                    "labour_register.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
