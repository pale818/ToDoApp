package hr.algebra.todoapp

import android.content.ContentUris
import android.content.ContentValues
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hr.algebra.todoapp.databinding.ActivityAddEditTaskBinding
import hr.algebra.todoapp.model.Task
import android.widget.Toast


class AddEditTaskActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    private lateinit var binding: ActivityAddEditTaskBinding
    private var taskId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L).takeIf { it != -1L }
        if (taskId != null) loadTask(taskId!!)

        binding.btnSave.setOnClickListener { save() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    private fun loadTask(id: Long) {
        val uri = ContentUris.withAppendedId(TODO_PROVIDER_CONTENT_URI, id)
        val cursor = contentResolver.query(uri, null, null, null, null) ?: return

        cursor.use {
            if (it.moveToFirst()) {
                binding.etTitle.setText(it.getString(it.getColumnIndexOrThrow(Task::title.name)))
                binding.etNotes.setText(it.getString(it.getColumnIndexOrThrow(Task::notes.name)))
                binding.etDueDate.setText(it.getString(it.getColumnIndexOrThrow(Task::dueDate.name)))
                binding.etPriority.setText(it.getInt(it.getColumnIndexOrThrow(Task::priority.name)).toString())
                binding.cbDone.isChecked = it.getInt(it.getColumnIndexOrThrow(Task::done.name)) == 1
            }
        }
    }

    private fun save() {
        val title = binding.etTitle.text.toString().trim()
        if (title.isBlank()) {
            binding.etTitle.error = getString(R.string.title_required)
            return
        }

        val values = ContentValues().apply {
            put(Task::title.name, title)
            put(Task::notes.name, binding.etNotes.text.toString().trim())
            put(Task::dueDate.name, binding.etDueDate.text.toString().trim())
            put(Task::priority.name, binding.etPriority.text.toString().toIntOrNull() ?: 0)
            put(Task::done.name, if (binding.cbDone.isChecked) 1 else 0)
        }

        try {
            if (taskId == null) {
                val res = contentResolver.insert(TODO_PROVIDER_CONTENT_URI, values)
                if (res == null || ContentUris.parseId(res) == -1L) {
                    Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show()
                    return
                }
            } else {
                val uri = ContentUris.withAppendedId(TODO_PROVIDER_CONTENT_URI, taskId!!)
                val count = contentResolver.update(uri, values, null, null)
                if (count == 0) {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, e.message ?: "Save failed", Toast.LENGTH_LONG).show()
        }
    }

}