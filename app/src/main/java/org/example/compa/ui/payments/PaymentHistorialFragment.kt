package org.example.compa.ui.payments

import android.app.DatePickerDialog
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HALF_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.AddPaymentFragmentBinding
import org.example.compa.databinding.PaymentDetailFragmentBinding
import org.example.compa.databinding.PaymentHistorialFragmentBinding
import org.example.compa.models.Payment
import org.example.compa.preferences.AppPreference
import org.example.compa.utils.DateUtil
import org.example.compa.utils.MaterialDialog
import org.example.compa.utils.StyleUtil
import java.util.*

class PaymentHistorialFragment : Fragment() {

    private lateinit var binding: PaymentHistorialFragmentBinding

    private var payments = arrayListOf<Payment>()
    private lateinit var paymentAdapter: MyPaymentAdapter
    private lateinit var db: FirebaseFirestore

    private val myCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PaymentHistorialFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        initializePayments()
        binding.recyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun initializePayments() {
        payments.clear()
        paymentAdapter = MyPaymentAdapter(payments, requireContext())
        db.collection("payments").get().addOnSuccessListener {
            for (payment in it.documents) {
                val id = payment.data?.get("id") as String
                db.collection("payments").document(id).get().addOnSuccessListener { payment ->
                    val transmitter = payment.data?.get("transmitter") as String
                    val receiver = payment.data?.get("receiver") as String

                    if (transmitter == AppPreference.getUserUsername() || receiver == AppPreference.getUserUsername()) {
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
                            price = price.toDouble(),
                            date = date,
                            concept = concept,
                            typePayment = typePayment,
                            statusPayment = statusPayment
                        )
                        payments.add(newPayment)
                        paymentAdapter.notifyDataSetChanged()
                    }
                    paymentAdapter = MyPaymentAdapter(payments, requireContext())
                    binding.recyclerViewHistorial.adapter = paymentAdapter
                    onClickPayment()
                }
            }
        }
    }

    private fun onClickPayment() {
        paymentAdapter.setOnItemClickListener(object : MyPaymentAdapter.OnItemClickListener {
            override fun onPaymentClick(payment: Payment) {
                showPaymentBottomSheet(payment)
            }

        })
    }

    private fun showPaymentBottomSheet(payment: Payment) {
        val paymentDialog = BottomSheetDialog(requireContext())
        val paymentBinding = PaymentDetailFragmentBinding.inflate(layoutInflater)

        paymentBinding.emisor.text = payment.transmitter
        paymentBinding.receptor.text = payment.receiver
        paymentBinding.concept.text = payment.concept
        paymentBinding.date.text = payment.date.toString()
        paymentBinding.price.text = payment.price.toString()

        paymentBinding.markAsPaidCard.setOnClickListener {
            if (payment.statusPayment == "NPA") {
                payPayment(payment, "PAY")
                paymentBinding.status.text = getString(R.string.paid)
            } else {
                payPayment(payment, "NPA")
                paymentBinding.status.text = getString(R.string.unpaid)
            }
            paymentBinding.paymentStatus.visibility = View.VISIBLE
        }

        paymentDialog.setContentView(paymentBinding.root)
        paymentDialog.dismissWithAnimation = true
        paymentDialog.behavior.state = STATE_EXPANDED

        paymentDialog.show()
        paymentDialog.setOnDismissListener {
            initializePayments()
        }

    }

    private fun payPayment(payment: Payment, status: String) {
        db.collection("payments").document(payment.id).update("statusPayment", status)
    }

}