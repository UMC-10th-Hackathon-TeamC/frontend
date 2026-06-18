package com.umc.hackathon.frontend.feature.home.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import com.umc.hackathon.frontend.core.model.MosquitoLevel
import kotlin.math.roundToInt

fun createDistrictMarkerBitmap(
    context: Context,
    districtName: String,
    mosquitoIndex: Int,
    level: MosquitoLevel
): Bitmap {
    // 마커 내부 여백, 텍스트 간격, 테두리 두께 등을 dp/sp 기준으로 px로 변환
    val horizontalPadding = context.dpToPx(16f)
    val verticalPadding = context.dpToPx(8f)
    val gap = context.dpToPx(8f)
    val strokeWidth = context.dpToPx(3f)
    val cornerRadius = context.dpToPx(24f)
    val textSize = context.spToPx(15f)

    // 구 이름과 모기지수 숫자에 공통으로 사용할 텍스트 스타일
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        this.textSize = textSize
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    // 실제 텍스트가 차지하는 크기를 계산하기 위한 Rect
    val nameBounds = Rect()
    val indexBounds = Rect()
    val indexText = mosquitoIndex.toString()

    textPaint.getTextBounds(districtName, 0, districtName.length, nameBounds)
    textPaint.getTextBounds(indexText, 0, indexText.length, indexBounds)

    // 텍스트 크기와 여백을 바탕으로 Bitmap 전체 크기를 계산
    val textHeight = maxOf(nameBounds.height(), indexBounds.height())
    val width = (
            horizontalPadding * 2 +
                    nameBounds.width() +
                    gap +
                    indexBounds.width()
            ).roundToInt()
    val height = (
            verticalPadding * 2 +
                    textHeight
            ).roundToInt()

    // 마커 이미지를 그릴 Bitmap과 Canvas 생성
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // stroke가 Bitmap 밖으로 잘리지 않도록 절반 두께만큼 안쪽으로 상입
    val backgroundRect = RectF(
        strokeWidth / 2f,
        strokeWidth / 2f,
        width - strokeWidth / 2f,
        height - strokeWidth / 2f
    )

    // 모기지수 단계에 따라 배경색을 다르게 적용
    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = markerBackgroundColor(level)
        style = Paint.Style.FILL
    }

    // 하얀색 Fill
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        this.strokeWidth = strokeWidth
    }

    // 둥근 형태
    canvas.drawRoundRect(
        backgroundRect,
        cornerRadius,
        cornerRadius,
        backgroundPaint
    )

    // 흰색 테두리
    canvas.drawRoundRect(
        backgroundRect,
        cornerRadius,
        cornerRadius,
        strokePaint
    )

    // 텍스트 중앙 배치를 위한 계산식
    val baseline = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f
    var x = horizontalPadding

    // 구 이름
    canvas.drawText(
        districtName,
        x,
        baseline,
        textPaint
    )

    // 구 이름 뒤에 모기지수
    x += nameBounds.width() + gap

    canvas.drawText(
        indexText,
        x,
        baseline,
        textPaint
    )

    return bitmap
}

// 모기지수 단계별 마커 배경색
private fun markerBackgroundColor(level: MosquitoLevel): Int {
    return when (level) {
        MosquitoLevel.LOW -> Color.rgb(47, 112, 71)
        MosquitoLevel.NORMAL -> Color.rgb(216, 162, 19)
        MosquitoLevel.HIGH -> Color.rgb(194, 90, 32)
        MosquitoLevel.VERY_HIGH -> Color.rgb(208, 32, 32)
    }
}

// dp 값을 현재 기기의 화면 밀도에 맞는 px 값으로 변환
private fun Context.dpToPx(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

// sp 값을 현재 기기의 글자 크기 설정에 맞는 px 값으로 변환
private fun Context.spToPx(sp: Float): Float {
    return sp * resources.displayMetrics.scaledDensity
}