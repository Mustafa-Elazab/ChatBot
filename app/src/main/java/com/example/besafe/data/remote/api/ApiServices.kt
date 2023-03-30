package com.example.besafe.data.remote.api

import com.example.besafe.data.remote.dto.BaseResponse
import com.example.besafe.data.remote.dto.CompletionRequest
import com.example.besafe.data.remote.dto.CompletionResponse
import com.example.besafe.data.remote.response.NetworkResponse
import retrofit2.http.*

interface ApiServices {


    @POST("completions")
    suspend fun completions(
        @Header("Authorization") auth: String?,
        @Body completionRequest: CompletionRequest
    ): NetworkResponse<CompletionResponse, BaseResponse>

}


