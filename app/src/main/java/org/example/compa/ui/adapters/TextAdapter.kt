package org.example.compa.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.databinding.ItemTextBinding
import org.example.compa.models.Member

class TextAdapter(private val listText: ArrayList<Member>, private val needsLine: Boolean, private val needsIcon: Boolean) :
    RecyclerView.Adapter<TextAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTextBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(member: Member) {
            binding.text.text = member.email

            if (needsLine) binding.line.visibility = View.VISIBLE
            else binding.line.visibility = View.GONE

            if (needsIcon) binding.cancel.visibility = View.VISIBLE
            else binding.cancel.visibility = View.GONE

            binding.cancel.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        fun onItemClick(position: Int, view: View)
    }
}