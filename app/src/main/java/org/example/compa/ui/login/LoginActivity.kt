package org.example.compa.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import org.example.compa.R
import org.example.compa.databinding.LoginActivityBinding
import org.example.compa.db.CompaSQLiteOpenHelper
import org.example.compa.ui.menu.MenuActivity
import org.example.compa.utils.MaterialDialog


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: LoginActivityBinding

    private lateinit var loginEmailTextInputEditText: TextInputEditText
    private lateinit var loginPasswordTextInputEditText: TextInputEditText

    private val urlTwitter = "https://twitter.com/soerane"
    private val urlLinkedIn = "https://www.linkedin.com/in/gema-ju%C3%A1rez-almaz%C3%A1n-68644b147/"

    private var loginEmailDatabase = ""
    private var loginPasswordDatabase = ""
    private var username:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.login.setOnClickListener {
            goToMenu()
            //showDataFromDatabase()
        }

    }

    private fun showDataFromDatabase() {
        val db = CompaSQLiteOpenHelper(
            this,
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        username = loginEmailTextInputEditText.text.toString()
        val params = arrayOf<String>(username)
        val row = dbCompa.rawQuery(
            "SELECT username, password FROM user WHERE username= ?",
            params
        )

        if (row.moveToFirst()) {
            loginEmailDatabase = row.getString(0)
            loginPasswordDatabase = row.getString(1)

            if (loginEmailDatabase == loginEmailTextInputEditText.text.toString() && loginPasswordDatabase == loginPasswordTextInputEditText.text.toString()) {
                goToMenu()
            } else {
                noLogin()
            }

            dbCompa.close()
        } else {
            noLogin()
            dbCompa.close()
        }
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