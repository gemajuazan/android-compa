package org.example.compa.ui.payments

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
        initializePayments()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        initializePayments()
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
                    binding.recyclerView.adapter = paymentAdapter
                    onClickPayment()
                }
            }
        }
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

        paymentBinding.emisor.text = payment.transmitter
        paymentBinding.receptor.text = payment.receiver
        paymentBinding.concept.text = payment.concept
        paymentBinding.date.text = payment.date.toString()
        paymentBinding.price.text = payment.price.toString()

        paymentBinding.markAsPaidCard.setOnClickListener {
            payPayment(payment)
            paymentBinding.paymentStatus.visibility = View.VISIBLE
            paymentBinding.status.text = getString(R.string.paid)
        }

        paymentDialog.setContentView(paymentBinding.root)
        paymentDialog.dismissWithAnimation = true
        paymentDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        paymentDialog.show()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            val window: Window? = paymentDialog.window
            if (window != null) {
                val metrics = DisplayMetrics()

                window.windowManager.defaultDisplay.getMetrics(metrics)

                val dimDrawable = GradientDrawable()
                val navigationDrawable = GradientDrawable()

                navigationDrawable.shape = GradientDrawable.RECTANGLE
                navigationDrawable.setColor(ContextCompat.getColor(requireContext(), R.color.onPrimaryColor))

                val layers: Array<Drawable> = arrayOf(dimDrawable, navigationDrawable)
                val windowBackground = LayerDrawable(layers)

                windowBackground.setLayerInsetTop(1, metrics.heightPixels - 10)
                window.setBackgroundDrawable(windowBackground)
            }
        }
    }

    private fun payPayment(payment: Payment) {
        db.collection("payments").document(payment.id).update("statusPayment", "PAY")
    }
}