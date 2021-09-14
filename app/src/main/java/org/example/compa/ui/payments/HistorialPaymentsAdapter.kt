package org.example.compa.ui.payments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.R
import org.example.compa.models.Payment

class HistorialPaymentsAdapter(private val listHistorialPayments: ArrayList<Payment>) : RecyclerView.Adapter<HistorialPaymentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistorialPaymentsAdapter.ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(
            R.layout.item_historial_recycler_view,
            parent,
            false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: HistorialPaymentsAdapter.ViewHolder, position: Int) = holder.bind(listHistorialPayments[position])

    override fun getItemCount() = listHistorialPayments.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        private val transmitter: TextView = itemView.findViewById(R.id.historial_transmitter)
        private val receiver: TextView = itemView.findViewById(R.id.historial_receiver)
        private val price: TextView = itemView.findViewById(R.id.historial_price)
        private val date: TextView = itemView.findViewById(R.id.historial_date)

        fun bind(item: Payment) {
            transmitter.text = item.transmitter
            receiver.text = item.receiver
            price.text = item.price.toString() + "€"
            //date.text = item.date

            //if (item.statusPayment == "PAY") price.setTextColor(android.R.color.holo_red_dark)
            //if (item.statusPayment == "REF") price.setTextColor(android.R.color.holo_green_dark)
        }
    }
}