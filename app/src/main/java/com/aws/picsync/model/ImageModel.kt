package com.aws.picsync.model

import android.content.ClipData
import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
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

    fun handleMultipleImageSelection(clipData: ClipData, contentResolver: ContentResolver) {
        for (i in 0 until clipData.itemCount) {
            val imageUri: Uri? = clipData.getItemAt(i).uri
            val imagePath = getPathFromURI(contentResolver, imageUri)

            if (imagePath != null) {
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
    }

    fun handleSingleImageSelection(imageUri: Uri, contentResolver: ContentResolver) {
        val imagePath = getPathFromURI(contentResolver, imageUri)

        if (imagePath != null) {
            val fileName = File(imagePath).name

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    s3Methods.createNewBucket(bucketName = dotenv["BUCKET_NAME"]);
                    s3Methods.uploadImage(
                        bucketName = dotenv["BUCKET_NAME"],
                        objectKey = fileName,
                        objectPath = imagePath
                    )
                }
            }
        }
    }

    private fun getPathFromURI(contentResolver: ContentResolver, uri: Uri?): String? {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }
}