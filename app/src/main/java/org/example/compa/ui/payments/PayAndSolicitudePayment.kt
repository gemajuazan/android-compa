package org.example.compa.ui.payments

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.pay_and_solicitude_payment_activity.*
import org.example.compa.R
import org.example.compa.db.CompaSQLiteOpenHelper
import org.example.compa.services.PaymentService
import org.example.compa.utils.MaterialDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PayAndSolicitudePayment : AppCompatActivity() {

    private var typePayment = ""

    private lateinit var addPayButton: Button

    private lateinit var payTransmitter: TextInputEditText
    private lateinit var payReceiver: TextInputEditText
    private lateinit var payDate: TextInputEditText
    private lateinit var payPrice: TextInputEditText
    private lateinit var payConcept: TextInputEditText

    private val myCalendar = Calendar.getInstance()
    private var time: Long? = 0
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_and_solicitude_payment_activity)
        addPayButton = findViewById(R.id.add_payment_button)

        payTransmitter = findViewById(R.id.pay_transmitter_edit)
        payReceiver = findViewById(R.id.pay_receiver_edit)
        payDate = findViewById(R.id.pay_date_edit)
        payPrice = findViewById(R.id.pay_price_edit)
        payConcept = findViewById(R.id.pay_concept_edit)

        if (intent.hasExtra("typePayment")) typePayment = intent.getStringExtra("typePayment")!!
        if (intent.hasExtra("username")) username = intent.getStringExtra("username")!!

        setToolbar(typePayment)

        if (typePayment == "pay") {
            addPayButton.text = getString(R.string.register_payment)
            payTransmitter.setText(username)
            pay_transmitter.isEnabled = false
        } else {
            addPayButton.text = getString(R.string.register_cobro)
            payReceiver.setText(username)
            pay_receiver.isEnabled = false
        }

        val date =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }

        payDate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                DatePickerDialog(
                    this@PayAndSolicitudePayment,
                    date,
                    myCalendar[Calendar.YEAR],
                    myCalendar[Calendar.MONTH],
                    myCalendar[Calendar.DAY_OF_MONTH]
                ).show()
            }
        })

        addPayButton.setOnClickListener {
            if (!payTransmitter.text.toString().isNullOrEmpty()
                && !payReceiver.text.toString().isNullOrEmpty()
                && !payDate.text.toString().isNullOrEmpty()
                && !payPrice.text.toString().isNullOrEmpty()
                && !payConcept.text.toString().isNullOrEmpty()) {
                addPaymentToDatabase()
            } else {
                MaterialDialog.createDialog(this) {
                    setTitle(getString(R.string.check_fields))
                    setMessage(getString(R.string.check_fields_info))
                    setPositiveButton(getString(R.string.accept)) { dialog, _ ->
                        dialog.cancel()
                    }
                }.show()
            }
        }
    }

    private fun addPaymentToDatabase() {
        val db = CompaSQLiteOpenHelper(this, "dbCompa", null, CompaSQLiteOpenHelper.DATABASE_VERSION)
        val dbCompa = db.writableDatabase


        val transmitter = payTransmitter.text.toString()
        val receiver = payReceiver.text.toString()
        val date = payDate.text.toString()
        var price: Double = 0.0
        if (payPrice.text.toString() != "")
        {
            price = payPrice.text.toString().toDouble()
        }
        val concept = payConcept.text.toString()

        val myFormat = "dd/MM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat)
        try {
            val d: Date = sdf.parse(date)
            time = d.time
        } catch (e: ParseException) {
            e.printStackTrace();
        }

        val register = ContentValues()
        register.put("transmitter", transmitter)
        register.put("receiver", receiver)
        register.put("date", time)
        register.put("price", price)
        register.put("concept", concept)

        dbCompa.insert("payment", null, register)

        dbCompa.close()

        if (typePayment == "pay") {
            MaterialDialog.createDialog(this) {
                setTitle(getString(R.string.register_payment))
                setMessage(getString(R.string.register_payment_info))
                setPositiveButton(getString(R.string.dabuti)) { dialog, _ ->
                    dialog.cancel()
                    finish()
                }
            }.show()
        } else {
            MaterialDialog.createDialog(this) {
                setTitle(getString(R.string.register_cobro))
                setMessage(getString(R.string.register_cobro_info))
                setPositiveButton(getString(R.string.what_a_pitty)) { dialog, _ ->
                    startService(Intent(this@PayAndSolicitudePayment, PaymentService::class.java))
                    dialog.cancel()
                    finish()
                }
            }.show()
        }
    }

    private fun setToolbar(type: String) {
        if (type == "pay") {
            toolbar2.title = getString(R.string.realize_payment)
        } else {
            toolbar2.title = getString(R.string.take_payment)
        }
        toolbar2.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateLabel() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat)
        payDate.setText(sdf.format(myCalendar.timeInMillis))
    }
}