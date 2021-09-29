package org.example.compa.ui.tasks

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.AddTaskActivityBinding
import org.example.compa.databinding.FriendsListBinding
import org.example.compa.databinding.TextListBinding
import org.example.compa.models.*
import org.example.compa.models.constants.Constants.CategoryTask.Companion.BUYING
import org.example.compa.models.constants.Constants.CategoryTask.Companion.CLEAN
import org.example.compa.models.constants.Constants.CategoryTask.Companion.CLEAN_CLOTHES
import org.example.compa.models.constants.Constants.CategoryTask.Companion.ENTERTAINMENT
import org.example.compa.models.constants.Constants.CategoryTask.Companion.HANG_UP
import org.example.compa.models.constants.Constants.CategoryTask.Companion.IRON
import org.example.compa.models.constants.Constants.CategoryTask.Companion.KITCHEN
import org.example.compa.models.constants.Constants.CategoryTask.Companion.TRASH
import org.example.compa.models.constants.Constants.CategoryTask.Companion.WASH
import org.example.compa.models.constants.Constants.CategoryTask.Companion.getCategoryTask
import org.example.compa.models.constants.Constants.StatusTask.Companion.FINISHED
import org.example.compa.models.constants.Constants.StatusTask.Companion.FOR_DOING
import org.example.compa.models.constants.Constants.StatusTask.Companion.IN_PROCESS
import org.example.compa.models.constants.Constants.StatusTask.Companion.IN_REVISION
import org.example.compa.models.constants.Constants.StatusTask.Companion.getStatusTask
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.adapters.*
import org.example.compa.utils.DataUtil.Companion.getPersonFromDatabaseHashMap
import org.example.compa.utils.DateUtil
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

    private lateinit var db: FirebaseFirestore
    private var membersTask: ArrayList<MemberTask> = arrayListOf()
    private val taskId = StyleUtil.getRandomString(16)

    private val members = ArrayList<Member>()
    private var friends = arrayListOf<Friend>()
    private var groups = arrayListOf<Group>()
    private var categories = arrayListOf<DataWithCode>()

    private var friendAdapter = FriendAdapter(arrayListOf(), false)

    private lateinit var membersAdapter: MemberTaskAdapter
    private lateinit var statusTaskAdapter: TextElementAdapter
    private lateinit var categoriesTaskAdapter: TextElementAdapter
    private lateinit var groupsAdapter: TextGroupAdapter

    private var statusTask: ArrayList<DataWithCode> = arrayListOf()

    private var groupSelected: Group? = null
    private var categorySelected: String? = null

    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddTaskActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInitConfiguration()
        setOnClickListeners()
    }

    private fun setInitConfiguration() {
        initFirebase()
        setRecyclerViewMembers()
        getIntents()
        getTask()
        getFriends()
        setStatusList()
        setCurrentUserInList()
        setToolbar()
        setStartDateCalendar()
        setFinishDateCalendar()
        setOnClickListeners()
        getGroups()
        setCategoryList()
    }

    private fun getIntents() {
        type = intent.getIntExtra("type", 0)
    }

    private fun initFirebase() {
        db = FirebaseFirestore.getInstance()
    }

    private fun setRecyclerViewMembers() {
        binding.membersTaskRecyclerView.layoutManager = LinearLayoutManager(this)
        membersAdapter = MemberTaskAdapter(arrayListOf(), this)
        binding.membersTaskRecyclerView.adapter = membersAdapter
    }

    private fun getTask() {
        val id = intent.getStringExtra("id") ?: ""
        if (id != "") {
            db.collection("tasks").document(id).get().addOnSuccessListener {
                getTaskFromDatabase(it, id)
                setTask()
            }
            db.collection("tasks_status").document(id).collection("members").get()
                .addOnSuccessListener {
                    for (member in it.documents) {
                        val hashMap = member.data?.get("member") as HashMap<String, Any>
                        val status = member.data?.get("statusTask") as String
                        val newMember = getMember(hashMap)
                        membersTask.add(
                            MemberTask(newMember, status)
                        )
                    }
                    getMembers()
                }
        }
    }

    private fun setTask() {
        binding.nameEditText.setText(task?.name)
        binding.groupEditText.setText(task?.group?.name)
        binding.dateStartEditText.setText(task?.startDate?.let { it1 ->
            DateUtil.getDate(
                it1,
                "dd/MM/yy"
            )
        })
        binding.dateFinishEditText.setText(task?.finishDate?.let { it1 ->
            DateUtil.getDate(
                it1,
                "dd/MM/yy"
            )
        })
        binding.categoryEditText.setText(getCategoryTask(task?.category ?: ""))
        binding.descriptionEditText.setText(task?.description)
        binding.groupEditText.setText(task?.name)
    }

    private fun getTaskFromDatabase(it: DocumentSnapshot, id: String) {
        val name = it.data?.get("name") as String
        val startDate = it.data?.get("startDate") as Long
        val finishDate = it.data?.get("finishDate") as Long
        val category = it.data?.get("category") as String
        val description = it.data?.get("description") as String
        val hashMap = it.data?.get("group") as HashMap<String, Any>
        val groupId = hashMap["id"] as String
        val groupName = hashMap["name"] as String
        val groupPlace = hashMap["place"] as String
        val group = Group(groupId, groupName, groupPlace)
        groupSelected = group
        categorySelected = category
        task = Task(
            id = id,
            name = name,
            startDate = startDate,
            finishDate = finishDate,
            category = category,
            description = description,
            group = group
        )
    }

    private fun getMember(hashMap: HashMap<String, Any>): Member {
        val id = hashMap["id"] as String
        val name = hashMap["name"] as String
        val username = hashMap["username"] as String
        val email = hashMap["email"] as String
        return Member(
            id,
            name = name,
            username = username,
            email = email
        )
    }

    private fun setCurrentUserInList() {
        if (type == 0) {
            val member = Member(
                AppPreference.getUserUID(),
                AppPreference.getUserName(),
                AppPreference.getUserUsername(),
                AppPreference.getUserEmail()
            )
            members.add(member)
            membersTask.add(MemberTask(member, FOR_DOING))
            getMembers()
        }
    }

    private fun setStatusList() {
        statusTask.add(DataWithCode(getString(getStatusTask(FOR_DOING)), FOR_DOING))
        statusTask.add(DataWithCode(getString(getStatusTask(IN_PROCESS)), IN_PROCESS))
        statusTask.add(DataWithCode(getString(getStatusTask(IN_REVISION)), IN_REVISION))
        statusTask.add(DataWithCode(getString(getStatusTask(FINISHED)), FINISHED))
    }

    private fun setCategoryList() {
        categories.add(DataWithCode(getString(getCategoryTask(CLEAN)), CLEAN))
        categories.add(DataWithCode(getString(getCategoryTask(BUYING)), BUYING))
        categories.add(DataWithCode(getString(getCategoryTask(TRASH)), TRASH))
        categories.add(DataWithCode(getString(getCategoryTask(WASH)), WASH))
        categories.add(DataWithCode(getString(getCategoryTask(KITCHEN)), KITCHEN))
        categories.add(DataWithCode(getString(getCategoryTask(IRON)), IRON))
        categories.add(DataWithCode(getString(getCategoryTask(CLEAN_CLOTHES)), CLEAN_CLOTHES))
        categories.add(DataWithCode(getString(getCategoryTask(HANG_UP)), HANG_UP))
        categories.add(DataWithCode(getString(getCategoryTask(ENTERTAINMENT)), ENTERTAINMENT))
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

    }

    private fun setToolbar() {
        if (type == 0) {
            binding.compaToolbar.title.text = getString(R.string.add_task)
            binding.saveTask.visibility = View.VISIBLE
            binding.addMember.visibility = View.VISIBLE
            binding.noMembers.visibility = View.VISIBLE
        } else {
            val nameTask = intent.getStringExtra("name_task") ?: ""
            binding.compaToolbar.title.text = nameTask
            binding.compaToolbar.arrorBack.setOnClickListener { finish() }
            binding.addMember.visibility = View.VISIBLE
            binding.saveTask.visibility = View.VISIBLE
        }

        binding.compaToolbar.arrorBack.setOnClickListener {
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
        addMemberOnClickListener()
        saveTaskOnClickListener()
        groupOnClickListener()
        memberOnClickListener()
        categoryOnClickListener()
        addExternalCalendarOnClickListener()
    }

    private fun addMemberOnClickListener() {
        binding.addMember.setOnClickListener {
            binding.addMemberLinearLayout.visibility = View.VISIBLE
        }
    }

    private fun saveTaskOnClickListener() {
        binding.saveTask.setOnClickListener {
            if (type != 0) {
                if (checkFields()) updateInfo()
                else {
                    MaterialDialog.createDialog(this) {
                        setMessage(getString(R.string.check_fields_info))
                    }.show()
                }
            } else {
                if (checkFields()) saveTask()
                else {
                    MaterialDialog.createDialog(this) {
                        setMessage(getString(R.string.check_fields_info))
                    }.show()
                }
            }
        }
    }

    private fun groupOnClickListener() {
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

    private fun memberOnClickListener() {
        binding.memberEditText.setOnClickListener {
            addMembertOflist()
        }
    }

    private fun categoryOnClickListener() {
        binding.categoryEditText.setOnClickListener {
            val dialogBinding = TextListBinding.inflate(layoutInflater)
            dialogBinding.elementsAdapter.layoutManager =
                LinearLayoutManager(this@AddTaskActivity)
            categoriesTaskAdapter = TextElementAdapter(categories)
            dialogBinding.elementsAdapter.adapter = categoriesTaskAdapter

            val dialog = MaterialDialog.createDialog(this@AddTaskActivity) {
                setTitle(R.string.select_category)
                setView(dialogBinding.root)
            }

            dialog.show()

            categoriesTaskAdapter.setOnItemClickListener(object :
                TextElementAdapter.OnItemClickListener {
                override fun onItemClick(statusCode: String) {
                    binding.categoryEditText.setText(getString(getCategoryTask(statusCode)))
                    categorySelected = statusCode
                    dialog.dismiss()
                }
            })
        }
    }

    private fun addExternalCalendarOnClickListener() {
        binding.addExternalCalendar.setOnClickListener {
            if (checkFields()) setDataToExternalCalendar()
            else {
                MaterialDialog.createDialog(this) {
                    setMessage(getString(R.string.check_fields_info))
                }.show()
            }
        }
    }

    private fun setDataToExternalCalendar() {
        val insertCalendarIntent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, binding.nameEditText.text.toString())
            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, myCalendarStart.timeInMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, myCalendarFinish.timeInMillis)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, binding.groupEditText.text.toString())
            .putExtra(
                CalendarContract.Events.DESCRIPTION,
                binding.descriptionEditText.text.toString()
            )
            .putExtra(Intent.EXTRA_EMAIL, AppPreference.getUserEmail())
            .putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE)
            .putExtra(
                CalendarContract.Events.AVAILABILITY,
                CalendarContract.Events.AVAILABILITY_FREE
            )
        startActivity(insertCalendarIntent)
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
                            membersTask.add(
                                MemberTask(
                                    Member(
                                        person.id,
                                        name = person.name + " " + person.surnames,
                                        username = person.username,
                                        email = person.email
                                    ), FOR_DOING
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
        binding.membersTaskRecyclerView.layoutManager = LinearLayoutManager(this@AddTaskActivity)
        membersAdapter = MemberTaskAdapter(membersTask, this@AddTaskActivity)
        membersAdapter.notifyDataSetChanged()
        binding.membersTaskRecyclerView.adapter = membersAdapter
        setOnClickListenerMembers()
    }

    private fun setOnClickListenerMembers() {
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
    }

    private fun updateStatus(memberTask: MemberTask, newCode: String) {
        db.collection("tasks_status").document(taskId).collection("members")
            .document(memberTask.member.id).update("statusTask", newCode)
    }

    private fun updateInfo() {
        db.collection("tasks").document(intent.getStringExtra("id") ?: "")
            .update("name", binding.nameEditText.text.toString())
        db.collection("tasks").document(intent.getStringExtra("id") ?: "")
            .update("description", binding.descriptionEditText.text.toString())
        db.collection("tasks").document(intent.getStringExtra("id") ?: "")
            .update("group", groupSelected)
        db.collection("tasks").document(intent.getStringExtra("id") ?: "")
            .update("dateStart", myCalendarStart.timeInMillis)
        db.collection("tasks").document(intent.getStringExtra("id") ?: "")
            .update("dateFinish", myCalendarFinish.timeInMillis)
        db.collection("tasks").document(intent.getStringExtra("id") ?: "")
            .update("category", categorySelected)
        finish()
    }

    private fun checkFields(): Boolean {
        if (binding.nameEditText.text.toString().isBlank()) {
            binding.nameTextInputLayout.error = getString(R.string.empty_field)
            return false
        }

        if (binding.groupEditText.text.toString().isBlank()) {
            binding.groupTextInputLayout.error = getString(R.string.empty_field)
            return false
        }

        if (binding.dateStartEditText.text.toString().isBlank()) {
            binding.dateStartTextInputLayout.error = getString(R.string.empty_field)
            return false
        }

        if (binding.dateFinishEditText.text.toString().isBlank()) {
            binding.dateFinishTextInputLayout.error = getString(R.string.empty_field)
            return false
        }

        if (binding.descriptionEditText.text.toString().isBlank()) {
            binding.descriptionTextInputLayout.error = getString(R.string.empty_field)
            return false
        }

        if (binding.categoryEditText.text.toString().isBlank()) {
            binding.categoryTextInputLayout.error = getString(R.string.empty_field)
            return false
        }

        return true
    }

    private fun saveTask() {
        val name = binding.nameEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val category = categorySelected
        val task = Task(
            id = taskId,
            name = name,
            startDate = myCalendarStart.timeInMillis,
            finishDate = myCalendarFinish.timeInMillis,
            group = groupSelected!!,
            category = category!!,
            description = description
        )

        val taskStatus = TaskStatus(taskId, 0)
        db.collection("tasks").document(taskId).set(task)
        db.collection("tasks_status").document(taskId).set(taskStatus)
        for (member in membersTask) {
            db.collection("tasks_status").document(taskId).collection("members")
                .document(member.member.id).set(member)
        }
        finish()
    }

    private fun getFriends() {
        db.collection("person").document(AppPreference.getUserUID()).collection("friends").get()
            .addOnSuccessListener {
                for (data in it.documents) {
                    val hashMap = data.data?.get("person") as HashMap<String, Any>
                    val solicitude = data.get("solicitude") as Boolean
                    val favourite = data.get("favourite") as Boolean
                    val person = getPersonFromDatabaseHashMap(hashMap)
                    val friend = Friend(person, solicitude = solicitude, favourite = favourite)
                    friends.add(friend)
                }
            }
    }

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