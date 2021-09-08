package org.example.compa.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.databinding.ItemGroupBinding
import org.example.compa.models.Group
import org.example.compa.models.Member
import org.example.compa.utils.StyleUtil


class GroupAdapter(
    private val listsGroups: ArrayList<Group>,
    private val listMembers: ArrayList<Member>,
    private val context: Context,
    private val needsLine: Boolean,
    private val needsIcon: Boolean
) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isRotated: Boolean = false

        fun bind(group: Group) {

            val animationStart =
                StyleUtil.setAlphaAnimation(0f, 1f, binding.membersRecyclerView, false)
            val animationEnd =
                StyleUtil.setAlphaAnimation(1f, 0f, binding.membersRecyclerView, true)

            binding.arrowGroup.setOnClickListener {
                isRotated = StyleUtil.rotateFab(binding.arrowGroup, !isRotated, 180f)
                if (isRotated) {
                    binding.membersRecyclerView.visibility = View.VISIBLE
                    binding.actionsGroup.visibility = View.VISIBLE
                } else {
                    binding.membersRecyclerView.visibility = View.GONE
                    binding.actionsGroup.visibility = View.GONE
                }
            }
            binding.nameGroup.text = group.name
            binding.membersRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.membersRecyclerView.adapter = TextAdapter(listMembers, needsLine, needsIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listsGroups[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return listsGroups.size
    }
}