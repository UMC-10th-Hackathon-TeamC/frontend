package com.umc.hackathon.frontend.core.network

import android.content.Context
import com.umc.hackathon.frontend.BuildConfig
import com.umc.hackathon.frontend.core.data.AuthTokenStore
import com.umc.hackathon.frontend.feature.community.data.api.CommunityApi
import com.umc.hackathon.frontend.feature.home.data.api.HomeApi
import com.umc.hackathon.frontend.feature.mypage.data.api.MyPageApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private lateinit var authTokenStore: AuthTokenStore

    fun initialize(context: Context) {
        if (!::authTokenStore.isInitialized) {
            authTokenStore = AuthTokenStore(context.applicationContext)
        }
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(requireAuthTokenStore()))
        .addInterceptor(loggingInterceptor)
        .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val homeApi: HomeApi by lazy { retrofit.create(HomeApi::class.java) }
    val myPageApi: MyPageApi by lazy { retrofit.create(MyPageApi::class.java) }
    val communityApi: CommunityApi by lazy { retrofit.create(CommunityApi::class.java) }

    private fun requireAuthTokenStore(): AuthTokenStore {
        check(::authTokenStore.isInitialized) {
            "NetworkModule.initialize(context) must be called before using remote APIs."
        }
        return authTokenStore
    }
}
