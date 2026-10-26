package com.example.healthmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.data.local.HealthMateDatabase
import com.example.healthmate.data.local.HealthRecord
import com.example.healthmate.data.repository.HealthRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HealthRecordViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: HealthRecordRepository

    val allRecords: Flow<List<HealthRecord>>

    init {
        val dao = HealthMateDatabase
            .getDatabase(application)
            .healthRecordDao()

        repository = HealthRecordRepository(dao)
        allRecords = repository.allRecords
    }

    fun insertRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.insertRecord(record)
        }
    }

    fun updateRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.updateRecord(record)
        }
    }

    fun deleteRecord(record: HealthRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
        }
    }

    fun getRecordsByDate(date: String): Flow<List<HealthRecord>> {
        return repository.getRecordsByDate(date)
    }
}
