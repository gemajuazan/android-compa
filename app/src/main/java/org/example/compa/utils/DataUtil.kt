package org.example.compa.utils

import com.google.firebase.firestore.DocumentSnapshot
import org.example.compa.models.Person
import java.util.HashMap

class DataUtil {

    companion object {

        fun getPersonFromDatabaseHashMap(hashMap: HashMap<String, Any>): Person {
            val id = hashMap["id"] as String? ?: ""
            val name = hashMap["name"] as String? ?: ""
            val surnames = hashMap["surnames"] as String? ?: ""
            val birthdate = hashMap["birthdate"] as Long? ?: -1
            val email = hashMap["email"] as String? ?: ""
            val username = hashMap["username"] as String? ?: ""
            val phone = hashMap["phone"] as String? ?: ""
            return Person(
                id = id,
                name = name,
                surnames = surnames,
                birthdate = birthdate,
                email = email,
                username = username,
                phone = phone,
                image = ""
            )
        }

        fun getPersonFromDatabase(it: DocumentSnapshot): Person {
            val id = it.data?.get("id") as String? ?: ""
            val name = it.data?.get("name") as String? ?: ""
            val surnames = it.data?.get("surnames") as String? ?: ""
            val birthdate = it.data?.get("birthdate") as Long? ?: -1
            val email = it.data?.get("email") as String? ?: ""
            val username = it.data?.get("username") as String? ?: ""
            val phone = it.data?.get("phone") as String? ?: ""
            return Person(
                id = id,
                name = name,
                surnames = surnames,
                birthdate = birthdate,
                email = email,
                username = username,
                phone = phone,
                image = ""
            )
        }

    }
}