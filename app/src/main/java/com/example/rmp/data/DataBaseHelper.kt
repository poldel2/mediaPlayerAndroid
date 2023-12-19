package com.example.rmp.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.rmp.data.model.LoggedInUser

class DatabaseHelper (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private var instance: DatabaseHelper? = null
        private const val DATABASE_NAME = "login.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_NAME = "User"
        private const val COLUMN_LOGIN = "login"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_GENDER = "gender"
        private const val COLUMN_BIRTHDATE = "birthdate"
        private const val COLUMN_PHOTO = "photo"
        private const val CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME +
                "(" + COLUMN_LOGIN + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_GENDER + " TEXT, " +
                COLUMN_BIRTHDATE + " TEXT, " +
                COLUMN_PHOTO + " BLOB)"

        @Synchronized
        fun getInstance(context: Context): DatabaseHelper? {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance
        }
    }

    private val database: SQLiteDatabase = writableDatabase

    fun getDatabase() : SQLiteDatabase {
        return database
    }


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_GENDER TEXT")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_BIRTHDATE TEXT")
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COLUMN_PHOTO BLOB")
        }
    }

    fun getUserByUsernameAndPassword(username: String, password: String): LoggedInUser? {

        val projection = arrayOf("login", "password", "name", "lastname", "gender", "birthdate", "photo")

        val selection = "$COLUMN_LOGIN = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)

        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null,
            "1"
        )
        var user: LoggedInUser? = null
        if (cursor.moveToFirst()) {
            val login = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOGIN))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
            val gender = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_GENDER))
            val birthdate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BIRTHDATE))
            val photo = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_PHOTO))
            user = LoggedInUser(login, name, password, gender, birthdate)
        }
        cursor.close()
        return user
    }

    fun updateUserInfo(login: String, name: String, gender: String, birthdate: String) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_LOGIN, login)
        contentValues.put(COLUMN_NAME, name)
        contentValues.put(COLUMN_GENDER, gender)
        contentValues.put(COLUMN_BIRTHDATE, birthdate)

        val selection = "$COLUMN_LOGIN = ?"
        val selectionArgs = arrayOf(login)

        db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

}
