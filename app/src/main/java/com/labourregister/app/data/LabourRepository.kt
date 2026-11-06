package com.labourregister.app.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class LabourRepository(
    private val labourerDao: LabourerDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun getAllLabourersFlow(): Flow<List<LabourerEntity>> =
        labourerDao.getAllLabourersFlow().flowOn(ioDispatcher)

    fun searchLabourersFlow(query: String): Flow<List<LabourerEntity>> {
        val trimmed = query.trim()
        return if (trimmed.isEmpty()) {
            labourerDao.getAllLabourersFlow().flowOn(ioDispatcher)
        } else {
            labourerDao.searchLabourersFlow(trimmed).flowOn(ioDispatcher)
        }
    }

    fun getCountFlow(): Flow<Int> =
        labourerDao.getCountFlow().flowOn(ioDispatcher)

    suspend fun getAllLabourers(): List<LabourerEntity> =
        withContext(ioDispatcher) {
            labourerDao.getAllLabourers()
        }

    suspend fun getLabourerById(id: Long): LabourerEntity? =
        withContext(ioDispatcher) {
            labourerDao.getById(id)
        }

    suspend fun insertLabourer(labourer: LabourerEntity): Long =
        withContext(ioDispatcher) {
            labourerDao.insert(labourer)
        }

    suspend fun updateLabourer(labourer: LabourerEntity) =
        withContext(ioDispatcher) {
            labourerDao.update(labourer)
        }

    suspend fun updateName(id: Long, name: String?) =
        withContext(ioDispatcher) {
            labourerDao.updateName(id, name)
        }

    suspend fun updateSkill(id: Long, skill: String?) =
        withContext(ioDispatcher) {
            labourerDao.updateSkill(id, skill)
        }

    suspend fun updateVillage(id: Long, village: String?) =
        withContext(ioDispatcher) {
            labourerDao.updateVillage(id, village)
        }

    suspend fun updateDailyCharge(id: Long, paise: Long?) =
        withContext(ioDispatcher) {
            labourerDao.updateDailyCharge(id, paise)
        }

    suspend fun updateMobileNumber(id: Long, mobile: String?) =
        withContext(ioDispatcher) {
            labourerDao.updateMobileNumber(id, mobile)
        }

    suspend fun deleteLabourer(labourer: LabourerEntity) =
        withContext(ioDispatcher) {
            labourerDao.delete(labourer)
        }

    suspend fun deleteById(id: Long) =
        withContext(ioDispatcher) {
            labourerDao.deleteById(id)
        }
}
