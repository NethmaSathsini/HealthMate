package com.example.healthmate.data.repository

import android.content.Context

class GoalsRepository(context: Context) {

    private val preferences = context.applicationContext
        .getSharedPreferences(
            "HealthMateGoals",
            Context.MODE_PRIVATE
        )

    fun getStepGoal(): Int {
        return preferences.getInt("stepGoal", 8000)
            .coerceAtLeast(1)
    }

    fun getWaterGoal(): Int {
        return preferences.getInt("waterGoal", 2000)
            .coerceAtLeast(1)
    }

    fun saveGoals(stepGoal: Int, waterGoal: Int): Boolean {
        if (stepGoal <= 0 || waterGoal <= 0) {
            return false
        }

        return preferences.edit()
            .putInt("stepGoal", stepGoal)
            .putInt("waterGoal", waterGoal)
            .commit()
    }
}
