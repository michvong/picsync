import json
import boto3
import gzip
import os
from dotenv import load_dotenv

load_dotenv()

def lambda_handler(event, context):
    s3_client = boto3.client(
        's3',
        aws_access_key_id=os.getenv("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.getenv("AWS_SECRET_ACCESS_KEY"),
        aws_session_token=os.getenv("AWS_SESSION_TOKEN"),
        region_name=os.getenv("REGION")
    )

    for record in event['Records']:
        bucket = record['s3']['bucket']['name']
        key = record['s3']['object']['key']

        response = s3_client.head_object(Bucket=bucket, Key=key)
        object_size = response['ContentLength']

        # Check if object size exceeds 10MB (10,000,000 bytes)
        # if object_size > 10000000:
        response = s3_client.get_object(Bucket=bucket, Key=key)
        object_data = response['Body'].read()

        compressed_data = gzip.compress(object_data)

        compressed_key = 'compressed/' + key

        s3_client.put_object(
            Bucket=bucket,
            Key=compressed_key,
            Body=compressed_data,
            ContentEncoding='gzip'
        )

        print(f"Object compressed and uploaded: {bucket}/{compressed_key}")

    return {
        'statusCode': 200,
        'body': json.dumps("Photo downloaded from S3: {bucket}/{key} -> {local_path}")

    }
