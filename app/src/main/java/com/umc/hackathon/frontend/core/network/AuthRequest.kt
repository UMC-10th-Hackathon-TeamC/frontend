package com.umc.hackathon.frontend.core.network

import okhttp3.HttpUrl

fun HttpUrl.isAuthRequest(): Boolean {
    val path = encodedPath.removePrefix("/")
    return path.endsWith("oauth2/login/google") ||
        path.endsWith("oauth2/callback/google") ||
        path.endsWith("auth/refresh")
}
