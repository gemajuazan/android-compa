package org.example.compa.ui.profile

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.profile_activity.*
import org.example.compa.R
import org.example.compa.db.CompaSQLiteOpenHelper
import org.example.compa.ui.login.LoginActivity
import org.example.compa.utils.MaterialDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var profileCheckOut: Button
    private lateinit var profileDisableAccount: Button
    private lateinit var profileEditName: ImageButton
    private lateinit var profileEditBirthdate: ImageButton
    private lateinit var profileEditEmail: ImageButton
    private lateinit var profileEditPassword: ImageButton

    private lateinit var profileModify: Button
    private lateinit var profileNameForm: LinearLayout
    private lateinit var profileBirthdateForm: TextInputLayout
    private lateinit var profileEmailForm: TextInputLayout
    private lateinit var profilePasswordForm: TextInputLayout

    private lateinit var profileName: TextView
    private lateinit var profileUsername: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileBirthdate: TextView

    private lateinit var registerName: TextInputEditText
    private lateinit var registerSurnames: TextInputEditText
    private lateinit var registerEmail: TextInputEditText
    private lateinit var registerPassword: TextInputEditText
    private lateinit var registerBirthdate: TextInputEditText

    private var userId = 0
    private val myCalendar = Calendar.getInstance()
    private var time: Long? = 0

    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)

        if (intent.hasExtra("username")) username = intent.getStringExtra("username")!!
        setToolbar()
        profileFields()
    }

    private fun profileFields() {
        profileNameForm = findViewById(R.id.profile_form_name)
        profileBirthdateForm = findViewById(R.id.profile_birthdate_form)
        profileEmailForm = findViewById(R.id.profile_email_form)
        profilePasswordForm = findViewById(R.id.profile_password_form)

        profileCheckOut = findViewById(R.id.signup)
        profileDisableAccount = findViewById(R.id.delete_profile)

        profileEditName = findViewById(R.id.edit_name_and_surname)
        profileEditBirthdate = findViewById(R.id.edit_birthdate)
        profileEditEmail = findViewById(R.id.edit_email)
        profileEditPassword = findViewById(R.id.edit_password)

        profileName = findViewById(R.id.profile_name)
        profileUsername = findViewById(R.id.profile_username)
        profileEmail = findViewById(R.id.profile_email)
        profileBirthdate = findViewById(R.id.profile_birthdate)

        profileModify = findViewById(R.id.profile_edit)

        registerName = findViewById(R.id.register_name_input_text)
        registerSurnames = findViewById(R.id.register_surnames_input_text)
        registerEmail = findViewById(R.id.profile_email_input_text)
        registerPassword = findViewById(R.id.profile_password_input_text)
        registerBirthdate = findViewById(R.id.register_birthdate_input)

        showDataFromDatabase()
        setOnClickListeners()
    }

    private fun showDataFromDatabase() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        val params = arrayOf(username)
        val row = dbCompa.rawQuery(
            "SELECT _id, username, email, name, surnames, birthdate FROM user WHERE username = ?",
            params
        )

        if (row.moveToFirst()) {
            userId = row.getInt(0)
            profileUsername.text = row.getString(1)
            profileEmail.text = row.getString(2)
            profileName.text = row.getString(3) + " " + row.getString(4)

            val date = Date(row.getString(5).toLong())
            val df2 = SimpleDateFormat("dd/MM/yyyy")
            profileBirthdate.text  = df2.format(date)
            dbCompa.close()
        } else {
            MaterialDialog.createDialog(this) {
                setTitle(getString(R.string.oops))
                setMessage(getString(R.string.no_info_profile))
                setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                    dialog.cancel()
                }
            }.show()
            dbCompa.close()
        }
    }

    private fun setOnClickListeners() {
        profileCheckOut.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        profileDisableAccount.setOnClickListener {
            MaterialDialog.createDialog(this) {
                setMessage(getString(R.string.are_you_sure_disable_account))
                setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                    disableAccount()
                }
                setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }
            }.show()
        }

        profileModify.setOnClickListener {
            profileNameForm.visibility = View.GONE
            profileBirthdateForm.visibility = View.GONE
            profileEmailForm.visibility = View.GONE
            profilePasswordForm.visibility = View.GONE
            profileModify.visibility = View.GONE
            profileCheckOut.isEnabled = true
            profileDisableAccount.isEnabled = true
            dataSaved()
        }

        profileEditName.setOnClickListener {
            profileModify.visibility = View.VISIBLE
            profileNameForm.visibility = View.VISIBLE
            profileModify.text = getString(R.string.save_info)
            profileCheckOut.isEnabled = false
            profileDisableAccount.isEnabled = false
        }

        profileEditBirthdate.setOnClickListener {
            profileModify.visibility = View.VISIBLE
            profileBirthdateForm.visibility = View.VISIBLE
            profileModify.text = getString(R.string.save_info)
            profileCheckOut.isEnabled = false
            profileDisableAccount.isEnabled = false
        }

        val date =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        registerBirthdate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                DatePickerDialog(
                    this@ProfileActivity,
                    date,
                    myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        })

        profileEditEmail.setOnClickListener {
            profileModify.visibility = View.VISIBLE
            profileEmailForm.visibility = View.VISIBLE
            profileModify.text = getString(R.string.save_info)
            profileCheckOut.isEnabled = false
        }

        profileEditPassword.setOnClickListener {
            profileModify.visibility = View.VISIBLE
            profilePasswordForm.visibility = View.VISIBLE
            profileModify.text = getString(R.string.save_info)
            profileCheckOut.isEnabled = false
            profileDisableAccount.isEnabled = false
        }
    }

    private fun dataSaved() {
        modifyNameAndSurnames()
        modifyBirthdate()
        modifyEmail()
        modifyPassword()
    }

    private fun setToolbar() {
        toolbar2.title = getString(R.string.menu_profile)
        toolbar2.setNavigationOnClickListener {
            finish()
        }
    }

    private fun disableAccount() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        val variable = dbCompa.delete("user", "_id=$userId", null)
        dbCompa.close()

        if (variable == 1) {
            MaterialDialog.createDialog(this) {
                setTitle(getString(R.string.disabled_account))
                setMessage(getString(R.string.disabled_account_info))
                setPositiveButton(getString(R.string.come_back)) { dialog, _ ->
                    dialog.cancel()
                    goToLogin()
                }
            }.show()
        } else {
            MaterialDialog.createDialog(this) {
                setTitle(getString(R.string.oops))
                setMessage(getString(R.string.error_disabling_account))
                setPositiveButton(getString(R.string.sure)) { dialog, _ ->
                    dialog.cancel()
                }
            }.show()
        }
    }

    private fun modifyNameAndSurnames() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        if (!registerName.text.isNullOrEmpty() && !registerSurnames.text.isNullOrEmpty()) {
            val register = ContentValues()
            register.put("name", registerName.text.toString())
            register.put("surnames", registerSurnames.text.toString())
            val variable = dbCompa.update("user", register, "_id=$userId", null)
            dbCompa.close()
            if (variable == 1) {
                profileName.text = registerName.text.toString() + " " + registerSurnames.text.toString()
                registerName.setText(" ")
                registerSurnames.setText(" ")
                Toast.makeText(this, "Se ha actualizado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                checkFields()
            }
        } else {
            dbCompa.close()
        }
    }

    private fun modifyEmail() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        if (!registerEmail.text.isNullOrEmpty()) {
            val register = ContentValues()
            register.put("email", registerEmail.text.toString())
            val variable = dbCompa.update("user", register, "_id=$userId", null)
            dbCompa.close()
            if (variable == 1) {
                profileEmail.text = registerEmail.text.toString()
                registerEmail.setText(" ")
                Toast.makeText(this, "Se ha actualizado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                checkFields()
            }
        } else {
            dbCompa.close()
        }
    }

    private fun modifyPassword() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        if (!registerPassword.text.isNullOrEmpty()) {
            val register = ContentValues()
            register.put("password", registerPassword.text.toString())
            val variable = dbCompa.update("user", register, "_id=$userId", null)
            dbCompa.close()
            if (variable == 1) {
                registerPassword.setText(" ")
                Toast.makeText(this, "Se ha actualizado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                checkFields()
            }
        } else {
            dbCompa.close()
        }
    }

    private fun modifyBirthdate() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        if (!registerBirthdate.text.isNullOrEmpty()) {
            val myFormat = "dd/MM/yyyy" //In which you need put here
            val sdf = SimpleDateFormat(myFormat)
            try {
                val d: Date = sdf.parse(registerBirthdate.text.toString())
                time = d.time
            } catch (e: ParseException) {
                e.printStackTrace();
            }

            val register = ContentValues()
            register.put("birthdate", time)
            val variable = dbCompa.update("user", register, "_id=$userId", null)
            dbCompa.close()
            if (variable == 1) {
                profileBirthdate.text = registerBirthdate.text.toString()
                registerBirthdate.setText(" ")
                Toast.makeText(this, "Se ha actualizado correctamente", Toast.LENGTH_SHORT).show()
            } else {
                checkFields()
            }
        } else {
            dbCompa.close()
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun checkFields() {
        MaterialDialog.createDialog(this) {
            setTitle(getString(R.string.check_fields))
            setMessage(getString(R.string.check_fields_info))
            setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        registerBirthdate.setText(sdf.format(myCalendar.timeInMillis))
    }

}