package org.example.compa.ui.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import org.example.compa.R
import org.example.compa.databinding.EntertainmentActivityBinding
import org.example.compa.databinding.LoginActivityBinding
import org.example.compa.databinding.MenuActivityBinding
import org.example.compa.ui.entertainment.EntertainmentActivity
import org.example.compa.ui.login.LoginActivity
import org.example.compa.ui.payments.PaymentsActivity
import org.example.compa.ui.profile.ProfileActivity
import org.example.compa.ui.tasks.TasksActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: MenuActivityBinding

    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra("username"))  username = intent.getStringExtra("username")!!

        binding.profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.tasks.setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
            startActivity(intent)
        }

        binding.payments.setOnClickListener {
            val intent = Intent(this, PaymentsActivity::class.java)
            startActivity(intent)
        }

        binding.entertainment.setOnClickListener {
            val intent = Intent(this, EntertainmentActivity::class.java)
            startActivity(intent)
        }

        binding.logOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}