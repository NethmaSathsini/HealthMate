
package com.example.healthmate.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.healthmate.R
import java.util.Calendar
import java.util.Locale
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import com.example.healthmate.data.local.HealthRecord
import com.example.healthmate.viewmodel.HealthRecordViewModel

class AddRecordActivity : AppCompatActivity() {
    private val viewModel: HealthRecordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_add_record)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val etDate = findViewById<EditText>(R.id.etDate)

        etDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    val selectedDate = String.format(
                        Locale.US,
                        "%04d-%02d-%02d",
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay
                    )

                    etDate.setText(selectedDate)
                },
                year,
                month,
                day
            )

            // Prevent selecting future dates
            datePicker.datePicker.maxDate = System.currentTimeMillis()

            datePicker.show()
        }

        // Get input fields
        val etSteps = findViewById<EditText>(R.id.etSteps)
        val etCalories = findViewById<EditText>(R.id.etCalories)
        val etWater = findViewById<EditText>(R.id.etWater)
        val btnSaveRecord = findViewById<Button>(R.id.btnSaveRecord)

        btnSaveRecord.setOnClickListener {

            val date = etDate.text.toString().trim()
            val stepsText = etSteps.text.toString().trim()
            val caloriesText = etCalories.text.toString().trim()
            val waterText = etWater.text.toString().trim()

            when {
                date.isEmpty() -> {
                    etDate.error = "Please select a date"
                    Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                }

                stepsText.isEmpty() -> {
                    etSteps.error = "Please enter steps"
                    etSteps.requestFocus()
                }

                caloriesText.isEmpty() -> {
                    etCalories.error = "Please enter calories"
                    etCalories.requestFocus()
                }

                waterText.isEmpty() -> {
                    etWater.error = "Please enter water intake"
                    etWater.requestFocus()
                }
                else -> {
                    val steps = stepsText.toIntOrNull()
                    val calories = caloriesText.toIntOrNull()
                    val water = waterText.toIntOrNull()

                    when {
                        steps == null || steps < 0 -> {
                            etSteps.error = "Enter a valid number"
                        }

                        calories == null || calories < 0 -> {
                            etCalories.error = "Enter a valid number"
                        }

                        water == null || water < 0 -> {
                            etWater.error = "Enter a valid number"
                        }

                        else -> {
                            val record = HealthRecord(
                                date = date,
                                steps = steps,
                                calories = calories,
                                water = water
                            )

                            viewModel.insertRecord(record)

                            Toast.makeText(
                                this,
                                "Record saved successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()
                        }
                    }
                }
            }
        }

    }
}
