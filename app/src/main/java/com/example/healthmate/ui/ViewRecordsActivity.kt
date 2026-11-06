
package com.example.healthmate.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.R
import com.example.healthmate.ui.adapter.HealthRecordAdapter
import com.example.healthmate.viewmodel.HealthRecordViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class ViewRecordsActivity : AppCompatActivity() {

    private val viewModel: HealthRecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_view_records)

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

        // Search date field
        val etSearchDate = findViewById<EditText>(
            R.id.etSearchDate
        )

        etSearchDate.setText(viewModel.searchDate.value ?: "")


        // Open DatePicker for searching records
        etSearchDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->

                    val selectedDate = String.format(
                        Locale.US,
                        "%04d-%02d-%02d",
                        year,
                        month + 1,
                        day
                    )

                    etSearchDate.setText(selectedDate)

                    // Update search state through ViewModel
                    viewModel.setSearchDate(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(
            R.id.recyclerViewRecords
        )

        // Empty records message
        val tvEmptyRecords = findViewById<TextView>(
            R.id.tvEmptyRecords
        )

        // RecyclerView Adapter
        val adapter = HealthRecordAdapter(

            onDeleteClick = { record ->

                AlertDialog.Builder(this)
                    .setTitle("Delete Record")
                    .setMessage(
                        "Are you sure you want to delete this record?"
                    )
                    .setPositiveButton("Delete") { _, _ ->

                        // Delete through ViewModel
                        viewModel.deleteRecord(record)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },

            onEditClick = { record ->

                val intent = Intent(
                    this,
                    AddRecordActivity::class.java
                )

                // Send existing record details for editing
                intent.putExtra("recordId", record.id)
                intent.putExtra("recordDate", record.date)
                intent.putExtra("recordSteps", record.steps)
                intent.putExtra("recordCalories", record.calories)
                intent.putExtra("recordWater", record.water)

                startActivity(intent)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Clear Search button
        val btnClearSearch = findViewById<Button>(
            R.id.btnClearSearch
        )

        btnClearSearch.setOnClickListener {

            etSearchDate.setText("")

            // Reset search state through ViewModel
            viewModel.setSearchDate(null)
        }

        // Observe filtered records from ViewModel
        lifecycleScope.launch {

            repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.filteredRecords.collect { records ->

                    // Update RecyclerView
                    adapter.updateRecords(records)

                    // Show empty message when no records exist
                    tvEmptyRecords.visibility =
                        if (records.isEmpty()) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }
            }
        }
    }
}
