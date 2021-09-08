package org.example.compa.ui.tasks

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.all_tasks_fragment.*
import org.example.compa.R
import org.example.compa.models.Member
import org.example.compa.models.Task

class AllTasksFragment : Fragment(), TasksAdapter.OnItemClickListener {

    private var listTasks = arrayListOf<Task>()
    private var myTasks = arrayListOf<Task>()
    private var listMembers = arrayListOf<Member>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.all_tasks_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeTasks()
        recycler_view_all_tasks.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_all_tasks.adapter = TasksAdapter(listTasks, this)
    }

    override fun onResume() {
        super.onResume()
        recycler_view_all_tasks.adapter?.notifyDataSetChanged()
    }

    private fun initializeTasks() {
        listMembers.add(Member(0, "Gema Juárez", "gemajuazan", ""))
        val task = Task(0, "Fregar platos", "05/04/2021", "05/04/2021", "Cocina", 1, listMembers, "Faena dura", false)
        listTasks.add(task)

        val task2 = Task(0, "Fregar platos", "05/04/2021", "05/04/2021", "Cocina", 1, listMembers, "Faena dura", true)
        listTasks.add(task2)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(requireContext(), TaskDetailActivity::class.java)
        intent.putExtra("id", listTasks[position].id)
        startActivity(intent)
    }
}