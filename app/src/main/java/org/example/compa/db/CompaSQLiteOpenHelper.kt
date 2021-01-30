package org.example.compa.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper;

class CompaSQLiteOpenHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(dbCompa: SQLiteDatabase?) {
        dbCompa?.execSQL("CREATE TABLE user (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT, " +
                "email TEXT, " +
                "password TEXT, "+
                "name TEXT, " +
                "surnames TEXT, " +
                "birthdate BIGINT)")
        dbCompa?.execSQL("CREATE TABLE payment (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "transmitter TEXT, " +
                "receiver TEXT, " +
                "date BIGINT, "+
                "price REAL, " +
                "concept TEXT, " +
                "status TEXT)")
        dbCompa?.execSQL("CREATE TABLE task (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT, " +
                "startDate BIGINT, " +
                "finishDate BIGINT, "+
                "category TEXT, " +
                "members TEXT," +
                "numMembers INTEGER, " +
                "description TEXT)")
    }

    override fun onUpgrade(dbCompa: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        const val DATABASE_VERSION = 4
    }
}