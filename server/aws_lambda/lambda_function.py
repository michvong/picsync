
import boto3
import urllib.parse
from io import BytesIO
from PIL import Image, ImageOps
from pillow_heif import register_heif_opener

s3_client = boto3.client('s3')

register_heif_opener()

def lambda_handler(event, context):

    for record in event['Records']:

        bucket = record['s3']['bucket']['name']

        key = urllib.parse.unquote_plus(
            record['s3']['object']['key']
        )

        if not key.lower().endswith('.heic'):
            print(f"Skipping non-HEIC file: {key}")
            continue

        response = s3_client.get_object(
            Bucket=bucket,
            Key=key
        )

        image_data = response['Body'].read()

        image = Image.open(BytesIO(image_data))

        # Correct orientation using EXIF metadata
        image = ImageOps.exif_transpose(image)

        # JPEG does not support transparency
        image = image.convert('RGB')

        # Convert image to JPEG in memory
        jpeg_buffer = BytesIO()

        image.save(
            jpeg_buffer,
            format='JPEG',
            quality=85,
            optimize=True
        )

        jpeg_buffer.seek(0)

        # Change file extension from .heic to .jpg
        jpeg_key = key.rsplit('.', 1)[0] + '.jpg'

        s3_client.put_object(
            Bucket=bucket,
            Key=jpeg_key,
            Body=jpeg_buffer.getvalue(),
            ContentType='image/jpeg'
        )

        print(f"JPEG uploaded: {bucket}/{jpeg_key}")

        s3_client.delete_object(
            Bucket=bucket,
            Key=key
        )

        print(f"Original HEIC deleted: {bucket}/{key}")

    return {
        'statusCode': 200,
        'body': 'Media processing completed'
    }
