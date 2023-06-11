import logging
import boto3
from botocore.exceptions import ClientError
import os
from dotenv import load_dotenv

load_dotenv()

def create_bucket(bucket_name):
    try:
        s3_client = boto3.client(
            's3',
            aws_access_key_id=os.getenv("AWS_ACCESS_KEY_ID"),
            aws_secret_access_key=os.getenv("AWS_SECRET_ACCESS_KEY"),
            aws_session_token=os.getenv("AWS_SESSION_TOKEN"),
            region_name=os.getenv("REGION")
        )
        location = {'LocationConstraint': os.getenv("REGION")}
        s3_client.create_bucket(Bucket=bucket_name,
                                CreateBucketConfiguration=location)
    except ClientError as e:
        logging.error(e)
        return "Bucket could not be created\n"
    return "Bucket was successfully created\n"

def upload_file(file, bucket):
    try:
        s3_client = boto3.client(
            's3',
            aws_access_key_id=os.getenv("AWS_ACCESS_KEY_ID"),
            aws_secret_access_key=os.getenv("AWS_SECRET_ACCESS_KEY"),
            aws_session_token=os.getenv("AWS_SESSION_TOKEN"),
            region_name=os.getenv("REGION")
        )
        s3_client.upload_fileobj(file, bucket, file.filename)
        logging.info("Photo uploaded successfully")
        return "Photo uploaded successfully\n"
    except Exception as e:
        logging.error(f"Failed to upload photo: {str(e)}")
        raise