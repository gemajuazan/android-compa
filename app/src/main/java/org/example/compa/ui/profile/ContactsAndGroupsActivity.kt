package org.example.compa.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.ContactsAndGroupsActivityBinding
import org.example.compa.models.Group
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.adapters.GroupAdapter
import org.example.compa.utils.MaterialDialog

class ContactsAndGroupsActivity : AppCompatActivity() {
    private lateinit var binding: ContactsAndGroupsActivityBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContactsAndGroupsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitConfiguration()
    }

    override fun onResume() {
        super.onResume()
        getGroups(arrayListOf())
    }

    private fun setInitConfiguration() {
        db = FirebaseFirestore.getInstance()
        setTranslations()
        setFABConfiguration()
        setRecyclerView()
    }

    private fun setTranslations() {
        binding.compaToolbar.title.text = getString(R.string.contacts)
        binding.compaToolbar.title.text = getString(R.string.groups)
/*        binding.compaToolbar.backButtonImageView.setOnClickListener {
            finish()
        }*/
    }

    private fun setFABConfiguration() {
        binding.fabGroups.setOnClickListener {
            goToNewGroup()
        }
    }

    private fun setRecyclerView() {
        val groups = ArrayList<Group>()
        binding.listGroups.layoutManager = LinearLayoutManager(this)
        getGroups(groups)
    }

    private fun getGroups(groups: ArrayList<Group>) {
        groupAdapter = GroupAdapter(
            listsGroups = groups,
            context = this@ContactsAndGroupsActivity,
            needsLine = false,
            needsIcon = false
        )
        db.collection("person").document(AppPreference.getUserUID()).collection("groups").get()
            .addOnSuccessListener {
                for (group in it.documents) {
                    val id = group.data?.get("id") as String
                    val place = group.data?.get("place") as String
                    val name = group.data?.get("name") as String
                    groups.add(Group(id, name, place))
                    groupAdapter.notifyDataSetChanged()

                }
                showNoGroups(groups)
                groupAdapter = GroupAdapter(
                    groups, context = this@ContactsAndGroupsActivity,
                    needsLine = false,
                    needsIcon = false
                )

                binding.listGroups.adapter = groupAdapter
                setActionsGroup()
            }

    }

    private fun showNoGroups(groups: ArrayList<Group>) {
        if (groups.size == 0) {
            binding.noGroups.visibility = View.VISIBLE
            binding.noGroupsMessage.visibility = View.VISIBLE
            binding.listGroups.visibility = View.GONE
        } else {
            binding.noGroups.visibility = View.GONE
            binding.noGroupsMessage.visibility = View.GONE
            binding.listGroups.visibility = View.VISIBLE
        }
    }

    private fun setActionsGroup() {
        groupAdapter.setOnItemClickListener(object : GroupAdapter.ItemClickListener {
            override fun onSeeGroupClicked(group: Group, position: Int) {
                val intent = Intent(this@ContactsAndGroupsActivity, CreateGroupActivity::class.java)
                intent.putExtra("action", 1)
                intent.putExtra("groupId", group.id)
                startActivity(intent)
            }

            override fun onEditGroupClicked(group: Group, position: Int) {
                val intent = Intent(this@ContactsAndGroupsActivity, CreateGroupActivity::class.java)
                intent.putExtra("groupId", group.id)
                intent.putExtra("action", 2)
                startActivity(intent)
            }

            override fun onRemoveGroupClicked(group: Group, position: Int) {
                MaterialDialog.createDialog(this@ContactsAndGroupsActivity) {
                    setTitle(R.string.delete_group)
                    setMessage(R.string.do_you_want_to_delete_group)
                    setPositiveButton(R.string.of_course_madafak) { _, _ ->
                        db.collection("groups").document(group.id).collection("members").document().delete()
                        db.collection("groups").document(group.id).delete()
                        getGroups(arrayListOf())
                    }
                    setNegativeButton(R.string.cancel) { _, _ ->

                    }
                }.show()

            }

        })
    }

    private fun goToNewGroup() {
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }
}