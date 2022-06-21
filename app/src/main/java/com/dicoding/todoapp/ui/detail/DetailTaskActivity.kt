package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.Toast
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
        val detailEdTitle = findViewById<TextInputEditText>(R.id.detail_ed_title)
        val detailEdDescription = findViewById<TextInputEditText>(R.id.detail_ed_description)
        val detailEdDueDate = findViewById<TextInputEditText>(R.id.detail_ed_due_date)

        val taskId = intent.getIntExtra(TASK_ID, 0)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory).get(DetailTaskViewModel::class.java)
        viewModel.apply {
            setTaskId(taskId)
            task.observe(this@DetailTaskActivity) {
                detailEdTitle.text = Editable.Factory.getInstance().newEditable(it.title)
                detailEdDescription.text =
                    Editable.Factory.getInstance().newEditable(it.description)
                detailEdDueDate.text = Editable.Factory
                    .getInstance()
                    .newEditable(DateConverter.convertMillisToString(it.dueDateMillis))
            }

            showToast.observe(this@DetailTaskActivity) { event ->
                val message = event.getContentIfNotHandled()
                message?.let {
                    Toast.makeText(this@DetailTaskActivity, it, Toast.LENGTH_SHORT).show()
                }
            }

            deletedTask.observe(this@DetailTaskActivity) { event ->
                val savedContent = event.getContentIfNotHandled()
                savedContent?.let {
                    if (it) {
                        viewModel.task.removeObservers(this@DetailTaskActivity)
                        finish()
                    }
                }
            }
        }

        btnDelete.setOnClickListener {
            viewModel.deleteTask()
        }

    }
}