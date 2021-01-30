package org.example.compa.ui.payments

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.payments_activity.*
import kotlinx.android.synthetic.main.tasks_activity.*
import kotlinx.android.synthetic.main.tasks_activity.view_pager
import org.example.compa.R
import org.example.compa.services.PaymentService

class PaymentsActivity : AppCompatActivity() {

    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payments_activity)

        setToolbar()

        var tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(view_pager)

        if (intent.hasExtra("username")) username = intent.getStringExtra("username")!!

        val intent = intent.extras
        val notificacion = intent?.getBoolean("notificacion")
        if (notificacion != null && notificacion) {
            stopService(Intent(this@PaymentsActivity, PaymentService::class.java))
        }

        add_collection.setOnClickListener {
            val intent = Intent(this, PayAndSolicitudePayment::class.java)
            intent.putExtra("username", username)
            intent.putExtra("typePayment", "sol")
            startActivity(intent)
        }

        add_payment.setOnClickListener {
            val intent = Intent(this, PayAndSolicitudePayment::class.java)
            intent.putExtra("username", username)
            intent.putExtra("typePayment", "pay")
            startActivity(intent)
        }

        val viewPager = findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = MiPagerAdapter(supportFragmentManager)
    }

    class MiPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> {
                    fragment = MyPaymentsFragment()
                    //fragment = PaymentHistorialFragment()
                }
                1 -> {
                    fragment = OweToMePaymentsFragment()
                }
                /*2 -> {
                    fragment = MyPaymentsFragment()
                }*/
            }
            return fragment!!
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> {
                    "Debes"
                }
                else -> {
                    "Me deben"
                }
                /*else -> {
                    "Debes"
                }*/
            }
        }

    }

    private fun setToolbar() {
        toolbar_payments.title = getString(R.string.menu_payments)
        toolbar_payments.setNavigationOnClickListener {
            finish()
        }
    }
}