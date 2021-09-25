package org.example.compa.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.example.compa.R
import org.example.compa.databinding.ProfileActivityBinding
import org.example.compa.models.Person
import org.example.compa.preferences.AppPreference
import org.example.compa.utils.DataUtil
import org.example.compa.utils.DataUtil.Companion.getPersonFromDatabase
import org.example.compa.utils.DateUtil
import org.example.compa.utils.MaterialDialog
import org.example.compa.utils.StyleUtil
import java.util.*


@SuppressLint("SetTextI18n")
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ProfileActivityBinding

    private var userId = ""
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var storageReference: StorageReference? = null

    private var person: Person? = null

    private val myCalendar = Calendar.getInstance()
    private lateinit var imageUri: Uri

    private var editMode: Boolean = false
    private var imageChanged: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        if (intent.hasExtra("id")) userId = intent.getStringExtra("id") ?: ""
        if (intent.hasExtra("edit")) editMode = intent.getBooleanExtra("edit", false)
        personalizeView()
        setOnClickListeners()
    }

    private fun personalizeView() {
        binding.toolbarProfile.title.text = getString(R.string.menu_profile)
        binding.toolbarProfile.arrorBack.setOnClickListener {
            finish()
        }
        getPerson()
        binding.root.setOnClickListener {
            clearFocus()
        }
        clearIndividuallyFocus()
        setEditMode()
    }

    private fun setEditMode() {
        if (!editMode) {
            disableView()
            binding.call.setOnClickListener {
                val permissionCheck =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.CALL_PHONE),
                        1234
                    )
                } else {
                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + person?.phone))
                    startActivity(intent)
                }
            }
        } else {
            binding.call.visibility = View.GONE
        }
    }

    private fun disableView() {
        binding.registerName.isEnabled = false
        binding.registerSurnames.isEnabled = false
        binding.registerBirthdate.isEnabled = false
        binding.registerPhone.isEnabled = false
        binding.registerUsername.isEnabled = false
        binding.call.visibility = View.VISIBLE
        binding.changeImage.visibility = View.GONE
    }

    private fun clearFocus() {
        StyleUtil.hideKeyboard(this, binding.root)
        binding.registerNameInputText.clearFocus()
        binding.registerSurnamesInputText.clearFocus()
        binding.registerPhoneInputText.clearFocus()
        binding.registerUsernameInputText.clearFocus()
        binding.registerBirthdateInputText.clearFocus()
    }

    private fun clearIndividuallyFocus() {
        binding.registerNameInputText.setOnClickListener {
            StyleUtil.clearError(binding.registerName)
        }

        binding.registerSurnamesInputText.setOnClickListener {
            StyleUtil.clearError(binding.registerUsername)
        }

        binding.registerPhoneInputText.setOnClickListener {
            StyleUtil.clearError(binding.registerPhone)
        }

        binding.registerUsernameInputText.setOnClickListener {
            StyleUtil.clearError(binding.registerUsername)
        }

        binding.registerBirthdateInputText.setOnClickListener {
            updateBirthdate()
            StyleUtil.clearError(binding.registerBirthdate)
        }
    }

    private fun setPerson() {
        binding.registerNameInputText.setText(person?.name)
        binding.registerSurnamesInputText.setText(person?.surnames)
        binding.registerPhoneInputText.setText(person?.phone)
        binding.registerUsernameInputText.setText(person?.username)
        binding.registerBirthdateInputText.setText(
            DateUtil.getDate(
                person?.birthdate ?: -1,
                "dd/MM/yyyy"
            )
        )
        binding.profileProgress.visibility = View.GONE

        storage.reference.child("images/$userId").downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it.toString())
                .into(binding.imageUser)
        }

        AppPreference.setUserEmail(person?.email ?: "")
        AppPreference.setUserName(person?.name + " " + person?.surnames)
        AppPreference.setUserUsername(person?.username ?: "")
    }

    private fun getPerson() {
        binding.profileProgress.visibility = View.VISIBLE
        db.collection("person").document(userId).get().addOnSuccessListener {
            person = getPersonFromDatabase(it)
            setPerson()
        }
    }

    private fun setOnClickListeners() {
        binding.saveInfo.setOnClickListener {
            if (!editMode) {
                binding.registerName.isEnabled = false
                binding.registerSurnames.isEnabled = false
                binding.registerBirthdate.isEnabled = false
                binding.registerPhone.isEnabled = false
                binding.registerUsername.isEnabled = false
                binding.changeImage.visibility = View.GONE
            } else {
                saveInfoUser()
            }
        }
        binding.changeImage.setOnClickListener {

            val permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    12345
                )
            } else {
                imageChanged = true
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 100)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            if (data?.data != null) {
                imageUri = data.data!!
                binding.imageUser.setImageURI(imageUri)
            }

        }
    }

    private fun updateBirthdate() {
        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.registerBirthdateInputText.setText(
                    DateUtil.getDate(
                        myCalendar.timeInMillis,
                        "dd/MM/yyyy"
                    )
                )

                db.collection("person").document(person?.id ?: "")
                    .update("birthdate", myCalendar.timeInMillis).addOnSuccessListener {
                }.addOnCanceledListener {
                        MaterialDialog.createDialog(this) {
                            setIcon(R.drawable.ic_baseline_error_24)
                            setTitle(R.string.something_was_wrong)
                            setMessage(R.string.something_was_wrong_message)
                            setPositiveButton(getString(R.string.accept)) { _, _ ->

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

    private fun saveInfoUser() {

        if (binding.registerNameInputText.text.toString()
                .isBlank() || binding.registerSurnamesInputText.text.toString()
                .isBlank() || binding.registerUsernameInputText.text.toString().isBlank()
            || binding.registerPhoneInputText.text.toString().isBlank()
            || binding.registerBirthdateInputText.text.toString().isBlank()
        ) {

            if (binding.registerNameInputText.text.toString().isBlank()) {
                binding.registerName.error = getString(R.string.empty_field)
                binding.registerName.setErrorIconDrawable(R.drawable.ic_baseline_error_24)
            }

            if (binding.registerSurnamesInputText.text.toString().isBlank()) {
                binding.registerSurnames.error = getString(R.string.empty_field)
                binding.registerName.setErrorIconDrawable(R.drawable.ic_baseline_error_24)
            }

            if (binding.registerUsernameInputText.text.toString().isBlank()) {
                binding.registerUsername.error = getString(R.string.empty_field)
                binding.registerName.setErrorIconDrawable(R.drawable.ic_baseline_error_24)
            }

            if (binding.registerPhoneInputText.text.toString().isBlank()) {
                binding.registerPhone.error = getString(R.string.empty_field)
                binding.registerName.setErrorIconDrawable(R.drawable.ic_baseline_error_24)
            }

            if (binding.registerBirthdateInputText.text.toString().isBlank()) {
                binding.registerBirthdate.error = getString(R.string.empty_field)
                binding.registerName.setErrorIconDrawable(R.drawable.ic_baseline_error_24)
            }

        } else {
            db.collection("person").document(person?.id ?: "")
                .update("name", binding.registerNameInputText.text.toString()).addOnSuccessListener {

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
                .update("surnames", binding.registerSurnamesInputText.text.toString()).addOnSuccessListener {

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
                .update("username", binding.registerUsernameInputText.text.toString()).addOnSuccessListener {

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
                .update("phone", binding.registerPhoneInputText.text.toString()).addOnSuccessListener {
                    MaterialDialog.createDialog(this) {
                        setMessage(R.string.data_saved)
                        setPositiveButton(R.string.accept) { _, _ ->
                            finish()
                        }
                    }.show()
                }.addOnCanceledListener {
                    MaterialDialog.createDialog(this) {
                        setIcon(R.drawable.ic_baseline_error_24)
                        setTitle(R.string.something_was_wrong)
                        setMessage(R.string.something_was_wrong_message)
                        setPositiveButton(getString(R.string.accept)) { _, _ ->

                        }
                    }.show()
                }

            if (imageChanged) uploadImage()

        }

    }

    private fun uploadImage() {
        if (imageUri != null) {

            // Code for showing progressDialog while uploading
            Toast.makeText(this, getString(R.string.uploading_image), Toast.LENGTH_SHORT).show()

            // Defining the child of storageReference
            val ref: StorageReference? = storageReference
                ?.child(
                    "images/"
                            + AppPreference.getUserUID()
                )

            ref?.putFile(imageUri)
                ?.addOnSuccessListener {
                    Toast
                        .makeText(
                            this@ProfileActivity,
                            "Image Uploaded!!",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                    finish()
                }
                ?.addOnFailureListener { e ->
                    Toast
                        .makeText(
                            this@ProfileActivity,
                            "Failed " + e.message,
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
                ?.addOnProgressListener { taskSnapshot ->
                    val progress = ((100.0
                            * taskSnapshot.bytesTransferred
                            / taskSnapshot.totalByteCount))
                   /* progressDialog.setMessage(
                        ("Uploaded "
                                + progress.toInt() + "%")
                    )*/
                }
        }
    }

}