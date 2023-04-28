package com.example.studentdiary.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val airplaneModeState = intent.getBooleanExtra("state", false)
        if (airplaneModeState){
            Toast.makeText(context, "SIM", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context, "Não", Toast.LENGTH_LONG).show()
        }

    }
}






