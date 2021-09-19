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

    interface CategoryTask {
        companion object {
            const val CLEAN = "CLE"
            const val BUYING = "BUY"
            const val TRASH = "TRA"
            const val WASH = "WAS"
            const val KITCHEN = "KIT"
            const val IRON = "IRO"

            fun getCategoryTask(statusCode: String): Int {
                return when (statusCode) {
                    CLEAN -> R.string.cleanliness
                    BUYING -> R.string.buying
                    TRASH -> R.string.trash
                    WASH -> R.string.washing
                    KITCHEN -> R.string.kitchen
                    IRON -> R.string.iron
                    else -> R.string.cleanliness
                }
            }

            fun getCategoryImageTask(statusCode: String): Int {
                return when (statusCode) {
                    CLEAN -> R.drawable.ic_scrub
                    BUYING -> R.drawable.ic_buying
                    TRASH -> R.drawable.ic_trash
                    WASH -> R.drawable.ic_wash
                    KITCHEN -> R.drawable.ic_kitchen
                    IRON -> R.drawable.ic_iron
                    else -> R.drawable.ic_scrub
                }
            }

            fun getCategoryTaskCode(type: Int): String {
                return when (type) {
                    R.string.cleanliness -> CLEAN
                    R.string.buying -> BUYING
                    R.string.trash -> TRASH
                    R.string.washing -> WASH
                    R.string.kitchen -> KITCHEN
                    R.string.iron -> IRON
                    else -> CLEAN
                }
            }
        }
    }
}