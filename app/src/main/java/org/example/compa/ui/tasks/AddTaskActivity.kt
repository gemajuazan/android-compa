package org.example.compa.ui.tasks

import android.app.DatePickerDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.add_task_activity.*
import org.example.compa.R
import org.example.compa.db.CompaSQLiteOpenHelper
import org.example.compa.utils.MaterialDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var addTaskButton: Button

    private lateinit var addTaskName: TextInputEditText
    private lateinit var addTaskStartDate: TextInputEditText
    private lateinit var addTaskFinishDate: TextInputEditText
    private lateinit var addTaskCategory: TextInputEditText
    private lateinit var addTaskDescription: TextInputEditText
    private lateinit var addTaskMembers: TextInputEditText


    private val myCalendarStart = Calendar.getInstance()
    private val myCalendarFinish = Calendar.getInstance()
    private var timeStart: Long? = 0
    private var timeFinish: Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_task_activity)
        setData()
        setOnClickListeners()
        toolbar2.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setData() {
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
    }

    private fun updateLabelStart() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        addTaskStartDate.setText(sdf.format(myCalendarStart.timeInMillis))
    }

    private fun updateLabelFinish() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        addTaskFinishDate.setText(sdf.format(myCalendarFinish.timeInMillis))
    }
}