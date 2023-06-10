from flask import Flask, request
import s3.s3_operations as s3
import os
from dotenv import load_dotenv

load_dotenv()

app = Flask(__name__)

@app.route('/')
def hello_world():
    return "<p>Hello, World!</p>"

@app.route('/create-bucket', methods=['GET', 'POST'])
def create_bucket_route():
    result = s3.create_bucket(os.getenv("BUCKET_NAME"));
    return result

@app.route('/upload-file', methods=['GET', 'POST'])
def upload_file_route():
    file = request.files['file']
    bucket_name = os.getenv("BUCKET_NAME")
    result = s3.upload_file(file, bucket_name)
    return result

if __name__ == '__main__':
    app.run("0.0.0.0", debug=True, port=5000)