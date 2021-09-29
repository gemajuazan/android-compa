package org.example.compa.ui.friends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.SearchFriendActivityBinding
import org.example.compa.models.Friend
import org.example.compa.models.Person
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.adapters.PeopleAdapter
import org.example.compa.utils.MaterialDialog

class SearchFriendActivity : AppCompatActivity() {

    private lateinit var binding: SearchFriendActivityBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var people = arrayListOf<Person>()
    private var friends = arrayListOf<String>()
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
        binding.toolbar.arrorBack.setOnClickListener {
            finish()
        }
        binding.toolbar.title.text = getString(R.string.search)
    }

    private fun initFirebase() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        getFriends()
    }

    private fun getPeople() {
        db.collection("person").get().addOnSuccessListener {
            for (data in it.documents) {
                val id = data?.get("id") as String? ?: ""

                if (id == AppPreference.getUserUID()) {
                    me = setPerson(data, id)
                }

                if (!checkIfPersonIsMyFriend(id)) {
                    val person = setPerson(data, id)
                    people.add(person)
                }
            }
            setFriendsAdapter()
        }
    }

    private fun setPerson(
        data: DocumentSnapshot?,
        id: String
    ): Person {
        val name = data?.get("name") as String? ?: ""
        val surnames = data?.get("surnames") as String? ?: ""
        val birthdate = data?.get("birthdate") as Long? ?: -1
        val email = data?.get("email") as String? ?: ""
        val username = data?.get("username") as String? ?: ""
        val phone = data?.get("phone") as String? ?: ""
        return Person(
            id = id,
            name = name,
            surnames = surnames,
            birthdate = birthdate,
            email = email,
            username = username,
            phone = phone,
            image = ""
        )
    }

    private fun setFriendsAdapter() {
        peopleAdapter = PeopleAdapter(people, true)
        binding.newFriends.layoutManager = LinearLayoutManager(this)
        binding.newFriends.adapter = peopleAdapter

        setOnQueryListenerSearchView()
        peopleAdapter.setOnItemClickListener { person, _, isFavourite, image ->
            if (isFavourite)
                showAddFriendAlert(image, person)
            else
                showDeleteFriendAlert(person)
        }
    }

    private fun showDeleteFriendAlert(person: Person) {
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

    private fun showAddFriendAlert(
        image: ImageButton,
        person: Person
    ) {
        MaterialDialog.createDialog(this) {
            setTitle(R.string.add_friend)
            setMessage(R.string.do_you_want_to_add_this_friend)
            setPositiveButton(R.string.accept) { _, _ ->
                image.setImageResource(R.drawable.ic_friendship)
                db.collection("person").document(auth.currentUser?.uid ?: "")
                    .collection("pending_friends").document(person.id)
                    .set(Friend(person, solicitude = true, favourite = false))

                val meAsFriend = me?.let { Friend(it, solicitude = false, favourite = false) }
                meAsFriend?.let {
                    db.collection("person").document(person.id)
                        .collection("pending_friends").document(auth.currentUser?.uid ?: "")
                        .set(it)
                }
            }
            setNegativeButton(R.string.cancel) { _, _ -> }
        }.show()
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

    private fun getFriends() {
        friends.clear()
        db.collection("person").document(AppPreference.getUserUID()).collection("friends").get().addOnSuccessListener {
            for (friend in it.documents) {
                val hashMap = friend.data?.get("person") as HashMap<String, Any>
                val id = hashMap["id"] as String
                friends.add(id)
            }
            getPendingFriends()
        }
    }

    private fun getPendingFriends() {
        db.collection("person").document(AppPreference.getUserUID()).collection("pending_friends").get().addOnSuccessListener {
            for (friend in it.documents) {
                val hashMap = friend.data?.get("person") as HashMap<String, Any>
                val id = hashMap["id"] as String
                friends.add(id)
            }
            getPeople()
        }
    }

    private fun checkIfPersonIsMyFriend(friendId: String): Boolean {
        friends.add(AppPreference.getUserUID())
        for (friend in friends) {
            if (friend == friendId) return true
        }
        return false
    }
}