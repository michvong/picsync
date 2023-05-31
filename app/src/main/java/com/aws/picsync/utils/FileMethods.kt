package com.aws.picsync.utils

import org.apache.tika.Tika
import java.io.File

const val MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

class FileMethods {
    private fun determineFileFormat(file: File): String {
        val tika = Tika()
        return tika.detect(file)
    }

    fun validateFile(file: File): Boolean {
        val fileFormat = determineFileFormat(file)
        val fileSize = file.length()

        val allowedFileFormats = listOf("image/jpeg")

        if (!allowedFileFormats.contains(fileFormat) || fileSize > MAX_FILE_SIZE) {
            // File format or size is not valid
            return false
        }

        return true
    }
}