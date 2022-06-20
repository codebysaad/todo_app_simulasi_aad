package com.dicoding.todoapp.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.setting.SettingsActivity
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.ui.add.AddTaskActivity
import com.dicoding.todoapp.utils.Event
import com.dicoding.todoapp.utils.TasksFilterType
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class TaskActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val addIntent = Intent(this, AddTaskActivity::class.java)
            startActivity(addIntent)
        }

        //TODO 6 : Initiate RecyclerView with LayoutManager
        recycler = findViewById(R.id.rv_task)
        adapter = TaskAdapter {task, state ->
            taskViewModel.completeTask(task, state)
        }
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        initAction()

        val factory = ViewModelFactory.getInstance(this)
        taskViewModel = ViewModelProvider(this, factory).get(TaskViewModel::class.java)

        taskViewModel.tasks.observe(this, Observer(this::showRecyclerView))

        //TODO 15 : Fixing bug : snackBar not show when task completed
        taskViewModel.snackbarText.observe(this, Observer(this::showSnackBar))
    }

    private fun showRecyclerView(task: PagedList<Task>) {
        //TODO 7 : Submit pagedList to adapter and update database when onCheckChange
        adapter.submitList(task)
    }

    private fun showSnackBar(eventMessage: Event<Int>) {
        val message = eventMessage.getContentIfNotHandled() ?: return
        Snackbar.make(
            findViewById(R.id.coordinator_layout),
            getString(message),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val settingIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingIntent)
                true
            }
            R.id.action_filter -> {
                showFilteringPopUpMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = findViewById<View>(R.id.action_filter) ?: return
        PopupMenu(this, view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                taskViewModel.filter(
                    when (it.itemId) {
                        R.id.active -> TasksFilterType.ACTIVE_TASKS
                        R.id.completed -> TasksFilterType.COMPLETED_TASKS
                        else -> TasksFilterType.ALL_TASKS
                    }
                )
                true
            }
            show()
        }
    }

    private fun initAction() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = (viewHolder as TaskAdapter.TaskViewHolder).getTask
                taskViewModel.deleteTask(task)
            }

        })
        itemTouchHelper.attachToRecyclerView(recycler)
    }
}