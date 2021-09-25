package org.example.compa.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import org.example.compa.R
import org.example.compa.databinding.SplashActivityBinding
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.menu.MenuNavigationActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: SplashActivityBinding

    private val display = 3000L
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAnimations()
        loadUser()
    }

    private fun loadUser() {
        auth = FirebaseAuth.getInstance()

        lifecycleScope.launchWhenStarted {
            delay(display)
            if (auth.currentUser != null) {
                AppPreference.setUserUID(auth.currentUser?.uid ?: "")
                AppPreference.setUserEmail(auth.currentUser?.email ?: "")
                goToMenu()
            } else {
                goToLogin()
            }
        }
    }

    private fun setAnimations() {
        binding.logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom))
        binding.loadApp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade))
    }

    private fun goToMenu() {
        val intent = Intent(this, MenuNavigationActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}