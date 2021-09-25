package org.example.compa.ui.friends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.SearchFriendActivityBinding
import org.example.compa.models.Friend
import org.example.compa.models.Person
import org.example.compa.ui.adapters.PeopleAdapter
import org.example.compa.utils.MaterialDialog

class SearchFriendActivity : AppCompatActivity() {

    private lateinit var binding: SearchFriendActivityBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var people = arrayListOf<Person>()
    private var peopleAdapter = PeopleAdapter(arrayListOf(), true)

    private var me: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchFriendActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitConfiguration()
    }

    private fun setInitConfiguration() {
        setToolbar()
        binding.newFriends.layoutManager = LinearLayoutManager(this)
        initFirebase()

    }

    private fun setToolbar() {
/*        binding.toolbar.backButtonImageView.setOnClickListener {
            finish()
        }*/
        binding.toolbar.title.text = getString(R.string.search)
    }

    private fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        db.collection("person").get().addOnSuccessListener {
            for ((index, data) in it.documents.withIndex()) {
                val id = data?.get("id") as String? ?: ""
                val name = data?.get("name") as String? ?: ""
                val surnames = data?.get("surnames") as String? ?: ""
                val birthdate = data?.get("birthdate") as Long? ?: -1
                val email = data?.get("email") as String? ?: ""
                val username = data?.get("username") as String? ?: ""
                val phone = data?.get("phone") as String? ?: ""

                if (email == auth.currentUser?.email) {
                    me = Person(
                        id = id,
                        name = name,
                        surnames = surnames,
                        birthdate = birthdate,
                        email = email,
                        username = username,
                        phone = phone
                    )
                } else {
                    val person = Person(
                        id = id,
                        name = name,
                        surnames = surnames,
                        birthdate = birthdate,
                        email = email,
                        username = username,
                        phone = phone
                    )
                    people.add(person)
                }
            }
            setFriendsAdapter()
        }


    }

    private fun setFriendsAdapter() {
        peopleAdapter = PeopleAdapter(people, true)
        binding.newFriends.layoutManager = LinearLayoutManager(this)
        binding.newFriends.adapter = peopleAdapter

        setOnQueryListenerSearchView()
        peopleAdapter.setOnItemClickListener { person, position, isFavourite ->
            if (isFavourite) {
                MaterialDialog.createDialog(this) {
                    setTitle(R.string.add_friend)
                    setMessage(R.string.do_you_want_to_add_this_friend)
                    setPositiveButton(R.string.accept) { _, _ ->
                        db.collection("person").document(auth.currentUser?.uid ?: "")
                            .collection("pending_friends").document(person.id).set(Friend(person, solicitude = true, favourite = false))

                        val meAsFriend = me?.let { Friend(it, solicitude = false, favourite = false) }
                        meAsFriend?.let {
                            db.collection("person").document(person.id)
                                .collection("pending_friends").document(auth.currentUser?.uid ?: "")
                                .set(it)
                        }
                    }
                    setNegativeButton(R.string.cancel) { _, _ -> }
                }.show()

            } else {
                MaterialDialog.createDialog(this) {
                    setTitle(R.string.delete_friend_title)
                    setMessage(R.string.are_you_sure_delete_friend)
                    setPositiveButton(getString(R.string.accept) + " :)") { _, _ ->
                        db.collection("person").document(auth.currentUser?.uid ?: "")
                            .collection("friends").document(person.id).delete()
                            .addOnSuccessListener {
                                MaterialDialog.createDialog(this@SearchFriendActivity) {
                                    setTitle(R.string.delete_friend_title)
                                    setMessage(getString(R.string.delete_friend_successfully))
                                    setPositiveButton(R.string.accept) { _, _ ->

                                    }
                                }.show()
                            }
                    }
                    setNegativeButton(R.string.cancel) { _, _ -> }
                }.show()

            }
        }
    }

    private fun setOnQueryListenerSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                peopleAdapter.filter.filter(newText)
                return false
            }

        })
    }
}