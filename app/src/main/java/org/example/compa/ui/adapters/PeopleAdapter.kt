package org.example.compa.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
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
class PeopleAdapter(
    private val listPeople: ArrayList<Person>,
    private val addFriend: Boolean
) :
    RecyclerView.Adapter<PeopleAdapter.ViewHolder>(), Filterable {

    var listFriends = ArrayList<Person>()
    var listFilteredFriends = ArrayList<Person>()
    var itemClickListener: ItemClickListener? = null

    init {
        listFriends = listPeople
        listFilteredFriends = listPeople
    }

    inner class ViewHolder(private val binding: ItemFriendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isFavourite = false

        fun bind(person: Person) {
            binding.nameTextView.text = person.name + " " + person.surnames
            binding.usernameTextView.text = person.username
            binding.birthdateTextView.text = DateUtil.getDate(person.birthdate ?: -1, "dd/MM/yyyy")

            if (addFriend) {
                binding.favourite.setImageResource(R.drawable.ic_baseline_person_add_24)
            } else {
                binding.favourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }


            val position = listFriends.indexOf(person)
            binding.favourite.setOnClickListener {
                isFavourite = !isFavourite
                if (isFavourite) {
                    if (addFriend) {
                        binding.favourite.setImageResource(R.drawable.ic_baseline_accessibility_24)
                    } else {
                        binding.favourite.setImageResource(R.drawable.ic_baseline_favorite_24)
                    }
                } else {
                    if (addFriend) {
                        binding.favourite.setImageResource(R.drawable.ic_baseline_person_add_24)
                    } else {
                        binding.favourite.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                    }
                }
                itemClickListener?.onItemClicked(person, position, isFavourite)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listFilteredFriends[position]
        holder.bind(item)
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
                    val resultList = ArrayList<Person>()
                    for (row in listPeople) {
                        if (row.username.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || row.name.toLowerCase(
                                Locale.ROOT
                            )
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || row.surnames.toLowerCase(
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
                listFilteredFriends = p1?.values as ArrayList<Person>
                notifyDataSetChanged()
            }

        }
    }

    fun interface ItemClickListener {
        fun onItemClicked(person: Person, position: Int, isFavourite: Boolean)
    }

    fun setOnItemClickListener(onItemClickListener: ItemClickListener) {
        itemClickListener = onItemClickListener
    }
}