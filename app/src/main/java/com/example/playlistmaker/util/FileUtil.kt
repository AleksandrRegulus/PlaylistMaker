package com.example.playlistmaker.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object FileUtil {

    fun saveImageToPrivateStorage(activity: Activity, uri: Uri): String {
        val filePath = File(
            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PRIVATE_STORAGE_FOLDER_NAME
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${System.currentTimeMillis()}.jpg")
        val inputStream = activity.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toString()
    }

    fun deleteOldPosterFromStorage(posterUri: String) {
        val file = File(posterUri)
        if (file.exists()) file.delete()
    }

    private const val PRIVATE_STORAGE_FOLDER_NAME = "playlists_poster"
}