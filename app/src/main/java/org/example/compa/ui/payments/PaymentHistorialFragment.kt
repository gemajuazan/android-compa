package org.example.compa.ui.payments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.AddPaymentFragmentBinding
import org.example.compa.databinding.FriendsListBinding
import org.example.compa.databinding.PaymentDetailFragmentBinding
import org.example.compa.databinding.PaymentHistorialFragmentBinding
import org.example.compa.models.Friend
import org.example.compa.models.Payment
import org.example.compa.models.Person
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.adapters.FriendAdapter
import org.example.compa.utils.DateUtil
import org.example.compa.utils.MaterialDialog
import org.example.compa.utils.StyleUtil
import java.text.NumberFormat
import java.util.*

class PaymentHistorialFragment : Fragment() {

    private lateinit var binding: PaymentHistorialFragmentBinding

    private var payments = arrayListOf<Payment>()
    private lateinit var paymentAdapter: MyPaymentAdapter
    private lateinit var db: FirebaseFirestore

    private val myCalendar = Calendar.getInstance()

    private var friends = arrayListOf<Friend>()
    private var friendAdapter = FriendAdapter(arrayListOf(), false)

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
        getFriends()
        setOnClickListeners()
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
            binding.loader.visibility = View.GONE
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

        var changed = false

        paymentBinding.emisor.text = payment.transmitter
        paymentBinding.receptor.text = payment.receiver
        paymentBinding.concept.text = payment.concept

        paymentBinding.date.text = DateUtil.getDate(payment.date ?: -1, "dd/MM/yyyy")

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance("EUR")
        paymentBinding.price.text = format.format(payment.price)

