package com.example.fermiontask.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.fermiontask.model.ProfileModel
import java.io.ByteArrayOutputStream

class UsersDatabase(
    context: Context?
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "profile_database"
        private val DATABASE_VERSION = 1

        val TABLE_NAME = "user"

        val ID_COL = "id"
        val PHONE_COl = "phone"
        val EMAIL_COl = "email"
        val ADDRESS_COl = "address"
        val IMAGE_COL = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            ("CREATE TABLE $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $PHONE_COl TEXT,$EMAIL_COl TEXT,$ADDRESS_COl TEXT,$IMAGE_COL BLOB)")

        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun addUser(user: ProfileModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(PHONE_COl, user.phoneNumber)
        values.put(EMAIL_COl, user.emailAddress)
        values.put(ADDRESS_COl, user.deliveryAddress)
        values.put(IMAGE_COL, convertBitmapToByteArray(user.bitmap!!))
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateUser(user: ProfileModel) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(PHONE_COl, user.phoneNumber)
        values.put(EMAIL_COl, user.emailAddress)
        values.put(ADDRESS_COl, user.deliveryAddress)
        values.put(IMAGE_COL, convertBitmapToByteArray(user.bitmap!!))
        db.update(TABLE_NAME, values, "$ID_COL=?", arrayOf("1"))
        db.close()
    }

    @SuppressLint("Range")
    fun getUser(): ProfileModel? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME LIMIT 1", null)
        var user: ProfileModel? = null
        if (cursor != null && cursor.moveToFirst()) {

            val bitmap = convertByteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex(IMAGE_COL)))

            user = ProfileModel(
                phoneNumber = cursor.getString(cursor.getColumnIndex(PHONE_COl)),
                emailAddress = cursor.getString(cursor.getColumnIndex(EMAIL_COl)),
                deliveryAddress = cursor.getString(cursor.getColumnIndex(ADDRESS_COl)),
                bitmap = bitmap
            )
        }

        cursor.close()
        db.close()

        return user

    }

    fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
        return stream.toByteArray()
    }

    fun convertByteArrayToBitmap(byte: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byte, 0, byte.size)

    }
}