package com.aws.picsync.model

import android.content.ContentResolver
import android.provider.MediaStore
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.aws.picsync.utils.S3Methods
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageModel {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val s3Methods = S3Methods();

    fun handleImageSend(imagePaths: SnapshotStateList<Int>, contentResolver: ContentResolver) {
        for (i in imagePaths.indices) {
            val imageIndex = imagePaths[i]
            val galleryPaths = getGalleryPhotos(contentResolver = contentResolver)
            val imagePath = galleryPaths[imageIndex]
            val fileName = File(imagePath).name

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    s3Methods.createNewBucket(bucketName = dotenv["BUCKET_NAME"])
                    s3Methods.uploadImage(
                        bucketName = dotenv["BUCKET_NAME"],
                        objectKey = fileName,
                        objectPath = imagePath
                    )
                }
            }
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