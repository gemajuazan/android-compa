package org.example.compa.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.example.compa.R
import org.example.compa.databinding.ProfileActivityBinding
import org.example.compa.models.Person

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ProfileActivityBinding

    private var username = ""

    private var user = Person(0, "Gema", "Juárez Almazán", "31/10/1996", "gemajuazan@gmail.com", "info1234")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("username")) username = intent.getStringExtra("username")!!
        personalizeView()
        setOnClickListeners()
    }

    private fun personalizeView() {
        binding.titleBirthdateTextView.text = getString(R.string.date_birth).toUpperCase()
        binding.titleContactsAndGroupsTextView.text = getString(R.string.contacts_and_groups).toUpperCase()
        setPerson()
    }

    private fun setPerson() {
        binding.nameUserTextView.text = user.name + " " + user.surnames
        binding.emailUserTextView.text = user.email
        binding.birthdateTextView.text = user.birthdate
    }

    private fun setOnClickListeners() {
        binding.backButtonImageView.setOnClickListener {
            finish()
        }
    }

}