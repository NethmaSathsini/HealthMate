
package com.example.healthmate.ui

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.healthmate.R
import com.example.healthmate.viewmodel.HealthRecordViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.widget.ProgressBar
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class DailyGoalsActivity : AppCompatActivity() {

    private val viewModel: HealthRecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_goals)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val btnChangeGoals = findViewById<Button>(R.id.btnChangeGoals)

        btnChangeGoals.setOnClickListener {

            val preferences = getSharedPreferences(
                "HealthMateGoals",
                MODE_PRIVATE
            )

            val stepInput = EditText(this).apply {
                hint = "Step Goal"
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(preferences.getInt("stepGoal", 8000).toString())
            }

            val waterInput = EditText(this).apply {
                hint = "Water Goal (ml)"
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(preferences.getInt("waterGoal", 2000).toString())
            }

            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                val padding = (20 * resources.displayMetrics.density).toInt()
                setPadding(padding, padding, padding, 0)
                addView(stepInput)
                addView(waterInput)
            }

            val dialog = AlertDialog.Builder(this)
                .setTitle("Set Daily Goals")
                .setView(container)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener {

                        val steps = stepInput.text.toString().toIntOrNull()
                        val water = waterInput.text.toString().toIntOrNull()

                        when {
                            steps == null || steps <= 0 -> {
                                stepInput.error = "Enter a valid step goal"
                            }

                            water == null || water <= 0 -> {
                                waterInput.error = "Enter a valid water goal"
                            }

                            else -> {
                                preferences.edit()
                                    .putInt("stepGoal", steps)
                                    .putInt("waterGoal", water)
                                    .apply()

                                dialog.dismiss()

                                // Refresh the screen with saved goals
                                recreate()
                            }
                        }
                    }
            }

            dialog.show()
        }


        // Today's Records TextView
        val tvTodayRecords = findViewById<TextView>(
            R.id.tvTodayRecords
        )

        // Goal progress views
        val tvStepGoalProgress =
            findViewById<TextView>(R.id.tvStepGoalProgress)

        val tvWaterGoalProgress =
            findViewById<TextView>(R.id.tvWaterGoalProgress)

        val tvStepGoalDetails =
            findViewById<TextView>(R.id.tvStepGoalDetails)

        val tvWaterGoalDetails =
            findViewById<TextView>(R.id.tvWaterGoalDetails)

        val progressStepGoal =
            findViewById<ProgressBar>(R.id.progressStepGoal)

        val progressWaterGoal =
            findViewById<ProgressBar>(R.id.progressWaterGoal)

        // Observe records from Room Database
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allRecords.collect { records ->

                    val today = SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.US
                    ).format(Date())

                    val todayRecords = records.filter {
                        it.date == today
                    }

                    // Default daily goals

                    // Load saved daily goals
                    val preferences = getSharedPreferences(
                        "HealthMateGoals",
                        MODE_PRIVATE
                    )

                    val stepGoal = preferences.getInt("stepGoal", 8000)
                        .coerceAtLeast(1)

                    val waterGoal = preferences.getInt("waterGoal", 2000)
                        .coerceAtLeast(1)


                    // Calculate today's totals
                    val totalSteps = todayRecords.sumOf { it.steps }
                    val totalWater = todayRecords.sumOf { it.water }

                    // Calculate progress percentages
                    val stepPercentage = (totalSteps.toLong() * 100 / stepGoal)
                        .coerceIn(0L, 100L).toInt()

                    val waterPercentage = (totalWater.toLong() * 100 / waterGoal)
                        .coerceIn(0L, 100L).toInt()

                    // Update Step Goal
                    tvStepGoalProgress.text = "$stepPercentage%"
                    progressStepGoal.progress = stepPercentage
                    tvStepGoalDetails.text = "$totalSteps / $stepGoal steps"

                    // Update Water Goal
                    tvWaterGoalProgress.text = "$waterPercentage%"
                    progressWaterGoal.progress = waterPercentage
                    tvWaterGoalDetails.text = "$totalWater / $waterGoal ml"

                    val recordCount = todayRecords.size

                    tvTodayRecords.text =
                        if (recordCount == 1) {
                            "1 record"
                        } else {
                            "$recordCount records"
                        }
                }
            }
        }
    }
}
