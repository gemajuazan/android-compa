package org.example.compa.models

import java.io.Serializable

data class MemberTask(
    val member: Member,
    var statusTask: String
) : Serializable