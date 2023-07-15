package com.example.studentdiary.utils.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.studentdiary.R
import com.example.studentdiary.extensions.toast


class AirplaneModeBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val airplaneModeState = intent.getBooleanExtra("state", false)
        if (airplaneModeState){
            context.toast( context.getString(R.string.message_airplane_mode_on), duration = Toast.LENGTH_LONG)
        }else{
            context.toast( context.getString(R.string.message_airplane_mode_off), duration = Toast.LENGTH_LONG)
        }
    }
}






