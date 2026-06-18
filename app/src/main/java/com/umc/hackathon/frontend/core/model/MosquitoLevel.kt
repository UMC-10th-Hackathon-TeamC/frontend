package com.umc.hackathon.frontend.core.model

enum class MosquitoLevel(
    val label: String
) {
    LOW("낮음"),
    NORMAL("보통"),
    HIGH("높음"),
    VERY_HIGH("매우 높음")
}

fun Int.toMosquitoLevel(): MosquitoLevel {
    return when (this) {
        in 0..25 -> MosquitoLevel.LOW
        in 26..50 -> MosquitoLevel.NORMAL
        in 51..75 -> MosquitoLevel.HIGH
        else -> MosquitoLevel.VERY_HIGH
    }
}
