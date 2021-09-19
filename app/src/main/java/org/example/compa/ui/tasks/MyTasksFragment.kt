package org.example.compa.ui.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.databinding.MyTasksFragmentBinding
import org.example.compa.models.Group
import org.example.compa.models.Task
import org.example.compa.preferences.AppPreference

class MyTasksFragment : Fragment() {

    private lateinit var binding: MyTasksFragmentBinding

    private var myTasks = arrayListOf<Task>()
    private var myTaskByMyUser = arrayListOf<String>()
    private lateinit var tasksAdapter: TasksAdapter

    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyTasksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        db = FirebaseFirestore.getInstance()
        getMyTask()
    }

    private fun getTasks() {
        myTasks.clear()
        for (idTask in myTaskByMyUser) {
            db.collection("tasks").document(idTask).get().addOnSuccessListener {
                val hashMap = it.data?.get("group") as HashMap<String, Any>
                val groupId = hashMap["id"] as String
                val id = it.data?.get("id") as String
                val name = it.data?.get("name") as String
                val startDate = it.data?.get("startDate") as Long
                val finishDate = it.data?.get("finishDate") as Long
                val category = it.data?.get("category") as String
                val description = it.data?.get("description") as String
                val nameGroup = hashMap["name"] as String
                val task = Task(
                    id = id,
                    name = name,
                    startDate = startDate,
                    finishDate = finishDate,
                    category = category,
                    members = arrayListOf(),
                    description = description,
                    group = Group(groupId, nameGroup, "")
                )
                myTasks.add(task)
                tasksAdapter.notifyDataSetChanged()
            }
            //tasksAdapter.notifyDataSetChanged()
        }
        tasksAdapter = TasksAdapter(myTasks, requireContext())
        binding.recyclerViewTasks.adapter = tasksAdapter

        tasksAdapter.setOnItemClickListener(object : TasksAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), AddTaskActivity::class.java)
                intent.putExtra("id", myTasks[position].id)
                startActivity(intent)
            }

        })
    }

    private fun getMyTask() {
        db.collection("tasks_status").get().addOnSuccessListener {
            for ((index, task) in it.documents.withIndex()) {
                val id = task.data?.get("id") as String
                db.collection("tasks_status").document(id).collection("members").get().addOnSuccessListener { members ->
                    for (member in members.documents) {
                        val hashMap = member.data?.get("member") as HashMap<String, Any>
                        val idMember = hashMap["id"] as String
                        if (idMember == AppPreference.getUserUID()) {
                            myTaskByMyUser.add(id)
                        }
                        if (index == it.documents.size - 1) getTasks()
                    }
                }

            }
        }
    }

}