package org.example.compa.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.my_tasks_fragment.*
import org.example.compa.R
import org.example.compa.db.CompaSQLiteOpenHelper
import org.example.compa.models.Task

class MyTasksFragment : Fragment(), TasksAdapter.OnItemClickListener {

    var listTasks = arrayListOf<Task>()
    private var myTasks = arrayListOf<Task>()
    private var tasksAdapter = TasksAdapter(arrayListOf(), this)
    private var username = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_tasks_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            username = requireArguments().getString("username").toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser()
        initializeTasks()
        filterTaskByUsername(username)
        recycler_view_tasks.layoutManager = LinearLayoutManager(requireContext())
        tasksAdapter = TasksAdapter(myTasks, this)
        recycler_view_tasks.adapter = tasksAdapter
    }

    override fun onResume() {
        super.onResume()
        tasksAdapter.notifyDataSetChanged()
    }

    private fun getUser() {
        val db = CompaSQLiteOpenHelper(requireContext(), "dbCompa", null, CompaSQLiteOpenHelper.DATABASE_VERSION)
        val dbCompa = db.writableDatabase

        val row = dbCompa.rawQuery("SELECT username FROM user", null)
        if (row.moveToFirst()) {
            username = row.getString(0)
        }

        dbCompa.close()
    }

    private fun initializeTasks() {
        val db = CompaSQLiteOpenHelper(requireContext(), "dbCompa", null, CompaSQLiteOpenHelper.DATABASE_VERSION)
        val dbCompa = db.writableDatabase

        var task: Task

        val row = dbCompa.rawQuery("SELECT _id, name, startDate, finishDate, category, members, description FROM task", null)

        while (row.moveToNext()) {
            val members = row.getString(5).split(", ")
            /*val startDate = Date(row.getString(2).toLong())
            val finishDate = Date(row.getString(3).toLong())
            val df2 = SimpleDateFormat("dd/MM/yyyy")*/
            /*task = Task(
                id = row.getInt(0),
                name = row.getString(1),
                startDate = row.getString(2),
                finishDate = row.getString(3),
                category = row.getString(4),
                members = members,
                numberMembers = members.size,
                description = row.getString(6)
            )*/
            //listTasks.add(task)
        }

        dbCompa.close()
    }

    private fun filterTaskByUsername(username: String) {
        for (task in listTasks) {
            val listMembers = task.members
            for (member in listMembers) {
                /*if (member == username) {
                    myTasks.add(task)
                }*/
            }
        }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(requireContext(), TaskDetailActivity::class.java)
        intent.putExtra("id", myTasks[position].id)
        startActivity(intent)
    }

}