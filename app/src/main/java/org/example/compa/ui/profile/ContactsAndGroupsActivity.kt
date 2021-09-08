package org.example.compa.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import org.example.compa.R
import org.example.compa.databinding.ContactsAndGroupsActivityBinding
import org.example.compa.models.Group
import org.example.compa.models.Member
import org.example.compa.ui.adapters.GroupAdapter
import org.example.compa.utils.StyleUtil

class ContactsAndGroupsActivity : AppCompatActivity() {
    private lateinit var binding: ContactsAndGroupsActivityBinding
    private var isRotated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContactsAndGroupsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setInitConfiguration()
    }

    private fun setInitConfiguration() {
        setTranslations()
        setFABConfiguration()
        setRecyclerView()
    }

    private fun setTranslations() {
        binding.compaToolbar.profileTitle.text = getString(R.string.contacts)
        binding.compaToolbar.profileSubtitle.text = getString(R.string.and_groups)
    }

    private fun setFABConfiguration() {
/*        StyleUtil.init(binding.editGroup)
        StyleUtil.init(binding.addGroup)
        StyleUtil.init(binding.removeGroup)*/
        binding.fabGroups.setOnClickListener {
            goToNewGroup()
/*            isRotated = StyleUtil.rotateFab(binding.fabGroups, !isRotated, 55F)
            if (isRotated) {
                StyleUtil.showIn(binding.editGroup)
                StyleUtil.showIn(binding.addGroup)
                StyleUtil.showIn(binding.removeGroup)
            } else {
                StyleUtil.showOut(binding.editGroup)
                StyleUtil.showOut(binding.addGroup)
                StyleUtil.showOut(binding.removeGroup)
            }*/
        }
    }

    private fun setRecyclerView() {
        val groups = ArrayList<Group>()
        val members = ArrayList<Member>()
        members.add(Member(1, "", "gemajuazan","gemajuazan@gmail.com"))
        members.add(Member(1, "", "gemajuazan","gemajuazan@gmail.com"))
        groups.add(Group("1", "Grupo Castellón", "Castellón"))
        groups.add(Group("1", "Grupo Valencia", "Castellón"))
        binding.listGroups.layoutManager = LinearLayoutManager(this)
        binding.listGroups.adapter =
            GroupAdapter(listsGroups = groups, listMembers = members, context = this@ContactsAndGroupsActivity, needsLine = false, needsIcon = false)
    }

    private fun goToNewGroup() {
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }
}