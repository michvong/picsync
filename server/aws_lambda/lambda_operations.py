import io
import json
import logging
import zipfile
import boto3
from botocore.exceptions import ClientError
import os
from dotenv import load_dotenv

load_dotenv()

logger = logging.getLogger(__name__)

# @staticmethod
def create_deployment_package():
    buffer = io.BytesIO()
    with zipfile.ZipFile(buffer, 'w') as zipped:
        zipped.write('/Users/mich/Documents/projects/picsync/server/aws_lambda/lambda_function.py', 'lambda_function.py')
    buffer.seek(0)
    return buffer.read()

def create_iam_role_for_lambda(iam_role_name):
    iam_client = boto3.client(
        'iam',
        aws_access_key_id=os.getenv("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.getenv("AWS_SECRET_ACCESS_KEY"),
        aws_session_token=os.getenv("AWS_SESSION_TOKEN"),
        region_name=os.getenv("REGION")
    )

    lambda_assume_role_policy = {
        'Version': '2012-10-17',
        'Statement': [
            {
                'Effect': 'Allow',
                'Principal': {
                    'Service': 'lambda.amazonaws.com'
                },
                'Action': 'sts:AssumeRole'
            }
        ]
    }
    policy_arn = 'arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole'

    try:
        role = iam_client.create_role(
            RoleName=iam_role_name,
            AssumeRolePolicyDocument=json.dumps(lambda_assume_role_policy))
        logger.info("Created role %s.", role.get('RoleName'))
        iam_client.attach_role_policy(
            RoleName=iam_role_name,
            PolicyArn=policy_arn
        )
        logger.info("Attached basic execution policy to role %s.", role.get('RoleName'))
    except ClientError as e:
        if e.response['Error']['Code'] == 'EntityAlreadyExists':
            logger.warning("The role %s already exists.", iam_role_name)
            role = iam_client.get_role(RoleName=iam_role_name)
        else:
            logger.exception(
                "Couldn't create role %s or attach policy %s.",
                iam_role_name, policy_arn)
            raise

    return role, "Role was successfully created"


def create_function(function_name, handler_name, iam_role, deployment_package):
    s3_client = boto3.client(
        'lambda',
        aws_access_key_id=os.getenv("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.getenv("AWS_SECRET_ACCESS_KEY"),
        aws_session_token=os.getenv("AWS_SESSION_TOKEN"),
        region_name=os.getenv("REGION")
    )
    logger.info(iam_role)
    try:
        response = s3_client.create_function(
            FunctionName=function_name,
            Description="AWS Lambda for PicSync",
            Runtime='python3.10',
            Role = iam_role[0]['Role']['Arn'],
            Handler=handler_name,
            Code={'ZipFile': deployment_package},
            Publish=True)
        function_arn = response['FunctionArn']
        waiter = s3_client.get_waiter('function_active_v2')
        waiter.wait(FunctionName=function_name)
        logger.info("Created function '%s' with ARN: '%s'.",
                    function_name, response['FunctionArn'])
    except ClientError:
        logger.error("Couldn't create function %s.", function_name)
        raise
    else:
        return function_arn