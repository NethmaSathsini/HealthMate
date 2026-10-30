
package com.example.healthmate.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import android.content.Intent

class ViewRecordsActivity : AppCompatActivity() {

    private val viewModel: HealthRecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_view_records)

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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRecords)
        val tvEmptyRecords = findViewById<TextView>(R.id.tvEmptyRecords)

        val adapter = HealthRecordAdapter(
            onDeleteClick = { record ->

            AlertDialog.Builder(this)
                .setTitle("Delete Record")
                .setMessage("Are you sure you want to delete this record?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteRecord(record)
                }
                .setNegativeButton("Cancel", null)
                .show()
            },
            onEditClick = { record ->
                val intent = Intent(this, AddRecordActivity::class.java)

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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allRecords.collect { records ->

                    adapter.updateRecords(records)

                    tvEmptyRecords.visibility =
                        if (records.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }
}
