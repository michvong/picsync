// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application") version "8.0.1" apply false
    id("com.android.library") version "8.0.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    kotlin("jvm") version "1.7.10"
    application
}

group = "com.aws.picsync"
version = "1.0-SNAPSHOT"

dependencies {
    implementation("aws.sdk.kotlin:s3:0.18.0-beta")
    implementation("aws.sdk.kotlin:dynamodb:0.18.0-beta")
    implementation("aws.sdk.kotlin:iam:0.18.0-beta")
    implementation("aws.sdk.kotlin:cloudwatch:0.18.0-beta")
    implementation("aws.sdk.kotlin:cognitoidentityprovider:0.18.0-beta")
    implementation("aws.sdk.kotlin:sns:0.18.0-beta")
    implementation("aws.sdk.kotlin:pinpoint:0.18.0-beta")
// single sign-on dependencies
    implementation("aws.sdk.kotlin:sso:0.18.0-beta")
    implementation("aws.sdk.kotlin:ssooidc:0.18.0-beta")
// test dependency
    testImplementation(kotlin("test"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}