package com.example.studentdiary.utils

import androidx.core.util.component1
import androidx.core.util.component2
import java.text.DateFormat.getDateInstance

fun concatenateDateValues(data : androidx.core.util.Pair<Long,Long>): String{
    val format = getDateInstance()
    val startData = format.format(data.component1())
    val endData = format.format(data.component2())

    return if (startData!= endData){
        "$startData-$endData"
    }else{
        startData
    }


}