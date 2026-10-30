
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

        val adapter = HealthRecordAdapter()

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
