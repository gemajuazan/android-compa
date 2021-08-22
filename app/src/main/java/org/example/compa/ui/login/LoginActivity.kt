package org.example.compa.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import org.example.compa.R
import org.example.compa.RegisterActivity
import org.example.compa.databinding.LoginActivityBinding
import org.example.compa.ui.menu.MenuActivity
import org.example.compa.utils.MaterialDialog
import org.example.compa.utils.StyleUtil


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding

    private lateinit var loginEmailTextInputEditText: TextInputEditText
    private lateinit var loginPasswordTextInputEditText: TextInputEditText

    private val urlTwitter = "https://twitter.com/soerane"
    private val urlLinkedIn = "https://www.linkedin.com/in/gema-ju%C3%A1rez-almaz%C3%A1n-68644b147/"

    private var username:String = ""
    private var customToken: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.login.setOnClickListener {
            //goToMenu()
            //showDataFromDatabase()
            checkLoginFields()
        }

        binding.root.setOnClickListener {
            clearFocus()
        }

        binding.noAccount.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun clearFocus() {
        StyleUtil.hideKeyboard(this, binding.root)
        binding.emailEditText.clearFocus()
        binding.passwordEditText.clearFocus()
    }

    private fun login() {
        // val user = FirebaseAuth.getInstance().currentUser
        auth.signInWithEmailAndPassword(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goToMenu()
                } else {
                    MaterialDialog.createDialog(this) {
                        setTitle(getString(R.string.incorrect_login_message))
                        setMessage(getString(R.string.incorrect_data_login_message))
                        setNegativeButton(getString(R.string.register)) { _, _ ->
                            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                            startActivity(intent)
                        }
                        setPositiveButton(getString(R.string.okay_makey)) { dialog, _ ->
                            dialog.cancel()
                        }
                    }.show()
                }
            }
    }

    private fun checkLoginFields() {
        if (binding.emailEditText.text.isNullOrBlank()) {
            binding.emailTextInputLayout.error = getString(R.string.empty_field)
            binding.emailTextInputLayout.boxStrokeWidth = 3
            return
        }
        if (binding.passwordEditText.text.isNullOrBlank()) {
            binding.passwordTextInputLayout.error = getString(R.string.empty_field)
            binding.passwordTextInputLayout.boxStrokeWidth = 3
            return
        }
        login()
    }

    private fun noLogin() {
        MaterialDialog.createDialog(this) {
            setTitle(getString(R.string.oops))
            setMessage(getString(R.string.no_login))
            setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }

    private fun goToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }
}