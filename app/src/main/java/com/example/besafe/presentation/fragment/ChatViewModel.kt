package com.example.besafe.presentation.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.besafe.data.remote.dto.*
import com.example.besafe.data.remote.response.NetworkResponse
import com.example.besafe.domain.usecase.GetChatUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val useCase: GetChatUseCase,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {


    var apiKey: String? = runBlocking {
        dataStore.data.first()[API_KEY]
    }


    private val _validateErrorState =
        MutableSharedFlow<ValidateError>()
    val validateErrorState get() = _validateErrorState.asSharedFlow()



    private val _chatCompletionState =
        MutableStateFlow<NetworkResponse<ChatCompletionResponseDTO, BaseResponse>?>(null)
    val chatCompletionState get() = _chatCompletionState.asStateFlow()

    private val _imageCompletionState =
        MutableStateFlow<NetworkResponse<ImageGenerationResponseDTO, BaseResponse>?>(null)
    val imageCompletionState get() = _imageCompletionState.asStateFlow()


    fun sendMessage(chatCompletionRequestDTO: ChatCompletionRequestDTO) {
        viewModelScope.launch {
            useCase.sendPromptMessage(auth = "Bearer $apiKey", chatCompletionRequestDTO).collect {
                _chatCompletionState.emit(it)
            }
        }
    }


    fun generateImages(imageGenerationRequest: ImageGenerationRequest) {
        viewModelScope.launch {
            useCase.sendPromptImage(auth = "Bearer $apiKey", imageGenerationRequest).collect {
                _imageCompletionState.emit(it)
            }
        }
    }


    fun downloadImage(imageUrl: String) {
        viewModelScope.launch {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection =
                    withContext(Dispatchers.IO) {
                        url.openConnection()
                    } as HttpURLConnection
                connection.doInput = true
                withContext(Dispatchers.IO) {
                    connection.connect()
                }
                val input = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(input)

                val filename = "myimage.jpg"
                val dir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val imageFile = File(dir, filename)

                val outStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
                outStream.flush()
                outStream.close()

                withContext(Dispatchers.Main) {
                    // Show a success message to the user

                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Show an error message to the user

                }
            }
        }
    }

    suspend fun validateApiKey(apiKey: String) {
        if (apiKey.isNotEmpty()) {

            dataStore.edit { preferences ->
                preferences[API_KEY] = apiKey
            }


        } else {
            _validateErrorState.emit(ValidateError.API_ERROR)
        }
    }


    companion object {
        val API_KEY = stringPreferencesKey("apikey")
    }
}

enum class ValidateError {
    API_ERROR
}




