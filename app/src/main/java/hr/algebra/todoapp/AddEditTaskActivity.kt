package hr.algebra.todoapp

import android.content.ContentUris
import android.content.ContentValues
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import hr.algebra.todoapp.databinding.ActivityAddEditTaskBinding
import hr.algebra.todoapp.model.Task
import android.widget.Toast
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.Locale
import hr.algebra.todoapp.framework.TaskReminderScheduler
import hr.algebra.todoapp.framework.notificationsEnabled
import android.widget.ArrayAdapter
import android.widget.AdapterView
import android.view.View




class AddEditTaskActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    private lateinit var binding: ActivityAddEditTaskBinding
    private var taskId: Long? = null

    private lateinit var categories: Array<String>
    private var selectedCategory: String = "personal"

    private var dueAtMillis: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // DATE PICKER
        binding.etDueDate.isFocusable = false
        binding.etDueDate.isClickable = true
        binding.etDueDate.setOnClickListener { pickDueDateTime() }

        // CATEGORY SPINNER (initialize FIRST)
        categories = resources.getStringArray(R.array.task_categories)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = adapter

        binding.spCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedCategory = categories[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedCategory = "personal"
                }
            }

        // NOW read extras + load
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
                //binding.etDueDate.setText(it.getLong(it.getColumnIndexOrThrow(Task::dueDate.name)).toString())
                val stored = it.getLong(it.getColumnIndexOrThrow(Task::dueDate.name))
                dueAtMillis = stored
                val fmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                binding.etDueDate.setText(fmt.format(stored))

                binding.etPriority.setText(it.getInt(it.getColumnIndexOrThrow(Task::priority.name)).toString())
                binding.cbDone.isChecked = it.getInt(it.getColumnIndexOrThrow(Task::done.name)) == 1

                // MIGRATION FEATURE category
                val cat = it.getString(it.getColumnIndexOrThrow(Task::category.name))
                val index = categories.indexOf(cat)
                if (index >= 0) {
                    binding.spCategory.setSelection(index)
                }



            }
        }
    }

    private fun save() {

        val title = binding.etTitle.text.toString().trim()
        if (title.isBlank()) {
            binding.etTitle.error = getString(R.string.title_required)
            return
        }

        val dueAtMillis = this.dueAtMillis
        val isDone = binding.cbDone.isChecked

        val values = ContentValues().apply {
            put(Task::title.name, title)
            put(Task::notes.name, binding.etNotes.text.toString().trim())
            put(Task::dueDate.name, dueAtMillis) // Long? (INTEGER in DB)
            put(Task::priority.name, binding.etPriority.text.toString().toIntOrNull() ?: 0)
            put(Task::done.name, if (isDone) 1 else 0)
            // MIGRATION FEATURE category
            put(Task::category.name, selectedCategory)

        }

        try {
            if (taskId == null) {
                val resUri = contentResolver.insert(TODO_PROVIDER_CONTENT_URI, values)
                val newId = resUri?.let { ContentUris.parseId(it) } ?: -1L
                if (newId == -1L) {
                    Toast.makeText(this, getString(R.string.insert_failed), Toast.LENGTH_SHORT).show()
                    return
                }

                // Schedule reminder for new task
                if (notificationsEnabled() && !isDone && dueAtMillis != null && dueAtMillis > System.currentTimeMillis()) {
                    TaskReminderScheduler.schedule(this, newId, title, dueAtMillis)
                }

            } else {
                val id = taskId!!

                // Cancel previous reminder (safe even if none existed)
                TaskReminderScheduler.cancel(this, id)

                val uri = ContentUris.withAppendedId(TODO_PROVIDER_CONTENT_URI, id)
                val count = contentResolver.update(uri, values, null, null)
                if (count == 0) {
                    Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
                    return
                }

                // Re-schedule reminder with updated data
                if (notificationsEnabled() && !isDone && dueAtMillis != null && dueAtMillis > System.currentTimeMillis()) {
                    TaskReminderScheduler.schedule(this, id, title, dueAtMillis)
                }
            }

            setResult(RESULT_OK)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.save_failed), Toast.LENGTH_SHORT).show()
        }
    }


    private fun computeDueAtMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(year, month, day, hour, minute, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun pickDueDateTime() {
        val now = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, day ->
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        val millis = computeDueAtMillis(year, month, day, hour, minute)
                        dueAtMillis = millis

                        val fmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        binding.etDueDate.setText(fmt.format(millis))
                    },
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    true
                ).show()
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


}