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
import org.example.compa.db.CompaSQLiteOpenHelper
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
        val db = CompaSQLiteOpenHelper(requireContext(), "dbCompa", null, CompaSQLiteOpenHelper.DATABASE_VERSION)
        val dbCompa = db.writableDatabase

        var task: Task

        val row = dbCompa.rawQuery("SELECT _id, name, startDate, finishDate, category, members, description FROM task", null)

        while (row.moveToNext()) {
            val members = row.getString(5).split(", ")
            task = Task(
                id = row.getInt(0),
                name = row.getString(1),
                startDate = row.getString(2),
                finishDate = row.getString(3),
                category = row.getString(4),
                members = members,
                numberMembers = members.size,
                description = row.getString(6)
            )
            listTasks.add(task)
        }

        dbCompa.close()
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(requireContext(), TaskDetailActivity::class.java)
        intent.putExtra("id", listTasks[position].id)
        startActivity(intent)
    }
}