package com.example.healthmate.viewmodel

data class DailyGoalsSummary(
    val recordCount: Int = 0,
    val totalSteps: Int = 0,
    val totalWater: Int = 0,
    val stepPercentage: Int = 0,
    val waterPercentage: Int = 0
)
