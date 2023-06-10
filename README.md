# PicSync 📸

An Android application that transfers photos from your phone to your computer.

## **Technologies**

- Android
- Jetpack Compose
- Flask
- AWS S3, Lambda

## **Getting Started**

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### **Prerequisites**

- An AWS account is required to run PicSync.

### **Installation**

1. Create a `.env` file in the root directory inside with the following:

```
BUCKET_NAME=<your bucket name>
REGION=<your region>
AWS_ACCESS_KEY_ID=<your AWS access key id>
AWS_SECRET_ACCESS_KEY=<your AWS secret access key>
AWS_SESSION_TOKEN=<your AWS session token>
```

Here is an example of the `.env` file:

```
BUCKET_NAME=my-picsync-bucket
REGION=us-east-1
AWS_ACCESS_KEY_ID=AKIAJSIE27KKMHXI3BJQ
AWS_SECRET_ACCESS_KEY=5bEYu26084qjSFyclM/f2pz4gviSfoOg+mFwBH39
AWS_SESSION_TOKEN=FQoDYXdzEPP//////////wEaDNiq11oUzqitIGSp7CKsAUoecwG4UGUhDYbo+leOoCr69T3zjxc3P4P0GM5nnHk7GX/qWtHngiwZ+qKTMsaB2LjyyR47CuAe8GZi2UKEk6aL5wyI3ZCZhUe+lRCBnG7bfPMtJ+70Ojyy6WfMdWaQwExFa/F8WfP2vChsJ3rO5zioqWkzT7qFyBK+qqhSFF7dmKzdYHW3mtfILjqeoLRmcjouNRGHdI/zdA6lZtiRKP4X0uDcEKzsfg/Z8Koow4Sl2QU=
```

For `BUCKET_NAME`, refer to the [Bucket Naming Rules](https://docs.aws.amazon.com/AmazonS3/latest/userguide/bucketnamingrules.html).

The AWS credentials can be accessed from your AWS access portal URL. Credentials that are created by IAM users are valid for the duration that you specify.
For detailed instructions, refer to the "Use temporary credentials" section in [AWS SDK for Kotlin documentation](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup-basic-onetime-setup.html).

## **Notes**

N/A
