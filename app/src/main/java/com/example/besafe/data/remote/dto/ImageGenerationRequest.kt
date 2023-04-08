package com.example.besafe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ImageGenerationRequest(
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("n")
    val numberOfImages: Int = 1,
    @SerializedName("size")
    val size: String = "512x512"
)