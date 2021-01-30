package org.example.compa

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.isEmpty
import androidx.databinding.DataBindingUtil
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.profile_activity.*
import kotlinx.android.synthetic.main.profile_activity.toolbar2
import kotlinx.android.synthetic.main.register_activity.*
import org.example.compa.db.CompaSQLiteOpenHelper
import org.example.compa.ui.menu.MenuActivity
import org.example.compa.utils.MaterialDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private var registerName: TextInputEditText? = null
    private var registerSurnames: TextInputEditText? = null
    private var registerBirthdate: TextInputEditText? = null
    private var registerUsername: TextInputEditText? = null
    private var registerEmail: TextInputEditText? = null
    private var registerRepeatEmail: TextInputEditText? = null
    private var registerPassword: TextInputEditText? = null
    private var registerRepeatPassword: TextInputEditText? = null

    private val myCalendar = Calendar.getInstance()
    private var time: Long? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( R.layout.register_activity)
        setToolbar()
        val registerButton = findViewById<Button>(R.id.register_button)

        registerFields()

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        registerBirthdate?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                DatePickerDialog(
                    this@RegisterActivity,
                    date,
                    myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        })

        registerButton.setOnClickListener {
            if (!checkForm()) {
                saveInDatabase()
                MaterialDialog.createDialog(this) {
                    setTitle(getString(R.string.register_user))
                    setMessage(getString(R.string.register_successfully))
                    setPositiveButton(getString(R.string.dabuti)) { dialog, _ ->
                        showMessageRegisterSuccessfully()
                    }
                }.show()
            }
        }
    }

    private fun showMessageRegisterSuccessfully() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("username", registerUsername?.text.toString())
        startActivity(intent)
    }

    private fun saveInDatabase() {
        val db = CompaSQLiteOpenHelper(this, "dbCompa", null, CompaSQLiteOpenHelper.DATABASE_VERSION)
        val dbCompa = db.writableDatabase

        val name = registerName?.text.toString()
        val surnames = registerSurnames?.text.toString()
        val birthdate = registerBirthdate?.text.toString()
        val username = registerUsername?.text.toString()
        val email = registerEmail?.text.toString()
        val password = registerPassword?.text.toString()


        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        try {
            val d: Date = sdf.parse(birthdate)
            time = d.time
        } catch (e: ParseException) {
            e.printStackTrace();
        }

        val register = ContentValues()
        register.put("name", name)
        register.put("surnames", surnames)
        register.put("birthdate", time)
        register.put("username", username)
        register.put("email", email)
        register.put("password", password)

        dbCompa.insert("user", null, register)
        dbCompa.close()
    }

    private fun registerFields() {
        registerName =
            findViewById(R.id.register_name_input_text)
        registerSurnames =
            findViewById(R.id.register_surnames_input_text)
        registerBirthdate = findViewById(R.id.register_birthdate_input_text)
        registerUsername =
            findViewById(R.id.register_username_input_text)
        registerEmail =
            findViewById(R.id.register_email_input_text)
        registerRepeatEmail =
            findViewById(R.id.register_repeat_email_input_text)
        registerPassword =
            findViewById(R.id.register_password_input_text)
        registerRepeatPassword =
            findViewById(R.id.register_repeat_password_input_text)

    }

    private fun checkForm(): Boolean {

        if (registerName?.text.toString().isNullOrEmpty()){
            registerName?.error = getString(R.string.empty_field)
            checkFieldsMessage()
            return true
        }

        if (registerSurnames?.text.toString().isNullOrEmpty()){
            registerSurnames?.error = getString(R.string.empty_field)
            checkFieldsMessage()
            return true
        }

        if (registerBirthdate?.text.toString().isNullOrEmpty()){
            registerSurnames?.error = getString(R.string.empty_field)
            checkFieldsMessage()
            return true
        }

        if (registerUsername?.text.toString().isNullOrEmpty()){
            registerUsername?.error = getString(R.string.empty_field)
            checkFieldsMessage()
            return true
        }

        if (checkAccessInformation()) {
            return true
        }

        return false
    }

    private fun checkAccessInformation(): Boolean {
        if (registerEmail?.text.toString().isNullOrEmpty() || registerRepeatEmail?.text.toString().isNullOrEmpty() || registerPassword?.text.toString().isNullOrEmpty() || registerRepeatPassword?.text.toString().isNullOrEmpty()) {
            checkFieldsMessage()
            return true
        }

        if (registerEmail?.text.toString() != registerRepeatEmail?.text.toString()) {
            emailsAreDifferent()
            return true
        }

        if (registerPassword?.text.toString() != registerRepeatPassword?.text.toString()) {
            passwordsAreDifferent()
            return true
        }

        if (registerPassword?.text.toString().count() < 8) {
            passwordHasMinorOfEightCharacters()
            return true
        }

        return false
    }

    private fun checkFieldsMessage() {
        MaterialDialog.createDialog(this) {
            setTitle("¡Cuidado!")
            setMessage("Rellena los campos")
            setCancelable(false)
            setPositiveButton("Aceptar") { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }

    private fun emailsAreDifferent() {
        MaterialDialog.createDialog(this) {
            setTitle("Revisa los campos")
            setMessage(R.string.emails_are_different)
            setPositiveButton(R.string.accept) { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }

    private fun passwordsAreDifferent() {
        MaterialDialog.createDialog(this) {
            setTitle(R.string.check_fields)
            setMessage(R.string.passwords_are_different)
            setPositiveButton(R.string.accept) { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }

    private fun passwordHasMinorOfEightCharacters() {
        MaterialDialog.createDialog(this) {
            setTitle(R.string.check_fields)
            setMessage(R.string.password_minor_eight_characters)
            setPositiveButton(R.string.accept) { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }

    @SuppressLint("ResourceAsColor")
    private fun setToolbar() {
        toolbar2.title = getString(R.string.signup)
        toolbar2.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        registerBirthdate?.setText(sdf.format(myCalendar.timeInMillis))
    }
}