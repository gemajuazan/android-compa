package org.example.compa.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import org.example.compa.R
import org.example.compa.ui.menu.MenuActivity

class SplashActivity : AppCompatActivity() {

    private val DISPLAY = 2000L
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        auth = FirebaseAuth.getInstance()

        lifecycleScope.launchWhenStarted {
            delay(DISPLAY)
            if (auth.currentUser != null) {
                goToMenu()
            } else {
                goToLogin()
            }
        }
    }

    private fun goToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}