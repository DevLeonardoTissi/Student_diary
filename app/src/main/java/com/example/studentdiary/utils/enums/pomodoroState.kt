package com.example.studentdiary.utils.enums

import com.example.studentdiary.ui.POMODORO_EXTRA_INTERVAL_MESSAGE
import com.example.studentdiary.ui.POMODORO_INTERVAL_MESSAGE
import com.example.studentdiary.ui.POMODORO_TIMER_MESSAGE

enum class PomodoroState {
    POMODORO_TIMER {
        override fun toString(): String {
            return POMODORO_TIMER_MESSAGE
        }
    },
    INTERVAL_TIMER {
        override fun toString(): String {
            return POMODORO_INTERVAL_MESSAGE
        }
    },
    EXTRA_INTERVAL_TIMER {
        override fun toString(): String {
            return POMODORO_EXTRA_INTERVAL_MESSAGE
        }
    }
}