package org.example.compa.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.databinding.ItemTextBinding
import org.example.compa.databinding.ItemTextWithoutIconBinding
import org.example.compa.models.DataWithCode
import org.example.compa.models.Member
import org.example.compa.models.constants.Constants.StatusTask.Companion.getStatusTask
import org.example.compa.models.constants.Constants.StatusTask.Companion.getStatusTaskCode

class TextElementAdapter(private val listText: ArrayList<DataWithCode>) :
    RecyclerView.Adapter<TextElementAdapter.ViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(private val binding: ItemTextWithoutIconBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(element: DataWithCode) {
            binding.text.text = element.text
            binding.text.setOnClickListener {
                itemClickListener?.onItemClick(element.code)
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
        fun onItemClick(statusCode: String)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        itemClickListener = onItemClickListener
    }
}