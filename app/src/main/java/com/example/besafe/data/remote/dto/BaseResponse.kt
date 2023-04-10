package com.example.besafe.data.remote.dto

import androidx.annotation.Keep
import java.io.Serializable

@Keep
open class BaseResponse(
    val code: Int? = null,
    val param: Int? = null,
    val type: String = "",
    val message: String = ""
) : Serializable