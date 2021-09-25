package org.example.compa.preferences

import android.content.SharedPreferences
import androidx.core.content.edit

class AppPreference {

    companion object {
        var sharedPreferences: SharedPreferences? = null

        const val USER_UID = "user_uid"
        const val USER_NAME = "user_name"
        const val USER_USERNAME = "user_username"
        const val USER_EMAIL = "user_email"

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

        fun getUserName(): String {
            return sharedPreferences?.getString(USER_NAME, "") ?: ""
        }

        fun setUserName(name: String) {
            sharedPreferences?.edit {
                putString(USER_NAME, name)
            }
        }

        fun getUserUsername(): String {
            return sharedPreferences?.getString(USER_USERNAME, "") ?: ""
        }

        fun setUserUsername(username: String) {
            sharedPreferences?.edit {
                putString(USER_USERNAME, username)
            }
        }

        fun getUserEmail(): String {
            return sharedPreferences?.getString(USER_EMAIL, "") ?: ""
        }

        fun setUserEmail(uid: String) {
            sharedPreferences?.edit {
                putString(USER_EMAIL, uid)
            }
        }
    }
}