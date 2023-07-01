package com.abstractcoder.baudoapp.utils

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.abstractcoder.baudoapp.LogInActivity
import com.abstractcoder.baudoapp.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val incomingTitle = intent?.getStringExtra("title")
        val incomingMessage = intent?.getStringExtra("message")
        val incomingRequestCode = intent?.getIntExtra("requestCode", 0)
        println("title NotificationReceiver: $incomingTitle")
        println("message NotificationReceiver: $incomingMessage")
        println("request code NotificationReceiver: $incomingRequestCode")
        val intent = Intent(context, LogInActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            incomingRequestCode!!,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            })

        val builder = NotificationCompat.Builder(context!!, "baudoandroid")
            .setSmallIcon(R.drawable.logo_baudo_small_yellow)
            .setContentTitle(incomingTitle)
            .setContentText(incomingMessage)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(123, builder.build())
    }
}