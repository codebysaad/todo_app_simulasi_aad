package com.dicoding.todoapp.ui.list

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.ui.detail.DetailTaskActivity
import com.dicoding.todoapp.utils.DateConverter
import com.dicoding.todoapp.utils.TASK_ID

class TaskAdapter(
    private val onCheckedChange: (Task, Boolean) -> Unit
) : PagedListAdapter<Task, TaskAdapter.TaskViewHolder>(DIFF_CALLBACK) {

    //TODO 8 : Create and initialize ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position) as Task
        //TODO 9 : Bind data to ViewHolder (You can run app to check)
        holder.bind(task)
        when {
            //TODO 10 : Display title based on status using TitleTextView
            task.isCompleted -> {
                //DONE
                holder.cbComplete.isChecked = true
                val title = TaskTitleView.DONE
                holder.tvTitle.state = title
            }
            task.dueDateMillis < System.currentTimeMillis() -> {
                //OVERDUE
                holder.cbComplete.isChecked = false
                val title = TaskTitleView.OVERDUE
                holder.tvTitle.state = title
            }
            else -> {
                //NORMAL
                holder.cbComplete.isChecked = false
                val title = TaskTitleView.NORMAL
                holder.tvTitle.state = title
            }
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TaskTitleView = itemView.findViewById(R.id.item_tv_title)
        val cbComplete: CheckBox = itemView.findViewById(R.id.item_checkbox)
        private val tvDueDate: TextView = itemView.findViewById(R.id.item_tv_date)

        lateinit var getTask: Task

        fun bind(task: Task) {
            getTask = task
            tvTitle.text = task.title
            tvDueDate.text = DateConverter.convertMillisToString(task.dueDateMillis)
            itemView.setOnClickListener {
                val detailIntent = Intent(itemView.context, DetailTaskActivity::class.java)
                detailIntent.putExtra(TASK_ID, task.id)
                itemView.context.startActivity(detailIntent)
            }
            cbComplete.setOnClickListener {
                onCheckedChange(task, !task.isCompleted)
            }
        }

    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem == newItem
            }
        }

    }

}