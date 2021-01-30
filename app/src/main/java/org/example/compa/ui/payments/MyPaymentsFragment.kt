package org.example.compa.ui.payments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.my_payments_fragment.*
import org.example.compa.R
import org.example.compa.db.CompaSQLiteOpenHelper
import org.example.compa.models.Payment


class MyPaymentsFragment : Fragment() {

    private var listMyPayments = arrayListOf<Payment>()

    private var username = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_payments_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUser()
        initializePayments()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MyPaymentAdapter(listMyPayments)
    }

    private fun getUser() {
        val db = CompaSQLiteOpenHelper(
            requireContext(),
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        val row = dbCompa.rawQuery("SELECT username FROM user ", null)
        if (row.moveToFirst()) {
            username = row.getString(0)
        }
        dbCompa.close()
    }

    private fun initializePayments() {
        val db = CompaSQLiteOpenHelper(
            requireContext(),
            "dbCompa",
            null,
            CompaSQLiteOpenHelper.DATABASE_VERSION
        )
        val dbCompa = db.writableDatabase

        var payment: Payment

        val row = dbCompa.rawQuery(
            "SELECT _id, transmitter, receiver, date, price, concept FROM payment",
            null
        )

        while (row.moveToNext()) {
            if (row.getString(1) == username) {
                payment = Payment(
                    id = row.getInt(0),
                    transmitter = row.getString(1),
                    receiver = row.getString(2),
                    date = row.getString(3),
                    price = row.getDouble(4),
                    concept = row.getString(5),
                    statusPayment = ""
                )
                listMyPayments.add(payment)
            }
        }

        dbCompa.close()
    }
}