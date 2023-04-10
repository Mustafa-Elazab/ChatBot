package com.example.besafe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ImageGenerationResponseDTO(
    @SerializedName("created")
    val created: Int? = null,
    @SerializedName("data")
    val `data`: List<Data?>? = null
)

data class Data(
    @SerializedName("url")
    val url: String? = null
)