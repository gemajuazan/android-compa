package org.example.compa.preferences

import android.content.SharedPreferences
import androidx.core.content.edit

class AppPreference {

    companion object {
        var sharedPreferences: SharedPreferences? = null

        const val USER_UID = "user_uid"

        @JvmStatic
        fun instance(): AppPreference {
            return AppPreference()
        }

        fun getUserUID(): String {
            return sharedPreferences?.getString(USER_UID, "") ?: ""
        }

        fun setUserUID(uid: String) {
            sharedPreferences?.edit {
                putString(USER_UID, uid)
            }
        }
    }
}