package com.example.besafe.domain.repository

import com.example.besafe.data.remote.dto.*
import com.example.besafe.data.remote.response.NetworkResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Header

interface ChatRepository {


    suspend fun generateImagesForPrompt(
        auth: String?,
        imageGenerationRequest: ImageGenerationRequest
    ): Flow<NetworkResponse<ImageGenerationResponseDTO, BaseResponse>>


    suspend fun getCompletionsForPrompt(
        auth: String?,
        chatCompletionRequestDTO: ChatCompletionRequestDTO
    ): Flow<NetworkResponse<ChatCompletionResponseDTO, BaseResponse>>

}