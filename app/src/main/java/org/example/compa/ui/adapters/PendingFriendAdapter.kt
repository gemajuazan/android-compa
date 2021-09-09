package org.example.compa.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.R
import org.example.compa.databinding.ItemFriendBinding
import org.example.compa.databinding.ItemPendingFriendBinding
import org.example.compa.models.Friend
import org.example.compa.models.Person
import org.example.compa.utils.DateUtil
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class PendingFriendAdapter(
    private val listPeople: ArrayList<Friend>,
    private val addFriend: Boolean
) :
    RecyclerView.Adapter<PendingFriendAdapter.ViewHolder>() {

    var itemClickListener: ItemClickListener? = null

    inner class ViewHolder(private val binding: ItemPendingFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isFavourite = false

        fun bind(friend: Friend, position: Int) {
            binding.nameTextView.text = friend.person.name + " " + friend.person.surnames
            binding.usernameTextView.text = friend.person.username
            binding.birthdateTextView.text = DateUtil.getDate(friend.person.birthdate ?: -1, "dd/MM/yyyy")

            if (friend.solicitude) {
                binding.accept.visibility = View.GONE
                binding.cancel.visibility = View.GONE
            } else {
                binding.accept.visibility = View.VISIBLE
                binding.cancel.visibility = View.VISIBLE
            }

            binding.accept.setOnClickListener {
                itemClickListener?.onAcceptClicked(friend.person, position)
            }

            binding.cancel.setOnClickListener {
                itemClickListener?.onCancelClicked(friend.person, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPendingFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPeople[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return listPeople.size
    }

    interface ItemClickListener {
        fun onAcceptClicked(person: Person, position: Int)
        fun onCancelClicked(person: Person, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: ItemClickListener) {
        itemClickListener = onItemClickListener
    }
}