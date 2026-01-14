package hr.algebra.todoapp.fragment

import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.todoapp.AddEditTaskActivity
import hr.algebra.todoapp.R
import hr.algebra.todoapp.TODO_PROVIDER_CONTENT_URI
import hr.algebra.todoapp.adapter.TasksAdapter
import hr.algebra.todoapp.model.Task

class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private lateinit var rvTasks: RecyclerView
    private lateinit var adapter: TasksAdapter

    private val addEditLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) loadTasks()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTasks = view.findViewById(R.id.rvTasks)

        adapter = TasksAdapter(
            onClick = { task ->
                val intent = Intent(requireContext(), AddEditTaskActivity::class.java).apply {
                    putExtra(AddEditTaskActivity.EXTRA_TASK_ID, task._id!!)
                }
                addEditLauncher.launch(intent)
            },
            onToggleDone = { task, isDone ->
                val values = ContentValues().apply {
                    put(Task::done.name, if (isDone) 1 else 0)
                }
                val uri = ContentUris.withAppendedId(TODO_PROVIDER_CONTENT_URI, task._id!!)
                requireContext().contentResolver.update(uri, values, null, null)
                loadTasks()
            },
            onLongClick = { task ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete_confirm)
                    .setPositiveButton("OK") { _, _ ->
                        val uri = ContentUris.withAppendedId(TODO_PROVIDER_CONTENT_URI, task._id!!)
                        requireContext().contentResolver.delete(uri, null, null)
                        loadTasks()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        )

        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        rvTasks.adapter = adapter

        loadTasks()
    }

    private fun loadTasks() {
        val cursor = requireContext().contentResolver.query(
            TODO_PROVIDER_CONTENT_URI,
            null, null, null,
            "${Task::dueDate.name} ASC"
        ) ?: return

        val tasks = mutableListOf<Task>()
        cursor.use {
            val idIx = it.getColumnIndexOrThrow(Task::_id.name)
            val titleIx = it.getColumnIndexOrThrow(Task::title.name)
            val notesIx = it.getColumnIndexOrThrow(Task::notes.name)
            val dueIx = it.getColumnIndexOrThrow(Task::dueDate.name)
            val doneIx = it.getColumnIndexOrThrow(Task::done.name)
            val prIx = it.getColumnIndexOrThrow(Task::priority.name)

            while (it.moveToNext()) {
                tasks.add(
                    Task(
                        _id = it.getLong(idIx),
                        title = it.getString(titleIx),
                        notes = it.getString(notesIx),
                        dueDate = it.getString(dueIx),
                        done = it.getInt(doneIx) == 1,
                        priority = it.getInt(prIx)
                    )
                )
            }
        }

        adapter.submitList(tasks)
    }
}
