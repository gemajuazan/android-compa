package org.example.compa.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.CreateGroupActivityBinding
import org.example.compa.models.Group
import org.example.compa.models.Member
import org.example.compa.ui.adapters.TextAdapter
import org.example.compa.utils.MaterialDialog
import org.example.compa.utils.StyleUtil.Companion.getRandomString

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: CreateGroupActivityBinding
    private var numMembers: Int = 0
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val members = ArrayList<Member>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CreateGroupActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitConfiguration()
    }

    private fun setInitConfiguration() {
        initFirebase()
        auth.currentUser?.email?.let { members.add(Member(1, "Fulanito", "gemajuazan", it)) }
        binding.membersGroup.layoutManager = LinearLayoutManager(this)
        setToolbar()
        getMembers()
        setOnClickListeners()
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
    }

    private fun setToolbar() {
        binding.toolbarCreateGroup.profileTitle.text = getString(R.string.add)
        binding.toolbarCreateGroup.profileSubtitle.text = getString(R.string.group)
    }

    private fun getMembers() {
        numMembers = members.size
        binding.membersGroup.adapter = TextAdapter(members, needsLine = true, needsIcon = true)
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
        binding.addMemberToList.setOnClickListener {
            showMemberAddedMessage()
        }
        binding.addGroup.setOnClickListener {
            showGroupAddedMessage()
        }
    }

    private fun showMemberAddedMessage() {
        MaterialDialog.createDialog(this) {
            setTitle(getString(R.string.add_member_button))
            setMessage(getString(R.string.do_you_want_to_add_this_compa))
            setPositiveButton(getString(R.string.accept)) { _, _ ->
                members.add(Member(2, "blabal", "", binding.memberEditText.text.toString()))
                getMembers()
                binding.memberEditText.setText("")
                binding.memberEditText.clearFocus()
            }
            setNegativeButton(getString(R.string.no_no_no)) { _, _ -> }
        }.show()
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
                    db.collection("groups").document(id).collection("members").document("members").set(member)
                    if (position == members.size - 1) {
                        finish()
                    }
                }
            }
            setNegativeButton(getString(R.string.no_no_no)) { _, _ -> }
        }.show()

    }
}