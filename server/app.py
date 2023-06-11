from flask import Flask, request
import os
from dotenv import load_dotenv
import s3.s3_operations as s3
import aws_lambda.lambda_operations as aws_lambda
import logging

logger = logging.getLogger(__name__)
load_dotenv()

app = Flask(__name__)

@app.route('/')
def hello_world():
    return "<p>Hello, World!</p>"

@app.route('/create-bucket', methods=['GET', 'POST'])
def create_bucket_route():
    result = s3.create_bucket(os.getenv("BUCKET_NAME"))
    return result

@app.route('/upload-file', methods=['GET', 'POST'])
def upload_file_route():
    file = request.files['file']
    bucket_name = os.getenv("BUCKET_NAME")
    result = s3.upload_file(file, bucket_name)
    return result

@app.route('/create-lambda-function', methods=['GET', 'POST'])
def create_lambda_function_route():
    deployment_package = aws_lambda.create_deployment_package()
    iam_role = aws_lambda.create_iam_role_for_lambda('my-picsync-lambda-role')
    result = aws_lambda.create_function('my-picsync-lambda', 'lambda_handler', iam_role, deployment_package)
    return result

if __name__ == '__main__':
    app.run("0.0.0.0", debug=True, port=5000)