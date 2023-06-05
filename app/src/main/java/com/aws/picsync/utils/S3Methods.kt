package com.aws.picsync.utils

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.sdk.kotlin.services.s3.model.CreateBucketConfiguration
import aws.sdk.kotlin.services.s3.model.CreateBucketRequest
import aws.sdk.kotlin.services.s3.model.Event
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.HeadBucketRequest
import aws.sdk.kotlin.services.s3.model.ListObjectsRequest
import aws.sdk.kotlin.services.s3.model.PutBucketNotificationConfigurationRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.model.TopicConfiguration
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.CreateTopicRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.writeToFile
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class S3Methods {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val snsMethods = SNSMethods();
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

    suspend fun addEventNotifConfig(bucketName: String) {
        val snsTopicArn = snsMethods.createSNSTopic()

        val request = PutBucketNotificationConfigurationRequest {
            bucket = bucketName
            notificationConfiguration {
                topicConfigurations = listOf(
                    TopicConfiguration {
                        topicArn = snsTopicArn
                        events = listOf(Event.S3ObjectCreated)
                })
            }
        }

        S3Client {
            region = dotenv["REGION"]
            credentialsProvider = getStatic()
        }.use { s3 ->
            s3.putBucketNotificationConfiguration(request)
            println("$bucketName will track object creation")
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

    suspend fun uploadObject(bucketName: String, objectKey: String, objectBytes: ByteStream) {
        val request = PutObjectRequest {
            bucket = bucketName
            key = objectKey
            body = objectBytes
        }

        S3Client {
            region = dotenv["REGION"]
            credentialsProvider = getStatic()
        }.use { s3 ->
            val response = s3.putObject(request)
            println("Tag information is ${response.eTag}")
        }
    }

    suspend fun getObject(bucketName: String, objectKey: String): File? {
        val file = withContext(Dispatchers.IO) {
            File.createTempFile("temp", null)
        }

        val request = GetObjectRequest {
            bucket = bucketName
            key = objectKey
        }

        S3Client {
            region = dotenv["REGION"]
            credentialsProvider = getStatic()
        }.use { s3 ->
             s3.getObject(request) { response ->
                 val myFile = File(file.path)
                 response.body?.writeToFile(myFile)
            }

        }

        return null
    }
}
