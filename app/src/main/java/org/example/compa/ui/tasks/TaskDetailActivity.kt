package org.example.compa.ui.tasks

import android.app.DatePickerDialog
import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.task_detail_activity.*
import org.example.compa.R
import org.example.compa.db.CompaSQLiteOpenHelper
import org.example.compa.utils.MaterialDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailActivity : AppCompatActivity() {

    private var id: String = ""
    private var name: String = ""

    private lateinit var editStartDate: TextInputEditText
    private lateinit var editFinishDate: TextInputEditText
    private lateinit var editDescription: TextView

    private val myCalendarStart = Calendar.getInstance()
    private val myCalendarFinish = Calendar.getInstance()
    private var timeStart: Long? = 0
    private var timeFinish: Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_detail_activity)

        if (intent.hasExtra("id")) id = intent.getIntExtra("id", 0).toString()

        setToolbar()

        personalizeView()

        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.status_task,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.isEnabled = false
        }
        getData()
        checkData()
    }

    private fun getData() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        val row = dbCompa.rawQuery(
            "SELECT name, startDate, finishDate, category, numMembers, description FROM task WHERE _id=$id",
            null
        )
        if (row.moveToFirst()) {
            val dateStart = Date(row.getString(1).toLong())
            val dateFinish = Date(row.getString(2).toLong())
            val df2 = SimpleDateFormat("dd/MM/yyyy")

            toolbar_task_detail.title = row.getString(0)
            editStartDate.setText(df2.format(dateStart))
            editFinishDate.setText(df2.format(dateFinish))
            editDescription.text = row.getString(5)
        }
    }

    private fun checkData() {

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
        editStartDate.setOnClickListener {
            DatePickerDialog(
                this@TaskDetailActivity,
                dateStart,
                myCalendarStart[Calendar.YEAR],
                myCalendarStart[Calendar.MONTH],
                myCalendarStart[Calendar.DAY_OF_MONTH]
            ).show()
        }

        editFinishDate.setOnClickListener {
            DatePickerDialog(
                this@TaskDetailActivity,
                dateFinish,
                myCalendarFinish[Calendar.YEAR],
                myCalendarFinish[Calendar.MONTH],
                myCalendarFinish[Calendar.DAY_OF_MONTH]
            ).show()
        }

        edit_info.setOnClickListener {
            save_info.visibility = View.VISIBLE
            spinner.isEnabled = true
            start_date.isEnabled = true
            finish_date.isEnabled = true
        }

        save_info.setOnClickListener {
            if (spinner.selectedItem.toString() == "Finalizado") {
                deleteTask()
            } else {
                setDataFromDatabase()
                save_info.visibility = View.GONE
            }
        }
    }

    private fun setDataFromDatabase() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        val startDate = editStartDate.text.toString()
        val finishDate = editFinishDate.text.toString()

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

        val register = ContentValues()
        register.put("startDate", timeStart)
        register.put("finishDate", timeFinish)

        val variable = dbCompa.update("task", register, "_id=$id", null)
        dbCompa.close()

        if (variable == 1) {
            MaterialDialog.createDialog(this) {
                setTitle(getString(R.string.modified_task))
                setMessage(getString(R.string.modified_task_info))
                setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                    dialog.cancel()
                    finish()
                }
            }.show()
        } else {
            MaterialDialog.createDialog(this) {
                setTitle(getString(R.string.oops))
                setMessage(getString(R.string.error_modifying_task))
                setPositiveButton(getString(R.string.sure)) { dialog, _ ->
                    dialog.cancel()
                }
            }.show()
        }
    }

    private fun deleteTask() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase


        val value = spinner.selectedItem.toString()
        if (value == "Finalizado") {
            val variable = dbCompa.delete("task", "_id=$id", null)
            dbCompa.close()
            if (variable == 1) {
                MaterialDialog.createDialog(this) {
                    setTitle(getString(R.string.finished_task))
                    setMessage(getString(R.string.finished_task_message))
                    setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                        dialog.cancel()
                        finish()
                    }
                }.show()
            } else {
                MaterialDialog.createDialog(this) {
                    setTitle(getString(R.string.oops))
                    setMessage(getString(R.string.error_deleting_task))
                    setPositiveButton(getString(R.string.sure)) { dialog, _ ->
                        dialog.cancel()
                    }
                }.show()
            }
        }
    }

    private fun setToolbar() {
        toolbar_task_detail.title = name
        toolbar_task_detail.setNavigationOnClickListener {
            finish()
        }
    }

    private fun personalizeView() {
        editDescription = findViewById(R.id.task_detail_description)
        editStartDate = findViewById(R.id.start_date_edit)
        editFinishDate = findViewById(R.id.finish_date_edit)
    }

    private fun updateLabelStart() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        editStartDate.setText(sdf.format(myCalendarStart.timeInMillis))
    }

    private fun updateLabelFinish() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        editFinishDate.setText(sdf.format(myCalendarFinish.timeInMillis))
    }
}