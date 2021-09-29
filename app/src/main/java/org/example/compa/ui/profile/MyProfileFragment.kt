package org.example.compa.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.example.compa.R
import org.example.compa.databinding.MyProfileFragmentBinding
import org.example.compa.models.Person
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.friends.FriendsActivity
import org.example.compa.ui.login.LoginActivity
import org.example.compa.utils.DataUtil.Companion.getPersonFromDatabase
import org.example.compa.utils.DateUtil

@SuppressLint("SetTextI18n")
class MyProfileFragment : Fragment() {

    private lateinit var binding: MyProfileFragmentBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private var person: Person? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitConfiguration()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MyProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        getPerson()
    }

    private fun setInitConfiguration() {
        setView()
        setOnClickListeners()
    }

    private fun setView() {
        binding.itemUsername.labelTextView.text = getString(R.string.username)
        binding.itemEmail.labelTextView.text = getString(R.string.email)
        binding.itemPhoneNumber.labelTextView.text = getString(R.string.phone)
        binding.itemBirthdate.labelTextView.text = getString(R.string.date_birth)
        binding.editInfo.labelTextView.text =  getString(R.string.modify_information)
        binding.groups.labelTextView.text = getString(R.string.groups)
        binding.searchFriends.labelTextView.text = getString(R.string.search_friends)
        binding.logOut.labelTextView.text = getString(R.string.logout)
    }

    private fun getPerson() {
        val user: FirebaseUser? = auth.currentUser
        db.collection("person").document(user?.uid ?: "").get().addOnSuccessListener {
            person = getPersonFromDatabase(it)
            setPerson()
        }
    }

    private fun setPerson() {
        binding.itemName.text = person?.name + " " + person?.surnames
        binding.itemUsername.valueTextView.text = person?.username
        binding.itemEmail.valueTextView.text = person?.email
        binding.itemPhoneNumber.valueTextView.text = person?.phone
        binding.itemBirthdate.valueTextView.text = DateUtil.getDate(person?.birthdate ?: -1, "dd/MM/yyyy")

        if (context != null) {
            storage.reference.child("images/${AppPreference.getUserUID()}").downloadUrl.addOnSuccessListener {
                Glide.with(requireContext())
                    .load(it.toString())
                    .into(binding.imageUser)
            }
        }

        AppPreference.setUserEmail(person?.email ?: "")
        AppPreference.setUserName(person?.name + " " + person?.surnames)
        AppPreference.setUserUsername(person?.username ?: "")
    }

    private fun setOnClickListeners() {
        binding.searchFriends.linearLayout.setOnClickListener {
            val intent = Intent(requireContext(), FriendsActivity::class.java)
            startActivity(intent)
        }
        binding.groups.linearLayout.setOnClickListener {
            val intent = Intent(requireContext(), GroupsActivity::class.java)
            startActivity(intent)
        }
        binding.editInfo.linearLayout.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            intent.putExtra("edit", true)
            intent.putExtra("id", AppPreference.getUserUID())
            startActivity(intent)
        }
        binding.logOut.linearLayout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

}