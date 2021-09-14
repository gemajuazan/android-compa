package org.example.compa.models.constants

import org.example.compa.R

class Constants {

    interface StatusTask {
        companion object {
            const val FOR_DOING = "FDO"
            const val IN_PROCESS = "IPR"
            const val IN_REVISION = "IRE"
            const val FINISHED = "FIN"

            fun getStatusTask(statusCode: String): Int {
                return when (statusCode) {
                    FOR_DOING -> R.string.for_doing
                    IN_PROCESS -> R.string.in_process
                    IN_REVISION -> R.string.in_revision
                    FINISHED -> R.string.finished
                    else -> R.string.for_doing
                }
            }

            fun getStatusTaskCode(type: Int): String {
                return when (type) {
                    R.string.for_doing -> FOR_DOING
                    R.string.in_process -> IN_PROCESS
                    R.string.in_revision -> IN_REVISION
                    R.string.finished -> FINISHED
                    else -> FOR_DOING
                }
            }
        }
    }
}