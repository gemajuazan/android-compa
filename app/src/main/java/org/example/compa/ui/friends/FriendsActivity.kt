package org.example.compa.ui.friends

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.FriendsActivityBinding
import org.example.compa.models.Friend
import org.example.compa.models.Person
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.adapters.FriendAdapter
import org.example.compa.ui.adapters.PendingFriendAdapter
import org.example.compa.utils.MaterialDialog

class FriendsActivity : AppCompatActivity() {

    private lateinit var binding: FriendsActivityBinding

    private lateinit var db: FirebaseFirestore
    private var friends = arrayListOf<Friend>()
    private var pendingFriends = arrayListOf<Friend>()

    private var pendingFriendsAdapter = PendingFriendAdapter(arrayListOf(), false)
    private var friendAdapter = FriendAdapter(arrayListOf(), false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FriendsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInitConfiguration()
    }

    override fun onResume() {
        super.onResume()
        initFirebase()
    }

    private fun setInitConfiguration() {
        resetView()
        setToolbar()
        binding.friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.pendingFriendsRecyclerView.layoutManager = LinearLayoutManager(this)
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
        friends.clear()
        pendingFriends.clear()
        getPendingFriends()
        getFriends()
    }

    private fun getPendingFriends() {
        db.collection("person").document(AppPreference.getUserUID()).collection("pending_friends")
            .get().addOnSuccessListener {
                for (data in it.documents) {
                    val hashMap = data.data?.get("person") as HashMap<String, Any>
                    val solicitude = data.get("solicitude") as Boolean
                    val favourite = data.get("favourite") as Boolean
                    val id = hashMap["id"] as String? ?: ""
                    val name = hashMap["name"] as String? ?: ""
                    val surnames = hashMap["surnames"] as String? ?: ""
                    val birthdate = hashMap["birthdate"] as Long? ?: -1
                    val email = hashMap["email"] as String? ?: ""
                    val username = hashMap["username"] as String? ?: ""
                    val person = Person(
                        id = id,
                        name = name,
                        surnames = surnames,
                        birthdate = birthdate,
                        email = email,
                        username = username
                    )
                    val friend = Friend(person, solicitude = solicitude, favourite = favourite)
                    pendingFriends.add(friend)
                }
                setPendingFriends()
            }
    }

    private fun getFriends() {
        db.collection("person").document(AppPreference.getUserUID()).collection("friends").get()
            .addOnSuccessListener {
                for (data in it.documents) {
                    val hashMap = data.data?.get("person") as HashMap<String, Any>
                    val solicitude = data.get("solicitude") as Boolean
                    val favourite = data.get("favourite") as Boolean
                    val id = hashMap["id"] as String? ?: ""
                    val name = hashMap["name"] as String? ?: ""
                    val surnames = hashMap["surnames"] as String? ?: ""
                    val birthdate = hashMap["birthdate"] as Long? ?: -1
                    val email = hashMap["email"] as String? ?: ""
                    val username = hashMap["username"] as String? ?: ""
                    val person = Person(
                        id = id,
                        name = name,
                        surnames = surnames,
                        birthdate = birthdate,
                        email = email,
                        username = username
                    )
                    val friend = Friend(person, solicitude = solicitude, favourite = favourite)
                    friends.add(friend)
                }

                setFriends()
            }
    }

    private fun setFriends() {
        if (friends.size < 1) {
            binding.noFriends.visibility = View.VISIBLE
            binding.friendsRecyclerView.visibility = View.GONE
        } else {
            binding.noFriends.visibility = View.GONE
            binding.friendsRecyclerView.visibility = View.VISIBLE
            friendAdapter = FriendAdapter(friends, false)
            binding.friendsRecyclerView.adapter = friendAdapter
            friendAdapter.setOnItemClickListener(object: FriendAdapter.ItemClickListener {
                override fun onItemClicked(person: Person, position: Int) {
                    TODO("Not yet implemented")
                }

                override fun onItemAddClicked(person: Person, position: Int) {
                    TODO("Not yet implemented")
                }

                override fun onItemLongClicked(person: Person, position: Int) {
                    MaterialDialog.createDialog(this@FriendsActivity){
                        setMessage(R.string.do_you_want_to_delete_friend)
                        setPositiveButton(R.string.accept) { _, _ ->
                            db.collection("person").document(AppPreference.getUserUID()).collection("friends").document(person.id).delete()
                            db.collection("person").document(person.id).collection("friends").document(AppPreference.getUserUID()).delete()
                            friends.clear()
                            getFriends()
                        }
                    }.show()

                }

            })
        }
        showViewAndHideProgress()
    }

    private fun setPendingFriends() {
        if (pendingFriends.size < 1) {
            binding.noPendingFriends.visibility = View.VISIBLE
            binding.pendingFriendsRecyclerView.visibility = View.GONE
        } else {
            binding.noPendingFriends.visibility = View.GONE
            binding.pendingFriendsRecyclerView.visibility = View.VISIBLE
            pendingFriendsAdapter = PendingFriendAdapter(pendingFriends, false)
            binding.pendingFriendsRecyclerView.adapter = pendingFriendsAdapter
            pendingFriendsAdapter.setOnItemClickListener(object : PendingFriendAdapter.ItemClickListener {
                override fun onAcceptClicked(person: Person, position: Int) {
                    db.collection("person").document(AppPreference.getUserUID()).collection("friends").document(person.id).set(Friend(person, false, favourite = false))
                    db.collection("person").document(AppPreference.getUserUID()).collection("pending_friends").document(person.id).delete()
                    db.collection("person").document(person.id).collection("pending_friends").document(AppPreference.getUserUID()).delete()
                    pendingFriends.clear()
                    friends.clear()
                    getFriends()
                    getPendingFriends()
                }

                override fun onCancelClicked(person: Person, position: Int) {
                    db.collection("person").document(AppPreference.getUserUID()).collection("pending_friends").document(person.id).delete()
                    db.collection("person").document(person.id).collection("pending_friends").document(AppPreference.getUserUID()).delete()
                    pendingFriends.clear()
                    getPendingFriends()
                }
            })
        }


    }

    private fun resetView() {
        binding.progressFriends.visibility = View.VISIBLE
        binding.toolbar.view.visibility = View.GONE
    }

    private fun showViewAndHideProgress() {
        binding.progressFriends.visibility = View.GONE
        binding.toolbar.view.visibility = View.VISIBLE
    }
}