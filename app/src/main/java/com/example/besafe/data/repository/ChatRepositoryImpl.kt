package com.example.besafe.data.repository

import com.example.besafe.BuildConfig
import com.example.besafe.data.remote.api.ApiServices
import com.example.besafe.data.remote.dto.BaseResponse
import com.example.besafe.data.remote.dto.CompletionRequest
import com.example.besafe.data.remote.dto.CompletionResponse
import com.example.besafe.data.remote.response.NetworkResponse
import com.example.besafe.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(private val api: ApiServices) : ChatRepository {
    override suspend fun completions(
        completionRequest: CompletionRequest
    ): Flow<NetworkResponse<CompletionResponse, BaseResponse>> = flow {
        when (val get = api.completions(
            auth = BuildConfig.CHAT_GPT_KEY,
            completionRequest
        )) {
            is NetworkResponse.ApiError -> if (get.code == 500) emit(
                NetworkResponse.ApiError(
                    BaseResponse(code = 500, message = "Internal Server Error"), get.code
                )
            ) else emit(NetworkResponse.ApiError(get.body, get.code))
            NetworkResponse.Loading -> emit(NetworkResponse.Loading)
            is NetworkResponse.NetworkError -> emit(NetworkResponse.NetworkError(get.error))
            is NetworkResponse.Success -> emit(NetworkResponse.Success(get.body))
            is NetworkResponse.UnknownError -> emit(NetworkResponse.UnknownError(get.error))
        }
    }
}