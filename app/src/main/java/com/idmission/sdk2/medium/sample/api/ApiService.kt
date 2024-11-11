package com.idmission.sdk2.identity.api

import com.idmission.sdk2.client.model.GteConnectTokenInfo
import com.idmission.sdk2.client.model.GteTrainingDataUploadResponse
import com.idmission.sdk2.client.model.GteUploadDataRequest
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers(
        "Accept-Charset: utf-8",
        "Content-Type: application/x-www-form-urlencoded"
    )
    @POST
    fun gteConnectAccessTokenApi(
        @Url url: String, @Body body: RequestBody
    ): Call<GteConnectTokenInfo>
}
