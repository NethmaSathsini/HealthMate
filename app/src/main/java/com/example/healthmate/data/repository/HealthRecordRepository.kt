
package com.example.healthmate.data.repository

import com.example.healthmate.data.local.HealthRecord
import com.example.healthmate.data.local.HealthRecordDao
import kotlinx.coroutines.flow.Flow

class HealthRecordRepository(
    private val healthRecordDao: HealthRecordDao
) {

    val allRecords: Flow<List<HealthRecord>> =
        healthRecordDao.getAllRecords()

    fun getRecordsByDate(date: String): Flow<List<HealthRecord>> {
        return healthRecordDao.getRecordsByDate(date)
    }

    suspend fun insertRecord(record: HealthRecord) {
        healthRecordDao.insertRecord(record)
    }

    suspend fun updateRecord(record: HealthRecord) {
        healthRecordDao.updateRecord(record)
    }

    suspend fun deleteRecord(record: HealthRecord) {
        healthRecordDao.deleteRecord(record)
    }
}
