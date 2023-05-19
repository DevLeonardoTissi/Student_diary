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

const val GOOGLE_EMAIL_TYPE = "com.google"
const val YAHOO_EMAIL_TYPE = "com.yahoo"
const val OUTLOOK_EMAIL_TYPE = "com.outlook"


const val FACEBOOK_PERMISSION_EMAIL = "email"
const val FACEBOOK_PERMISSION_PROFILE = "public_profile"



const val GITHUB_LINK = "https://github.com/DevLeonardoTissi"
const val LINKEDIN_LINK = "https://www.linkedin.com/in/leonardotissi/"
const val STUDENT_DIARY_GITHUB_LINK = "https://github.com/DevLeonardoTissi/Student_diary"

const val  PUBLIC_TENDER_COLLECTION = "public_tender"
