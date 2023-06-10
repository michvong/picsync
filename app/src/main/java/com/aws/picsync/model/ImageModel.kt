package com.aws.picsync.model

import android.content.ContentResolver
import android.provider.MediaStore
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.aws.picsync.services.Api
import java.io.File


class ImageModel {
    private val api = Api()

    fun handleImageSend(imagePaths: SnapshotStateList<Int>, contentResolver: ContentResolver) {
        api.createBucket()
        for (i in imagePaths.indices) {
            val imageIndex = imagePaths[i]
            val galleryPaths = getGalleryPhotos(contentResolver)
            val imagePath = galleryPaths[imageIndex]
            val imageFile = File(imagePath)

            api.uploadFile(imageFile)
        }
    }

    fun getGalleryPhotos(contentResolver: ContentResolver): List<String> {
        val galleryPaths = mutableListOf<String>()
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val query = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        query?.use { cursor ->
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            while (cursor.moveToNext()) {
                val data = cursor.getString(dataColumn)
                galleryPaths.add(data)
            }
        }

        return galleryPaths
    }
}