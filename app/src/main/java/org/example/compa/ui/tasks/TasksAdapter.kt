package org.example.compa.ui.tasks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.my_tasks_item_recycler_view.view.*
import org.example.compa.R
import org.example.compa.models.Task
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TasksAdapter(
    private val listTasks: ArrayList<Task>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<TasksAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(
            R.layout.my_tasks_item_recycler_view,
            parent,
            false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTasks[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return listTasks.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val nameTask: TextView = itemView.findViewById(R.id.name_task)
        private val dateTask: TextView  = itemView.findViewById(R.id.date_task)
        private val categoryTask: TextView  = itemView.findViewById(R.id.category_task)
        private val membersTask: TextView  = itemView.findViewById(R.id.number_members_task)

        init {
            itemView.cardViewMyTasks.setOnClickListener(this)
        }

        fun bind(item: Task) {

            val dateStart = Date(item.startDate.toLong())
            val dateFinish = Date(item.finishDate.toLong())
            val df = SimpleDateFormat("dd/MM/yyyy")

            nameTask.text = item.name
            dateTask.text = df.format(dateStart) + " - " + df.format(dateFinish)
            categoryTask.text = item.category

            if (item.numberMembers > 1) {
                membersTask.text = item.numberMembers.toString() + " miembros"
            } else {
                membersTask.text = item.numberMembers.toString() + " miembro"
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position = position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}