package com.example.besafe.domain.usecase

import com.example.besafe.data.remote.dto.CompletionRequest
import com.example.besafe.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatUseCase @Inject constructor(private val repository: ChatRepository) {

    suspend fun sendMessage(completionRequest: CompletionRequest) =
        repository.completions(completionRequest = completionRequest)

}