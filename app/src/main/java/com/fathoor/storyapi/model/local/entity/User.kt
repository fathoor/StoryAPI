package com.fathoor.storyapi.model.local.entity

data class User(
    val name: String,
    val email: String,
    val password: String,
    val token: String
)