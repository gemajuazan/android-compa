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
import org.example.compa.models.Friend
import org.example.compa.models.Person
import org.example.compa.utils.DateUtil
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class FriendAdapter(
    private val listPeople: ArrayList<Friend>,
    private val addFriend: Boolean
) :
    RecyclerView.Adapter<FriendAdapter.ViewHolder>(), Filterable {

    var listFriends = ArrayList<Friend>()
    var listFilteredFriends = ArrayList<Friend>()
    var itemClickListener: ItemClickListener? = null

    init {
        listFriends = listPeople
        listFilteredFriends = listPeople
    }

    inner class ViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isFavourite = false

        fun bind(friend: Friend, position: Int) {
            binding.nameTextView.text = friend.person.name + " " + friend.person.surnames
            binding.usernameTextView.text = friend.person.username
            binding.birthdateTextView.text = DateUtil.getDate(friend.person.birthdate ?: -1, "dd/MM/yyyy")

            if (addFriend) {
                binding.favourite.setImageResource(R.drawable.ic_add_user)
            } else {
                binding.favourite.visibility = View.GONE
            }

            val position = listFriends.indexOf(friend.person)
            binding.favourite.setOnClickListener {
                isFavourite = !isFavourite
                if (isFavourite) {
                    if (addFriend) {
                        binding.favourite.setImageResource(R.drawable.ic_baseline_accessibility_24)
                    } else {
                        binding.favourite.visibility = View.GONE
                    }
                } else {
                    if (addFriend) {
                        binding.favourite.setImageResource(R.drawable.ic_add_user)
                    } else {
                        binding.favourite.visibility = View.GONE
                    }
                }
                itemClickListener?.onItemAddClicked(friend.person, position)
            }

            if (position == listFilteredFriends.size - 1) {
                binding.viewFinal.visibility = View.GONE
            }

            if (position == listFriends.size - 1) {
                binding.viewFinal.visibility = View.GONE
            }

            if (position == listPeople.size - 1) {
                binding.viewFinal.visibility = View.GONE
            }

            binding.linearLayout4.setOnLongClickListener {
                itemClickListener?.onItemLongClicked(person = friend.person, position)
                true
            }

            binding.linearLayout4.setOnClickListener {
                itemClickListener?.onItemClicked(person = friend.person, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listFilteredFriends[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return listFilteredFriends.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charSearch = p0.toString()
                listFilteredFriends = if (charSearch.isEmpty()) {
                    listPeople
                } else {
                    val resultList = ArrayList<Friend>()
                    for (row in listPeople) {
                        if (row.person.username.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || row.person.name.toLowerCase(
                                Locale.ROOT
                            )
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || row.person.surnames.toLowerCase(
                                Locale.ROOT
                            ).contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listFilteredFriends
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                listFilteredFriends = p1?.values as ArrayList<Friend>
                notifyDataSetChanged()
            }

        }
    }

    interface ItemClickListener {
        fun onItemClicked(person: Person, position: Int)
        fun onItemAddClicked(person: Person, position: Int)
        fun onItemLongClicked(person: Person, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: ItemClickListener) {
        itemClickListener = onItemClickListener
    }
}