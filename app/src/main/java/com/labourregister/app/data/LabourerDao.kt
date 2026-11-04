package com.labourregister.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LabourerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(labourer: LabourerEntity): Long

    @Update
    suspend fun update(labourer: LabourerEntity)

    @Query("SELECT * FROM labourers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): LabourerEntity?

    @Query("SELECT * FROM labourers ORDER BY id DESC")
    fun getAllLabourersFlow(): Flow<List<LabourerEntity>>

    @Query("SELECT * FROM labourers ORDER BY id DESC")
    suspend fun getAllLabourers(): List<LabourerEntity>

    @Query("""
        SELECT * FROM labourers 
        WHERE (name LIKE '%' || :query || '%' 
           OR village LIKE '%' || :query || '%' 
           OR mobile_number LIKE '%' || :query || '%')
        ORDER BY id DESC
    """)
    fun searchLabourersFlow(query: String): Flow<List<LabourerEntity>>

    @Query("SELECT COUNT(*) FROM labourers")
    fun getCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM labourers")
    suspend fun getCount(): Int

    @Delete
    suspend fun delete(labourer: LabourerEntity)

    @Query("DELETE FROM labourers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE labourers SET name = :name, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateName(id: Long, name: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE labourers SET village = :village, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateVillage(id: Long, village: String?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE labourers SET daily_charge_paise = :paise, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateDailyCharge(id: Long, paise: Long?, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE labourers SET mobile_number = :mobile, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateMobileNumber(id: Long, mobile: String?, updatedAt: Long = System.currentTimeMillis())
}
