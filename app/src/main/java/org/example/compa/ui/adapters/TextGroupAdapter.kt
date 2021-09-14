package org.example.compa.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.databinding.ItemTextBinding
import org.example.compa.databinding.ItemTextWithoutIconBinding
import org.example.compa.models.Group
import org.example.compa.models.Member
import org.example.compa.models.constants.Constants.StatusTask.Companion.getStatusTask
import org.example.compa.models.constants.Constants.StatusTask.Companion.getStatusTaskCode

class TextGroupAdapter(private val listText: ArrayList<Group>) :
    RecyclerView.Adapter<TextGroupAdapter.ViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(private val binding: ItemTextWithoutIconBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(element: Group) {
            binding.text.text = element.name
            binding.text.setOnClickListener {
                itemClickListener?.onItemClick(element)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTextWithoutIconBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listText[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return listText.size
    }

    interface OnItemClickListener {
        fun onItemClick(group: Group)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        itemClickListener = onItemClickListener
    }
}