package com.aws.picsync.utils

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.HeadBucketRequest
import aws.sdk.kotlin.services.s3.model.ListObjectsRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import java.io.File
import java.lang.Exception

class S3Methods {
    suspend fun checkBucketExists(bucketName: String) {
        val request = HeadBucketRequest {
            bucket = bucketName
        }

        try {
            S3Client { region = "us-west-1" }.use { s3 ->
                s3.headBucket(request)
            }
        } catch (e: Exception) {
            println("$bucketName already exists")
        }
    }
    suspend fun createNewBucket(bucketName: String) {
        val request = CreateBucketRequest {
            bucket = bucketName
        }

        S3Client { region = "us-west-1" }.use { s3 ->
            s3.createBucket(request)
            println("$bucketName is ready")
        }
    }

    suspend fun listBucketObjects(bucketName: String) {
        val request = ListObjectsRequest {
            bucket = bucketName
        }

        S3Client { region = "us-west-1" }.use { s3 ->
            val response = s3.listObjects(request)
            response.contents?.forEach { myObject ->
                println("The name of the key is ${myObject.key}")
                println("The owner is ${myObject.owner}")
            }
        }
    }

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
