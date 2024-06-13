package com.localazy.quicknote.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.localazy.quicknote.startFloatingService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.startFloatingService()
    }
}
