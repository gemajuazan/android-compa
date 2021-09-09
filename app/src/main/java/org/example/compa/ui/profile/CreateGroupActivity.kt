package org.example.compa.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.CreateGroupActivityBinding
import org.example.compa.databinding.FriendsListBinding
import org.example.compa.databinding.ItemFriendBinding
import org.example.compa.models.Friend
import org.example.compa.models.Group
import org.example.compa.models.Member
import org.example.compa.models.Person
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.adapters.FriendAdapter
import org.example.compa.ui.adapters.TextAdapter
import org.example.compa.utils.MaterialDialog
import org.example.compa.utils.StyleUtil.Companion.getRandomString

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: CreateGroupActivityBinding
    private var numMembers: Int = 0
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val members = ArrayList<Member>()
    private var friends = arrayListOf<Friend>()

    private var friendAdapter = FriendAdapter(arrayListOf(), false)
    private var membersAdapter = TextAdapter(arrayListOf(), needsLine = true, needsIcon = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateGroupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setInitConfiguration()
    }

    private fun setInitConfiguration() {
        initFirebase()
        binding.membersGroup.layoutManager = LinearLayoutManager(this)
        setToolbar()
        getMembers()
        setOnClickListeners()

    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        getFriends()
    }

    private fun setToolbar() {
        binding.toolbarCreateGroup.profileTitle.text = getString(R.string.add)
        binding.toolbarCreateGroup.profileSubtitle.text = getString(R.string.group)
    }

    private fun getMembers() {
        numMembers = members.size
        membersAdapter = TextAdapter(members, needsLine = true, needsIcon = true)
        membersAdapter.setOnItemClickListener(object : TextAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                members.removeAt(position)
            }
        })
        binding.membersGroup.adapter = membersAdapter
        if (numMembers == 1) {
            binding.noMembers.visibility = View.VISIBLE
        } else {
            binding.noMembers.visibility = View.GONE
        }
    }

    private fun setOnClickListeners() {
        binding.addMember.setOnClickListener {
            binding.addMemberLinearLayout.visibility = View.VISIBLE
        }
        binding.memberEditText.setOnClickListener {
            val dialogBinding = FriendsListBinding.inflate(layoutInflater)
            val dialog = MaterialDialog.createDialog(this@CreateGroupActivity) {
                setTitle(R.string.select_friend)
                setView(dialogBinding.root)
            }

            dialogBinding.friendsRecyclerView.layoutManager = LinearLayoutManager(this)
            dialogBinding.friendsRecyclerView.visibility = View.VISIBLE
            friendAdapter = FriendAdapter(friends, false)
            dialogBinding.friendsRecyclerView.adapter = friendAdapter
            friendAdapter.setOnItemClickListener(object: FriendAdapter.ItemClickListener {
                override fun onItemClicked(person: Person, position: Int) {
                    binding.memberEditText.setText(person.username)
                    dialog.dismiss()
                    binding.addMemberToList.setOnClickListener {
                        MaterialDialog.createDialog(this@CreateGroupActivity) {
                            setTitle(getString(R.string.add_member_button))
                            setMessage(getString(R.string.do_you_want_to_add_this_compa))
                            setPositiveButton(getString(R.string.accept)) { _, _ ->
                                members.add(Member(person.id, name = person.name + " " + person.surnames, username = person.username, email = person.email))
                                getMembers()
                                binding.memberEditText.setText("")
                                binding.memberEditText.clearFocus()
                                dialog.dismiss()
                            }
                            setNegativeButton(getString(R.string.no_no_no)) { _, _ -> }
                        }.show()
                    }
                }

                override fun onItemAddClicked(person: Person, position: Int) {
                    // Ignore
                }

                override fun onItemLongClicked(person: Person, position: Int) {
                    // Ignore
                }
            })
            dialog.show()
        }
        binding.addGroup.setOnClickListener {
            showGroupAddedMessage()
        }
    }

    private fun showGroupAddedMessage() {
        MaterialDialog.createDialog(this) {
            setTitle(getString(R.string.add_group))
            setMessage(getString(R.string.do_you_want_to_add_group))
            setPositiveButton(getString(R.string.meh)) { _, _ ->
                val id = getRandomString(12)
                val group = Group(id = id, name = binding.nameEditText.text.toString(), place = binding.placeEditText.text.toString())
                db.collection("groups").document(id).set(group)

                for ((position, member) in members.withIndex()) {
                    db.collection("groups").document(id).collection("members").document(member.id).set(member)
                    if (position == members.size - 1) {
                        finish()
                    }
                }
            }
            setNegativeButton(getString(R.string.no_no_no)) { _, _ -> }
        }.show()
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
            }
    }
}