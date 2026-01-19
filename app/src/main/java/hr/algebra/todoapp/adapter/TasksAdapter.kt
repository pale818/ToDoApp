package hr.algebra.todoapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hr.algebra.todoapp.R
import hr.algebra.todoapp.model.Task

class TasksAdapter(
    private val onClick: (Task) -> Unit,
    private val onToggleDone: (Task, Boolean) -> Unit,
    private val onLongClick: (Task) -> Unit
) : ListAdapter<Task, TasksAdapter.TaskVH>(DIFF) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        // Safe fallback just in case something slips in without an id
        return getItem(position)._id ?: RecyclerView.NO_ID
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskVH(view)
    }

    override fun onBindViewHolder(holder: TaskVH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)

        fun bind(task: Task) {
            tvTitle.text = task.title

            val prefs = PreferenceManager.getDefaultSharedPreferences(itemView.context)
            val font = prefs.getString("pref_font", "medium")

            val sizeSp = when (font) {
                "small" -> 14f
                "large" -> 20f
                else -> 16f
            }
            tvTitle.textSize = sizeSp

            cbDone.setOnCheckedChangeListener(null)
            cbDone.isChecked = task.done

            itemView.setOnClickListener { onClick(task) }
            itemView.setOnLongClickListener {
                onLongClick(task)
                true
            }

            cbDone.setOnCheckedChangeListener { _, isChecked ->
                onToggleDone(task, isChecked)
            }
        }

    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem._id == newItem._id
            override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
        }
    }
}
