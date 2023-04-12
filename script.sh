#!/bin/bash

# Define variables
JAR_NAME="deploy-0.0.1-SNAPSHOT.jar"
EC2_INSTANCE_USER="ubuntu"
EC2_INSTANCE_IP="ec2-54-90-141-30.compute-1.amazonaws.com"
EC2_INSTANCE_KEY_FILE_PATH="/Users/gmartirosyan/Downloads/deploy/myCiCdkeyPair.pem"
UBUNTU_HOME="/home/ubuntu/"
PORT="8080"

PID=$(ssh -i "$EC2_INSTANCE_KEY_FILE_PATH" "$EC2_INSTANCE_USER@$EC2_INSTANCE_IP" "sudo lsof -t -i:$PORT")

# If the PID is not empty, stop the process
if [ -n "$PID" ]; then
  echo "Stopping previously running Java process (PID: $PID) on port 8080..."
  ssh -i "$EC2_INSTANCE_KEY_FILE_PATH" "$EC2_INSTANCE_USER@$EC2_INSTANCE_IP" "sudo kill $PID"
  sleep 5
fi

# Build the project with Maven
mvn clean install

echo "copying jar into remote host"
# Copy the JAR file to the EC2 instance
scp -i "$EC2_INSTANCE_KEY_FILE_PATH" target/"$JAR_NAME" "$EC2_INSTANCE_USER@$EC2_INSTANCE_IP":"$UBUNTU_HOME"
echo "starting app..."

# Connect to the EC2 instance and start the application
ssh -i "$EC2_INSTANCE_KEY_FILE_PATH" "$EC2_INSTANCE_USER@$EC2_INSTANCE_IP" "java -jar $UBUNTU_HOME$JAR_NAME"
