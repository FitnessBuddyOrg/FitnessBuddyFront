package com.project.fitnessbuddy.screens.common

import android.content.Context
import com.project.fitnessbuddy.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class Functions {
    companion object {
        fun enumToTitleCase(name: String): String {
            return name.lowercase().replaceFirstChar { it.uppercase() }
        }

        fun generateRandomLong(): Long {
            return (0..Long.MAX_VALUE).random()
        }
    }
}

fun Date.timeAgo(context: Context): String {
    val now = Date()
    val diffInMillis = now.time - this.time

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

    return when {
        seconds < 60 -> "$seconds ${context.getString(R.string.seconds_ago)}"
        minutes < 60 -> "$minutes ${context.getString(R.string.minutes_ago)}"
        hours < 24 -> "$hours ${context.getString(R.string.hours_ago)}"
        days == 1L -> context.getString(R.string.yesterday)
        days < 7 -> "$days ${context.getString(R.string.days_ago)}"
        days < 30 -> "${days / 7} ${context.getString(R.string.weeks_ago)}"
        days < 365 -> "${days / 30} ${context.getString(R.string.months_ago)}"
        else -> "${days / 365} ${context.getString(R.string.years_ago)}"
    }
}

fun Date.format(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}
