package hr.algebra.todoapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import hr.algebra.todoapp.R

enum class TasksFilter { ALL, ACTIVE, DONE }

class TasksPagerFragment : Fragment(R.layout.fragment_tasks_pager) {


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
}
