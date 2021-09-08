package org.example.compa.ui.friends

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.FriendsActivityBinding
import org.example.compa.models.Person
import org.example.compa.ui.adapters.FriendAdapter
import org.example.compa.ui.adapters.TextAdapter

class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: FriendsActivityBinding

    private lateinit var db: FirebaseFirestore
    private var people = arrayListOf<Person>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FriendsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitConfiguration()
    }

    private fun setInitConfiguration() {
        initFirebase()
        setToolbar()
        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.addFriendsFab.setOnClickListener {
            val intent = Intent(this, SearchFriendActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setToolbar() {
        binding.toolbar.backButtonImageView.setOnClickListener {
            finish()
        }
        binding.toolbar.profileTitle.text = getString(R.string.menu_friends)
        binding.toolbar.profileSubtitle.text = getString(R.string.menu_friends)
    }

    private fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        db.collection("person").get().addOnSuccessListener {
            for(data in it.documents) {
                val id = data?.get("id") as String? ?: ""
                val name = data?.get("name") as String? ?: ""
                val surnames = data?.get("surnames") as String? ?: ""
                val birthdate = data?.get("birthdate") as Long? ?: -1
                val email = data?.get("email") as String? ?: ""
                val username = data?.get("username") as String? ?: ""
                val person = Person(
                    id = id,
                    name = name,
                    surnames = surnames,
                    birthdate = birthdate,
                    email = email,
                    username = username
                )
                people.add(person)
            }
            setPeopleList()
        }
    }

    private fun setPeopleList() {
        if (people.size <= 1) {
            binding.noFriends.visibility = View.VISIBLE
            binding.friendsRecyclerView.visibility = View.GONE
        } else {
            binding.noFriends.visibility = View.GONE
            binding.friendsRecyclerView.visibility = View.VISIBLE
            binding.friendsRecyclerView.adapter = FriendAdapter(people, false)
        }
    }
}