package com.fathoor.storyapi.model.remote.response

data class User(
    val name: String,
    val email: String,
    val password: String,
    val token: String
)