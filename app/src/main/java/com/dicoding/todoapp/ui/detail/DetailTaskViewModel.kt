package com.dicoding.todoapp.ui.detail

import androidx.lifecycle.*
import com.dicoding.todoapp.R
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.data.TaskRepository
import com.dicoding.todoapp.utils.Event
import kotlinx.coroutines.launch

class DetailTaskViewModel(private val taskRepository: TaskRepository): ViewModel() {

    private val _taskId = MutableLiveData<Int>()

    private val _task = _taskId.switchMap { id ->
        taskRepository.getTaskById(id)
    }
    val task: LiveData<Task> = _task

    private val _showToast = MutableLiveData<Event<Int>>()
    val showToast: LiveData<Event<Int>> = _showToast

    private val _deletedTask = MutableLiveData<Event<Boolean>>()
    val deletedTask: LiveData<Event<Boolean>> = _deletedTask

    fun setTaskId(taskId: Int?) {
        if (taskId == _taskId.value) {
            return
        }
        _taskId.value = taskId
    }

    fun deleteTask() {
        viewModelScope.launch {
            _task.value?.let {
                taskRepository.deleteTask(it)
                _showToast.value = Event(R.string.delete_task)
                _deletedTask.value = Event(true)
            }
        }
    }
}