package com.depromeet.sloth.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.depromeet.sloth.R
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.ui.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import kotlin.random.Random

private const val CHANNEL_ID = "fcm_default_channel"

class FirebaseService : FirebaseMessagingService() {

    val preferenceManager: PreferenceManager = PreferenceManager(this)

    //최초 설치시 토큰이 한번 발급되고 나서 onNewToken 이 불려지지 않는다.
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
    }

    //포그라운드 상태인 앱에서 알림 메시지 또는 데이터 메시지를 수신하려면 onMessageReceived 콜백을 처리하는 코드를 작성해야 합니다.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Timber.tag("onMessageReceived").d("From: %s", remoteMessage.from)

        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        if (remoteMessage.data.isEmpty()) {
            Timber.tag("onMessageReceived").d("Message data payload: %s", remoteMessage.data)

            val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.data["title"])
                .setContentText(remoteMessage.data["message"])
                .setSmallIcon(R.mipmap.ic_sloth_logo)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            notificationManager.notify(notificationID, notification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}