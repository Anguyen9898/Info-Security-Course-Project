package com.anguyen.mymap.temporary_unused.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.anguyen.mymap.R
import com.anguyen.mymap.firebase_managers.FirebaseDataManager
import com.anguyen.mymap.ui.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NotificationService: FirebaseMessagingService(){

    private val database =
        FirebaseDataManager(FirebaseDatabase.getInstance())

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        sendNotification(remoteMessage.data)
    }

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid!!
        database.saveTokenToDatabase(currentUser, token)
    }

    private fun createIntent(actionName: String, notificationId: Int, mission: String): Intent {
        val intent = Intent(this, MainActivity::class.java)

        intent.apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            action = actionName
            putExtra("notificationId", notificationId)
            putExtra("message", mission)
        }

        return intent
    }

    private fun sendNotification(data: Map<String, Any?>?){
        try {
            val name = data!!["name"] as String
            val mission = data["mission"] as String

            val status = (data["status"] as String).toInt()
            val notificationId = Random.nextInt()

            val title = "Hi $name, you have a location-following request from..."
            val message = "yeah     $mission"

            val acceptIntent = createIntent(MainActivity.ACCEPT_ACTION, notificationId, mission)
            val refuseIntent = createIntent(MainActivity.REFUSE_ACTION, notificationId, mission)
            val intent = createIntent(MainActivity.SHOW_ACTION, notificationId, mission)

            val pendingIntent = PendingIntent.getActivity(
                this,
                0 ,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT

            )

            val channelId = getString(R.string.project_id)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background))
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)

                .addAction(NotificationCompat.Action(
                    android.R.drawable.sym_call_missed,
                    "Refuse",
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                ))

                .addAction( NotificationCompat.Action(
                    android.R.drawable.sym_call_outgoing,
                    "Accept",
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)));

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT)

                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(0, notificationBuilder.build())

        }catch (e: Exception){
            e.printStackTrace()
        }
    }
}