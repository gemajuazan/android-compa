package org.example.compa.models

import java.io.Serializable

data class User(
    val username: String,
    val email: String,
    val uid: String,
    val emailVerified: Boolean
) : Serializable