package org.example.compa.ui.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.ProfileActivityBinding
import org.example.compa.models.Person
import org.example.compa.preferences.AppPreference
import org.example.compa.utils.DateUtil
import org.example.compa.utils.MaterialDialog
import java.util.*

@SuppressLint("SetTextI18n")
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ProfileActivityBinding

    private var username = ""
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var person: Person? = null

    private val myCalendar = Calendar.getInstance()

    private var editMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        if (intent.hasExtra("username")) username = intent.getStringExtra("username")!!
        personalizeView()
        setOnClickListeners()
    }

    private fun setStrings() {
/*        binding.toolbarProfile.profileTitle.text = getString(R.string.menu_profile)
        binding.toolbarProfile.profileSubtitle.text = getString(R.string.menu_profile)*/
        binding.titleBirthdateTextView.text =
            getString(R.string.date_birth).toUpperCase(Locale.ROOT)
    }

    private fun personalizeView() {
        setStrings()
        getPerson()
    }


    private fun setPerson() {
        binding.nameUserTextView.text = person?.name + " " + person?.surnames
        binding.emailUserTextView.text = person?.email
        binding.usernameTextView.text = person?.username
        binding.birthdateTextView.text = DateUtil.getDate(person?.birthdate ?: -1, "dd/MM/yyyy")
        binding.profileProgress.visibility = View.GONE
        //binding.toolbarProfile.view.visibility = View.VISIBLE

        AppPreference.setUserEmail(person?.email ?: "")
        AppPreference.setUserName(person?.name + " " + person?.surnames)
        AppPreference.setUserUsername(person?.username ?: "")
    }

    private fun getPerson() {
        binding.profileProgress.visibility = View.VISIBLE
        //binding.toolbarProfile.view.visibility = View.GONE
        val user: FirebaseUser? = auth.currentUser
        db.collection("person").document(user?.uid ?: "").get().addOnSuccessListener {
            val id = it.data?.get("id") as String? ?: ""
            val name = it.data?.get("name") as String? ?: ""
            val surnames = it.data?.get("surnames") as String? ?: ""
            val birthdate = it.data?.get("birthdate") as Long? ?: -1
            val email = it.data?.get("email") as String? ?: ""
            val username = it.data?.get("username") as String? ?: ""
            person = Person(
                id = id,
                name = name,
                surnames = surnames,
                birthdate = birthdate,
                email = email,
                username = username
            )

            setPerson()
        }
    }

    private fun setOnClickListeners() {
        /*binding.toolbarProfile.backButtonImageView.setOnClickListener {
            finish()
        }*/
        binding.editUser.setOnClickListener {
            if (!editMode) {
                updateInfoUser()
            } else {
                saveInfoUser()
            }
        }
        binding.profileEditBirthdate.setOnClickListener {
            updateBirthdate()
        }
    }

    private fun updateBirthdate() {
        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.birthdateTextView.text =
                    DateUtil.getDate(myCalendar.timeInMillis, "dd/MM/yyyy")

                db.collection("person").document(person?.id ?: "")
                    .update("birthdate", myCalendar.timeInMillis).addOnSuccessListener {
                    MaterialDialog.createDialog(this) {
                        setMessage(getString(R.string.update_birthdate_successfully))
                        setPositiveButton(R.string.accept) { dialog, _ ->
                            dialog.cancel()
                        }
                    }.show()
                }
            }

        DatePickerDialog(
            this@ProfileActivity,
            date,
            myCalendar[Calendar.YEAR],
            myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    private fun updateInfoUser() {
        binding.editUserLinearLayout.visibility = View.VISIBLE
        binding.editUser.setImageResource(R.drawable.ic_baseline_done_24)
        editMode = true
    }

    private fun saveInfoUser() {

        if (binding.nameEditText.text.toString()
                .isBlank() || binding.surnamesEditText.text.toString()
                .isBlank() || binding.usernameEditText.text.toString().isBlank()
        ) {
            MaterialDialog.createDialog(this) {
                setIcon(R.drawable.ic_baseline_error_24)
                setTitle(R.string.empty_field)
                setMessage(R.string.check_fields)
                setPositiveButton(getString(R.string.accept)) { _, _ ->

                }
            }.show()
        } else {
            db.collection("person").document(person?.id ?: "")
                .update("name", binding.nameEditText.text.toString()).addOnSuccessListener {
                    binding.nameUserTextView.text =
                        binding.nameEditText.text.toString() + " " + binding.surnamesEditText.text.toString()
                }.addOnCanceledListener {
                    MaterialDialog.createDialog(this) {
                        setIcon(R.drawable.ic_baseline_error_24)
                        setTitle(R.string.something_was_wrong)
                        setMessage(R.string.something_was_wrong_message)
                        setPositiveButton(getString(R.string.accept)) { _, _ ->

                        }
                    }.show()
                }
            db.collection("person").document(person?.id ?: "")
                .update("username", binding.usernameEditText.text.toString()).addOnSuccessListener {
                    binding.usernameTextView.text = binding.usernameEditText.text.toString()
                }.addOnCanceledListener {
                    MaterialDialog.createDialog(this) {
                        setIcon(R.drawable.ic_baseline_error_24)
                        setTitle(R.string.something_was_wrong)
                        setMessage(R.string.something_was_wrong_message)
                        setPositiveButton(getString(R.string.accept)) { _, _ ->

                        }
                    }.show()
                }
            editMode = false
            binding.editUserLinearLayout.visibility = View.GONE
            binding.editUser.setImageResource(R.drawable.ic_edit_24)

        }

    }

}