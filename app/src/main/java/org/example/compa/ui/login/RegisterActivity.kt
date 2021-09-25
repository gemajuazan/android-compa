package org.example.compa.ui.login

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.RegisterActivityBinding
import org.example.compa.models.Person
import org.example.compa.models.User
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.menu.MenuActivity
import org.example.compa.utils.MaterialDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: RegisterActivityBinding

    private val myCalendar = Calendar.getInstance()
    private var time: Long? = 0

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val registerButton = findViewById<Button>(R.id.register_button)

        binding.toolbar2.title.text = getString(R.string.register)

        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        binding.registerBirthdateInputText.setOnClickListener {
            DatePickerDialog(
                this@RegisterActivity,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        registerButton.setOnClickListener {
            if (!checkForm()) {
                registerInDatabase()

            }
        }
    }

    private fun showMessageRegisterSuccessfully() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("username", binding.registerNameInputText.text.toString())
        startActivity(intent)
    }

    private fun registerInDatabase() {
        auth.createUserWithEmailAndPassword(binding.registerEmailInputText.text.toString(), binding.registerPasswordInputText.text.toString())
            .addOnCompleteListener(this
            ) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser

                    val newUser = User(binding.registerUsernameInputText.text.toString(), binding.registerEmailInputText.text.toString(), user?.uid ?: "", user?.isEmailVerified ?: false)

                    val myFormat = "dd/MM/yyyy" //In which you need put here
                    val sdf = SimpleDateFormat(myFormat)
                    try {
                        val d: Date = sdf.parse(binding.registerBirthdateInputText?.text.toString())
                        time = d.time
                    } catch (e: ParseException) {
                        e.printStackTrace();
                    }
                    AppPreference.setUserUID(user?.uid ?: "")
                    val newPerson = Person(
                        id = user?.uid ?: "",
                        name = binding.registerNameInputText.text.toString(),
                        surnames = binding.registerSurnamesInputText.text.toString(),
                        birthdate = time,
                        email = binding.registerEmailInputText.text.toString(),
                        username = binding.registerUsernameInputText.text.toString(),
                        phone = binding.registerPhoneInputText.text.toString()
                    )

                    AppPreference.setUserEmail(binding.registerEmailInputText.text.toString())
                    AppPreference.setUserName(binding.registerNameInputText.text.toString() + " " + binding.registerSurnamesInputText.text.toString())
                    AppPreference.setUserUsername(binding.registerUsernameInputText.text.toString())

                    db.collection("user").document(user?.uid ?: "").set(newUser)
                    db.collection("person").document(user?.uid ?: "").set(newPerson)

                    MaterialDialog.createDialog(this) {
                        setTitle(getString(R.string.register_user))
                        setMessage(getString(R.string.register_successfully))
                        setPositiveButton(getString(R.string.dabuti)) { _, _ ->
                            showMessageRegisterSuccessfully()
                        }
                    }.show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        this@RegisterActivity, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkForm(): Boolean {

        if (binding.registerNameInputText.text.toString().isEmpty()){
            binding.registerName.error = getString(R.string.empty_field)
            checkFieldsMessage()
            return true
        }

        if (binding.registerSurnamesInputText.text.toString().isEmpty()){
            binding.registerSurnames.error = getString(R.string.empty_field)
            checkFieldsMessage()
            return true
        }

        if (binding.registerBirthdateInputText.text.toString().isEmpty()){
            binding.registerBirthdate.error = getString(R.string.empty_field)
            checkFieldsMessage()
            return true
        }

        if (binding.registerUsernameInputText.text.toString().isEmpty()){
            binding.registerUsername.error = getString(R.string.empty_field)
            checkFieldsMessage()
            return true
        }

        if (checkAccessInformation()) {
            return true
        }

        return false
    }

    private fun checkAccessInformation(): Boolean {
        if (binding.registerEmailInputText.text.toString().isEmpty()
            || binding.registerRepeatEmailInputText.text.toString().isEmpty()
            || binding.registerPasswordInputText.text.toString().isEmpty()
            || binding.registerRepeatPasswordInputText.text.toString().isEmpty()) {
            checkFieldsMessage()
            return true
        }

        if (binding.registerEmailInputText.text.toString() != binding.registerRepeatEmailInputText.text.toString()) {
            emailsAreDifferent()
            return true
        }

        if (binding.registerPasswordInputText.text.toString() != binding.registerRepeatPasswordInputText.text.toString()) {
            passwordsAreDifferent()
            return true
        }

        if (binding.registerRepeatPasswordInputText.text.toString().count() < 8) {
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

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        binding.registerBirthdateInputText.setText(sdf.format(myCalendar.timeInMillis))
    }

    companion object {
        const val TAG = "Register"
    }
}