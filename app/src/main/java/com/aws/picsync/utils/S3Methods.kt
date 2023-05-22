package com.aws.picsync.utils

import io.github.cdimascio.dotenv.dotenv
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.CreateBucketConfiguration
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.HeadBucketRequest
import aws.sdk.kotlin.services.s3.model.ListObjectsRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.asByteStream
import java.io.File
import java.lang.Exception

class S3Methods {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private fun getStatic() : StaticCredentialsProvider {
        val staticCredentials = StaticCredentialsProvider {
            accessKeyId = dotenv["AWS_ACCESS_KEY_ID"]
            secretAccessKey = dotenv["AWS_SECRET_ACCESS_KEY"]
            sessionToken = dotenv["AWS_SESSION_TOKEN"]
        }
        return staticCredentials
    }
    private suspend fun checkBucketExists(bucketName: String): Boolean {
        val request = HeadBucketRequest {
            bucket = bucketName
        }

        return try {
            S3Client {
                region = dotenv["REGION"]
                credentialsProvider = getStatic()
            }.use { s3 ->
                s3.headBucket(request)
            }
            println("$bucketName already exists")
            true
        } catch (e: Exception) {
            println("$bucketName doesn't exist")
            false
        }
    }
    suspend fun createNewBucket(bucketName: String) {
        if (checkBucketExists(bucketName)) {
            return;
        }

        val request = CreateBucketRequest {
            bucket = bucketName
            createBucketConfiguration = CreateBucketConfiguration {
                locationConstraint = BucketLocationConstraint.fromValue(dotenv["REGION"])
            }
        }

        S3Client {
            region = dotenv["REGION"]
            credentialsProvider = getStatic()
        }.use { s3 ->
            s3.createBucket(request)
            println("$bucketName is ready")
        }
    }

    suspend fun listBucketObjects(bucketName: String) {
        val request = ListObjectsRequest {
            bucket = bucketName
        }

        S3Client { region = dotenv["REGION"] }.use { s3 ->
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

        S3Client {
            region = dotenv["REGION"]
            credentialsProvider = getStatic()
        }.use { s3 ->
            val response = s3.putObject(request)
            println("Tag information is ${response.eTag}")
        }
    }
}
