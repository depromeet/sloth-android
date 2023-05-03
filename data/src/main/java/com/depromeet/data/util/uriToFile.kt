package com.depromeet.data.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)

    // 파일 확장자 추출
    val extension = uri.path?.let { path ->
        path.substring(path.lastIndexOf(".") + 1)
    } ?: "tmp"

    // 임시파일 생성
    val cacheFile = File.createTempFile("temp_image", ".$extension", context.cacheDir)
    val outputStream = FileOutputStream(cacheFile)

    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }

    return cacheFile
}