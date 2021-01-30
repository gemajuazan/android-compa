package org.example.compa.ui.payments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.payment_historial_fragment.*
import org.example.compa.R
import org.example.compa.models.Payment
import org.example.compa.models.Person

class PaymentHistorialFragment : Fragment() {

    private var listHistorialPayment = arrayListOf<Payment>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.payment_historial_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializePayments()
        recycler_view_historial.layoutManager = LinearLayoutManager(requireContext())
        recycler_view_historial.adapter = HistorialPaymentsAdapter(listHistorialPayment)
    }

    private fun initializePayments() {


    }

}