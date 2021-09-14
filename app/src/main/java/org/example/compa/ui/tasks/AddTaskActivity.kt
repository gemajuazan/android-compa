package org.example.compa.ui.tasks

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.AddTaskActivityBinding
import org.example.compa.databinding.FriendsListBinding
import org.example.compa.databinding.TextListBinding
import org.example.compa.models.*
import org.example.compa.models.constants.Constants.StatusTask.Companion.FINISHED
import org.example.compa.models.constants.Constants.StatusTask.Companion.FOR_DOING
import org.example.compa.models.constants.Constants.StatusTask.Companion.IN_PROCESS
import org.example.compa.models.constants.Constants.StatusTask.Companion.IN_REVISION
import org.example.compa.models.constants.Constants.StatusTask.Companion.getStatusTask
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.adapters.*
import org.example.compa.utils.MaterialDialog
import org.example.compa.utils.StyleUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: AddTaskActivityBinding

    private var type: Int = 0

    private val myCalendarStart = Calendar.getInstance()
    private val myCalendarFinish = Calendar.getInstance()
    private var timeStart: Long? = 0
    private var timeFinish: Long? = 0

    private lateinit var db: FirebaseFirestore
    private var group: Group? = null
    private var membersTask: ArrayList<MemberTask> = arrayListOf()
    private val taskId = StyleUtil.getRandomString(16)

    private val members = ArrayList<Member>()
    private var friends = arrayListOf<Friend>()
    private var groups = arrayListOf<Group>()

    private var friendAdapter = FriendAdapter(arrayListOf(), false)

    private lateinit var membersAdapter: MemberTaskAdapter
    private lateinit var statusTaskAdapter: TextElementAdapter
    private lateinit var groupsAdapter: TextGroupAdapter

    private var statusTask: ArrayList<String> = arrayListOf()

    private var groupSelected: Group? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTaskActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInitConfiguration()
        setOnClickListeners()
    }

    private fun setInitConfiguration() {
        db = FirebaseFirestore.getInstance()
        membersAdapter = MemberTaskAdapter(arrayListOf(), this)
        setStatusList()
        setToolbar()
        setStartDateCalendar()
        setFinishDateCalendar()
        setOnClickListeners()
        getGroups()
    }

    private fun setStatusList() {
        statusTask.add(getString(getStatusTask(FOR_DOING)))
        statusTask.add(getString(getStatusTask(IN_PROCESS)))
        statusTask.add(getString(getStatusTask(IN_REVISION)))
        statusTask.add(getString(getStatusTask(FINISHED)))
    }

    private fun getGroups() {
        groupsAdapter = TextGroupAdapter(arrayListOf())
        db.collection("person").document(AppPreference.getUserUID()).collection("groups").get()
            .addOnSuccessListener {
                for (group in it.documents) {
                    val id = group.data?.get("id") as String
                    val place = group.data?.get("place") as String
                    val name = group.data?.get("name") as String
                    groups.add(Group(id, name, place))
                    groupsAdapter.notifyDataSetChanged()

                }
                groupsAdapter = TextGroupAdapter(groups)

            }
        binding.groupEditText.setOnClickListener {
            val dialogBinding = TextListBinding.inflate(layoutInflater)
            dialogBinding.elementsAdapter.layoutManager = LinearLayoutManager(this)
            dialogBinding.elementsAdapter.adapter = groupsAdapter

            val dialog = MaterialDialog.createDialog(this@AddTaskActivity) {
                setTitle(R.string.select_group)
                setView(dialogBinding.root)
            }

            dialog.show()

            groupsAdapter.setOnItemClickListener(object : TextGroupAdapter.OnItemClickListener {
                override fun onItemClick(group: Group) {
                    binding.groupEditText.setText(group.name)
                    groupSelected = group
                    dialog.dismiss()
                }

            })
        }

    }

    private fun setToolbar() {
        if (type == 0) {
            binding.compaToolbar.profileSubtitle.text = getString(R.string.add_task)
            binding.saveTask.visibility = View.VISIBLE
            binding.addMember.visibility = View.VISIBLE
        } else {
            val nameTask = intent.getStringExtra("name_task") ?: ""
            binding.compaToolbar.profileSubtitle.text = nameTask
            binding.addMember.visibility = View.GONE
            binding.saveTask.visibility = View.GONE
        }

        binding.compaToolbar.backButtonImageView.setOnClickListener {
            finish()
        }
    }

    private fun setStartDateCalendar() {
        val dateStart =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendarStart.set(Calendar.YEAR, year)
                myCalendarStart.set(Calendar.MONTH, monthOfYear)
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelStart()
            }
        binding.dateStartEditText.setOnClickListener {
            DatePickerDialog(
                this@AddTaskActivity,
                dateStart,
                myCalendarStart[Calendar.YEAR],
                myCalendarStart[Calendar.MONTH],
                myCalendarStart[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun setFinishDateCalendar() {
        val dateFinish =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendarFinish.set(Calendar.YEAR, year)
                myCalendarFinish.set(Calendar.MONTH, monthOfYear)
                myCalendarFinish.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelFinish()
            }
        binding.dateFinishEditText.setOnClickListener {
            DatePickerDialog(
                this@AddTaskActivity,
                dateFinish,
                myCalendarFinish[Calendar.YEAR],
                myCalendarFinish[Calendar.MONTH],
                myCalendarFinish[Calendar.DAY_OF_MONTH]
            ).show()
        }
    }

    private fun setOnClickListeners() {
        binding.addMember.setOnClickListener {
            binding.addMemberLinearLayout.visibility = View.VISIBLE
        }

        binding.membersTaskRecyclerView.setOnClickListener {
            addMembertOflist()
        }

        binding.saveTask.setOnClickListener {
            saveTask()
        }
    }

    private fun addMembertOflist() {
        val dialogBinding = FriendsListBinding.inflate(layoutInflater)
        val dialog = MaterialDialog.createDialog(this@AddTaskActivity) {
            setTitle(R.string.select_friend)
            setView(dialogBinding.root)
        }

        dialogBinding.friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        dialogBinding.friendsRecyclerView.visibility = View.VISIBLE
        friendAdapter = FriendAdapter(friends, false)
        dialogBinding.friendsRecyclerView.adapter = friendAdapter
        friendAdapter.setOnItemClickListener(object : FriendAdapter.ItemClickListener {
            override fun onItemClicked(person: Person, position: Int) {
                binding.memberEditText.setText(person.username)
                dialog.dismiss()
                binding.addMemberToList.setOnClickListener {
                    MaterialDialog.createDialog(this@AddTaskActivity) {
                        setTitle(getString(R.string.add_member_button))
                        setMessage(getString(R.string.do_you_want_to_add_this_compa))
                        setPositiveButton(getString(R.string.accept)) { _, _ ->
                            members.add(
                                Member(
                                    person.id,
                                    name = person.name + " " + person.surnames,
                                    username = person.username,
                                    email = person.email
                                )
                            )
                            getMembers()
                            binding.memberEditText.setText("")
                            binding.memberEditText.clearFocus()
                            dialog.dismiss()
                        }
                        setNegativeButton(getString(R.string.no_no_no)) { _, _ -> }
                    }.show()
                }
            }

            override fun onItemAddClicked(person: Person, position: Int) {
                // Ignore
            }

            override fun onItemLongClicked(person: Person, position: Int) {
                // Ignore
            }
        })
        dialog.show()
    }

    private fun getMembers() {
        membersAdapter = MemberTaskAdapter(membersTask, this)

        membersAdapter.setOnItemClickListener(object : MemberTaskAdapter.ItemClickListener {
            override fun onEditClicked(person: MemberTask, position: Int, textView: TextView) {
                val dialogBinding = TextListBinding.inflate(layoutInflater)
                dialogBinding.elementsAdapter.layoutManager =
                    LinearLayoutManager(this@AddTaskActivity)
                statusTaskAdapter = TextElementAdapter(statusTask)
                dialogBinding.elementsAdapter.adapter = statusTaskAdapter
                statusTaskAdapter.setOnItemClickListener(object :
                    TextElementAdapter.OnItemClickListener {
                    override fun onItemClick(statusCode: String) {
                        textView.text = getString(getStatusTask(statusCode))
                        updateStatus(person, statusCode)
                    }
                })
            }
        })
        binding.membersTaskRecyclerView.adapter = membersAdapter
        if (membersTask.size == 1) {
            binding.noMembers.visibility = View.VISIBLE
        } else {
            binding.noMembers.visibility = View.GONE
        }
    }

    private fun updateStatus(memberTask: MemberTask, newCode: String) {
        db.collection("tasks_status").document(taskId).collection("members")
            .document(memberTask.member.id).update("statusTask", newCode)
    }

    private fun saveTask() {
        val name = binding.nameEditText.text.toString()
        val dateStart = binding.nameEditText.text.toString()
        val dateFinish = binding.nameEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val category = binding.categoryEditText.text.toString()
        val task = Task(
            id = taskId,
            name = name,
            startDate = dateStart,
            finishDate = dateFinish,
            group = group!!,
            category = category,
            description = description
        )

        val taskStatus = TaskStatus(taskId, 0)
        db.collection("tasks").document(taskId).set(task)
        db.collection("tasks_status").document(taskId).set(taskStatus)
        for (member in membersTask) {
            db.collection("task_status").document(taskId).collection("members")
                .document(member.member.id).set(member)
        }
    }

    /*private fun setData() {
        addTaskButton = findViewById(R.id.add_task_button)
        addTaskName = findViewById(R.id.add_task_name_task_edit)
        addTaskStartDate = findViewById(R.id.add_task_start_date_edit)
        addTaskFinishDate = findViewById(R.id.add_task_finish_date_edit)
        addTaskCategory = findViewById(R.id.add_task_category_edit)
        addTaskDescription = findViewById(R.id.add_task_description_edit)
        addTaskMembers = findViewById(R.id.add_task_members_edit)
    }

    private fun setOnClickListeners() {
        val dateStart =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendarStart.set(Calendar.YEAR, year)
                myCalendarStart.set(Calendar.MONTH, monthOfYear)
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelStart()
            }
        val dateFinish =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendarFinish.set(Calendar.YEAR, year)
                myCalendarFinish.set(Calendar.MONTH, monthOfYear)
                myCalendarFinish.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabelFinish()
            }

        addTaskStartDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                DatePickerDialog(
                    this@AddTaskActivity,
                    dateStart,
                    myCalendarStart[Calendar.YEAR],
                    myCalendarStart[Calendar.MONTH],
                    myCalendarStart[Calendar.DAY_OF_MONTH]
                ).show()
            }
        })

        addTaskFinishDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                DatePickerDialog(
                    this@AddTaskActivity,
                    dateFinish,
                    myCalendarFinish[Calendar.YEAR],
                    myCalendarFinish[Calendar.MONTH],
                    myCalendarFinish[Calendar.DAY_OF_MONTH]
                ).show()
            }
        })

        addTaskButton.setOnClickListener {
            addNewTaskToDatabase()
        }
    }

    private fun addNewTaskToDatabase() {
        val db = CompaSQLiteOpenHelper(this, "dbCompa", null, CompaSQLiteOpenHelper.DATABASE_VERSION)
        val dbCompa = db.writableDatabase

        val name = addTaskName.text.toString()
        val startDate = addTaskStartDate.text.toString()
        val finishDate = addTaskFinishDate.text.toString()
        val category = addTaskCategory.text.toString()
        val description = addTaskDescription.text.toString()
        val members = addTaskMembers.text.toString()

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        try {
            val dStartDate: Date = sdf.parse(startDate)
            val dFinishDate: Date = sdf.parse(finishDate)
            timeStart = dStartDate.time
            timeFinish = dFinishDate.time
        } catch (e: ParseException) {
            e.printStackTrace();
        }

        val array = members.split(", ")

        val register = ContentValues()
        register.put("name", name)
        register.put("startDate", timeStart)
        register.put("finishDate", timeFinish)
        register.put("category", category)
        register.put("description", description)
        register.put("numMembers", array.size)
        register.put("members", members)

        dbCompa.insert("task", null, register)
        dbCompa.close()

        MaterialDialog.createDialog(this) {
            setTitle(getString(R.string.new_task))
            setMessage(getString(R.string.new_task_info))
            setPositiveButton(getString(R.string.dabuti)) { dialog, _ ->
                dialog.cancel()
                finish()
            }
        }.show()
    }*/

    private fun updateLabelStart() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        binding.dateStartEditText.setText(sdf.format(myCalendarStart.timeInMillis))
    }

    private fun updateLabelFinish() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        binding.dateFinishEditText.setText(sdf.format(myCalendarFinish.timeInMillis))
    }
}