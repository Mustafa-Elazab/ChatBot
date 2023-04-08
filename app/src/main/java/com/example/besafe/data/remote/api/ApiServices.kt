package com.example.besafe.data.remote.api

import com.example.besafe.data.remote.dto.*
import com.example.besafe.data.remote.response.NetworkResponse
import retrofit2.http.*

interface ApiServices {


    @POST("images/generations")
    suspend fun generateImagesForPrompt(
        @Header("Authorization") auth: String?,
        @Body imageGenerationRequest: ImageGenerationRequest
    ): NetworkResponse<ImageGenerationResponseDTO, BaseResponse>


    @POST("chat/completions")
    suspend fun getCompletionsForPrompt(
        @Header("Authorization") auth: String?,
        @Body chatCompletionRequestDTO: ChatCompletionRequestDTO
    ): NetworkResponse<ChatCompletionResponseDTO, BaseResponse>

}


