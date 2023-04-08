package com.example.besafe.domain.usecase

import com.example.besafe.data.remote.dto.ChatCompletionRequestDTO
import com.example.besafe.data.remote.dto.ImageGenerationRequest
import com.example.besafe.domain.repository.ChatRepository
import javax.inject.Inject

class GetChatUseCase @Inject constructor(private val repository: ChatRepository) {

    suspend fun sendPromptImage(auth: String?, imageGenerationRequest: ImageGenerationRequest) =
        repository.generateImagesForPrompt(
            auth = auth,
            imageGenerationRequest = imageGenerationRequest
        )


    suspend fun sendPromptMessage(
        auth: String?,
        chatCompletionRequestDTO: ChatCompletionRequestDTO
    ) =
        repository.getCompletionsForPrompt(
            auth = auth,
            chatCompletionRequestDTO = chatCompletionRequestDTO
        )

}