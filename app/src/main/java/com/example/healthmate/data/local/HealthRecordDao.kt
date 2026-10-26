
package com.example.healthmate.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {

    @Insert
    suspend fun insertRecord(record: HealthRecord)

    @Update
    suspend fun updateRecord(record: HealthRecord)

    @Delete
    suspend fun deleteRecord(record: HealthRecord)

    @Query("SELECT * FROM health_records ORDER BY date DESC, id DESC")
    fun getAllRecords(): Flow<List<HealthRecord>>

    @Query("SELECT * FROM health_records WHERE date = :date ORDER BY id DESC")
    fun getRecordsByDate(date: String): Flow<List<HealthRecord>>
}
