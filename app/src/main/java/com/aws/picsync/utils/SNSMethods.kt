package com.aws.picsync.utils

import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.model.CreateTopicRequest
import io.github.cdimascio.dotenv.dotenv

class SNSMethods {
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }

    suspend fun createSNSTopic(): String {
        val request = CreateTopicRequest {
            name = (dotenv["TOPIC_NAME"])
        }

        SnsClient { region = dotenv["REGION"] }.use { snsClient ->
            val result = snsClient.createTopic(request)
            return result.topicArn.toString()
        }
    }
}