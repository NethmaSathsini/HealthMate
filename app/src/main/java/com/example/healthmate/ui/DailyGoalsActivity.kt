
package com.example.healthmate.ui

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.healthmate.R
import com.example.healthmate.viewmodel.HealthRecordViewModel
import kotlinx.coroutines.launch

class DailyGoalsActivity : AppCompatActivity() {

    private val viewModel: HealthRecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_daily_goals)

        // Handle system bar spacing
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

        // Change Daily Goals button
        val btnChangeGoals = findViewById<Button>(
            R.id.btnChangeGoals
        )

        btnChangeGoals.setOnClickListener {

            // Get saved goals through ViewModel
            val stepInput = EditText(this).apply {
                hint = "Step Goal"
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(viewModel.getStepGoal().toString())
            }

            val waterInput = EditText(this).apply {
                hint = "Water Goal (ml)"
                inputType = InputType.TYPE_CLASS_NUMBER
                setText(viewModel.getWaterGoal().toString())
            }

            // Create dialog layout
            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL

                val padding = (
                        20 * resources.displayMetrics.density
                        ).toInt()

                setPadding(padding, padding, padding, 0)

                addView(stepInput)
                addView(waterInput)
            }

            // Create goal settings dialog
            val dialog = AlertDialog.Builder(this)
                .setTitle("Set Daily Goals")
                .setView(container)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .create()

            dialog.setOnShowListener {

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener {

                        val steps = stepInput.text.toString()
                            .toIntOrNull()

                        val water = waterInput.text.toString()
                            .toIntOrNull()

                        // Validate goal values
                        when {
                            steps == null || steps <= 0 -> {
                                stepInput.error =
                                    "Enter a valid step goal"
                            }

                            water == null || water <= 0 -> {
                                waterInput.error =
                                    "Enter a valid water goal"
                            }

                            else -> {

                                // Save goals through ViewModel
                                val saved = viewModel.saveDailyGoals(
                                    steps,
                                    water
                                )

                                if (saved) {
                                    dialog.dismiss()

                                    // Refresh progress with new goals
                                    recreate()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Unable to save daily goals",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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

        // Goal progress TextViews
        val tvStepGoalProgress = findViewById<TextView>(
            R.id.tvStepGoalProgress
        )

        val tvWaterGoalProgress = findViewById<TextView>(
            R.id.tvWaterGoalProgress
        )

        val tvStepGoalDetails = findViewById<TextView>(
            R.id.tvStepGoalDetails
        )

        val tvWaterGoalDetails = findViewById<TextView>(
            R.id.tvWaterGoalDetails
        )

        // Goal progress bars
        val progressStepGoal = findViewById<ProgressBar>(
            R.id.progressStepGoal
        )

        val progressWaterGoal = findViewById<ProgressBar>(
            R.id.progressWaterGoal
        )

        // Observe health records through ViewModel
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.allRecords.collect { records ->

                    // Load saved goals through ViewModel
                    val stepGoal = viewModel.getStepGoal()
                    val waterGoal = viewModel.getWaterGoal()

                    // Calculate summary through ViewModel
                    val summary =
                        viewModel.calculateDailyGoalsSummary(
                            records,
                            stepGoal,
                            waterGoal
                        )

                    val totalSteps = summary.totalSteps
                    val totalWater = summary.totalWater

                    val stepPercentage = summary.stepPercentage
                    val waterPercentage = summary.waterPercentage

                    // Update Step Goal progress
                    tvStepGoalProgress.text =
                        "$stepPercentage%"

                    progressStepGoal.progress = stepPercentage

                    tvStepGoalDetails.text =
                        "$totalSteps / $stepGoal steps"

                    // Update Water Goal progress
                    tvWaterGoalProgress.text =
                        "$waterPercentage%"

                    progressWaterGoal.progress = waterPercentage

                    tvWaterGoalDetails.text =
                        "$totalWater / $waterGoal ml"

                    // Update Today's Records count
                    val recordCount = summary.recordCount

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
