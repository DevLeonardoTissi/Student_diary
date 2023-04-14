package com.example.studentdiary.ui

import android.provider.CalendarContract

const val TAG_TIME_PICKER = "TIMER PICKER"
const val TAG_DATA_PICKER = "DATA_PICKER"
const val TIME_ZONE_ID = "UTC"

const val PROJECTION_ID_INDEX: Int = 0

val EVENT_PROJECTION: Array<String> = arrayOf(
    CalendarContract.Calendars._ID,
    CalendarContract.Calendars.ACCOUNT_NAME,
    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
    CalendarContract.Calendars.OWNER_ACCOUNT
)
const val GOOGLE_EMAIL_TYPE_DESCRIPTION = "Google/Gmail"
const val GOOGLE_EMAIL_TYPE = "com.google"
const val YAHOO_EMAIL_TYPE_DESCRIPTION = "Yahoo"
const val YAHOO_EMAIL_TYPE = "com.yahoo"
const val OUTLOOK_EMAIL_TYPE_DESCRIPTION = "Outlook"
const val OUTLOOK_EMAIL_TYPE = "com.outlook"

val ITENS_EMAIL_TYPE = arrayOf(GOOGLE_EMAIL_TYPE_DESCRIPTION, YAHOO_EMAIL_TYPE_DESCRIPTION, OUTLOOK_EMAIL_TYPE_DESCRIPTION)