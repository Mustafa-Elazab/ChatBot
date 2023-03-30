package com.example.besafe.presentation.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.besafe.data.remote.dto.BaseResponse
import com.example.besafe.data.remote.dto.CompletionRequest
import com.example.besafe.data.remote.dto.CompletionResponse
import com.example.besafe.data.remote.response.NetworkResponse
import com.example.besafe.domain.usecase.GetChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val useCase: GetChatUseCase
) : ViewModel() {


    private val _chatState =
        MutableStateFlow<NetworkResponse<CompletionResponse, BaseResponse>?>(null)
    val chatState get() = _chatState.asStateFlow()




    fun setState(completionRequest: CompletionRequest) {
        viewModelScope.launch {
            useCase.sendMessage(completionRequest).collect {
                _chatState.emit(it)
            }
        }
    }


}



