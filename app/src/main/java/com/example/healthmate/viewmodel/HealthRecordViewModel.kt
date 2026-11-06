package com.example.healthmate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.data.local.HealthMateDatabase
import com.example.healthmate.data.local.HealthRecord
import com.example.healthmate.data.repository.HealthRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    // Calculate today's health summary
    val todaySummary: Flow<DailySummary>
        get() = allRecords.map { records ->

            val today = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            ).format(Date())

            val todayRecords = records.filter {
                it.date == today
            }

            DailySummary(
                steps = todayRecords.sumOf { it.steps },
                calories = todayRecords.sumOf { it.calories },
                water = todayRecords.sumOf { it.water }
            )

        }

    fun calculateDailyGoalsSummary(
        records: List<HealthRecord>,
        stepGoal: Int,
        waterGoal: Int
    ): DailyGoalsSummary {

        val today = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.US
        ).format(Date())

        val todayRecords = records.filter {
            it.date == today
        }

        val totalSteps = todayRecords.sumOf { it.steps }
        val totalWater = todayRecords.sumOf { it.water }

        val validStepGoal = stepGoal.coerceAtLeast(1)
        val validWaterGoal = waterGoal.coerceAtLeast(1)

        val stepPercentage =
            (totalSteps.toLong() * 100 / validStepGoal)
                .coerceIn(0L, 100L).toInt()

        val waterPercentage =
            (totalWater.toLong() * 100 / validWaterGoal)
                .coerceIn(0L, 100L).toInt()

        return DailyGoalsSummary(
            recordCount = todayRecords.size,
            totalSteps = totalSteps,
            totalWater = totalWater,
            stepPercentage = stepPercentage,
            waterPercentage = waterPercentage
        )
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
