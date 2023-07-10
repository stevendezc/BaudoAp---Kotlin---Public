package com.abstractcoder.baudoapp.recyclers

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.abstractcoder.baudoapp.BrowserActivity
import com.abstractcoder.baudoapp.R
import com.abstractcoder.baudoapp.databinding.EventListItemBinding
import com.abstractcoder.baudoapp.utils.NotificationReceiver
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventAdapter(private val context: Context, private val parentFragment: FragmentManager, private val eventList: ArrayList<EventMain>) : RecyclerView.Adapter<EventAdapter.EventHolder>() {

    private lateinit var notificationReceiver: NotificationReceiver
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var pendingIntent2: PendingIntent

    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.event_list_item,
            parent, false
        )
        return EventHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        val currentItem = eventList[position]
        val dateAlreadyHappened = checkDateAlreadyHappened(currentItem)

        val dateFormat = SimpleDateFormat("dd 'de' MMMM '-' yyyy", Locale("es", "ES"))
        holder.eventDate.text = dateFormat.format(currentItem.date?.toDate() ?: null)
        holder.eventTitle.text = currentItem.title.toString()
        holder.eventDescription.text = currentItem.description.toString()

        createCustomNotificationChannel()

        val permission = Manifest.permission.POST_NOTIFICATIONS
        var notificationPermission = ContextCompat.checkSelfPermission(context, permission)
        if (!dateAlreadyHappened) {
            if (notificationPermission == PackageManager.PERMISSION_DENIED) {
                //ActivityCompat.requestPermissions(context, arrayOf(permission), )
            } else {
                holder.reminderButton.visibility = Button.VISIBLE
                holder.reminderButton.setOnClickListener {
                    setNotificationPendingIntent(currentItem.title.toString())
                    setReminder(currentItem)
                }
            }
        }
        holder.eventButton.setOnClickListener {
            var browserIntent = Intent(holder.itemView.context, BrowserActivity::class.java).apply {
                putExtra("incoming_url", currentItem.event_url)
            }
            holder.itemView.context.startActivity(browserIntent)
        }
    }

    private fun checkDateAlreadyHappened(currentItem: EventMain): Boolean {
        val dateInMillis = getTimestampForDay(currentItem)
        println("dateInMillis: $dateInMillis")
        val currentTimeMillis = System.currentTimeMillis()
        println("currentTimeMillis: $currentTimeMillis")
        if (currentTimeMillis > dateInMillis) {
            println("La fecha ya paso")
            return true
        } else {
            println("La fecha aun no llega")
            return false
        }
    }

    private fun createCustomNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Baudo Notification"
            val description = "Channel for Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("baudoandroid", name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setNotificationPendingIntent(title: String) {
        calendar = Calendar.getInstance()
        alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", "El evento esta pronto a ocurrir")
        intent.putExtra("message", "$title va a ocurrir el dia de mañana")
        intent.putExtra("requestCode", calendar.timeInMillis)
        pendingIntent = PendingIntent.getBroadcast(
            context,
            calendar.timeInMillis.toInt(),
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        val intent2 = Intent(context, NotificationReceiver::class.java)
        intent2.putExtra("title", "El evento es hoy")
        intent2.putExtra("message", "El dia de hoy se llevara a cabo el evento $title")
        intent.putExtra("requestCode", calendar.timeInMillis + 1)
        pendingIntent2 = PendingIntent.getBroadcast(
            context,
            (calendar.timeInMillis + 1).toInt(),
            intent2,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    private fun setReminder(event: EventMain) {
        val date = Date(event.date!!.seconds * 1000)
        val calendar1 = Calendar.getInstance()
        calendar1.time = date

        calendar = Calendar.getInstance()
        val triggerAtMillis = calendar.timeInMillis
        var pendingIntentTriggerMillis: Long? = null
        var pending2IntentTriggerMillis: Long? = calendar1.timeInMillis + (AlarmManager.INTERVAL_HALF_DAY / 2)

        if (triggerAtMillis < (calendar1.timeInMillis - AlarmManager.INTERVAL_HALF_DAY)) {
            pendingIntentTriggerMillis = calendar1.timeInMillis - AlarmManager.INTERVAL_HALF_DAY
        }
        if (triggerAtMillis > (calendar1.timeInMillis - AlarmManager.INTERVAL_HALF_DAY) &&
            triggerAtMillis < (calendar1.timeInMillis - AlarmManager.INTERVAL_HOUR)) {
            pendingIntentTriggerMillis = triggerAtMillis + AlarmManager.INTERVAL_HOUR
        }
        if (pendingIntentTriggerMillis != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, pendingIntentTriggerMillis, pendingIntent)
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, pending2IntentTriggerMillis!!, pendingIntent2)
        Toast.makeText(context, "Recordatorio creado exitosamente", Toast.LENGTH_SHORT).show()
    }

    fun getTimestampForDay(event: EventMain): Long {
        println("date: ${event.date}")
        println("date date: ${event.date?.toDate()}")

        val date = Date(event.date!!.seconds * 1000)
        val calendar1 = Calendar.getInstance()
        calendar1.time = date
        val day = calendar1.get(Calendar.DAY_OF_MONTH)
        val month = calendar1.get(Calendar.MONTH) + 1 // Adding 1 because months are zero-based
        val year = calendar1.get(Calendar.YEAR)
        println("year1: $year")
        println("month1: $month")
        println("day1: $day")
        val calendar = Calendar.getInstance().apply {
            set(year, month - 1, day, 9, 0, 0) // Set the desired day and time (month is zero-based)
            set(Calendar.MILLISECOND, 0) // Clear the milliseconds
        }
        return calendar.timeInMillis
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    class EventHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding = EventListItemBinding.bind(itemView)

        val eventDate = binding.eventDate
        val eventTitle = binding.eventTitle
        val eventDescription = binding.eventDescription

        val eventButton = binding.eventButton
        val reminderButton = binding.reminderButton
    }
}