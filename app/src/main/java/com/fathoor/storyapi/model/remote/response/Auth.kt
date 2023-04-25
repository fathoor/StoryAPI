package com.fathoor.storyapi.model.remote.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("loginResult")
    val loginResult: LoginResult
)

data class LoginResult(
    @field:SerializedName("token")
    val token: String
)