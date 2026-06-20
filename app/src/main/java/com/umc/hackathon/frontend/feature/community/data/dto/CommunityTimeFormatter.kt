package com.umc.hackathon.frontend.feature.community.data.dto

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private const val MINUTE_IN_MILLIS = 60_000L
private const val HOUR_IN_MILLIS = 60L * MINUTE_IN_MILLIS
private const val DAY_IN_MILLIS = 24L * HOUR_IN_MILLIS

internal fun String.toRelativeTimeTextFromIso(): String {
    val date = runCatching {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse(this)
    }.getOrNull()

    return date?.time?.toRelativeTimeText() ?: this
}

private fun Long.toRelativeTimeText(): String {
    val diffMillis = (System.currentTimeMillis() - this).coerceAtLeast(0L)
    val minutes = diffMillis / MINUTE_IN_MILLIS
    val hours = diffMillis / HOUR_IN_MILLIS
    val days = diffMillis / DAY_IN_MILLIS

    return when {
        minutes < 1L -> "방금 전"
        hours < 1L -> "${minutes}분 전"
        hours < 24L -> "${hours}시간 전"
        else -> "${days}일 전"
    }
}
