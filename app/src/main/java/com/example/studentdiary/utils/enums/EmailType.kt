package com.example.studentdiary.utils.enums

import com.example.studentdiary.ui.GOOGLE_EMAIL_TYPE
import com.example.studentdiary.ui.OUTLOOK_EMAIL_TYPE
import com.example.studentdiary.ui.YAHOO_EMAIL_TYPE

enum class EmailType {

    GOOGLE {
        override fun getEmailType(): String = GOOGLE_EMAIL_TYPE
    },

    YAHOO {
        override fun getEmailType(): String = YAHOO_EMAIL_TYPE

    },
    OUTLOOK {
        override fun getEmailType(): String = OUTLOOK_EMAIL_TYPE
    };

    abstract fun getEmailType(): String
}