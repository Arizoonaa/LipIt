package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.BuildConfig
import com.ssafy.lipit_app.base.ApplicationClass
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtil {


    private val springRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL_SPRING)
            .client(ApplicationClass.client)
            .addConverterFactory(GsonConverterFactory.create(ApplicationClass.gson))
            .build()
    }

    private val fastApiRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL_FASTAPI)
            .client(ApplicationClass.client)
            .addConverterFactory(GsonConverterFactory.create(ApplicationClass.gson))
            .build()
    }

    // OpenAI API용 Retrofit 인스턴스
    private val openAIRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(ApplicationClass.client)
            .addConverterFactory(GsonConverterFactory.create(ApplicationClass.gson))
            .build()
    }

    val authService: AuthService by lazy {
        springRetrofit.create(AuthService::class.java)
    }

    val scheduleService: ScheduleService by lazy {
        springRetrofit.create(ScheduleService::class.java)
    }

    val myVoiceService: MyVoiceService by lazy {
        springRetrofit.create(MyVoiceService::class.java)
    }

    val reportService: ReportService by lazy {
        fastApiRetrofit.create(ReportService::class.java)
    }

    /************ CustomVoice 생성 관련 *************/
    val whisperService: WhisperApiService by lazy {
        openAIRetrofit.create(WhisperApiService::class.java)
    }

    val presignService: S3PresignService by lazy {
        // FastAPI 를 통해 AWS S3 에 저장할 파일(음성, 이미지)의 저장 경로를 얻기 위함
        fastApiRetrofit.create(S3PresignService::class.java)
    }

    val onBoardingService: OnBoardingService by lazy {
        springRetrofit.create(OnBoardingService::class.java)
    }

}
