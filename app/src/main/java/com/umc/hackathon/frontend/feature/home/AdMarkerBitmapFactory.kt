package com.umc.hackathon.frontend.feature.home.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import com.umc.hackathon.frontend.feature.home.AdMarker
import kotlin.math.roundToInt

fun createAdMarkerBitmap(
    context: Context,
    adMarker: AdMarker
): Bitmap {
    val horizontalPadding = context.dpToPx(9f)
    val verticalPadding = context.dpToPx(5f)
    val iconTextGap = context.dpToPx(4f)
    val strokeWidth = context.dpToPx(2f)
    val cornerRadius = context.dpToPx(14f)
    val shadowPadding = context.dpToPx(4f)

    val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(47, 112, 71)
        textSize = context.spToPx(11f)
        typeface = Typeface.DEFAULT_BOLD
    }
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(47, 112, 71)
        textSize = context.spToPx(12f)
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    val iconBounds = Rect()
    val nameBounds = Rect()
    iconPaint.getTextBounds(adMarker.iconText, 0, adMarker.iconText.length, iconBounds)
    textPaint.getTextBounds(adMarker.name, 0, adMarker.name.length, nameBounds)

    val contentHeight = maxOf(iconBounds.height(), nameBounds.height()).toFloat()
    val markerWidth = (
        horizontalPadding * 2 +
            iconBounds.width() +
            iconTextGap +
            nameBounds.width()
        ).roundToInt()
    val markerHeight = (verticalPadding * 2 + contentHeight).roundToInt()
    val bitmapWidth = (markerWidth + shadowPadding * 2).roundToInt()
    val bitmapHeight = (markerHeight + shadowPadding * 2).roundToInt()

    val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val markerRect = RectF(
        shadowPadding,
        shadowPadding,
        shadowPadding + markerWidth,
        shadowPadding + markerHeight
    )

    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(45, 0, 0, 0)
    }
    canvas.drawRoundRect(
        RectF(
            markerRect.left + context.dpToPx(1f),
            markerRect.top + context.dpToPx(2f),
            markerRect.right + context.dpToPx(1f),
            markerRect.bottom + context.dpToPx(2f)
        ),
        cornerRadius,
        cornerRadius,
        shadowPaint
    )

    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(47, 112, 71)
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
    }

    canvas.drawRoundRect(markerRect, cornerRadius, cornerRadius, backgroundPaint)
    canvas.drawRoundRect(markerRect, cornerRadius, cornerRadius, strokePaint)

    val iconBaseline = markerRect.centerY() - (iconPaint.descent() + iconPaint.ascent()) / 2f
    val textBaseline = markerRect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
    var x = markerRect.left + horizontalPadding

    canvas.drawText(adMarker.iconText, x, iconBaseline, iconPaint)
    x += iconBounds.width() + iconTextGap
    canvas.drawText(adMarker.name, x, textBaseline, textPaint)

    return bitmap
}

private fun Context.dpToPx(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

private fun Context.spToPx(sp: Float): Float {
    return sp * resources.displayMetrics.scaledDensity
}
