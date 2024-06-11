package com.localazy.quicknote

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

const val INTENT_COMMAND = "com.localazy.quicknote.COMMAND"
const val INTENT_COMMAND_EXIT = "EXIT"
const val INTENT_COMMAND_NOTE = "NOTE"

private const val NOTIFICATION_CHANNEL_GENERAL = "quicknote_general"
private const val CODE_FOREGROUND_SERVICE = 1
private const val CODE_EXIT_INTENT = 2
private const val CODE_NOTE_INTENT = 3

class FloatingService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Remove the foreground notification and stop the service.
     */
    private fun stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH)
        }
        stopSelf()
    }

    /**
     * Create and show the foreground notification.
     */
    private fun showNotification() {

        val manager = NotificationManagerCompat.from(this)

        val exitIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_EXIT)
        }

        val noteIntent = Intent(this, FloatingService::class.java).apply {
            putExtra(INTENT_COMMAND, INTENT_COMMAND_NOTE)
        }

        val exitPendingIntent = PendingIntent.getService(
            this, CODE_EXIT_INTENT, exitIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notePendingIntent = PendingIntent.getService(
            this, CODE_NOTE_INTENT, noteIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // From Android O, it's necessary to create a notification channel first.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                with(
                    NotificationChannel(
                        NOTIFICATION_CHANNEL_GENERAL,
                        getString(R.string.notification_channel_general),
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                ) {
                    enableLights(false)
                    setShowBadge(false)
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    manager.createNotificationChannel(this)
                }
            } catch (ignored: Exception) {
                // Ignore exception.
            }
        }

        with(
            NotificationCompat.Builder(
                this,
                NOTIFICATION_CHANNEL_GENERAL
            )
        ) {
            setTicker(null)
            setContentTitle(getString(R.string.app_name))
            setContentText(getString(R.string.notification_text))
            setAutoCancel(false)
            setOngoing(true)
            setWhen(System.currentTimeMillis())
            setSmallIcon(R.drawable.baseline_note_black_36)
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            addAction(
                NotificationCompat.Action(
                    0,
                    getString(R.string.add_note),
                    notePendingIntent
                )
            )
            addAction(
                NotificationCompat.Action(
                    0,
                    getString(R.string.notification_exit),
                    exitPendingIntent
                )
            )
            startForeground(CODE_FOREGROUND_SERVICE, build())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val command = intent.getStringExtra(INTENT_COMMAND)

        // Exit the service if we receive the EXIT command.
        // START_NOT_STICKY is important here, we don't want
        // the service to be relaunched.
        if (command == INTENT_COMMAND_EXIT) {
            stopService()
            return START_NOT_STICKY
        }

        // Be sure to show the notification first for all commands.
        // Don't worry, repeated calls have no effects.
        showNotification()

        // Show the floating window for adding a new note.
        if (command == INTENT_COMMAND_NOTE) {
            Toast.makeText(
                this,
                "Floating window to be added in the next lessons.",
                Toast.LENGTH_SHORT
            ).show()
        }

        return START_STICKY
    }
}
