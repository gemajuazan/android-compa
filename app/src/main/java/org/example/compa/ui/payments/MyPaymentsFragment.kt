package org.example.compa.ui.payments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.MyPaymentsFragmentBinding
import org.example.compa.databinding.PaymentDetailFragmentBinding
import org.example.compa.models.Payment
import org.example.compa.preferences.AppPreference
import org.example.compa.utils.DateUtil
import java.text.NumberFormat
import java.util.*


class MyPaymentsFragment : Fragment() {

    private lateinit var binding: MyPaymentsFragmentBinding

    private var payments = arrayListOf<Payment>()
    private lateinit var paymentAdapter: MyPaymentAdapter
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyPaymentsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        initializePayments()
    }

    private fun initializePayments() {
        payments.clear()
        binding.loader.visibility = View.VISIBLE
        paymentAdapter = MyPaymentAdapter(payments, requireContext())
        db.collection("payments").get().addOnSuccessListener {
            for (payment in it.documents) {
                val id = payment.data?.get("id") as String
                db.collection("payments").document(id).get().addOnSuccessListener { payment ->
                    val transmitter = payment.data?.get("transmitter") as String
                    val receiver = payment.data?.get("receiver") as String

                    if (transmitter == AppPreference.getUserUsername()) {
                        val id = payment.data?.get("id") as String
                        val price = payment.data?.get("price") as Double
                        val date = payment.data?.get("date") as Long
                        val concept = payment.data?.get("concept") as String
                        val typePayment = payment.data?.get("typePayment") as String
                        val statusPayment = payment.data?.get("statusPayment") as String
                        val newPayment = Payment(
                            id = id,
                            transmitter = transmitter,
                            receiver = receiver,
                            price = price,
                            date = date,
                            concept = concept,
                            typePayment = typePayment,
                            statusPayment = statusPayment
                        )
                        payments.add(newPayment)
                        paymentAdapter.notifyDataSetChanged()
                    }

                    paymentAdapter = MyPaymentAdapter(payments, requireContext())
                    binding.recyclerView.adapter = paymentAdapter
                    onClickPayment()
                }
            }
        }
        binding.loader.visibility = View.GONE
    }

    private fun onClickPayment() {
        paymentAdapter.setOnItemClickListener(object  : MyPaymentAdapter.OnItemClickListener {
            override fun onPaymentClick(payment: Payment) {
                showPaymentBottomSheet(payment)
            }

        })
    }

    private fun showPaymentBottomSheet(payment: Payment) {
        val paymentDialog = BottomSheetDialog(requireContext())
        val paymentBinding = PaymentDetailFragmentBinding.inflate(layoutInflater)

        var changed = false

        paymentBinding.emisor.text = payment.transmitter
        paymentBinding.receptor.text = payment.receiver
        paymentBinding.concept.text = payment.concept
        paymentBinding.date.text = DateUtil.getDate(payment.date ?: -1, "dd/MM/yyyy")

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("EUR")
        paymentBinding.price.text = format.format(payment.price)

        if (payment.statusPayment == "NPA") {
            paymentBinding.markAsPaidTextView.text = getString(R.string.mark_as_paid)
        } else {
            paymentBinding.markAsPaidTextView.text = getString(R.string.mark_as_unpaid)
        }

        paymentBinding.markAsPaidCard.setOnClickListener {
            changed = true
            if (payment.statusPayment == "NPA") {
                payPayment(payment)
                paymentBinding.status.text = getString(R.string.paid)
                paymentBinding.status.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorAccent))
                paymentBinding.markAsPaidTextView.text = getString(R.string.mark_as_unpaid)
            } else {
                payPayment(payment)
                paymentBinding.status.text = getString(R.string.unpaid)
                paymentBinding.status.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorError))
                paymentBinding.markAsPaidTextView.text = getString(R.string.mark_as_paid)
            }
            paymentBinding.paymentStatus.visibility = View.VISIBLE
        }

        paymentDialog.setContentView(paymentBinding.root)
        paymentDialog.dismissWithAnimation = true
        paymentDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        paymentDialog.show()

        paymentDialog.setOnDismissListener {
            if (changed) initializePayments()
        }
    }

    private fun payPayment(payment: Payment) {
        db.collection("payments").document(payment.id).update("statusPayment", "PAY")
    }
}