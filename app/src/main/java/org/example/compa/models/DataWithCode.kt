package org.example.compa.models

import java.io.Serializable

data class DataWithCode(
    val text: String,
    val code: String
) : Serializable