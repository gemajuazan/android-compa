package org.example.compa.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.MyProfileFragmentBinding
import org.example.compa.models.Person
import org.example.compa.preferences.AppPreference
import org.example.compa.ui.friends.FriendsActivity
import org.example.compa.ui.login.LoginActivity
import org.example.compa.utils.DateUtil

class MyProfileFragment : Fragment() {

    private lateinit var binding: MyProfileFragmentBinding

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var person: Person? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInitConfiguration()

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        getPerson()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = MyProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
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
            val id = it.data?.get("id") as String? ?: ""
            val name = it.data?.get("name") as String? ?: ""
            val surnames = it.data?.get("surnames") as String? ?: ""
            val birthdate = it.data?.get("birthdate") as Long? ?: -1
            val email = it.data?.get("email") as String? ?: ""
            val username = it.data?.get("username") as String? ?: ""
            person = Person(
                id = id,
                name = name,
                surnames = surnames,
                birthdate = birthdate,
                email = email,
                username = username
            )

            setPerson()
        }
    }

    private fun setPerson() {
        binding.itemName.text = person?.name + " " + person?.surnames
        binding.itemUsername.valueTextView.text = person?.username
        binding.itemEmail.valueTextView.text = person?.email
        binding.itemPhoneNumber.valueTextView.text = person?.username
        binding.itemBirthdate.valueTextView.text = DateUtil.getDate(person?.birthdate ?: -1, "dd/MM/yyyy")

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
            val intent = Intent(requireContext(), ContactsAndGroupsActivity::class.java)
            startActivity(intent)
        }
        binding.editInfo.linearLayout.setOnClickListener {
            val intent = Intent(requireContext(), FriendsActivity::class.java)
            startActivity(intent)
        }
        binding.logOut.linearLayout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

}