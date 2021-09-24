package org.example.compa.ui.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import org.example.compa.R
import org.example.compa.databinding.MenuNavigationActivityBinding
import org.example.compa.ui.home.HomeFragment
import org.example.compa.ui.payments.MyPaymentsFragment
import org.example.compa.ui.payments.PaymentHistorialFragment
import org.example.compa.ui.profile.MyProfileFragment
import org.example.compa.ui.tasks.AllTasksFragment

class MenuNavigationActivity : AppCompatActivity() {

    private lateinit var binding: MenuNavigationActivityBinding

    private lateinit var profileFragment: MyProfileFragment
    private lateinit var tasksFragment: AllTasksFragment
    private lateinit var paymentsFragment: PaymentHistorialFragment
    private lateinit var settingsFragment: MyPaymentsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MenuNavigationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigation.selectedItemId = R.id.menu_main_tasks
        tasksFragment = AllTasksFragment()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_view, tasksFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()

        setFragments()
    }

    private fun setFragments() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_main_profile -> {
                    profileFragment = MyProfileFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, profileFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
                }
                R.id.menu_main_tasks -> {
                    tasksFragment = AllTasksFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, tasksFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
                }
                R.id.menu_main_payments -> {
                    paymentsFragment = PaymentHistorialFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, paymentsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
                }
                R.id.menu_main_configuration -> {
                    settingsFragment = MyPaymentsFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_view, settingsFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
                }
            }
            true
        }
    }
}