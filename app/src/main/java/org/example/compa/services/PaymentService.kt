package org.example.compa.services

import org.example.compa.R
import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import org.example.compa.ui.payments.PaymentsActivity

class PaymentService : Service() {

    private var notificationManager: NotificationManager? = null
    private val CANALID = "mi_canal"
    private val NOTIFICACIONID = 1

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "dnskdfnere", Toast.LENGTH_SHORT)
    }

    override fun onStartCommand(intenc: Intent?, flags: Int, idArranque: Int): Int {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CANALID,
                "Mis Notificaciones",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = "Descripcion del canal"
            notificationChannel.enableVibration(true)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }
        val notificacion = NotificationCompat.Builder(this, CANALID)
            .setSmallIcon(R.drawable.ic_logo_compa)
            .setContentTitle(getString(R.string.new_cobro))
            .setContentText(getString(R.string.new_cobro_info))
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_logo_compa))
            .setWhen(System.currentTimeMillis() + 1000 * 60)
            .setDefaults(Notification.DEFAULT_VIBRATE)
        val intent = Intent(this, PaymentsActivity::class.java)
        intent.putExtra("notificacion", true)
        val intencionPendiente = PendingIntent.getActivity(this, 0, intent, 0)
        notificacion.setContentIntent(intencionPendiente)
        startForeground(NOTIFICACIONID, notificacion.build())
        return START_STICKY
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}