        if (payment.transmitter == AppPreference.getUserUsername()) {
            paymentBinding.price.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorError
                )
            )
        } else {
            paymentBinding.price.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.colorAccent
                )
            )
        }

        if (payment.statusPayment == "NPA") {
            paymentBinding.markAsPaidTextView.text = getString(R.string.mark_as_paid)
        } else {
            paymentBinding.markAsPaidTextView.text = getString(R.string.mark_as_unpaid)
        }

        paymentBinding.markAsPaidCard.setOnClickListener {
            changed = true
            if (payment.statusPayment == "NPA") {
                payPayment(payment, "PAY")
                paymentBinding.status.text = getString(R.string.paid)
                paymentBinding.status.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorAccent
                    )
                )
                paymentBinding.markAsPaidTextView.text = getString(R.string.mark_as_unpaid)

            } else {
                payPayment(payment, "NPA")
                paymentBinding.status.text = getString(R.string.unpaid)
                paymentBinding.status.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorError
                    )
                )
                paymentBinding.markAsPaidTextView.text = getString(R.string.mark_as_paid)
            }
            paymentBinding.paymentStatus.visibility = View.VISIBLE
        }

        paymentDialog.setContentView(paymentBinding.root)
        paymentDialog.dismissWithAnimation = true
        paymentDialog.behavior.state = STATE_EXPANDED

        paymentDialog.show()
        paymentDialog.setOnDismissListener {
            if (changed) initializePayments()
        }

    }

    private fun payPayment(payment: Payment, status: String) {
        db.collection("payments").document(payment.id).update("statusPayment", status)
    }

    private fun showAddPaymentBottomSheet() {
        val paymentDialog = BottomSheetDialog(requireContext())
        val paymentBinding = AddPaymentFragmentBinding.inflate(layoutInflater)

        paymentBinding.paymentDetailTitle.text = getString(R.string.pay_debt)
        paymentBinding.paymentDetailMessage.text = getString(R.string.who_pay)

        paymentBinding.transmitterEditText.setText(AppPreference.getUserUsername())
        paymentBinding.transmitterTextInput.isEnabled = false

        paymentBinding.receiverEditText.isFocusableInTouchMode = false
        paymentBinding.receiverEditText.isLongClickable = false

        paymentBinding.markAsPaidTextView.text = getString(R.string.pay)

        var changed = false

        paymentBinding.receiverEditText.setOnClickListener {
            val dialogBinding = FriendsListBinding.inflate(layoutInflater)
            val dialog = MaterialDialog.createDialog(requireContext()) {
                setTitle(R.string.select_friend)
                setView(dialogBinding.root)
            }

            dialogBinding.friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            dialogBinding.friendsRecyclerView.visibility = View.VISIBLE
            friendAdapter = FriendAdapter(friends, false)
            dialogBinding.friendsRecyclerView.adapter = friendAdapter
            friendAdapter.setOnItemClickListener(object : FriendAdapter.ItemClickListener {
                override fun onItemClicked(person: Person, position: Int) {
                    paymentBinding.receiverEditText.setText(person.username)
                    dialog.dismiss()
                }

                override fun onItemAddClicked(person: Person, position: Int) {
                    // Ignore
                }

                override fun onItemLongClicked(person: Person, position: Int) {
                    // Ignore
                }
            })
            dialog.show()
        }

        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                paymentBinding.dateFinishEditText.setText(DateUtil.getDate(myCalendar.timeInMillis, "dd/MM/yyyy"))
            }

        paymentBinding.dateFinishEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        paymentBinding.markAsPaidCard.setOnClickListener {
            if ((paymentBinding.receiverEditText.text.toString() != "" && paymentBinding.conceptEditText.toString() != "")
                && (paymentBinding.dateFinishEditText.text.toString() != "" && paymentBinding.conceptEditText.text.toString() != "")) {
                MaterialDialog.createDialog(requireContext()) {
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
                        changed = true
                        paymentDialog.dismiss()
                    }
                    setNegativeButton(R.string.cancel) { _, _ -> }
                }.show()
            } else {
                if (paymentBinding.transmitterEditText.text.toString() == "") paymentBinding.transmitterTextInput.error = getString(R.string.empty_field)
                if (paymentBinding.costEditText.text.toString() == "") paymentBinding.costTextInputLayout.error = getString(R.string.empty_field)
                if (paymentBinding.dateFinishEditText.text.toString() == "") paymentBinding.dateFinishTextInputLayout.error = getString(R.string.empty_field)
                if (paymentBinding.conceptEditText.text.toString() == "") paymentBinding.conceptTextInputLayout.error = getString(R.string.empty_field)
            }
        }

        paymentDialog.setContentView(paymentBinding.root)
        paymentDialog.dismissWithAnimation = true
        paymentDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        paymentDialog.show()
        paymentDialog.setOnDismissListener {
            if (changed) {
                initializePayments()
            }
        }

    }

    private fun showAddCollectionBottomSheet() {
        val paymentDialog = BottomSheetDialog(requireContext())
        val paymentBinding = AddPaymentFragmentBinding.inflate(layoutInflater)

        paymentBinding.paymentDetailTitle.text = getString(R.string.collect_payment)
        paymentBinding.paymentDetailMessage.text = getString(R.string.who_collect)

        paymentBinding.receiverEditText.setText(AppPreference.getUserUsername())
        paymentBinding.receiverTextInputLayout.isEnabled = false

        paymentBinding.transmitterEditText.isFocusableInTouchMode = false
        paymentBinding.transmitterEditText.isLongClickable = false

        paymentBinding.markAsPaidTextView.text = getString(R.string.collect)

        var changed = false

        paymentBinding.transmitterEditText.setOnClickListener {
            val dialogBinding = FriendsListBinding.inflate(layoutInflater)
            val dialog = MaterialDialog.createDialog(requireContext()) {
                setTitle(R.string.select_friend)
                setView(dialogBinding.root)
            }

            dialogBinding.friendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            dialogBinding.friendsRecyclerView.visibility = View.VISIBLE
            friendAdapter = FriendAdapter(friends, false)
            dialogBinding.friendsRecyclerView.adapter = friendAdapter
            friendAdapter.setOnItemClickListener(object : FriendAdapter.ItemClickListener {
                override fun onItemClicked(person: Person, position: Int) {
                    paymentBinding.transmitterEditText.setText(person.username)
                    dialog.dismiss()
                }

                override fun onItemAddClicked(person: Person, position: Int) {
                    // Ignore
                }

                override fun onItemLongClicked(person: Person, position: Int) {
                    // Ignore
                }
            })
            dialog.show()
        }

        val date =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                paymentBinding.dateFinishEditText.setText(DateUtil.getDate(myCalendar.timeInMillis, "dd/MM/yyyy"))
            }

        paymentBinding.dateFinishEditText.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        paymentBinding.markAsPaidCard.setOnClickListener {
            if ((paymentBinding.transmitterEditText.text.toString() != "" && paymentBinding.costEditText.toString() != "")
                && (paymentBinding.dateFinishEditText.text.toString() != "" && paymentBinding.conceptEditText.text.toString() != "")) {
                MaterialDialog.createDialog(requireContext()) {
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
                        changed = true
                        db.collection("payments").document(id).set(payment)
                        paymentDialog.dismiss()
                    }
                    setNegativeButton(R.string.cancel) { _, _ -> }
                }.show()
            } else {
                if (paymentBinding.transmitterEditText.text.toString() == "") paymentBinding.transmitterTextInput.error = getString(R.string.empty_field)
                if (paymentBinding.costEditText.text.toString() == "") paymentBinding.costTextInputLayout.error = getString(R.string.empty_field)
                if (paymentBinding.dateFinishEditText.text.toString() == "") paymentBinding.dateFinishTextInputLayout.error = getString(R.string.empty_field)
                if (paymentBinding.conceptEditText.text.toString() == "") paymentBinding.conceptTextInputLayout.error = getString(R.string.empty_field)
            }
        }

        paymentDialog.setContentView(paymentBinding.root)
        paymentDialog.dismissWithAnimation = true
        paymentDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        paymentDialog.show()
        paymentDialog.setOnDismissListener {
            if (changed) {
                initializePayments()
            }
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

    private fun getFriends() {
        db.collection("person").document(AppPreference.getUserUID()).collection("friends").get()
            .addOnSuccessListener {
                for (data in it.documents) {
                    val hashMap = data.data?.get("person") as HashMap<String, Any>
                    val solicitude = data.get("solicitude") as Boolean
                    val favourite = data.get("favourite") as Boolean
                    val id = hashMap["id"] as String? ?: ""
                    val name = hashMap["name"] as String? ?: ""
                    val surnames = hashMap["surnames"] as String? ?: ""
                    val birthdate = hashMap["birthdate"] as Long? ?: -1
                    val email = hashMap["email"] as String? ?: ""
                    val username = hashMap["username"] as String? ?: ""
                    val person = Person(
                        id = id,
                        name = name,
                        surnames = surnames,
                        birthdate = birthdate,
                        email = email,
                        username = username
                    )
                    val friend = Friend(person, solicitude = solicitude, favourite = favourite)
                    friends.add(friend)
                }
            }
    }

}