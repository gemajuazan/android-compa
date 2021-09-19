package org.example.compa.ui.tasks

import android.content.Intent
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.tasks_activity.*
import org.example.compa.R
import org.example.compa.databinding.TasksActivityBinding

class TasksActivity : AppCompatActivity() {

    private lateinit var binding: TasksActivityBinding
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TasksActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setToolbar()

        if (intent.hasExtra("username")) username = intent.getStringExtra("username")!!

        binding.addTask.setOnClickListener {
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        binding.backButtonImageView.setOnClickListener {
            finish()
        }

        var tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(view_pager)

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = MiPagerAdapter(supportFragmentManager)
    }

    class MiPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> {
                    fragment = MyTasksFragment()
                }
                1 -> {
                    fragment = AllTasksFragment()
                }
            }
            return fragment!!
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> {
                    "Mis tareas"
                }
                else -> {
                    "Todas"
                }
            }
        }

    }

    /*private fun setToolbar() {
        toolbar2.title = getString(R.string.menu_tasks)
        toolbar2.setNavigationOnClickListener {
            finish()
        }
    }*/
}