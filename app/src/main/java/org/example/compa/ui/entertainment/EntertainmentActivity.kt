package org.example.compa.ui.entertainment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.example.compa.databinding.EntertainmentActivityBinding
import java.util.*


class EntertainmentActivity : AppCompatActivity() {

    private lateinit var binding: EntertainmentActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntertainmentActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickDay()
    }

    private fun onClickDay() {
        /*val events: ArrayList<EventDay> = arrayListOf()
        binding.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar
                MaterialDialog.createDialog(this@EntertainmentActivity) {
                    setMessage(clickedDayCalendar.time.toString())
                }.show()
                events.add(EventDay(clickedDayCalendar, R.drawable.ic_add_24))
            }
        })*/
        val cal = Calendar.getInstance()
        val intent = Intent(Intent.ACTION_EDIT)
        intent.type = "vnd.android.cursor.item/event"
        intent.putExtra("beginTime", cal.timeInMillis)
        intent.putExtra("allDay", true)
        intent.putExtra("rrule", "FREQ=YEARLY")
        intent.putExtra("endTime", cal.timeInMillis + 60 * 60 * 1000)
        intent.putExtra("title", "A Test Event from android app")
        startActivity(intent)
    }
}