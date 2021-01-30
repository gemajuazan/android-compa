package org.example.compa.ui.menu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import org.example.compa.R
import org.example.compa.ui.payments.PaymentsActivity
import org.example.compa.ui.profile.ProfileActivity
import org.example.compa.ui.tasks.TasksActivity

class MenuActivity : AppCompatActivity() {
    private lateinit var menuProfileButton: Button
    private lateinit var menuTasksButton: Button
    private lateinit var menuPaymentsButton: Button

    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

        if (intent.hasExtra("username"))  username = intent.getStringExtra("username")!!

        menuProfileButton = findViewById(R.id.profile)
        menuTasksButton = findViewById(R.id.tasks)
        menuPaymentsButton = findViewById(R.id.payments)

        menuProfileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        menuTasksButton.setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        menuPaymentsButton.setOnClickListener {
            val intent = Intent(this, PaymentsActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }
}