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
        /* 네트워크 요청에서 토큰을 꺼낼 수 있도록 앱 시작 시 저장소를 초기화 */
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
            /* 모든 API 요청에 로그인 토큰을 자동으로 붙이는 인터셉터 */
            .addInterceptor(AuthInterceptor(requireAuthTokenStore()))
        .addInterceptor(loggingInterceptor)
        .build()
    }

    private val retrofit by lazy {
        /* local.properties의 API_BASE_URL을 기준으로 실제 서버 요청 생성 */
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
