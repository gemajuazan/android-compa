package org.example.compa.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import org.example.compa.R
import org.example.compa.databinding.SettingsFragmentBinding
import org.example.compa.preferences.AppPreference
import org.example.compa.utils.MaterialDialog

class SettingsFragment : Fragment() {

    private lateinit var binding: SettingsFragmentBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        setFirestore()
        deleteAccount()
    }

    private fun setToolbar() {
        binding.toolbar.arrorBack.visibility = View.GONE
        binding.toolbar.title.text = getString(R.string.menu_configuration)
    }

    private fun setFirestore() {
        db = FirebaseFirestore.getInstance()
    }

    private fun deleteAccount() {
        binding.deleteUser.setOnClickListener {
            MaterialDialog.createDialog(requireContext()) {
                setTitle(R.string.delete_account)
                setMessage(R.string.delete_account_message)
                setPositiveButton(R.string.accept) { _, _ ->
                    db.collection("person").document(AppPreference.getUserUID()).delete()
                }
            }.show()
        }
    }
}