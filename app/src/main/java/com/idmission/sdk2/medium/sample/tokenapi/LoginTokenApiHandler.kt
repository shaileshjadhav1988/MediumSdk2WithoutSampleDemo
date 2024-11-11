package com.idmission.sdk2.sample.tokenapi

import android.util.Log
import com.idmission.idmissioncapture.presentation.model.LoginAccessTokenInfo
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.Calendar
import java.util.concurrent.TimeUnit

internal object LoginTokenApiHandler {
    private const val apiUrl: String = "auth/realms/identity/protocol/openid-connect/token"

    private const val prodUrl = "https://auth.idmission.com/"
    private const val uatUrl = "https://uatauth.idmission.com/"
    private const val demoUrl = "https://demoauth.idmission.com/"

    private var retrofit: Retrofit? = null
    private var httpClient: OkHttpClient? = null
    private var loginTokenApiService: LoginTokenApiService? = null


    private fun getLoginTokenApiService(tokenCreateEnvironment : String): LoginTokenApiService {
        if (loginTokenApiService == null) {
            loginTokenApiService = getRetrofit(tokenCreateEnvironment).create(LoginTokenApiService::class.java)
        }
        return loginTokenApiService!!
    }


    private fun getRetrofit(tokenCreateEnvironment : String): Retrofit {
        if (retrofit == null) {
            val requestUrl = if (tokenCreateEnvironment.contains("KYC", true)) {
                prodUrl
            }else if(tokenCreateEnvironment.contains("UAT", true)){
                uatUrl
            } else {
                demoUrl
            }
            retrofit = Retrofit.Builder()
                .client(getHttpClient())
                .baseUrl(requestUrl)
                .addConverterFactory(Json {
                    prettyPrint = true
                    useArrayPolymorphism = false
                }.asConverterFactory("application/json".toMediaType()))
                .build()
        }

        return retrofit!!
    }

    private fun getHttpClient(): OkHttpClient {
        if (httpClient == null) {
            val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
                this.level = HttpLoggingInterceptor.Level.BODY
            }

            httpClient = OkHttpClient.Builder().apply {
                this.addInterceptor(interceptor)
                this.connectTimeout(5, TimeUnit.MINUTES)
                this.readTimeout(5, TimeUnit.MINUTES)
                this.writeTimeout(5, TimeUnit.MINUTES)
            }.build()
        }
        return httpClient as OkHttpClient
    }


    fun getLoginAccessTokenRequest(
        clientId: String,
        userId: String,
        password: String,
        clientSecret: String,
        tokenCreateEnvironment: String,
    ): LoginAccessTokenInfo? {
        try {

            val requestUrl = if (tokenCreateEnvironment.contains("KYC", true)) {
                prodUrl + apiUrl
            }else if(tokenCreateEnvironment.contains("UAT", true)){
                uatUrl + apiUrl
            } else {
                demoUrl + apiUrl
            }
            val requestData: String = createLoginTokenRequestData(clientId,clientSecret, userId, password)

            try {
                val response = getLoginTokenApiService(tokenCreateEnvironment).gteLoginAccessTokenApi(
                    requestUrl,
                    requestData.toRequestBody("application/x-www-form-urlencoded".toMediaTypeOrNull())
                ).execute().body()
                response?.tokenCreatedEnvironment = tokenCreateEnvironment
                val cal = Calendar.getInstance()
                val expiresTime: Long = cal.timeInMillis + (response?.expires_in!!) * 1000
                cal.timeInMillis = expiresTime
                response.expireTimeInMilli = cal.timeInMillis
                Log.i("Response-", "${response.access_token}")
                return response;
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null;
    }

    private fun createLoginTokenRequestData(
        clientId:String,
        clientSecret: String?,
        userId: String,
        password: String,

    ): String {
        val urlParameter: String
        val rawDataBuilder = StringBuilder()
            .append("grant_type=password")
            .append("&client_id=")
            .append(clientId)
            .append("&client_secret=")
            .append(clientSecret)
            .append("&username=")
            .append(userId)
            .append("&password=")
            .append(password)
            .append("&scope=api_access")
        urlParameter = rawDataBuilder.toString()
        return urlParameter
    }
}


