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

    private var onlyRead: Boolean = false
    private var edit: Boolean = false
    private var groupId: String = ""

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
        setOnClickListeners()
        when (intent.getIntExtra("action", -1)) {
            1 -> {
                binding.addMember.visibility = View.GONE
                binding.addMemberToList.visibility = View.GONE
                binding.addGroup.visibility = View.GONE
                binding.nameTextInputLayout.isEnabled = false
                binding.placeTextInputLayout.isEnabled = false
                setGroupData(intent.getStringExtra("groupId") ?: "")
                onlyRead = true
                getMembers()
            }
            2 -> {
                onlyRead = false
                edit = true
                setGroupData(intent.getStringExtra("groupId") ?: "")
                getMembers()
            }
            else -> {
                onlyRead = false
                members.clear()
                members.add(
                    Member(
                        AppPreference.getUserUID(),
                        AppPreference.getUserName(),
                        AppPreference.getUserUsername(),
                        AppPreference.getUserEmail()
                    )
                )
                getMembers()
            }
        }

    }

    private fun setGroupData(newGroupId: String) {
        groupId = newGroupId
        db.collection("groups").document(groupId).get().addOnSuccessListener {
            val name = it.data?.get("name") as String
            val place = it.data?.get("place") as String
            binding.nameEditText.setText(name)
            binding.placeEditText.setText(place)
            db.collection("groups").document(groupId).collection("members").get()
                .addOnSuccessListener {
                    for (member in it.documents) {
                        val id = member.get("id") as String
                        val name = member.get("name") as String
                        val username = member.get("username") as String
                        val email = member.get("email") as String
                        members.add(Member(id, name, username, email))
                    }
                    getMembers()
                }
        }
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
        membersAdapter = if (onlyRead)
            TextAdapter(members, needsLine = true, needsIcon = false)
        else
            TextAdapter(members, needsLine = true, needsIcon = true)

        membersAdapter.setOnItemClickListener(object : TextAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (position > -1 && position < members.size) {
                    members.removeAt(position)
                    getMembers()
                }
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
            friendAdapter.setOnItemClickListener(object : FriendAdapter.ItemClickListener {
                override fun onItemClicked(person: Person, position: Int) {
                    binding.memberEditText.setText(person.username)
                    dialog.dismiss()
                    binding.addMemberToList.setOnClickListener {
                        MaterialDialog.createDialog(this@CreateGroupActivity) {
                            setTitle(getString(R.string.add_member_button))
                            setMessage(getString(R.string.do_you_want_to_add_this_compa))
                            setPositiveButton(getString(R.string.accept)) { _, _ ->
                                members.add(
                                    Member(
                                        person.id,
                                        name = person.name + " " + person.surnames,
                                        username = person.username,
                                        email = person.email
                                    )
                                )
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
            if (edit) showGroupEditMessage()
            else {
                if (binding.nameEditText.text.toString()
                        .isNotBlank() && binding.placeEditText.text.toString().isNotBlank()
                ) showGroupAddedMessage()
                else {
                    if (binding.nameEditText.text.toString().isBlank()) binding.nameTextInputLayout.error = getString(R.string.empty_field)
                    if (binding.placeEditText.text.toString().isBlank()) binding.placeTextInputLayout.error = getString(R.string.empty_field)

                    MaterialDialog.createDialog(this) {
                        setTitle(R.string.check_fields)
                        setMessage(R.string.check_fields_info)
                    }.show()
                }
            }
        }
    }

    private fun showGroupAddedMessage() {
        MaterialDialog.createDialog(this) {
            setTitle(getString(R.string.add_group))
            setMessage(getString(R.string.do_you_want_to_add_group))
            setPositiveButton(getString(R.string.meh)) { _, _ ->
                val id = getRandomString(16)
                val group = Group(
                    id = id,
                    name = binding.nameEditText.text.toString(),
                    place = binding.placeEditText.text.toString()
                )
                db.collection("groups").document(id).set(group)

                for ((position, member) in members.withIndex()) {
                    db.collection("groups").document(id).collection("members").document(member.id)
                        .set(member)
                    if (position == members.size - 1) {
                        finish()
                    }
                }
            }
            setNegativeButton(getString(R.string.no_no_no)) { _, _ -> }
        }.show()
    }

    private fun showGroupEditMessage() {
        MaterialDialog.createDialog(this) {
            setTitle(getString(R.string.add_group))
            setMessage(getString(R.string.do_you_want_to_add_group))
            setPositiveButton(getString(R.string.meh)) { _, _ ->

                db.collection("groups").document(groupId)
                    .update("name", binding.nameEditText.text.toString())
                db.collection("groups").document(groupId)
                    .update("place", binding.placeEditText.text.toString())
                db.collection("groups").document(groupId).collection("members").document().delete()

                for ((position, member) in members.withIndex()) {
                    db.collection("groups").document(groupId).collection("members")
                        .document(member.id).set(member)
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