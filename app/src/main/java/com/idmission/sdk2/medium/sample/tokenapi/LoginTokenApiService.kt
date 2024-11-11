package com.idmission.sdk2.sample.tokenapi

import com.idmission.idmissioncapture.presentation.model.LoginAccessTokenInfo
import com.idmission.sdk2.identity.api.ApiService
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface LoginTokenApiService : ApiService {
    @Headers(
        "Accept-Charset: utf-8",
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST
    fun gteLoginAccessTokenApi(
        @Url url: String, @Body body: RequestBody
    ): Call<LoginAccessTokenInfo>


}
