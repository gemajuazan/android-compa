package org.example.compa.ui.payments

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.example.compa.R
import org.example.compa.databinding.ItemMyPaymentRecyclerViewBinding
import org.example.compa.databinding.ItemOwePaymentsRecyclerViewBinding
import org.example.compa.models.Payment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SimpleDateFormat", "SetTextI18n")
class MyPaymentAdapter(
    private val payments: ArrayList<Payment>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                ViewHolder(
                    ItemMyPaymentRecyclerViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                ViewHolderMine(
                    ItemOwePaymentsRecyclerViewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                val holder1: ViewHolder = holder as ViewHolder
                holder1.bind(payments[position], position)
            }
            else -> {
                val holder2: ViewHolderMine = holder as ViewHolderMine
                holder2.bind(payments[position], position)
            }
        }
    }

    override fun getItemCount() = payments.size

    override fun getItemViewType(position: Int): Int {
        return if (payments[position].typePayment == "PAY") {
            0
        } else 1
    }

    inner class ViewHolder(private val binding: ItemMyPaymentRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(item: Payment, position: Int) {
            val dateC = Date(item.date)
            val df = SimpleDateFormat("dd/MM/yyyy")

            binding.emisor.text = item.transmitter
            binding.receptor.text = item.receiver
            binding.price.text = item.price.toString() + " €"
            binding.date.text = df.format(dateC)
            binding.concept.text = item.concept

            if (item.typePayment == "PAY") {
                binding.price.setTextColor(ContextCompat.getColor(context, R.color.colorError))
            }

            if (item.typePayment == "REF") {
                binding.price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            }

            if (item.statusPayment == "PAY") {
                binding.statusPayment.text = context.getString(R.string.paid)
                binding.statusPaymentCard.visibility = View.VISIBLE
                binding.statusNoPaymentCard.visibility = View.GONE
            }

            if (item.statusPayment == "NPA") {
                binding.statusNoPayment.text = context.getString(R.string.unpaid)
                binding.statusPaymentCard.visibility = View.GONE
                binding.statusNoPaymentCard.visibility = View.VISIBLE
            }

            binding.cardViewMyPayments.setOnClickListener {
                itemClickListener?.onPaymentClick(payment = item)
            }
        }
    }

    inner class ViewHolderMine(private val binding: ItemOwePaymentsRecyclerViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Payment, position: Int) {
            val dateC = Date(item.date.toLong())
            val df = SimpleDateFormat("dd/MM/yyyy")

            binding.emisor.text = item.transmitter
            binding.receptor.text = item.receiver
            binding.price.text = item.price.toString() + " €"
            binding.date.text = df.format(dateC)
            binding.concept.text = item.concept

            if (item.typePayment == "PAY") {
                binding.price.setTextColor(ContextCompat.getColor(context, R.color.colorError))
            }
            if (item.typePayment == "REF") {
                binding.price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            }

            if (item.statusPayment == "PAY") {
                binding.statusPayment.text = context.getString(R.string.paid)
                binding.statusPaymentCard.visibility = View.VISIBLE
                binding.statusNoPaymentCard.visibility = View.GONE
            }
            if (item.statusPayment == "NPA") {
                binding.statusNoPayment.text = context.getString(R.string.unpaid)
                binding.statusPaymentCard.visibility = View.GONE
                binding.statusNoPaymentCard.visibility = View.VISIBLE
            }

            binding.cardViewMyPayments.setOnClickListener {
                itemClickListener?.onPaymentClick(payment = item)
            }
        }
    }

    interface OnItemClickListener {
        fun onPaymentClick(payment: Payment)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        itemClickListener = onItemClickListener
    }
}