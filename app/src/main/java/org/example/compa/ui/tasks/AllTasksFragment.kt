package org.example.compa.ui.tasks

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.AllTasksFragmentBinding
import org.example.compa.models.Group
import org.example.compa.models.Task
import org.example.compa.preferences.AppPreference

class AllTasksFragment : Fragment() {

    private lateinit var binding: AllTasksFragmentBinding

    private var tasks = arrayListOf<Task>()
    private lateinit var tasksAdapter: AllTasksAdapter

    private lateinit var db: FirebaseFirestore
    private var groupsWithId = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = AllTasksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewAllTasks.layoutManager = LinearLayoutManager(requireContext())

        binding.addTask.setOnClickListener {
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        db = FirebaseFirestore.getInstance()
        getMyGroups()
    }

    private fun getTasks() {
        tasks.clear()
        tasksAdapter = AllTasksAdapter(tasks, requireContext())
        db.collection("tasks").get().addOnSuccessListener {
            for (task in it.documents) {
                val hashMap = task.data?.get("group") as HashMap<String, Any>
                val groupId = hashMap["id"] as String
                if (checkGroup(groupId)) {
                    val id = task.data?.get("id") as String
                    val name = task.data?.get("name") as String
                    val startDate = task.data?.get("startDate") as Long
                    val finishDate = task.data?.get("finishDate") as Long
                    val category = task.data?.get("category") as String
                    val description = task.data?.get("description") as String
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
                    tasks.add(task)
                }

            }
            if (tasks.size == 0) {
                binding.noTasks.visibility = View.VISIBLE
                binding.noTasksMessage.visibility = View.VISIBLE
                binding.recyclerViewAllTasks.visibility = View.GONE
            } else {
                binding.noTasks.visibility = View.GONE
                binding.noTasksMessage.visibility = View.GONE
                binding.recyclerViewAllTasks.visibility = View.VISIBLE
            }

            tasksAdapter.notifyDataSetChanged()
        }
        tasksAdapter = AllTasksAdapter(tasks, requireContext())
        binding.recyclerViewAllTasks.adapter = tasksAdapter

        tasksAdapter.setOnItemClickListener(object : AllTasksAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), AddTaskActivity::class.java)
                intent.putExtra("id", tasks[position].id)
                intent.putExtra("name_task", tasks[position].name)
                intent.putExtra("type", 1)
                startActivity(intent)
            }

        })
    }

    private fun getMyGroups() {
        binding.groupsChipGroup.removeAllViews()
        val chip = Chip(requireContext())
        chip.text = getString(R.string.all)
        chip.setChipBackgroundColorResource(R.color.primaryLightColor)
        chip.setTextAppearance(R.style.chipText)
        chip.setOnClickListener {
            getTasks()
        }
        binding.groupsChipGroup.addView(chip)

        db.collection("person").document(AppPreference.getUserUID()).collection("groups").get().addOnSuccessListener {
            for (group in it.documents) {
                val id = group.data?.get("id") as String
                val name = group.data?.get("name") as String
                groupsWithId.add(id)

                val chip = Chip(requireContext())
                chip.text = name
                chip.setChipBackgroundColorResource(R.color.primaryLightColor)
                chip.setTextAppearance(R.style.chipText)

                chip.setOnClickListener {
                    filterByGroup(id)
                }

                binding.groupsChipGroup.addView(chip)
            }
            getTasks()
        }
    }

    private fun filterByGroup(groupId: String) {
        tasks.clear()
        tasksAdapter = AllTasksAdapter(tasks, requireContext())
        db.collection("tasks").get().addOnSuccessListener {
            for (task in it.documents) {
                val hashMap = task.data?.get("group") as HashMap<String, Any>
                val id = hashMap["id"] as String
                if (id == groupId) {
                    val id = task.data?.get("id") as String
                    val name = task.data?.get("name") as String
                    val startDate = task.data?.get("startDate") as Long
                    val finishDate = task.data?.get("finishDate") as Long
                    val category = task.data?.get("category") as String
                    val description = task.data?.get("description") as String
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
                    tasks.add(task)
                }
            }
            tasksAdapter.notifyDataSetChanged()
        }
        tasksAdapter = AllTasksAdapter(tasks, requireContext())
        binding.recyclerViewAllTasks.adapter = tasksAdapter

        tasksAdapter.setOnItemClickListener(object : AllTasksAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), AddTaskActivity::class.java)
                intent.putExtra("id", tasks[position].id)
                intent.putExtra("name_task", tasks[position].name)
                startActivity(intent)
            }

        })
    }

    private fun checkGroup(groupId: String): Boolean {
        for (group in groupsWithId) {
            if (group == groupId) return true
        }
        return false
    }
}