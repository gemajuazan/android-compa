package org.example.compa.models

import java.io.Serializable

data class Subtask(
    val id: Int,
    val name: String,
    val description: String,
    val status: String,
    val dateStart: String,
    val dateFinish: String,
    val category: String,
    val memberCreated: String,
    val subTaskMembers: ArrayList<Member>
) : Serializable