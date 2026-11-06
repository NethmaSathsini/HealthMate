
package com.example.healthmate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.healthmate.ui.AddRecordActivity
import com.example.healthmate.ui.ViewRecordsActivity
import com.example.healthmate.ui.DailyGoalsActivity
import com.example.healthmate.viewmodel.HealthRecordViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: HealthRecordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

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

        // Dashboard values
        val tvSteps = findViewById<TextView>(R.id.tvSteps)
        val tvCalories = findViewById<TextView>(R.id.tvCalories)
        val tvWater = findViewById<TextView>(R.id.tvWater)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.todaySummary.collect { summary ->

                    tvSteps.text = "${summary.steps} steps"
                    tvCalories.text = "${summary.calories} kcal"
                    tvWater.text = "${summary.water} ml"

                }
            }
        }

        // Open Add Health Record screen
        val btnAddRecord = findViewById<Button>(R.id.btnAddRecord)

        btnAddRecord.setOnClickListener {
            val intent = Intent(this, AddRecordActivity::class.java)
            startActivity(intent)
        }

        // Open View Health Records screen
        val btnViewRecords = findViewById<Button>(R.id.btnViewRecords)

        btnViewRecords.setOnClickListener {
            val intent = Intent(this, ViewRecordsActivity::class.java)
            startActivity(intent)
        }

        // Open My Daily Goals screen
        val btnDailyGoals = findViewById<Button>(R.id.btnDailyGoals)

        btnDailyGoals.setOnClickListener {
            val intent = Intent(this, DailyGoalsActivity::class.java)
            startActivity(intent)
        }

    }
}
