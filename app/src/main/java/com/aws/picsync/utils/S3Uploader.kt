package com.aws.picsync.utils

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import java.io.File

class S3Uploader {
    suspend fun uploadImage(bucketName: String, objectKey: String, objectPath: String) {
        val request = PutObjectRequest {
            bucket = bucketName
            key = objectKey
            body = File(objectPath).asByteStream()
        }

        S3Client { region = "us-west-1" }.use { s3 ->
            val response = s3.putObject(request)
            println("Tag information is ${response.eTag}")
        }
    }
}
