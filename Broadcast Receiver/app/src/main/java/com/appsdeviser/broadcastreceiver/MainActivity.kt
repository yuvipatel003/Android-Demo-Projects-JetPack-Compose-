package com.appsdeviser.broadcastreceiver

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.appsdeviser.broadcastreceiver.ui.theme.BroadcastReceiverTheme

class MainActivity : ComponentActivity() {

    private val airplaneModeReceiver = AirplaneModeReceiver()
    private val testBroadcastReceiver = TestBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(
            airplaneModeReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                testBroadcastReceiver, IntentFilter("TEST_ACTION"),
                RECEIVER_EXPORTED
            )
        }

        setContent {
            BroadcastReceiverTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = {
                        sendBroadcast(
                            Intent("TEST_ACTION")
                        )
                    }) {
                        Text(
                            text = "Send Broadcast"
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(airplaneModeReceiver)
        unregisterReceiver(testBroadcastReceiver)
    }
}