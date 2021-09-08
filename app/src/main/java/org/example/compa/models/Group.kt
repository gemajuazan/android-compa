package org.example.compa.models

import java.io.Serializable

data class Group(
    val id: String,
    val name: String,
    val place: String
) : Serializable