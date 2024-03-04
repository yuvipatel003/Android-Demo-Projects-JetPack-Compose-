package com.appsdeviser.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings

class TestBroadcastReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == "TEST_ACTION") {
            println("Received Test Intent from Broadcast")
        }
    }
}