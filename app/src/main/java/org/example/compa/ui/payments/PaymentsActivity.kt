package org.example.compa.ui.payments

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.AddPaymentFragmentBinding
import org.example.compa.databinding.PaymentsActivityBinding
import org.example.compa.models.Payment
import org.example.compa.preferences.AppPreference
import org.example.compa.services.PaymentService
import org.example.compa.utils.DateUtil
import org.example.compa.utils.MaterialDialog
import org.example.compa.utils.StyleUtil
import java.util.*

class PaymentsActivity : AppCompatActivity() {

    private lateinit var binding: PaymentsActivityBinding
    private var username: String = ""

    private lateinit var db: FirebaseFirestore
    private val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PaymentsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        setToolbar()
        setNotifications()
        setOnClickListeners()

        binding.viewPager.adapter = MiPagerAdapter(this)
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.historial)
                1 -> tab.text = getString(R.string.owe_me)
                2 -> tab.text = getString(R.string.what_i_owe)
            }
        }.attach()
    }

    private fun setNotifications() {
        if (intent.hasExtra("username")) username = intent.getStringExtra("username")!!

        val intent = intent.extras
        val notificacion = intent?.getBoolean("notificacion")
        if (notificacion != null && notificacion) {
            stopService(Intent(this@PaymentsActivity, PaymentService::class.java))
        }
    }

    private fun setOnClickListeners() {
        binding.addCollection.setOnClickListener {
            showAddCollectionBottomSheet()
        }

        binding.addPayment.setOnClickListener {
            showAddPaymentBottomSheet()
        }
    }

    private fun showAddPaymentBottomSheet() {
        val paymentDialog = BottomSheetDialog(this)
        val paymentBinding = AddPaymentFragmentBinding.inflate(layoutInflater)

        paymentBinding.paymentDetailTitle.text = getString(R.string.pay_debt)
        paymentBinding.paymentDetailMessage.text = getString(R.string.who_pay)

        paymentBinding.transmitterEditText.setText(AppPreference.getUserUsername())
        paymentBinding.transmitterTextInput.isEnabled = false

        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                paymentBinding.dateFinishEditText.setText(DateUtil.getDate(myCalendar.timeInMillis, "dd/MM/yyyy"))
            }

        paymentBinding.dateFinishEditText.setOnClickListener {
            DatePickerDialog(
                this,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        paymentBinding.markAsPaidCard.setOnClickListener {
            MaterialDialog.createDialog(this) {
                setMessage(R.string.do_you_want_to_add_this_payment)
                setPositiveButton(R.string.accept) { _, _ ->
                    val id = StyleUtil.getRandomString(16)
                    val payment = Payment(
                        id = id,
                        transmitter = AppPreference.getUserUsername(),
                        receiver = paymentBinding.receiverEditText.text.toString(),
                        date = myCalendar.timeInMillis,
                        price = paymentBinding.costEditText.text.toString().toDouble(),
                        concept = paymentBinding.conceptEditText.text.toString(),
                        typePayment = "PAY",
                        statusPayment = "NPA"
                    )
                    db.collection("payments").document(id).set(payment)
                    paymentDialog.dismiss()
                }
                setNegativeButton(R.string.cancel) { _, _ -> }
            }.show()
        }

        paymentDialog.setContentView(paymentBinding.root)
        paymentDialog.dismissWithAnimation = true
        paymentDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        paymentDialog.show()
        paymentDialog.setOnDismissListener {
            finish()
            startActivity(intent)
        }

    }

    private fun showAddCollectionBottomSheet() {
        val paymentDialog = BottomSheetDialog(this)
        val paymentBinding = AddPaymentFragmentBinding.inflate(layoutInflater)

        paymentBinding.paymentDetailTitle.text = getString(R.string.collect_payment)
        paymentBinding.paymentDetailMessage.text = getString(R.string.who_collect)

        paymentBinding.receiverEditText.setText(AppPreference.getUserUsername())
        paymentBinding.receiverTextInputLayout.isEnabled = false

        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                paymentBinding.dateFinishEditText.setText(DateUtil.getDate(myCalendar.timeInMillis, "dd/MM/yyyy"))
            }

        paymentBinding.dateFinishEditText.setOnClickListener {
            DatePickerDialog(
                this,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        paymentBinding.markAsPaidCard.setOnClickListener {
            MaterialDialog.createDialog(this) {
                setMessage(R.string.do_you_want_to_add_this_collection)
                setPositiveButton(R.string.accept) { _, _ ->
                    val id = StyleUtil.getRandomString(16)
                    val payment = Payment(
                        id = id,
                        transmitter = paymentBinding.transmitterEditText.text.toString(),
                        receiver = AppPreference.getUserUsername(),
                        date = myCalendar.timeInMillis,
                        price = paymentBinding.costEditText.text.toString().toDouble(),
                        concept = paymentBinding.conceptEditText.text.toString(),
                        typePayment = "REF",
                        statusPayment = "NPA"
                    )
                    db.collection("payments").document(id).set(payment)
                    paymentDialog.dismiss()
                }
                setNegativeButton(R.string.cancel) { _, _ -> }
            }.show()
        }

        paymentDialog.setContentView(paymentBinding.root)
        paymentDialog.dismissWithAnimation = true
        paymentDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        paymentDialog.show()
        paymentDialog.setOnDismissListener {
            finish()
            startActivity(intent)
        }
    }

    class MiPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> {
                    fragment = PaymentHistorialFragment()
                }
                1 -> {
                    fragment = OweToMePaymentsFragment()
                }
                2 -> {
                    fragment = MyPaymentsFragment()
                }
            }
            return fragment!!
        }

    }

    private fun setToolbar() {
        binding.toolbarPayments.profileSubtitle.text = getString(R.string.menu_payments)
        binding.toolbarPayments.view.visibility = View.GONE
        binding.toolbarPayments.backButtonImageView.setOnClickListener {
            finish()
        }
    }
}