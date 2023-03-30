package com.example.besafe.domain.repository

import com.example.besafe.data.remote.dto.BaseResponse
import com.example.besafe.data.remote.dto.CompletionRequest
import com.example.besafe.data.remote.dto.CompletionResponse
import com.example.besafe.data.remote.response.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface ChatRepository {


    suspend fun completions(
        completionRequest: CompletionRequest
    ): Flow<NetworkResponse<CompletionResponse, BaseResponse>>

}