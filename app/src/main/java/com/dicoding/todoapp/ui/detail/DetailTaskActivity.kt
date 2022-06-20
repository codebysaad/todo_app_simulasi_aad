package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.R
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID
import com.google.android.material.textfield.TextInputEditText

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var viewModel: DetailTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        //TODO 11 : Show detail task and implement delete action
        val btnDelete = findViewById<Button>(R.id.btn_delete_task)
        val edtTitle = findViewById<TextInputEditText>(R.id.detail_ed_title)
        val edtDesc = findViewById<TextInputEditText>(R.id.detail_ed_description)
        val edtDueDate = findViewById<TextInputEditText>(R.id.detail_ed_due_date)

        val taskId = intent.getStringExtra(TASK_ID)?.toInt()

        val factoryModel = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factoryModel) [
            DetailTaskViewModel::class.java
        ]

        viewModel.setTaskId(taskId)

        viewModel.task.observe(this) { task ->
            if (task != null) {
                edtTitle.setText(task.title)
                edtDesc.setText(task.description)
                edtDueDate.setText(DateConverter.convertMillisToString(task.dueDateMillis))
            }
        }

        btnDelete.setOnClickListener {
            viewModel.deleteTask()
        }

    }
}