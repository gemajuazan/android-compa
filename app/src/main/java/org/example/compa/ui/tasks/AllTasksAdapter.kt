package org.example.compa.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.my_tasks_item_recycler_view.view.*
import org.example.compa.R
import org.example.compa.databinding.ItemMyTaskBinding
import org.example.compa.databinding.MyTasksItemRecyclerViewBinding
import org.example.compa.models.Task
import org.example.compa.models.constants.Constants
import org.example.compa.ui.adapters.FriendAdapter
import org.example.compa.utils.DateUtil
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AllTasksAdapter(
    private val listTasks: ArrayList<Task>,
    private val context: Context
) : RecyclerView.Adapter<AllTasksAdapter.ViewHolder>(){

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTasks[position]
        holder.bind(item, position)
    }

    override fun getItemCount(): Int {
        return listTasks.size
    }

    inner class ViewHolder(private val binding: ItemMyTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Task, position: Int) {
            binding.nameTask.text = item.name
            binding.dateTask.text = "${DateUtil.getDate(item.startDate, "dd/MM/yy")} - ${DateUtil.getDate(item.finishDate, "dd/MM/yy")}"
            binding.categoryTask.text = context.getString(Constants.CategoryTask.getCategoryTask(item.category))

            binding.imageTask.setImageResource(Constants.CategoryTask.getCategoryImageTask(item.category))

            binding.cardViewNoMyTask.setOnClickListener {
                itemClickListener?.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        itemClickListener = onItemClickListener
    }
}