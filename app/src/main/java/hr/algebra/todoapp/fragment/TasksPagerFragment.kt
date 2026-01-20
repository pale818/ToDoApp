package hr.algebra.todoapp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import hr.algebra.todoapp.R
import hr.algebra.todoapp.AddEditTaskActivity
import hr.algebra.todoapp.TODO_PROVIDER_CONTENT_URI
import android.app.AlertDialog
import android.util.Log
import hr.algebra.todoapp.SettingsActivity
import hr.algebra.todoapp.TasksReceiver

enum class TasksFilter { ALL, ACTIVE, DONE }

class TasksPagerFragment : Fragment(R.layout.fragment_tasks_pager) {

    private var sortMode = 0 // 0 = newest, 1 = title

    private fun notifyTabsToRefresh() {
        val current = childFragmentManager.fragments
            .firstOrNull { it is TasksFragment && it.isVisible } as? TasksFragment
        current?.setSortMode(sortMode)
    }

    private val settingsLauncher =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) {
            // force visible list to rebind items so font size is reapplied
            childFragmentManager.fragments
                .filterIsInstance<TasksFragment>()
                .forEach { it.refreshListUI() }
        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById<com.google.android.material.tabs.TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPager)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = 3

            override fun createFragment(position: Int): Fragment {
                val filter = when (position) {
                    0 -> TasksFilter.ALL
                    1 -> TasksFilter.ACTIVE
                    else -> TasksFilter.DONE
                }
                return TasksFragment.newInstance(filter)
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "All"
                1 -> "Active"
                else -> "Done"
            }
        }.attach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tasks, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(requireContext(), AddEditTaskActivity::class.java))
                true
            }
            R.id.action_sort -> {
                // We'll implement in step 3 below
                sortMode = (sortMode + 1) % 2
                notifyTabsToRefresh()
                true
            }
            R.id.action_clear_completed -> {
                confirmClearCompleted()
                true
            }
            R.id.action_settings -> {
                settingsLauncher.launch(Intent(requireContext(), SettingsActivity::class.java))
                true
            }

            R.id.action_test_receiver -> {
                Log.d("TaskPagerFragment", "Alarm/trigger fired")

                val i = Intent(requireContext(), TasksReceiver::class.java).apply {
                    putExtra("TASK_ID", 999L)
                    putExtra("TITLE", "Receiver test")
                    putExtra("TEXT", "Alarm fired → receiver ran.")
                }
                requireContext().sendBroadcast(i)
                true
            }



            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun notifyAllTabsReload() {
        childFragmentManager.fragments
            .filterIsInstance<TasksFragment>()
            .forEach { it.reload() }
    }

    private fun confirmClearCompleted() {
        AlertDialog.Builder(requireContext())
            .setTitle("Clear completed?")
            .setMessage("All completed tasks will be removed.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Clear") { _, _ ->
                clearCompleted()  // <- your existing clear completed logic
            }
            .show()
    }
    private fun clearCompleted() {
        // easiest: launch an intent/dialog later; for now just run delete logic
        val ctx = requireContext().applicationContext
        ctx.contentResolver.delete(
            TODO_PROVIDER_CONTENT_URI,
            "done = ?",
            arrayOf("1")
        )
        notifyAllTabsReload()
    }


}
