package org.example.compa.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.databinding.ItemMemberTaskBinding
import org.example.compa.models.MemberTask
import org.example.compa.models.constants.Constants.StatusTask.Companion.getStatusTask
import kotlin.collections.ArrayList

@SuppressLint("SetTextI18n")
class MemberTaskAdapter(
    private val membersTask: ArrayList<MemberTask>,
    private val context: Context
) :
    RecyclerView.Adapter<MemberTaskAdapter.ViewHolder>() {

    var itemClickListener: ItemClickListener? = null


    inner class ViewHolder(private val binding: ItemMemberTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(memberTask: MemberTask, position: Int) {
            binding.email.text = memberTask.member.email
            binding.status.text = context.getString(getStatusTask(memberTask.statusTask))
            binding.edit.setOnClickListener {
                itemClickListener?.onEditClicked(memberTask, position, binding.status)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = membersTask[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return membersTask.size
    }

    interface ItemClickListener {
        fun onEditClicked(person: MemberTask, position: Int, textView: TextView)
    }

    fun setOnItemClickListener(onItemClickListener: ItemClickListener) {
        itemClickListener = onItemClickListener
    }
}