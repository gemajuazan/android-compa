package org.example.compa.models

import java.io.Serializable

data class Task(
    val id: String,
    val name: String,
    val startDate: String,
    val finishDate: String,
    val category: String,
    val members: ArrayList<Member> = arrayListOf(),
    val description: String,
    val group: Group
) : Serializable