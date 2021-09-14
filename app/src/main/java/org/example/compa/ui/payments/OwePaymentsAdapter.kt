package org.example.compa.ui.payments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.R
import org.example.compa.models.Payment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OwePaymentsAdapter(private val listHistorialPayments: ArrayList<Payment>) : RecyclerView.Adapter<OwePaymentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OwePaymentsAdapter.ViewHolder {
        val item = LayoutInflater.from(parent.context).inflate(
            R.layout.item_owe_payments_recycler_view,
            parent,
            false
        )
        return ViewHolder(item)
    }

    override fun onBindViewHolder(holder: OwePaymentsAdapter.ViewHolder, position: Int) = holder.bind(listHistorialPayments[position])

    override fun getItemCount() = listHistorialPayments.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        /*private val transmitter: TextView = itemView.findViewById(R.id.owe_transmitter)
        private val receiver: TextView = itemView.findViewById(R.id.owe_receiver)
        private val price: TextView = itemView.findViewById(R.id.owe_price)
        private val date: TextView = itemView.findViewById(R.id.owe_date)
        private val concept: TextView = itemView.findViewById(R.id.owe_payments_concept)*/

        fun bind(item: Payment) {
            /*val dateC = Date(item.date.toLong())
            val df = SimpleDateFormat("dd/MM/yyyy")

            transmitter.text = item.transmitter
            receiver.text = item.receiver
            price.text = item.price.toString() + " €"
            date.text = df.format(dateC)
            concept.text = item.concept*/
        }
    }
}